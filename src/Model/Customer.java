package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class contains the attributes, constructors, getters, and setters for the customer object
 */
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

    /**
     *
     * @param customerID
     * @param customerName
     * @param customerAddress
     * @param stateProvince
     * @param country
     * @param postalCode
     * @param phoneNumber
     * @param divisionID
     *
     * constructor
     */
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

    /**
     *
     * @param customerID
     * @param totalWeekAppointments
     * @param totalMonthAppointments
     *
     * constructor
     */
    public Customer(int customerID, int totalWeekAppointments, int totalMonthAppointments) {
        this.customerID = customerID;
        this.totalWeekAppointments = totalWeekAppointments;
        this.totalMonthAppointments = totalMonthAppointments;
    }

    //getters

    /**
     *
     * @return
     *
     * getters
     */
    public static ObservableList<Customer> getCustomerList() {
        return customerList;
    }
    public ObservableList<Appointments> getAppointments() {
        return appointments;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public int getDivisionID() {
        return divisionID;
    }

    /**
     *
     * setters
     */

    public void setAppointments(ObservableList<Appointments> customerAppointments) {
        appointments = customerAppointments;
    }


    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }


    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }


    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public static void setCustomerList(ObservableList<Customer> customerList) {
        Customer.customerList = customerList;
    }
}
