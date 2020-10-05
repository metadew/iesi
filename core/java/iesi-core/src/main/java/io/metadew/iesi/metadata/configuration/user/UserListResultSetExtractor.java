package io.metadew.iesi.metadata.configuration.user;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.user.RoleKey;
import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import org.springframework.dao.DataAccessException;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.*;

public class UserListResultSetExtractor {

    public List<User> extractData(CachedRowSet rs) throws SQLException, DataAccessException {
        Map<UUID, User> userMap = new HashMap<>();
        User user;
        while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString("user_id"));
            user = userMap.get(uuid);
            if (user == null) {
                user = mapRow(rs);
                userMap.put(uuid, user);
            }
            addRole(user, rs);
        }
        return new ArrayList<>(userMap.values());
    }

    private User mapRow(CachedRowSet cachedRowSet) throws SQLException {
        return new User(new UserKey(UUID.fromString(cachedRowSet.getString("user_id"))),
                cachedRowSet.getString("user_username"),
                cachedRowSet.getString("user_password"),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("user_enabled")),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("user_expired")),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("user_credentials_expired")),
                SQLTools.getBooleanFromSql(cachedRowSet.getString("user_locked")),
                new HashSet<>());
    }

    private void addRole(User user, CachedRowSet cachedRowSet) throws SQLException {
        if (cachedRowSet.getString("role_id") != null) {
            user.getRoleKeys().add(new RoleKey(UUID.fromString(cachedRowSet.getString("role_id"))));
        }
    }

}
