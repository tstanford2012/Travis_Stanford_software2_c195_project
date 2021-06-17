package Model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class Appointments {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private Timestamp start;
    private Timestamp end;
    private ZonedDateTime startZonedDateTime;
    private ZonedDateTime endZoneDateTime;
    private int customerID;
    private String appointmentCustomerName;
    private int userID;

    //constructor
    public Appointments(int appointmentID, String title, String description, String location, String contact, String type, Timestamp start, Timestamp end, int customerID, int userID, String appointmentCustomerName) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.appointmentCustomerName = appointmentCustomerName;
    }

    //constructor
    public Appointments(int appointmentID, String type, String description, ZonedDateTime startZonedDateTime, ZonedDateTime endZoneDateTime, int customerID, String appointmentCustomerName) {
        this.appointmentID = appointmentID;
        this.type = type;
        this.description = description;
        this.startZonedDateTime = startZonedDateTime;
        this.endZoneDateTime = endZoneDateTime;
        this.customerID = customerID;
        this.appointmentCustomerName = appointmentCustomerName;
    }

    //constructor
    public Appointments(int appointment_id, Timestamp start, Timestamp end, int customer_id, int user_id) {
        this.appointmentID = appointment_id;
        this.start = start;
        this.end = end;
        this.customerID = customer_id;
        this.userID = user_id;
    }

    //constructor
    public Appointments(int appointmentID, String type, int customerID, int userID) {
        this.appointmentID = appointmentID;
        this.type = type;
        this.customerID = customerID;
        this.userID = userID;
    }


    //constructor
    public Appointments(int appointment_id, String title, String type, String description, Timestamp start, Timestamp end, int customer_id) {
        this.appointmentID = appointment_id;
        this.title = title;
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
        this.customerID = customer_id;
    }

    //getters and setters
    public int getAppointmentID() {
        return appointmentID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getUserID() {
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Object getContact() {
        return contact;
    }
    public String getStringContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getAppointmentCustomerName() {
        return appointmentCustomerName;
    }

    public void setAppointmentCustomerName(String customerName) {
        this.appointmentCustomerName = customerName;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public ZonedDateTime getStartZonedDateTime() {
        return startZonedDateTime;
    }

    public void setStartZonedDateTime(ZonedDateTime startZonedDateTime) {
        this.startZonedDateTime = startZonedDateTime;
    }

    public ZonedDateTime getEndZoneDateTime() {
        return endZoneDateTime;
    }

    public void setEndZoneDateTime(ZonedDateTime endZoneDateTime) {
        this.endZoneDateTime = endZoneDateTime;
    }
}
