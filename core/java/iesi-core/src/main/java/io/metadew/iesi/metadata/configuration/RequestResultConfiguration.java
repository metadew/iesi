package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.exception.RequestResultAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.RequestResultDoesNotExistException;
import io.metadew.iesi.metadata.definition.RequestResult;
import io.metadew.iesi.metadata.definition.key.RequestResultKey;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestResultConfiguration extends Configuration<RequestResult, RequestResultKey> {

	public RequestResultConfiguration() {
		super();
	}

	@Override
	public Optional<RequestResult> get(RequestResultKey key) throws SQLException {
		String query = "select REQUEST_ID, PARENT_REQUEST_ID, RUN_ID, ORIGIN_NM, REQUEST_NM, SCOPE_NM, CONTEXT_NM, SPACE_NM, USER_NM, REQUEST_TMS, ST_NM, STRT_TMS, END_TMS from "
				+ getMetadataControl().getResultMetadataRepository().getTableNameByLabel("RequestResults") + " where "
				+ "REQUEST_ID = " + SQLTools.GetStringForSQL(key.getRequestId()) + ";";
		CachedRowSet cachedRowSet = getMetadataControl().getResultMetadataRepository().executeQuery(query, "reader");
		if (cachedRowSet.size() == 0) {
			return Optional.empty();
		} else if (cachedRowSet.size() > 1) {
			// TODO: log
		}
		cachedRowSet.next();
		return Optional.of(new RequestResult(new RequestResultKey(cachedRowSet.getString("REQUEST_ID")),
				cachedRowSet.getString("PARENT_REQUEST_ID"), cachedRowSet.getString("RUN_ID"),
				cachedRowSet.getString("ORIGIN_NM"), cachedRowSet.getString("REQUEST_NM"),
				cachedRowSet.getString("SCOPE_NM"), cachedRowSet.getString("CONTEXT_NM"),
				cachedRowSet.getString("SPACE_NM"), cachedRowSet.getString("USER_NM"), cachedRowSet.getString("ST_NM"),
				cachedRowSet.getObject("REQUEST_TMS") == null ? null : SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
				cachedRowSet.getObject("STRT_TMS") == null ? null : SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
				cachedRowSet.getObject("END_TMS") == null ? null : LocalDateTime.parse("END_TMS")));
	}

	@Override
	public List<RequestResult> getAll() throws SQLException {
		List<RequestResult> requestResults = new ArrayList<>();
		String query = "select REQUEST_ID, PARENT_REQUEST_ID, RUN_ID, ORIGIN_NM, REQUEST_NM, SCOPE_NM, CONTEXT_NM, SPACE_NM, USER_NM, REQUEST_TMS, ST_NM, STRT_TMS, END_TMS from "
				+ getMetadataControl().getResultMetadataRepository().getTableNameByLabel("RequestResults") + ";";
		CachedRowSet cachedRowSet = getMetadataControl().getResultMetadataRepository().executeQuery(query, "reader");
		while (cachedRowSet.next()) {
			requestResults.add(new RequestResult(new RequestResultKey(
					cachedRowSet.getString("REQUEST_ID")),
					cachedRowSet.getString("PARENT_REQUEST_ID"),
					cachedRowSet.getString("RUN_ID"),
					cachedRowSet.getString("ORIGIN_NM"),
					cachedRowSet.getString("REQUEST_NM"),
					cachedRowSet.getString("SCOPE_NM"),
					cachedRowSet.getString("CONTEXT_NM"),
					cachedRowSet.getString("SPACE_NM"),
					cachedRowSet.getString("USER_NM"),
					cachedRowSet.getString("ST_NM"),
					SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("REQUEST_TMS")),
					SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("STRT_TMS")),
					SQLTools.getLocalDatetimeFromSql(cachedRowSet.getString("END_TMS"))));
		}
		return requestResults;
	}

	@Override
	public void delete(RequestResultKey key) throws SQLException, RequestResultDoesNotExistException {
		if (!exists(key)) {
			throw new RequestResultDoesNotExistException(
					MessageFormat.format("Request Result {0} does not exist", key.getRequestId()));
		}
		String query = "delete from "
				+ getMetadataControl().getResultMetadataRepository().getTableNameByLabel("RequestResults") + " where "
				+ "REQUEST_ID = " + SQLTools.GetStringForSQL(key.getRequestId()) + ";";
		getMetadataControl().getResultMetadataRepository().executeUpdate(query);
	}

	@Override
	public void insert(RequestResult requestResult) throws SQLException, RequestResultAlreadyExistsException {
		if (exists(requestResult.getMetadataKey())) {
			throw new RequestResultAlreadyExistsException(MessageFormat.format("Request Result {0} already exists",
					requestResult.getMetadataKey().getRequestId()));
		}
		String query = "insert into "
				+ getMetadataControl().getResultMetadataRepository().getTableNameByLabel("RequestResults")
				+ " (REQUEST_ID, PARENT_REQUEST_ID, RUN_ID, ORIGIN_NM, REQUEST_NM, SCOPE_NM, CONTEXT_NM, SPACE_NM, USER_NM, REQUEST_TMS, ST_NM, STRT_TMS, END_TMS) values ("
				+ SQLTools.GetStringForSQL(requestResult.getMetadataKey().getRequestId()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getParentRequestId() == null ? "-1" : requestResult.getParentRequestId()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getRunId()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getOrigin()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getName()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getScope()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getContext()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getSpace()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getUser()) + ", "
				+ SQLTools.GetStringForSQL((requestResult.getRequestTimestamp() == null ? LocalDateTime.now() : requestResult.getRequestTimestamp())) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getStatus()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getStartTimestamp() == null ? null : requestResult.getStartTimestamp()) + ", "
				+ SQLTools.GetStringForSQL(requestResult.getEndTimestamp() == null ? null : requestResult.getEndTimestamp())
				+ ");";
		getMetadataControl().getResultMetadataRepository().executeUpdate(query);
	}
}
