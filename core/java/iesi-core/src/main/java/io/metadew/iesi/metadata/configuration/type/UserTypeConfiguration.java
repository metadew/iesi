package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.user.UserType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class UserTypeConfiguration {

    private UserType userType;
    private String dataObjectType = "UserType";

    // Constructors
    public UserTypeConfiguration(UserType userType) {
        this.setUserType(userType);
    }

    public UserTypeConfiguration() {
    }

    // Methods
    public UserType getUserType(String userTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), userTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        UserType userType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                UserType.class);
        return userType;
    }

    // Getters and Setters
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}