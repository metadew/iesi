package io.metadew.iesi.metadata.configuration.impersonation;

import io.metadew.iesi.metadata.definition.impersonation.Impersonation;
import io.metadew.iesi.metadata.definition.impersonation.ImpersonationParameter;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImpersonationExtractor implements ResultSetExtractor<List<Impersonation>> {

    @Override
    public List<Impersonation> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<String, Impersonation> impersonationHashMap = new HashMap<>();
        Impersonation impersonation;
        while (resultSet.next()) {
            String name = resultSet.getString("Impersonations_IMP_NM");
            impersonation = impersonationHashMap.get(name);
            if (impersonation == null) {
                impersonation = mapRow(resultSet);
                impersonationHashMap.put(name, impersonation);
            }
            addMapping(impersonation, resultSet);
        }
        return new ArrayList<>(impersonationHashMap.values());
    }

    private Impersonation mapRow(ResultSet rs) throws SQLException {

        ImpersonationKey impersonationKey = ImpersonationKey.builder().name(rs.getString("Impersonations_IMP_NM")).build();
        return Impersonation.builder().impersonationKey(
                impersonationKey)
                .description(rs.getString("Impersonations_IMP_DSC"))
                .parameters(new ArrayList<>())
                .build();
    }

    private void addMapping(Impersonation impersonation, ResultSet rs) throws SQLException {
        ImpersonationKey impersonationKey = ImpersonationKey.builder().name(rs.getString("Impersonations_IMP_NM")).build();
        ImpersonationParameterKey impersonationParameterKey = ImpersonationParameterKey.builder()
                .impersonationKey(impersonationKey)
                .parameterName(rs.getString("ImpersonationParameters_CONN_NM"))
                .build();
        ImpersonationParameter environmentParameter = ImpersonationParameter.builder()
                .impersonationParameterKey(impersonationParameterKey)
                .description(rs.getString("ImpersonationParameters_CONN_IMP_DSC"))
                .impersonatedConnection(rs.getString("ImpersonationParameters_CONN_IMP_NM"))
                .build();
        impersonation.addParameters(environmentParameter);
    }
}