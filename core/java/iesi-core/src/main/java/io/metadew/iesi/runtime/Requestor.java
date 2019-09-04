//package io.metadew.iesi.runtime;
//
//import io.metadew.iesi.framework.instance.FrameworkInstance;
//import io.metadew.iesi.metadata.configuration.request.RequestConfiguration;
//import io.metadew.iesi.metadata.definition.Request;
//import io.metadew.iesi.metadata.definition.RequestParameter;
//import io.metadew.iesi.server.execution.tools.ExecutionServerTools;
//
//public class Requestor {
//
//	private static Requestor INSTANCE;
//
//	public Requestor() {}
//
//	public synchronized static Requestor getInstance() {
//		if (INSTANCE == null) {
//			INSTANCE = new Requestor();
//		}
//		return INSTANCE;
//	}
//
//	public void init() {
//	}
//
//	public synchronized String submit(Request request) {
//    	if (!ExecutionServerTools.isAlive()) {
//    		throw new RuntimeException("framework.server.down");
//    	}
//
//		RequestConfiguration requestConfiguration = new RequestConfiguration();
//
//    	boolean exitOverwrite = false;
//		for (RequestParameter requestParameter : request.getParameters()) {
//			if (requestParameter.getType().equalsIgnoreCase("exit")) {
//				requestParameter.setValue(Boolean.toString(false));
//				exitOverwrite = true;
//			}
//		}
//		if (!exitOverwrite) {
//	    	request.getParameters().add(new RequestParameter("exit","flag",Boolean.toString(false)));
//		}
//
//		FrameworkInstance.getInstance().getExecutionServerRepositoryConfiguration().executeBatch(requestConfiguration.getInsertStatement(request));
//
//		return request.getId();
//    }
//
//}
