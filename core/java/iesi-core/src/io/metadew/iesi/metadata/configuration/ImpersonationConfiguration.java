package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.FileTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkObjectConfiguration;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.ImpersonationDoesNotExistException;
import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.metadata.definition.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.ListObject;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImpersonationConfiguration extends MetadataConfiguration{

    private Impersonation impersonation;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ImpersonationConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public ImpersonationConfiguration(Impersonation impersonation, FrameworkInstance frameworkInstance) {
        this.setImpersonation(impersonation);
        this.setFrameworkInstance(frameworkInstance);
    }

    // Abstract method implementations
	@Override
	public List<Impersonation> getAllObjects() {
		return this.getAllImpersonations();
	}
    
    // Methods
    public Optional<Impersonation> getImpersonation(String impersonationName) {
        Impersonation impersonation = null;
        String queryImpersonation = "select IMP_NM, IMP_DSC from "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations")
                + " where IMP_NM = '" + impersonationName + "'";
        CachedRowSet crsImpersonation = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryImpersonation, "reader");
        try {
            while (crsImpersonation.next()) {
                String description = crsImpersonation.getString("IMP_DSC");

                // Get parameters
                String queryImpersonationParameters = "select IMP_NM, CONN_NM from "
                        + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters")
                        + " where IMP_NM = '" + impersonationName + "'";
                CachedRowSet crsImpersonationParameters = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository()
                        .executeQuery(queryImpersonationParameters, "reader");
                List<ImpersonationParameter> impersonationParameterList = new ArrayList<>();
                while (crsImpersonationParameters.next()) {
                    impersonationParameterList.add(new ImpersonationParameterConfiguration(this.getFrameworkInstance())
                            .getImpersonationParameter(impersonationName, crsImpersonationParameters.getString("CONN_NM")));
                }
                crsImpersonationParameters.close();
                impersonation = new Impersonation(impersonationName, description, impersonationParameterList);
            }
            crsImpersonation.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(impersonation);
    }

    public List<Impersonation> getAllImpersonations() {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log("Getting all impersonations {0}.", Level.TRACE);
        List<Impersonation> impersonations = new ArrayList<>();
        String query = "select IMP_NM from " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations")
                + " order by IMP_NM ASC";
        CachedRowSet crs = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(query, "reader");
        try {
            while (crs.next()) {
                String impersonationName = crs.getString("IMP_NM");
                getImpersonation(impersonationName).ifPresent(impersonations::add);
            }
            crs.close();
        } catch (SQLException e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return impersonations;
    }

    public boolean exists(Impersonation impersonation) {
        String queryImpersonation = "select * from "
                + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations")
                + " where IMP_NM = '"
                + impersonation.getName() + "'";

        CachedRowSet crsEnvironment = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(queryImpersonation, "reader");
        return crsEnvironment.size() == 1;
    }

    public void deleteImpersonation(Impersonation impersonation) throws ImpersonationDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Deleting impersonation {0}.", impersonation.getName()), Level.TRACE);
        if (!exists(impersonation)) {
            throw new ImpersonationDoesNotExistException(
                    MessageFormat.format("Impersonation {0} is not present in the repository so cannot be updated",
                            impersonation.getName()));

        }
        String query = getDeleteStatement(impersonation);
        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(query);
    }

    public void deleteAllImpersonations() {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log("Deleting all impersonations", Level.TRACE);
        String query = getDeleteAllStatement();
        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(query);
    }

    private String getDeleteAllStatement() {
        String sql = "";

        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations");
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters");
        sql += ";";
        sql += "\n";

        return sql;
    }

    public void insertImpersonation(Impersonation impersonation) throws ImpersonationAlreadyExistsException {
    	//TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Inserting impersonation {0}.", impersonation.getName()), Level.TRACE);
        if (exists(impersonation)) {
            throw new ImpersonationAlreadyExistsException(MessageFormat.format("Impersonation {0} already exists", impersonation.getName()));
        }
        String query = getInsertStatement(impersonation);
        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeUpdate(query);
    }

    public void updateImpersonation(Impersonation impersonation) throws ImpersonationDoesNotExistException {
        //TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Updating impersonation {0}.", impersonation.getName()), Level.TRACE);
        try {
            deleteImpersonation(impersonation);
            insertImpersonation(impersonation);
        } catch (ImpersonationDoesNotExistException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Impersonation {0} is not present in the repository so cannot be updated", impersonation.getName()),Level.TRACE);
            throw new ImpersonationDoesNotExistException(MessageFormat.format(
                    "Impersonation {0} is not present in the repository so cannot be updated", impersonation.getName()));

        } catch (ImpersonationAlreadyExistsException e) {
            //TODO fix logging
        	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Environment {0} is not deleted correctly during update. {1}", impersonation.getName(), e.toString()),Level.WARN);
        }
    }

    public String getDeleteStatement(Impersonation impersonation) {
        String sql = "";

        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations");
        sql += " WHERE IMP_NM = "
                + SQLTools.GetStringForSQL(impersonation.getName());
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters");
        sql += " WHERE IMP_NM = "
                + SQLTools.GetStringForSQL(impersonation.getName());
        sql += ";";
        sql += "\n";

        return sql;
    }

    public String getInsertStatement(Impersonation impersonation) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations");
        sql += " (IMP_NM, IMP_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(impersonation.getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(impersonation.getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(impersonation);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(Impersonation impersonation) {
        String result = "";

        // Catch null parameters
        if (this.getImpersonation().getParameters() == null)
            return result;

        for (ImpersonationParameter impersonationParameter : impersonation.getParameters()) {
            ImpersonationParameterConfiguration impersonationParameterConfiguration = new ImpersonationParameterConfiguration(
                    this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += impersonationParameterConfiguration.getInsertStatement(impersonation.getName(), impersonationParameter);
        }

        return result;
    }
    // Delete

    public String getDeleteStatement() {
        String sql = "";

        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations");
        sql += " WHERE IMP_NM = "
                + SQLTools.GetStringForSQL(this.getImpersonation().getName());
        sql += ";";
        sql += "\n";
        sql += "DELETE FROM " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("ImpersonationParameters");
        sql += " WHERE IMP_NM = "
                + SQLTools.GetStringForSQL(this.getImpersonation().getName());
        sql += ";";
        sql += "\n";

        return sql;

    }
    // Insert

    public String getInsertStatement() {
        String sql = "";

        if (this.exists()) {
            sql += this.getDeleteStatement();
        }

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations");
        sql += " (IMP_NM, IMP_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(this.getImpersonation().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getImpersonation().getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(this.getImpersonation().getName());
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(String impersonationName) {
        String result = "";

        // Catch null parameters
        if (this.getImpersonation().getParameters() == null)
            return result;

        for (ImpersonationParameter impersonationParameter : this.getImpersonation().getParameters()) {
            ImpersonationParameterConfiguration impersonationParameterConfiguration = new ImpersonationParameterConfiguration(impersonationParameter,
                    this.getFrameworkInstance());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += impersonationParameterConfiguration.getInsertStatement(impersonationName);
        }

        return result;
    }

    public ListObject getImpersonations() {
        List<Impersonation> impersonationList = new ArrayList<>();
        CachedRowSet crs = null;
        String query = "select IMP_NM from " + this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().getTableNameByLabel("Impersonations")
                + " order by IMP_NM ASC";
        crs = this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeQuery(query, "reader");
        ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(this.getFrameworkInstance());
        try {
            String impersonationName = "";
            while (crs.next()) {
                impersonationName = crs.getString("IMP_NM");
                impersonationConfiguration.getImpersonation(impersonationName).ifPresent(impersonationList::add);
            }
            crs.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }

        return new ListObject(FrameworkObjectConfiguration.getFrameworkObjectType(new Impersonation()), impersonationList);
    }

//	public void createImpersonation(String data) {
//		DataObjectConfiguration dataObjectConfiguration = new DataObjectConfiguration(this.getFrameworkInstance());
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		if (dataObjectConfiguration.isJSONArray(data)) {
//			for (DataObject doDataObject : dataObjectConfiguration.getDataArray(data)) {
//
//				Impersonation impersonation = objectMapper.convertValue(doDataObject.getData(), Impersonation.class);
//				ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkInstance());
//				String output = impersonationConfiguration.getInsertStatement();
//
//				InputStream inputStream = FileTools
//						.convertToInputStream(output, this.getFrameworkInstance().getFrameworkControl());
//				this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
//
//			}
//		} else {
//			Impersonation impersonation = objectMapper.convertValue(dataObjectConfiguration.getDataObject(data).getData(), Impersonation.class);
//			ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkInstance());
//			String output = impersonationConfiguration.getInsertStatement();
//
//			InputStream inputStream = FileTools.convertToInputStream(output,
//					this.getFrameworkInstance().getFrameworkControl());
//			this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
//		}
//
//	}

    public void deleteImpersonation(String impersonationName) {
        this.getImpersonation(impersonationName).ifPresent(impersonation -> {
            ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkInstance());
            String output = impersonationConfiguration.getDeleteStatement();

            InputStream inputStream = FileTools
                    .convertToInputStream(output, this.getFrameworkInstance().getFrameworkControl());
            this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
        });

    }

    public void copyImpersonation(String fromImpersonationName, String toImpersonationName) {
        // TODO: check optionallity of impersonation
        Impersonation impersonation = this.getImpersonation(fromImpersonationName).get();

        // Set new impersonation name
        impersonation.setName(toImpersonationName);

        ImpersonationConfiguration impersonationConfiguration = new ImpersonationConfiguration(impersonation, this.getFrameworkInstance());
        String output = impersonationConfiguration.getInsertStatement();

        InputStream inputStream = FileTools.convertToInputStream(output,
                this.getFrameworkInstance().getFrameworkControl());
        this.getFrameworkInstance().getMetadataControl().getConnectivityMetadataRepository().executeScript(inputStream);
    }

    // Exists
    public boolean exists() {
        return true;
    }

    // Getters and Setters
    public Impersonation getImpersonation() {
        return impersonation;
    }

    public void setImpersonation(Impersonation impersonation) {
        this.impersonation = impersonation;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}