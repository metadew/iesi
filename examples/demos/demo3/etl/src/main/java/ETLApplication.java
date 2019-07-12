import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import org.apache.commons.cli.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ETLApplication {

    public static void main(String[] args) throws SQLException, MetadataAlreadyExistsException, ParseException {

        Options options = new Options()
                .addOption(Option.builder("sourceA")
                        .hasArg()
                        .required()
                        .desc("Absolute location of source A's customer repository")
                        .build())
                .addOption(Option.builder("sourceB")
                        .hasArg()
                        .required()
                        .desc("Absolute location of source B's customer repository")
                        .build())
                .addOption(Option.builder("target")
                        .hasArg()
                        .required()
                        .desc("Absolute location of target's customer repository")
                        .build())
                .addOption(Option.builder("duplicates")
                        .optionalArg(true)
                        .numberOfArgs(1)
                        .required()
                        .desc("allow duplicates")
                        .build());

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = parser.parse(options, args);

        SqliteDatabase companyADatabase = new SqliteDatabase(new SqliteDatabaseConnection(cmd.getOptionValue("sourceA")));
        SqliteDatabase companyBDatabase = new SqliteDatabase(new SqliteDatabaseConnection(cmd.getOptionValue("sourceB")));
        SqliteDatabase targetDatabase = new SqliteDatabase(new SqliteDatabaseConnection(cmd.getOptionValue("target")));

        CustomerRepository customerRepositoryCompanyA = new CustomerRepository(null, companyADatabase, "customer");
        CustomerRepository customerRepositoryCompanyB = new CustomerRepository(null, companyBDatabase, "customer");
        CustomerRepository customerRepositoryCompanyATarget = new CustomerRepository(null, targetDatabase, "customerA");
        CustomerRepository customerRepositoryCompanyBTarget = new CustomerRepository(null, targetDatabase, "customerB");
        CustomerRepository customerRepositoryTarget = new CustomerRepository(null, targetDatabase, "customer");

        customerRepositoryTarget.deleteAll();
        customerRepositoryCompanyATarget.deleteAll();
        customerRepositoryCompanyBTarget.deleteAll();

        List<Customer> customersCompanyA = customerRepositoryCompanyA.getAll();
        for (Customer customer : customersCompanyA) {
            customerRepositoryCompanyATarget.insert(customer);
        }
        System.out.println(customersCompanyA.size() + " customers in company A");
        List<Customer> customersCompanyB = customerRepositoryCompanyB.getAll();
        for (Customer customer : customersCompanyB) {
            customerRepositoryCompanyBTarget.insert(customer);
        }
        System.out.println(customersCompanyB.size() + " customers in company B");

        List<Customer> customersNotInCompanyA = customersCompanyB.stream()
                .filter(customer -> !containsCustomer(customersCompanyA, customer.getFirstName(), customer.getLastName()))
                .collect(Collectors.toList());
        System.out.println(customersNotInCompanyA.size() + " customers in company B not in company A");

        System.out.println(cmd.getOptionValue("duplicates"));
        if (cmd.getOptionValue("duplicates", "false").equals("true")) {
            customersCompanyA.addAll(customersCompanyB);
        } else {
            customersCompanyA.addAll(customersNotInCompanyA);
        }
        System.out.println(customersCompanyA.size() + " customers after merger");
        for (Customer customer : customersCompanyA) {
            Customer cleanCustomer = customer.createClone();
            customerRepositoryTarget.insert(cleanCustomer);
        }
    }

    private static boolean containsCustomer(List<Customer> customers, String firstName, String lastName) {
        return customers.stream().anyMatch(customer -> customer.getFirstName().equals(firstName) && customer.getLastName().equals(lastName));
    }
}