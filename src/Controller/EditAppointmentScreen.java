package Controller;

import Model.Appointments;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class allows the user to edit/modify an existing appointment that was selected in the table on the appointment screen.
 */
public class EditAppointmentScreen implements Initializable {
    Appointments appointments;
    @FXML
    TextField titleTextField;
    @FXML
    TextField descriptionTextField;
    @FXML
    TextField startDateTextField;
    @FXML
    TextField endDateTextField;
    @FXML
    ComboBox<String> contactNameComboBox;
    @FXML
    ComboBox<String> startTimeComboBox;
    @FXML
    ComboBox<String> endTimeComboBox;
    @FXML
    ComboBox<String> locationComboBox;
    @FXML
    ComboBox<String> customerComboBox;
    @FXML
    ComboBox<String> typeComboBox;
    @FXML
    Label startTimeErrorLabel;
    @FXML
    Label endTimeErrorLabel;
    @FXML
    Label appointmentIDLabel;
    @FXML
    RadioButton officeLocationTimeRadioBtn;
    @FXML
    RadioButton timezoneRadioBtn;

    private final ObservableList<String> contactNames = FXCollections.observableArrayList();
    private final ObservableList<String> customerNames = FXCollections.observableArrayList();
    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private static final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private static final ZoneId zoneId = ZoneId.systemDefault();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter localTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter formatterWithMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.s");



    /**
     *
     * @param url
     * @param resourceBundle
     * Sets the error labels to be invisible, sets the radio button, and calls several methods to add information to combo boxes during initialization.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startTimeErrorLabel.setOpacity(0);
        endTimeErrorLabel.setOpacity(0);
        appointmentIDLabel.setText("");
        officeLocationTimeRadioBtn.setSelected(true);
        timezoneRadioBtn.setSelected(false);
        endDateTextField.setEditable(false);


        //Initial setOnAction for the comboBoxes
        handleComboBoxSelection(startTimeComboBox, endTimeComboBox);
        //handleComboBoxSelection(endTimeComboBox, endTimeTextField, startDateTextField);
        //calls the method and adds the types of appointments to the combo box
        addTypesToComboBox();

        try {
            //calls the methods and adds the names of the customers/contacts to the combo boxes
            pullContactNames();
            pullCustomers();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        //adds the locations to the location combo box
        locationComboBox.getItems().addAll("Phoenix", "White Plains", "Montreal", "London");
    }

    /**
     * Mouse click handler for the start combo box.
     * Displays the error label if the start combo box is clicked with no date entered.
     */
    public void startTimesComboBoxClicked() {
        if(startDateTextField.getText().isEmpty()) {
            startTimeErrorLabel.setOpacity(1);
        }
    }

    /**
     * Handler for the start time combo box.
     * Calls the method to remove previous times from the end combo box based on the start time selected.
     */
    public void startTimeComboBoxHandler() {
        handleComboBoxSelection(startTimeComboBox, endTimeComboBox);

    }

    /**
     * Handler for the end time combo box.
     */
    public void endTimeComboBoxHandler() {
    }

    /**
     * Mouse click handler for the end times combo box.
     * Prints an error to the console when either the start date or end date text fields are empty.
     */
    public void endTimesComboBoxClicked() {
        if(endDateTextField.getText().isEmpty() || startDateTextField.getText().isEmpty()) {
            System.out.println("Please enter a start and end date and click the \"Check Times\" button to view times");
        }
        else {
            System.out.println(endTimeComboBox.getValue());
        }
    }

    /**
     * Handler for the location combo box.
     * Currently does nothing.
     */
    public void locationComboBoxHandler() {
    }



    /**
     * Handler for the customerComboBox.
     * Currently does nothing.
     */
    public void customerComboBoxHandler() {
    }

    /**
     * Handler for the view times button.
     * Verifies that the entered date is valid and not on a weekend.
     * Adds the times to the start and end combo boxes based on the selected location.
     */
    public void startTimesButtonHandler() {
        if(startDateTextField.getText().isEmpty()) {
            System.out.println("Must enter a date");
        }
        else {
            startTimeErrorLabel.setOpacity(0);
            String enteredStartDateString = startDateTextField.getText();
            endDateTextField.setText(enteredStartDateString);
            String enteredEndDateString = endDateTextField.getText();
            startTimeComboBox.getItems().clear();
            String locationSelection = locationComboBox.getValue();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                LocalDate enteredStartDate = LocalDate.parse(enteredStartDateString, formatter);
                LocalDate enteredEndDate = LocalDate.parse(enteredEndDateString, formatter);

                if(timezoneRadioBtn.isSelected()) {
                    if(enteredStartDate.getDayOfWeek() == DayOfWeek.SATURDAY || enteredStartDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error getting times");
                        alert.setContentText("Times only available Monday-Friday");
                        alert.showAndWait();
                    }
                    else {
                        //Do nothing. Functionality to be added in a later iteration
                        //getAvailableTimes(enteredStartDate, enteredEndDate);
                    }
                }
                else {
                    //Prevents the user from attempting to view location times with no location selected
                    if(locationSelection == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Location not selected");
                        alert.setContentText("Please select a location and try again");
                        alert.show();
                    }
                    else {
                        String location = locationComboBox.getValue();
                        String startTime = startTimeComboBox.getValue();
                        String endTime = endTimeComboBox.getValue();

                        //Checks if the date entered is on a weekend and throws an error
                        //Calls the method to add times to the combo box based on which radio button is selected
                        if(officeLocationTimeRadioBtn.isSelected()) {
                            //Alerts the user that only Monday-Friday days are allowed
                            if(enteredStartDate.getDayOfWeek() == DayOfWeek.SATURDAY || enteredStartDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText("Error getting times");
                                alert.setContentText("Times only available Monday-Friday");
                                alert.showAndWait();
                            }
                            else {
                                startTimes.clear();
                                endTimes.clear();
                                addLocationTimesToComboBox(location);
                                printAppointmentTimes(enteredStartDate);
                                startTimeComboBox.setValue(startTime);
                                endTimeComboBox.setValue(endTime);
                                //getAvailableLocationTimes(enteredStartDate, enteredEndDate);

                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Something went wrong! Try entering a valid date.");
                alert.showAndWait();
            }
        }
    }

    /**
     *
     * @param actionEvent uses the action event to go to the appointment screen after saving to the database.
     * @throws SQLException
     * @throws IOException
     * Handler for the save button.
     * Verifies that all fields have values.
     * Converts the selected time to the appropriate time based on the selected location and then to UTC to save to the database.
     * Updates the record in the database with the new information if valid.
     */
    public void saveButtonHandler(ActionEvent actionEvent) throws SQLException, IOException {
        boolean isValid = false;

        if(titleTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty() || locationComboBox.getValue() == null || typeComboBox.getValue() == null ||
                contactNameComboBox.getValue() == null || customerComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error saving");
            alert.setContentText("Title, Description, Location, Type, Contact, or Customer fields are empty");
            alert.showAndWait();
        }
        else if(startDateTextField.getText().isEmpty() || endDateTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error saving");
            alert.setContentText("Please enter a start or end date");
            alert.showAndWait();
        }
        else if(startTimeComboBox.getValue() == null || endTimeComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error saving");
            alert.setContentText("Please select a start or end time");
            alert.showAndWait();
        }
        else {
            //adds string information for formatting purposes
            String tempStart = startTimeComboBox.getValue();
            String tempEnd = endTimeComboBox.getValue();

            if(tempStart.contains(".0")) {
                String[] parts = tempStart.split(":");
                String hour = parts[0];
                String minute = parts[1];
                tempStart = hour + ":" + minute;

                startTimeComboBox.setValue(hour + ":" + minute);
                endTimeComboBox.setValue(tempEnd);

            }

            if(tempEnd.contains(".0")) {
                String[] parts = tempEnd.split(":");
                String hour = parts[0];
                String minute = parts[1];

                endTimeComboBox.setValue(hour + ":" + minute);
                startTimeComboBox.setValue(tempStart);
            }

            String fullStart = startDateTextField.getText() + " " + startTimeComboBox.getValue() + ":00";
            String fullEnd = endDateTextField.getText() + " " + endTimeComboBox.getValue() + ":00";


            LocalDateTime fullStartLocal = LocalDateTime.parse(fullStart, dateTimeFormatter);
            LocalDateTime fullEndLocal = LocalDateTime.parse(fullEnd, dateTimeFormatter);

            ZoneId phoenixZone = ZoneId.of("America/Phoenix");
            ZoneId londonZone = ZoneId.of("Europe/London");
            ZoneId easternZone = ZoneId.of("America/New_York");
            ZoneId utc = ZoneId.of("UTC");



            ZonedDateTime fullStartZone;
            ZonedDateTime fullEndZone;
            String location = locationComboBox.getValue();

            ZonedDateTime selectionTimeLocation = fullStartLocal.atZone(zoneId);


            //converts the selected time to the location time
//            if(location.contains("Phoenix")) {
//                selectionTimeLocation = fullStartLocal.atZone(phoenixZone);
//            }
//            else if(location.contains("London")) {
//                selectionTimeLocation = fullStartLocal.atZone(londonZone);
//            }
//            else {
//                selectionTimeLocation = fullStartLocal.atZone(easternZone);
//            }
            ZonedDateTime selectionTimeInLocalTime = selectionTimeLocation.withZoneSameInstant(zoneId);

            String selectionTimeInLocalTimeString = selectionTimeInLocalTime.toString();
            String[] selectionSplit1 = selectionTimeInLocalTimeString.split("T");
            String split1Time = selectionSplit1[1];


            String[] selectionSplit2 = split1Time.split("-");
            String selectionTime = selectionSplit2[0];
            //String fullZone = selectionSplit2[1];

            //String[] selectionSplit3 = fullZone.split("05:00");
            //String zoneOnly = selectionSplit3[1];


            //confirmation that the selected time is correct
            //displays in the users local time
            Alert timeAlert = new Alert(Alert.AlertType.CONFIRMATION);
            timeAlert.setHeaderText("Are you sure you want this time?");
            timeAlert.setContentText("The time you selected is " + selectionTime + " in your local time.");
            Optional<ButtonType> result = timeAlert.showAndWait();


            if(result.get() == ButtonType.OK) {
                //calls the validAppointment method and saves to the database if true is returned
                if(validAppointment(fullStartLocal, fullEndLocal)) {
                    System.out.println("Appointment is valid");
                    isValid = true;

                    LocalDateTime localStartToDatabase;
                    LocalDateTime localEndToDatabase;

                    fullStartZone = ZonedDateTime.of(fullStartLocal, zoneId);
                    ZonedDateTime startTimeToDatabase = fullStartZone.withZoneSameInstant(utc);

                    fullEndZone = ZonedDateTime.of(fullEndLocal, zoneId);
                    ZonedDateTime endTimeToDatabase = fullEndZone.withZoneSameInstant(utc);

                    localStartToDatabase = startTimeToDatabase.toLocalDateTime();
                    localEndToDatabase = endTimeToDatabase.toLocalDateTime();

                    //converts the time from the location time to UTC based on the location selected
//                    if(locationComboBox.getValue().contains("Phoenix")) {
//                        fullStartZone = ZonedDateTime.of(fullStartLocal, phoenixZone);
//                        ZonedDateTime startTimeToDatabase = fullStartZone.withZoneSameInstant(utc);
//
//                        fullEndZone = ZonedDateTime.of(fullEndLocal, phoenixZone);
//                        ZonedDateTime endTimeToDatabase = fullEndZone.withZoneSameInstant(utc);
//
//                        localStartToDatabase = startTimeToDatabase.toLocalDateTime();
//                        localEndToDatabase = endTimeToDatabase.toLocalDateTime();
//
//
//
//                    }
//                    //converts the time from the location time to UTC based on the location selected
//                    else if(locationComboBox.getValue().contains("London")) {
//                        fullStartZone = ZonedDateTime.of(fullStartLocal, londonZone);
//                        ZonedDateTime startTimeToDatabase = fullStartZone.withZoneSameInstant(utc);
//
//                        fullEndZone = ZonedDateTime.of(fullEndLocal, londonZone);
//                        ZonedDateTime endTimeToDatabase = fullEndZone.withZoneSameInstant(utc);
//
//                        localStartToDatabase = startTimeToDatabase.toLocalDateTime();
//                        localEndToDatabase = endTimeToDatabase.toLocalDateTime();
//
//                    }
//                    //converts the time from the location time to UTC based on the location selected
//                    else {
//                        fullStartZone = ZonedDateTime.of(fullStartLocal, easternZone);
//                        ZonedDateTime startTimeToDatabase = fullStartZone.withZoneSameInstant(utc);
//
//                        fullEndZone = ZonedDateTime.of(fullEndLocal, easternZone);
//                        ZonedDateTime endTimeToDatabase = fullEndZone.withZoneSameInstant(utc);
//
//                        localStartToDatabase = startTimeToDatabase.toLocalDateTime();
//                        localEndToDatabase = endTimeToDatabase.toLocalDateTime();
//                    }



                    String[] parts = localStartToDatabase.toString().split("T");
                    String date = parts[0];
                    String time = parts[1];

                    String startToDatabase = date + " " + time + ":00";

                    parts = localEndToDatabase.toString().split("T");
                    date = parts[0];
                    time = parts[1];

                    String endToDatabase = date + " " + time + ":00";



                    Connection connection = DBConnection.getConnection();
                    String selectCustomerName = "SELECT Customer_ID, Customer_Name from customers";
                    String selectContactName = "SELECT Contact_ID, Contact_Name from contacts";
                    String updateStatement = "Update appointments set title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, " +
                            "Created_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? where Appointment_ID = ?";

                    DBQuery.setPreparedStatement(connection, selectCustomerName);
                    PreparedStatement customerPreparedStatement = DBQuery.getPreparedStatement();
                    ResultSet resultSet = customerPreparedStatement.executeQuery();

                    int customerID = 0;

                    while(resultSet.next()) {
                        int tempCustomerID = resultSet.getInt("Customer_ID");
                        String customerName = resultSet.getString("Customer_Name");

                        if(customerComboBox.getValue().contains(customerName)) {
                            customerID = tempCustomerID;
                            break;
                        }
                    }

                    DBQuery.setPreparedStatement(connection, selectContactName);
                    PreparedStatement contactPreparedStatement = DBQuery.getPreparedStatement();
                    ResultSet resultSet1 = contactPreparedStatement.executeQuery();

                    int contactID = 0;

                    while (resultSet1.next()) {
                        int tempContactID = resultSet1.getInt("Contact_ID");
                        String contactName = resultSet1.getString("Contact_Name");

                        if(contactNameComboBox.getValue().contains(contactName)) {
                            contactID = tempContactID;
                        }
                        else {
                            //do nothing
                        }
                    }

                    DBQuery.setPreparedStatement(connection, updateStatement);
                    PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

                    preparedStatement.setString(1, titleTextField.getText());
                    preparedStatement.setString(2, descriptionTextField.getText());
                    preparedStatement.setString(3, locationComboBox.getValue());
                    preparedStatement.setString(4, typeComboBox.getValue());
                    preparedStatement.setString(5, startToDatabase);
                    preparedStatement.setString(6, endToDatabase);
                    preparedStatement.setString(7, User.getUserName());
                    preparedStatement.setInt(8, customerID);

                    //The userID of the user that is logged in is automatically associated with the appointment when it is saved
                    preparedStatement.setInt(9, User.getUserID());
                    preparedStatement.setInt(10, contactID);
                    preparedStatement.setInt(11, Integer.parseInt(appointmentIDLabel.getText()));

                    preparedStatement.execute();
                    System.out.println("Record Updated");
                    try {
                        appointmentChanges(true, appointmentIDLabel.getText(), startToDatabase, endToDatabase, customerID, contactID);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println("Error while attempting to record an appointment change");
                    }

                    nextScreen(actionEvent);


                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not save appointment!");
                    alert.setHeaderText("Appointment times conflict with another scheduled appointment, has invalid date/time, or has a time outside of business hours.");
                    alert.showAndWait();
                }
            }
            else {
                System.out.println("No longer saving....");
            }
        }
    }

    /**
     *
     * @param actionEvent uses the action event to go back to the appointments screen without saving.
     * @throws IOException
     * Cancel button handler.
     * Goes back to the appointments screen after confirmation when the button is pressed.
     */
    public void cancelButtonHandler(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Changes will not be saved");
        Optional<ButtonType> result = alert.showAndWait();


        if(result.get() == ButtonType.OK) {
            nextScreen(actionEvent);
        }
        else {
            System.out.println("No longer cancelling");
        }
    }

    /**
     * Handler for the office location radio button.
     * Deselects the time zone radio button when selected.
     */
    public void officeLocationTimeRadioBtnHandler() {
        timezoneRadioBtn.setSelected(false);
    }

    /**
     * Handler for the time zone radio button.
     * Deselects the office location radio button when selected.
     */
    public void timezoneRadioBtnHandler() {
        officeLocationTimeRadioBtn.setSelected(false);
    }

    /**
     *
     * @param appointments
     * Sets the values for the text fields and combo boxes to be the values from the appointment that was selected on the appointment table.
     */
    public void setAppointment(Appointments appointments) {
        this.appointments = appointments;

        appointmentIDLabel.setText(Integer.toString(appointments.getAppointmentID()));
        titleTextField.setText(appointments.getTitle());
        descriptionTextField.setText(appointments.getDescription());
        locationComboBox.setValue(appointments.getLocation());
        customerComboBox.setValue(appointments.getAppointmentCustomerName());
        typeComboBox.setValue(appointments.getType());

        String start = appointments.getStart().toString();
        String end = appointments.getEnd().toString();

        ZoneId easternZone = ZoneId.of("America/New_York");
        ZoneId londonZone = ZoneId.of("Europe/London");
        ZoneId phoenixZone = ZoneId.of("America/Phoenix");

        LocalDateTime startLocal = LocalDateTime.parse(start, formatterWithMS);
        ZonedDateTime startZone = startLocal.atZone(zoneId);

        LocalDateTime endLocal = LocalDateTime.parse(end, formatterWithMS);
        ZonedDateTime endZone = endLocal.atZone(zoneId);

        ZonedDateTime startZoneOf = startZone.withZoneSameInstant(zoneId);
        ZonedDateTime endZoneOf = endZone.withZoneSameInstant(zoneId);
        boolean isLondon = false;

//        if(locationComboBox.getValue().contains("Phoenix")) {
//            startZoneOf = startZone.withZoneSameInstant(phoenixZone);
//            endZoneOf = endZone.withZoneSameInstant(phoenixZone);
//        }
//        else if(locationComboBox.getValue().contains("London")) {
//            isLondon = true;
//            startZoneOf = startZone.withZoneSameInstant(londonZone);
//            endZoneOf = endZone.withZoneSameInstant(londonZone);
//
//        }
//        else {
//            startZoneOf = startZone.withZoneSameInstant(easternZone);
//            endZoneOf = endZone.withZoneSameInstant(easternZone);
//
//        }

//        System.out.println(startZoneOf);
//        System.out.println(endZoneOf);
        String startZoneString = startZoneOf.toString();
        String endZoneString = endZoneOf.toString();

        String[] startTimeSplit = startZoneString.split("T");
        String[] endTimeSplit = endZoneString.split("T");

        String startHourZone = startTimeSplit[1];
        String endHourZone = endTimeSplit[1];

        String[] startHourZoneSplit;
        String[] endHourZoneSplit;

        if(isLondon) {
            startHourZoneSplit = startHourZone.split("\\+");
            endHourZoneSplit = endHourZone.split("\\+");
        }
        else {
            startHourZoneSplit = startHourZone.split("-");
            endHourZoneSplit = endHourZone.split("-");
        }
        String startTime = startHourZoneSplit[0];
        String endTime = endHourZoneSplit[0];

        String startDate = startTimeSplit[0];
        String endDate = endTimeSplit[0];

//        String[] parts = start.split(" ");
//        String startDate = parts[0];
//        String startTime = parts[1];
//
//        String[] endParts = end.split(" ");
//        String endDate = endParts[0];
//        String endTime = endParts[1];




        startDateTextField.setText(startDate);
        startTimeComboBox.setValue(startTime);

        endDateTextField.setText(endDate);
        endTimeComboBox.setValue(endTime);

        contactNameComboBox.setValue(appointments.getStringContact());


    }

    /**
     *
     * @throws SQLException
     * Pulls the contact names from the database and adds them to the combo box.
     */
    private void pullContactNames() throws SQLException {
        Connection connection = DBConnection.getConnection();

        String selectStatement = "Select Contact_Name from contacts";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
        ResultSet resultSet = preparedStatement.executeQuery();
        String contactName;

        while(resultSet.next()) {
            contactName = resultSet.getString("Contact_Name");
            contactNames.add(contactName);
            contactNameComboBox.getItems().add(contactName);
        }
    }

    /**
     *
     * @throws SQLException
     * Pulls the customer names from the database and adds them to the combo box.
     */
    public void pullCustomers() throws SQLException {
        Connection connection = DBConnection.getConnection();

        String selectStatement = "SELECT Customer_Name from customers";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
        ResultSet resultSet = preparedStatement.executeQuery();
        String customerName;

        while (resultSet.next()) {
            customerName = resultSet.getString("Customer_Name");
            customerNames.add(customerName);
            customerComboBox.getItems().add(customerName);
        }
    }



    //method currently disabled. To be added to a later iteration. Used to remove times from the combo boxes

//    public void getAvailableLocationTimes(LocalDate enteredStartDate, LocalDate enteredEndDate) throws SQLException {
//        ZonedDateTime localTime = ZonedDateTime.now(zoneId);
//
//        String currentTimeSplit = localTime.toString();
//        String[] parts = currentTimeSplit.split("T");
//        String currentDate = parts[0];
//        String currentTime = parts[1];
//
//        String[] parts1 = currentTime.split(":");
//        currentTime = parts1[0] + ":00";
//
//
//        if(currentDate.equals(enteredStartDate.toString())) {
//            if(startTimeComboBox.getItems().contains(currentTime)) {
//                int enteredTimeIndex = startTimeComboBox.getItems().indexOf(currentTime);
//                int firstIndex = 0;
//                while(startTimeComboBox.getItems().contains(currentTime)) {
//                    if(enteredTimeIndex > firstIndex) {
//                        System.out.println(startTimeComboBox.getItems().get(0));
//                        startTimeComboBox.getItems().remove(0);
//                    }
//                }
//            }
//        }
//
//        Connection connection = DBConnection.getConnection();
//
//        String selectStatement = "SELECT Start, END from appointments";
//        DBQuery.setPreparedStatement(connection, selectStatement);
//        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
//
//        ResultSet resultSet = preparedStatement.executeQuery();
//
//        startEndTimeSplitFromDatabase(enteredStartDate, enteredEndDate, resultSet, startTimeComboBox, endTimeComboBox);
//
//
//    }

    //method currently disabled. To be added to a later iteration.

//    static void startEndTimeSplitFromDatabase(LocalDate enteredStartDate, LocalDate enteredEndDate, ResultSet resultSet, ComboBox<String> startTimeComboBox, ComboBox<String> endTimeComboBox) throws SQLException {
//        Timestamp start;
//        Timestamp end;
//        while (resultSet.next()) {
//            start = resultSet.getTimestamp("Start");
//            end = resultSet.getTimestamp("End");
//            LocalDateTime localTimeStart = start.toLocalDateTime();
//            LocalDateTime localTimeEnd = end.toLocalDateTime();
//            String startString = localTimeStart.toString();
//            String endString = localTimeEnd.toString();
//
//            String[] startParts = startString.split("T");
//            String[] endParts = endString.split("T");
//
//
//            String startDate = startParts[0];
//            String endDate = endParts[0];
//
//            String startTime = startParts[1];
//            String endTime = endParts[1];
//            String[] startTimeSplit = startTime.split(":");
//            String[] endTimeSplit = endTime.split(":");
//            String startTimeHour = startTimeSplit[0];
//            String endTimeHour = endTimeSplit[0];
//
//            if(startDate.equals(enteredStartDate.toString())) {
//                if(startTimeComboBox.getItems().contains(startTimeHour + ":00") || startTimeComboBox.getItems().contains(endTimeHour)) {
//                    int beginningIndex = startTimeComboBox.getItems().indexOf(startTimeHour + ":00");
//                    int endIndex = startTimeComboBox.getItems().indexOf(endTimeHour + ":00");
//                    while(beginningIndex <= endIndex) {
//                        startTimeComboBox.getItems().remove(beginningIndex);
//                        endIndex = startTimeComboBox.getItems().indexOf(endTimeHour + ":00");
//                    }
//
//                    //startTimeComboBox.getItems().removeAll(startTimeHour + ":00");
//                }
//                if(endTimeComboBox.getItems().contains(endTimeHour + ":00") || endTimeComboBox.getItems().contains(startTimeHour + ":00")) {
//                    int beginningIndex = endTimeComboBox.getItems().indexOf(startTimeHour + ":00");
//                    int endIndex = endTimeComboBox.getItems().indexOf(endTimeHour + ":00");
//                    while(beginningIndex <= endIndex) {
//                        endTimeComboBox.getItems().remove(beginningIndex);
//                        endIndex = endTimeComboBox.getItems().indexOf(endTimeHour + ":00");
//                    }
//
//                    //endTimeComboBox.getItems().removeAll(endTimeHour + ":00", startTimeHour + ":00");
//                }
//            }
//        }
//    }


    /**
     *
     * @param location Adds the start and end times to the combo boxes based on the location that is selected.
     * All times added are within the EST business hours
     */
    public void addLocationTimesToComboBox(String location) {
        startTimeComboBox.getItems().clear();
        endTimeComboBox.getItems().clear();

//        startTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
//                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00");
//        endTimeComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00",
//                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");

        startTimeComboBox.getItems().addAll("01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        endTimeComboBox.getItems().addAll( "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59");

        startTimes.addAll(startTimeComboBox.getItems());
        endTimes.addAll(endTimeComboBox.getItems());

//        if(location.contains("Phoenix")) {
//            startTimeComboBox.getItems().add(0, "05:00");
//            startTimeComboBox.getItems().add(1, "06:00");
//            startTimeComboBox.getItems().add(2, "07:00");
//
//            endTimeComboBox.getItems().add(0, "06:00");
//            endTimeComboBox.getItems().add(1, "07:00");
//            endTimeComboBox.getItems().add(2, "08:00");
//
//            startTimes.add(0, "05:00");
//            startTimes.add(1, "06:00");
//            startTimes.add(2, "07:00");
//            //startTimes.addAll("05:00", "06:00", "07:00");
//
//            endTimes.add(0, "06:00");
//            endTimes.add(1, "07:00");
//            endTimes.add(2, "08:00");
//            //endTimes.addAll("06:00", "07:00");
//
//            startTimeComboBox.getItems().removeAll("19:00", "20:00", "21:00", "22:00");
//            endTimeComboBox.getItems().removeAll("20:00", "21:00", "22:00");
//            startTimes.removeAll("19:00", "20:00", "21:00", "22:00");
//            endTimes.removeAll("20:00", "21:00", "22:00");
//        }
//        else if (location.contains("London")) {
//            startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
//            endTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00");
//
//            startTimeComboBox.getItems().add("23:00");
//            endTimeComboBox.getItems().addAll("23:00", "23:59");
//
//            startTimes.add("23:00");
//            endTimes.addAll("23:00", "23:59");
//
//            startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
//            endTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
//        }
//        else {
//            System.out.println("Eastern Times already added");
//        }
    }

    /**
     * Adds the appointment types to the type combo box.
     */
    private void addTypesToComboBox() {
        typeComboBox.getItems().addAll("De-Briefing", "Planning Session", "New Account", "Follow Up");
    }

    /**
     *
     * @param actionEvent takes the action event that was passed to go to the appointments screen.
     * @throws IOException
     * Goes back to the appointments screen when called.
     */
    private void nextScreen(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/appointments.fxml"));

        root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Removes previous times from the end combo box based on the selected start time.
     * Lambda used to reduce the amount of code necessary for the function.
     */
    public static void handleComboBoxSelection(ComboBox<String> startTimeComboBox, ComboBox<String> endTimeComboBox) {
        ZoneId easternZone = ZoneId.of("America/New_York");


        //
        startTimeComboBox.setOnAction(e -> {
            String selectedStartTimeString = startTimeComboBox.getValue();
            endTimeComboBox.getItems().setAll(endTimes);
            //LocalTime selectedStartTime = LocalTime.parse(selectedStartTimeString);
            if(endTimeComboBox.getItems().contains(selectedStartTimeString)) {
                int index = endTimeComboBox.getItems().indexOf(selectedStartTimeString);
                while(index > 0) {
                    endTimeComboBox.getItems().remove(0);
                    index = endTimeComboBox.getItems().indexOf(selectedStartTimeString);
                }
                endTimeComboBox.setValue("");
                endTimeComboBox.getItems().remove(index);


            }
        });
    }


    /**
     *
     * @param start takes the start time that was passed to perform the validation
     * @param end takes the end time that was passed to perform the validation
     * @return
     * Validation for the appointment.
     * Calls the database and checks if the appointment is either during or overlaps an already existing appointment.
     */
    private boolean validAppointment(LocalDateTime start, LocalDateTime end) {
        int appointmentID = -1;
        String userName = User.getUserName();
        int tempAppointmentID = appointments.getAppointmentID();

        try {
            if(isBusinessHours(start, end)) {
                Connection connection = DBConnection.getConnection();
                String selectAppointmentID = "Select Appointment_ID, Start, End from appointments";

                String selectStatement = "SELECT * FROM appointments WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End) AND " +
                        "(Created_By = ? AND Appointment_ID != ?)";

                DBQuery.setPreparedStatement(connection, selectAppointmentID);
                PreparedStatement preparedAppointmentStatement = DBQuery.getPreparedStatement();

                ResultSet resultSet1 = preparedAppointmentStatement.executeQuery();
                while (resultSet1.next()) {
                    LocalDateTime tempStart = resultSet1.getTimestamp("Start").toLocalDateTime();
                    LocalDateTime tempEnd = resultSet1.getTimestamp("End").toLocalDateTime();
                    if(resultSet1.getInt("Appointment_ID") == tempAppointmentID) {
                        if(start.isEqual(tempStart) && end.isEqual(tempEnd)) {
                            return true;
                        }
                        else {
                            DBQuery.setPreparedStatement(connection, selectStatement);
                            PreparedStatement preparedStatement = DBQuery.getPreparedStatement();


                            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
                            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
                            preparedStatement.setTimestamp(3, Timestamp.valueOf(start));
                            preparedStatement.setTimestamp(4, Timestamp.valueOf(end));
                            preparedStatement.setString(5, userName);
                            preparedStatement.setInt(6, tempAppointmentID);
                            ResultSet resultSet = preparedStatement.executeQuery();

                            if (resultSet.next()) {
                                if(resultSet.getTimestamp("Start").equals(Timestamp.valueOf(end))) {
                                    return true;
                                }
                                else {
                                    System.out.println(resultSet.getInt("Appointment_ID"));
                                    System.out.println("Time conflicts with another appointment");
                                    return false;
                                }

                            }
                            ZoneId locationZone;
//                            if(locationComboBox.getValue().contains("Phoenix")) {
//                                locationZone = ZoneId.of("America/Phoenix");
//                            }
//                            else if(locationComboBox.getValue().contains("London")) {
//                                locationZone = ZoneId.of("Europe/London");
//                            }
//                            else {
//                                locationZone = ZoneId.of("America/New_York");
//                            }
//                            ZonedDateTime currentDate = LocalDateTime.now().atZone(zoneId);
//                            ZonedDateTime currentDateLocation = currentDate.withZoneSameInstant(zoneId);
//                            String[] parts = currentDateLocation.toString().split("T");
//                            String date = parts[0];
//                            if(startDateTextField.getText().contains(date)) {
//                                LocalDateTime startDateTime = LocalDateTime.parse(date + "T" + startTimeComboBox.getValue());
//                                if(startDateTime.isBefore(ChronoLocalDateTime.from(currentDateLocation))) {
//                                    System.out.println("Cannot schedule a time on the current date that has already passed.");
//                                    return false;
//                                }
//                            }
                        }
                    }
                }
            }
            else {
                System.out.println("Time is not within business hours");
                return false;
            }
        } catch (SQLException sqe) {
            System.out.println("SQL error while calling validAppointment");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("An unknown nonSQL error occurred");
            e.printStackTrace();
        }
        return true;
    }

    /**
     *
     * @param enteredDate
     * @throws SQLException
     * Prints the times for the appointments that are on the selected date to the console in the users local time when called.
     */
    private void printAppointmentTimes(LocalDate enteredDate) throws SQLException {
        String appointmentDate = null;
        LocalDateTime localAppointmentDate = null;
        String sqlDate = null;
        Connection connection = DBConnection.getConnection();

        String selectAppointmentStatement = "SELECT Start, End from appointments";

        DBQuery.setPreparedStatement(connection, selectAppointmentStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

        ResultSet resultSet = preparedStatement.executeQuery();

        try {
            while (resultSet.next()) {
                appointmentDate = resultSet.getString("Start");


                if(appointmentDate.contains(enteredDate.toString())) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    localAppointmentDate = LocalDateTime.parse(appointmentDate, formatter);


                }
            }
            if(localAppointmentDate == null) {
                System.out.println("No appointments on the selected date");
            }
            else {
                String selectStatement = "SELECT Start, End from appointments where Start between '" + enteredDate + "' and '" + enteredDate + " 23:59:59'";
                DBQuery.setPreparedStatement(connection, selectStatement);
                PreparedStatement printPreparedStatement = DBQuery.getPreparedStatement();

                //printPreparedStatement.setString(1, String.valueOf(enteredDate));
                //printPreparedStatement.setString(2, String.valueOf(enteredDate));

                ResultSet printResultSet = printPreparedStatement.executeQuery();
                System.out.println("This date has the following appointments in your local time: ");
                while (printResultSet.next()) {
                    System.out.println("Start: " + printResultSet.getTimestamp("Start") + " ||| End: " + printResultSet.getTimestamp("End") + "\n");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param valid takes the valid boolean to determine which version to write to the text file.
     * @param appointmentID uses the appointment ID to specify the appointment that was changed.
     * @param start start time of the appointment is written to the file.
     * @param end end time of the appointment is written to the file.
     * @param customerID customerID is written to the file.
     * @param contactID contact ID is written to the file.
     * @throws IOException
     *
     * Report that tracks when appointments are changed.
     * Adds a timestamp, the user, and the appointment information to the appointment_changes text file when called.
     */
    private void appointmentChanges(boolean valid, String appointmentID, String start, String end, int customerID, int contactID) throws IOException {
        String fileName = "appointment_changes.txt";
        String userName = User.getUserName();
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime startLocal = LocalDateTime.parse(start, dateTimeFormatter);
        ZonedDateTime startZoneTime = startLocal.atZone(utc);
        ZonedDateTime startZoneLocal = startZoneTime.withZoneSameInstant(zoneId);

        LocalDateTime endLocal = LocalDateTime.parse(end, dateTimeFormatter);
        ZonedDateTime endZoneTime = endLocal.atZone(utc);
        ZonedDateTime endZoneLocal = endZoneTime.withZoneSameInstant(zoneId);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));
        if(valid) {
            bufferedWriter.append(String.valueOf(LocalDateTime.now())).append(" ").append("User: " + userName + " has altered the appointment with ID: ").append(appointmentID).append("\n").append(
                    "Info: " + titleTextField.getText() + "\n" + descriptionTextField.getText() + "\n" + locationComboBox.getValue() + "\n" + typeComboBox.getValue() + "\n" +
                    "Start time: " + startZoneLocal + " " + "\n" + "End Time: " + endZoneLocal +  " " + "\n" + "CustomerID: " + customerID + "\n" + "ContactID: " + contactID + "\n" + "==========================").append("\n");
        }
        else {
            bufferedWriter.append(String.valueOf(LocalDateTime.now())).append(" ").append("User: " + userName + " had a failed attempt to edit the appointment with ID: " + appointmentID).append("\n");
        }

        System.out.println("Appointment change activity added to " + fileName);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static boolean isBusinessHours(LocalDateTime start, LocalDateTime end) {
        ZoneId easternZone = ZoneId.of("America/New_York");

        ZonedDateTime startWithUserZone = start.atZone(zoneId);
        ZonedDateTime startWithEasternZone = startWithUserZone.withZoneSameInstant(easternZone);

        ZonedDateTime endWithUserZone = end.atZone(zoneId);
        ZonedDateTime endWithEasternZone = endWithUserZone.withZoneSameInstant(easternZone);

        if(startWithEasternZone.isBefore(startWithEasternZone.withHour(8)) || startWithEasternZone.isAfter(startWithEasternZone.withHour(22))) {
            System.out.println("Start time is before 08:00 or after 22:00 Eastern");
            return false;
        }
        else if(endWithEasternZone.isBefore(endWithEasternZone.withHour(8)) || endWithEasternZone.isAfter(endWithEasternZone.withHour(22))) {
            System.out.println("End time is before 08:00 or after 22:00 Eastern");
            return false;
        }
        return true;
    }


}
