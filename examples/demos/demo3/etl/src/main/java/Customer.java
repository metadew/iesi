import io.metadew.iesi.metadata.definition.Metadata;

public class Customer extends Metadata<CustomerKey> {

    private String gender;
    private String firstName;
    private String lastName;
    private String city;
    private String birhtdate;
    private String street;
    private String email;
    private String phone;
    private String country;

    public Customer(CustomerKey customerKey, String gender, String firstName, String lastName, String city, String street, String email, String birthdate, String phone, String country) {
        super(customerKey);
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.street = street;
        this.email = email;
        this.birhtdate = birthdate;
        this.phone = phone;
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCity() {
        return city;
    }

    public String getBirhtdate() {
        return birhtdate;
    }

    public String getStreet() {
        return street;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountry() {
        return country;
    }

    public Customer createClone() {
        return new Customer(new CustomerKey(), gender, firstName, lastName, city, street, email, birhtdate, phone, country);
    }

}
