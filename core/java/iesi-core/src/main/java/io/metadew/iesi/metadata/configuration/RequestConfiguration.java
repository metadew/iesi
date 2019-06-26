package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.RequestAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.RequestDoesNotExistException;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;
import io.metadew.iesi.metadata.definition.ListObject;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestConfiguration {

	private Request request;
	private FrameworkInstance frameworkInstance;

	// Constructors
	public RequestConfiguration(FrameworkInstance frameworkInstance) {
		this.setFrameworkInstance(frameworkInstance);
	}

	public RequestConfiguration(Request request, FrameworkInstance frameworkInstance) {
		this.setRequest(request);
		this.setFrameworkInstance(frameworkInstance);
	}

	public Optional<Request> getRequest(String requestId) {
		Request request = null;
		String queryRequest = "select request_id, parent_request_id, request_typ_nm, request_tms, request_nm, request_dsc, amount_nb, notif_email, scope_nm, context_nm, space_nm, user_nm, user_password, exe_id from "
				+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
						.getTableNameByLabel("Requests")
				+ " where request_id = " + SQLTools.GetStringForSQL(requestId);

		CachedRowSet crsRequest = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.executeQuery(queryRequest, "reader");
		RequestParameterConfiguration requestParameterConfiguration = new RequestParameterConfiguration(
				this.getFrameworkInstance());
		try {
			while (crsRequest.next()) {
				// Get parameters
				String queryRequestParameters = "select REQUEST_ID, REQUEST_PAR_TYP_NM, REQUEST_PAR_NM, REQUEST_PAR_VAL from "
						+ this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
								.getTableNameByLabel("RequestParameters")
						+ " where REQUEST_ID = '" + requestId + "'";
				CachedRowSet crsRequestParameters = this.getFrameworkInstance()
						.getExecutionServerRepositoryConfiguration().executeQuery(queryRequestParameters, "reader");
				List<RequestParameter> requestParameters = new ArrayList<>();
				while (crsRequestParameters.next()) {
					requestParameters.add(requestParameterConfiguration.getRequestParameter(requestId,
							crsRequestParameters.getString("REQUEST_PAR_TYP_NM"),
							crsRequestParameters.getString("REQUEST_PAR_NM")));
				}
				crsRequestParameters.close();
				
				request = new Request(requestId, crsRequest.getString("REQUEST_TYP_NM"),
						crsRequest.getString("REQUEST_TMS"), crsRequest.getString("REQUEST_NM"),
						crsRequest.getString("REQUEST_DSC"), crsRequest.getInt("AMOUNT_NB"),
						crsRequest.getString("NOTIF_EMAIL"), crsRequest.getString("SCOPE_NM"),
						crsRequest.getString("CONTEXT_NM"), crsRequest.getString("SPACE_NM"),
						crsRequest.getString("USER_NM"), crsRequest.getString("USER_PASSWORD"), requestParameters);
			}
			crsRequest.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return Optional.ofNullable(request);
	}

	public boolean exists(Request request) {
		String queryRequest = "select * from " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests") + " where REQUEST_ID = '" + request.getId() + "'";
		CachedRowSet crsRequest = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.executeQuery(queryRequest, "reader");
		return crsRequest.size() == 1;
	}

	public List<Request> getAllRequests() {
		List<Request> requests = new ArrayList<>();
		String query = "select REQUEST_ID from " + this.getFrameworkInstance()
				.getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests") + " order by LOAD_TMS ASC";
		CachedRowSet crs = this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeQuery(query,
				"reader");
		RequestConfiguration requestConfiguration = new RequestConfiguration(this.getFrameworkInstance());
		try {
			while (crs.next()) {
				String requestId = crs.getString("REQUEST_ID");
				requestConfiguration.getRequest(requestId).ifPresent(requests::add);
			}
			crs.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return requests;
	}

	public void deleteRequest(Request request) throws RequestDoesNotExistException {
		// TODO: logging
		// this.getFrameworkInstance().getFrameworkLog().log(MessageFormat.format("Deleting
		// request {0}", request.getName()),Level.TRACE);
		if (!exists(request)) {
			throw new RequestDoesNotExistException(MessageFormat
					.format("Request {0} is not present in the repository so cannot be deleted", request.getId()));
		}
		String query = getDeleteStatement(request);
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(query);
	}

	public String getDeleteStatement(Request request) {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests");
		sql += " WHERE REQUEST_ID = " + SQLTools.GetStringForSQL(request.getId());
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("RequestParameters");
		sql += " WHERE REQUEST_ID = " + SQLTools.GetStringForSQL(request.getId());
		sql += ";";
		sql += "\n";

		return sql;

	}

	public void deleteAllRequests() {
		// TODO: logging
		// this.getFrameworkInstance().getFrameworkLog().log("Deleting all requests",
		// Level.TRACE);
		String query = getDeleteAllStatement();
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(query);
	}

	private String getDeleteAllStatement() {
		String sql = "";

		sql += "DELETE FROM " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests");
		sql += ";";
		sql += "\n";
		sql += "DELETE FROM " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("RequestParameters");
		sql += ";";
		sql += "\n";

		return sql;
	}

	public void insertRequest(Request request) throws RequestAlreadyExistsException {
		// TODO:logging
		// this.getFrameworkInstance().getFrameworkLog().log(MessageFormat.format("Inserting
		// request {0}", request.getName()), Level.TRACE);
		if (exists(request)) {
			throw new RequestAlreadyExistsException(
					MessageFormat.format("Request {0} already exists", request.getId()));
		}
		String query = getInsertStatement(request);
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration().executeUpdate(query);
	}

	public String getInsertStatement(Request request) {
		String sql = "";
		sql += "INSERT INTO " + this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests");
		sql += " (request_id, parent_request_id, request_typ_nm, request_tms, request_nm, request_dsc, amount_nb, notif_email, scope_nm, context_nm, space_nm, user_nm, user_password, exe_id) ";
		sql += "VALUES ";
		sql += "(";
		sql += SQLTools.GetStringForSQL(request.getId());
		sql += ",";
		sql += SQLTools.GetStringForSQL("0");
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getTimestamp());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getDescription());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getAmount());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getEmail());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getScope());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getContext());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getSpace());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getUser());
		sql += ",";
		sql += SQLTools.GetStringForSQL(request.getPassword());
		sql += ",";
		sql += SQLTools.GetStringForSQL(-1);
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(request);
		if (!sqlParameters.equalsIgnoreCase("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	public void updateRequest(Request request) throws RequestDoesNotExistException {
		// this.getFrameworkInstance().getFrameworkLog().log(MessageFormat.format("Updating
		// request {0}.", request.getName()),Level.TRACE);
		try {
			deleteRequest(request);
			insertRequest(request);
		} catch (RequestDoesNotExistException e) {
//			this.getFrameworkInstance().getFrameworkLog().log(MessageFormat.format("Request {0} is not present in the repository so cannot be updated", request.getName()),	Level.TRACE);
			throw new RequestDoesNotExistException(MessageFormat
					.format("Request {0} is not present in the repository so cannot be updated", request.getId()));
		} catch (RequestAlreadyExistsException e) {
			// this.getFrameworkInstance().getFrameworkLog().log(MessageFormat.format("Request
			// {0} is not deleted correctly during update. {1}", request.getName(),
			// e.toString()),Level.WARN);
		}
	}

	// Delete
	public String getDeleteStatement() {
		String sql = "";

		sql += this.getDeleteStatement(this.getRequest());

		return sql;

	}

	// Insert
	public String getInsertStatement() {
		String sql = "";

		if (this.exists()) {
			sql += this.getDeleteStatement();
		}

		sql += this.getInsertStatement(this.getRequest());

		return sql;
	}

	private String getParameterInsertStatements(Request request) {
		String result = "";

		// Catch null parameters
		if (request.getParameters() == null)
			return result;

		for (RequestParameter requestParameter : request.getParameters()) {
			RequestParameterConfiguration requestParameterConfiguration = new RequestParameterConfiguration(
					this.getFrameworkInstance());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += requestParameterConfiguration.getInsertStatement(request.getId(), requestParameter);
		}

		return result;
	}

	@SuppressWarnings("unused")
	private String getParameterInsertStatements(String requestName) {
		String result = "";

		// Catch null parameters
		if (this.getRequest().getParameters() == null)
			return result;

		for (RequestParameter requestParameter : this.getRequest().getParameters()) {
			RequestParameterConfiguration requestParameterConfiguration = new RequestParameterConfiguration(
					requestParameter, this.getFrameworkInstance());
			if (!result.equalsIgnoreCase(""))
				result += "\n";
			result += requestParameterConfiguration.getInsertStatement(requestName);
		}

		return result;
	}

	public ListObject getRequests() {
		return new ListObject(FrameworkObjectConfiguration.getFrameworkObjectType(new Request()), this.getAllRequests());
	}

	public void createRequest(String data) {
		DataObjectConfiguration dataObjectConfiguration = new DataObjectConfiguration();
		ObjectMapper objectMapper = new ObjectMapper();

		if (dataObjectConfiguration.isJSONArray(data)) {
			for (DataObject dataObject : dataObjectConfiguration.getDataArray(data)) {

				Request request = objectMapper.convertValue(dataObject.getData(), Request.class);
				RequestConfiguration requestConfiguration = new RequestConfiguration(request,
						this.getFrameworkInstance());
				String output = requestConfiguration.getInsertStatement();

				InputStream inputStream = FileTools.convertToInputStream(output,
						this.getFrameworkInstance().getFrameworkControl());
				this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
						.executeScript(inputStream);

			}
		} else {
			Request request = objectMapper.convertValue(dataObjectConfiguration.getDataObject(data).getData(),
					Request.class);
			RequestConfiguration requestConfiguration = new RequestConfiguration(request, this.getFrameworkInstance());
			String output = requestConfiguration.getInsertStatement();

			InputStream inputStream = FileTools.convertToInputStream(output,
					this.getFrameworkInstance().getFrameworkControl());
			this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
					.executeScript(inputStream);
		}

	}

	public void deleteRequest(String requestName) {
		this.getRequest(requestName).ifPresent(request -> {
			RequestConfiguration requestConfiguration = new RequestConfiguration(request, this.getFrameworkInstance());
			String output = requestConfiguration.getDeleteStatement();

			InputStream inputStream = FileTools.convertToInputStream(output,
					this.getFrameworkInstance().getFrameworkControl());
			this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
					.executeScript(inputStream);
		});

	}

	public void copyRequest(String fromRequestName, String toRequestName) {
		// TODO: check optional
		Request request = this.getRequest(fromRequestName).get();

		// Set new request name
		request.setName(toRequestName);

		RequestConfiguration requestConfiguration = new RequestConfiguration(request, this.getFrameworkInstance());
		String output = requestConfiguration.getInsertStatement();

		InputStream inputStream = FileTools.convertToInputStream(output,
				this.getFrameworkInstance().getFrameworkControl());
		this.getFrameworkInstance().getExecutionServerRepositoryConfiguration()
				.executeScript(inputStream);
	}

	// Exists
	public boolean exists() {
		return true;
	}

	// Getters and Setters
	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}