package sample.Model;

public class Customer {
    private String name;
    private String phone;
    private String gstIn;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String id;

    public Customer(String name, String phone, String gstIn, String streetAddress
            , String city, String state, String zipCode, String id) {
        this.name = name;
        this.phone = phone;
        this.gstIn = gstIn;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public String getGstIn() {
        return gstIn;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", gstIn='" + gstIn + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
