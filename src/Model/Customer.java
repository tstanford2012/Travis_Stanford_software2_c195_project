package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Customer {
    private int customerID;
    private String customerName;
    private String customerAddress;
    private String stateProvince;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private int divisionID;
    private int totalWeekAppointments;
    private int totalMonthAppointments;

    private ObservableList<Appointments> appointments = FXCollections.observableArrayList();
    public static ObservableList<Customer> customerList = FXCollections.observableArrayList();

    public Customer(int customerID, String customerName, String customerAddress, String stateProvince, String country, String postalCode, String phoneNumber, int divisionID) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.stateProvince = stateProvince;
        this.country = country;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;
    }

    public Customer(int customerID, int totalWeekAppointments, int totalMonthAppointments) {
        this.customerID = customerID;
        this.totalWeekAppointments = totalWeekAppointments;
        this.totalMonthAppointments = totalMonthAppointments;
    }

    public static ObservableList<Customer> getCustomerList() {
        return customerList;
    }
    public ObservableList<Appointments> getAppointments() {
        return appointments;
    }

    public void addCustomer(Customer newCustomer) {
        customerList.add(newCustomer);
    }

    public void addAppointment(Appointments newAppointment) {
        appointments.add(newAppointment);
    }

    public void setAppointments(ObservableList<Appointments> customerAppointments) {
        appointments = customerAppointments;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public static void setCustomerList(ObservableList<Customer> customerList) {
        Customer.customerList = customerList;
    }
}
