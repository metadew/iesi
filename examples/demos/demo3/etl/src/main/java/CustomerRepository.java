import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepository extends Configuration<Customer, CustomerKey> {

    private final Database database;
    private final String tableName;

    public CustomerRepository(MetadataControl metadataControl, Database database, String tableName) {
        super(metadataControl);
        this.database = database;
        this.tableName = tableName;
    }

    public Optional<Customer> get(CustomerKey customerKey) throws SQLException {
        return Optional.empty();
    }

    public List<Customer> getAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        CachedRowSet cachedRowSet = database.executeQuery("SELECT * from " + tableName + ";");
        while (cachedRowSet.next()) {
            customers.add(new Customer(new CustomerKey(cachedRowSet.getLong("ID")), cachedRowSet.getString("Gender"),
                    cachedRowSet.getString("FirstName"),
                    cachedRowSet.getString("LastName"),
                    cachedRowSet.getString("City"),
                    cachedRowSet.getString("Street"),
                    cachedRowSet.getString("Email"),
                    cachedRowSet.getString("Birthdate"),
                    cachedRowSet.getString("Phone"),
                    cachedRowSet.getString("Country")));
        }
        return customers;
    }

    public void delete(CustomerKey customerKey) throws MetadataDoesNotExistException, SQLException {
    }

    public void insert(Customer customer) throws MetadataAlreadyExistsException, SQLException {
        String query = MessageFormat.format("insert into " + tableName + " (ID, Gender, FirstName, LastName, City, Street, Email, Birthdate, Phone, Country) values " +
                "({0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9});",
                SQLTools.GetStringForSQL(customer.getMetadataKey().getId()),
                SQLTools.GetStringForSQL(customer.getGender()),
                SQLTools.GetStringForSQL(customer.getFirstName()),
                SQLTools.GetStringForSQL(customer.getLastName()),
                SQLTools.GetStringForSQL(customer.getCity()),
                SQLTools.GetStringForSQL(customer.getStreet()),
                SQLTools.GetStringForSQL(customer.getEmail()),
                SQLTools.GetStringForSQL(customer.getBirhtdate()),
                SQLTools.GetStringForSQL(customer.getPhone()),
                SQLTools.GetStringForSQL(customer.getCountry()));
        database.executeUpdate(query);
    }

    public void deleteAll() {
        database.executeUpdate("delete from " + tableName + ";");
    }
}
