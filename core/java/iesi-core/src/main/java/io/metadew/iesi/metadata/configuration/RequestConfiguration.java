package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.RequestAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.RequestDoesNotExistException;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.ListObject;
import io.metadew.iesi.metadata.definition.Request;
import io.metadew.iesi.metadata.definition.RequestParameter;

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

	// Constructors
	public RequestConfiguration() {
	}

	public RequestConfiguration(Request request) {
		this.setRequest(request);
	}

	public Optional<Request> getRequest(String requestId) {
		Request request = null;
		String queryRequest = "select request_id, parent_request_id, request_typ_nm, request_tms, request_nm, request_dsc, amount_nb, notif_email, scope_nm, context_nm, space_nm, user_nm, user_password, exe_id from "
				+ FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
						.getTableNameByLabel("Requests")
				+ " where request_id = " + SQLTools.GetStringForSQL(requestId);

		CachedRowSet crsRequest = FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.executeQuery(queryRequest, "reader");
		RequestParameterConfiguration requestParameterConfiguration = new RequestParameterConfiguration();
		try {
			while (crsRequest.next()) {
				// Get parameters
				String queryRequestParameters = "select REQUEST_ID, REQUEST_PAR_TYP_NM, REQUEST_PAR_NM, REQUEST_PAR_VAL from "
						+ FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
								.getTableNameByLabel("RequestParameters")
						+ " where REQUEST_ID = '" + requestId + "'";
				CachedRowSet crsRequestParameters = FrameworkInstance.getInstance()
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
		String queryRequest = "select * from " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests") + " where REQUEST_ID = '" + request.getId() + "'";
		CachedRowSet crsRequest = FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.executeQuery(queryRequest, "reader");
		return crsRequest.size() == 1;
	}

	public List<Request> getAllRequests() {
		List<Request> requests = new ArrayList<>();
		String query = "select REQUEST_ID from " + FrameworkInstance.getInstance()
				.getExecutionServerRepositoryConfiguration().getTableNameByLabel("Requests") + " order by LOAD_TMS ASC";
		CachedRowSet crs = FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeQuery(query,
				"reader");
		RequestConfiguration requestConfiguration = new RequestConfiguration();
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
		// FrameworkInstance.getInstance().getFrameworkLog().log(MessageFormat.format("Deleting
		// request {0}", request.getScriptName()),Level.TRACE);
		if (!exists(request)) {
			throw new RequestDoesNotExistException(MessageFormat
					.format("Request {0} is not present in the repository so cannot be deleted", request.getId()));
		}
		List<String> query = getDeleteStatement(request);
		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeBatch(query);
	}

	public List<String> getDeleteStatement(Request request) {
		List<String> queries = new ArrayList<>();
		queries.add("DELETE FROM " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests") + " WHERE REQUEST_ID = "
				+ SQLTools.GetStringForSQL(request.getId()) + ";");
		queries.add("DELETE FROM " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("RequestParameters") + " WHERE REQUEST_ID = " +
				SQLTools.GetStringForSQL(request.getId()) + ";");
		return queries;

	}

	public void deleteAllRequests() {
		// TODO: logging
		// FrameworkInstance.getInstance().getFrameworkLog().log("Deleting all requests",
		// Level.TRACE);
		List<String> query = getDeleteAllStatement();
		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeBatch(query);
	}

	private List<String> getDeleteAllStatement() {
		List<String> queries = new ArrayList<>();
		queries.add("DELETE FROM " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests") + ";");
		queries.add("DELETE FROM " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("RequestParameters") + ";");
		return queries;
	}

	public void insertRequest(Request request) throws RequestAlreadyExistsException {
		// TODO:logging
		// FrameworkInstance.getInstance().getFrameworkLog().log(MessageFormat.format("Inserting
		// request {0}", request.getScriptName()), Level.TRACE);
		if (exists(request)) {
			throw new RequestAlreadyExistsException(
					MessageFormat.format("Request {0} already exists", request.getId()));
		}
		List<String> query = getInsertStatement(request);
		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeBatch(query);
	}

	public List<String> getInsertStatement(Request request) {
		List<String> queries = new ArrayList<>();
		
		queries.add("INSERT INTO " + FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
				.getTableNameByLabel("Requests") + 
				"(request_id, parent_request_id, request_typ_nm, request_tms, request_nm, request_dsc, amount_nb, notif_email, scope_nm, context_nm, space_nm, user_nm, user_password, exe_id) VALUES (" + 
				SQLTools.GetStringForSQL(request.getId()) + ","
		+ SQLTools.GetStringForSQL("0") + ","
		+ SQLTools.GetStringForSQL(request.getType()) + ","
		+ SQLTools.GetStringForSQL(request.getTimestamp()) + ","
		+ SQLTools.GetStringForSQL(request.getName()) + ","
		+ SQLTools.GetStringForSQL(request.getDescription()) + ","
		+ SQLTools.GetStringForSQL(request.getAmount()) + ","
		+ SQLTools.GetStringForSQL(request.getEmail()) + ","
		+ SQLTools.GetStringForSQL(request.getScope()) + ","
		+ SQLTools.GetStringForSQL(request.getContext()) + ","
		+ SQLTools.GetStringForSQL(request.getSpace()) + ","
		+ SQLTools.GetStringForSQL(request.getUser()) + ","
		+ SQLTools.GetStringForSQL(request.getPassword()) + ","
		+ SQLTools.GetStringForSQL(-1) + ");");

		// add Parameters

		RequestParameterConfiguration requestParameterConfiguration = new RequestParameterConfiguration();
		for (RequestParameter requestParameter : request.getParameters()) {
			queries.add(requestParameterConfiguration.getInsertStatement(request.getId(), requestParameter));
		}

		return queries;
	}

	public void updateRequest(Request request) throws RequestDoesNotExistException {
		// FrameworkInstance.getInstance().getFrameworkLog().log(MessageFormat.format("Updating
		// request {0}.", request.getScriptName()),Level.TRACE);
		try {
			deleteRequest(request);
			insertRequest(request);
		} catch (RequestDoesNotExistException e) {
//			FrameworkInstance.getInstance().getFrameworkLog().log(MessageFormat.format("Request {0} is not present in the repository so cannot be updated", request.getScriptName()),	Level.TRACE);
			throw new RequestDoesNotExistException(MessageFormat
					.format("Request {0} is not present in the repository so cannot be updated", request.getId()));
		} catch (RequestAlreadyExistsException e) {
			// FrameworkInstance.getInstance().getFrameworkLog().log(MessageFormat.format("Request
			// {0} is not deleted correctly during update. {1}", request.getScriptName(),
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
			RequestParameterConfiguration requestParameterConfiguration = new RequestParameterConfiguration();
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
			RequestParameterConfiguration requestParameterConfiguration = new RequestParameterConfiguration();
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
				RequestConfiguration requestConfiguration = new RequestConfiguration(request);
				String output = requestConfiguration.getInsertStatement();

				InputStream inputStream = FileTools.convertToInputStream(output,
						FrameworkControl.getInstance());
				FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
						.executeScript(inputStream);

			}
		} else {
			Request request = objectMapper.convertValue(dataObjectConfiguration.getDataObject(data).getData(),
					Request.class);
			RequestConfiguration requestConfiguration = new RequestConfiguration(request);
			String output = requestConfiguration.getInsertStatement();

			InputStream inputStream = FileTools.convertToInputStream(output,
					FrameworkControl.getInstance());
			FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
					.executeScript(inputStream);
		}

	}

	public void deleteRequest(String requestName) {
		this.getRequest(requestName).ifPresent(request -> {
			RequestConfiguration requestConfiguration = new RequestConfiguration(request);
			String output = requestConfiguration.getDeleteStatement();

			InputStream inputStream = FileTools.convertToInputStream(output,
					FrameworkControl.getInstance());
			FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
					.executeScript(inputStream);
		});

	}

	public void copyRequest(String fromRequestName, String toRequestName) {
		// TODO: check optional
		Request request = this.getRequest(fromRequestName).get();

		// Set new request name
		request.setName(toRequestName);

		RequestConfiguration requestConfiguration = new RequestConfiguration(request);
		String output = requestConfiguration.getInsertStatement();

		InputStream inputStream = FileTools.convertToInputStream(output,
				FrameworkControl.getInstance());
		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration()
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

}