package Controller;

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
import javafx.scene.control.ComboBox;

import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * This class allows the user to enter information into combo boxes and text fields in order to save them as an appointment in the database.
 */
public class AddAppointmentScreen implements Initializable {

    //Zone IDs to allow for time changing
    private final ZoneId zoneId = ZoneId.systemDefault();
    ZoneId phoenixZone = ZoneId.of("America/Phoenix");
    ZoneId londonZone = ZoneId.of("Europe/London");
    ZoneId easternZone = ZoneId.of("America/New_York");
    ZoneId utc = ZoneId.of("UTC");

    @FXML
    Label startTimeErrorLabel;
    @FXML
    TextField titleTextField;
    @FXML
    TextField descriptionTextField;
    @FXML
    ComboBox<String> typeComboBox;
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
    Button startTimesButton;
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;
    @FXML
    Button addTestApptButton;
    @FXML
    Button revealTestButton;
    @FXML
    RadioButton officeLocationTimeRadioBtn;
    @FXML
    RadioButton timezoneRadioBtn;


    /**
     * Observable lists
     */
    private final ObservableList<String> contactNames = FXCollections.observableArrayList();
    private final ObservableList<String> customerNames = FXCollections.observableArrayList();
    private static final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private static final ObservableList<String> endTimes = FXCollections.observableArrayList();

    /**
     * Date formatter for the times
     */
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private boolean testAppointment = false;


    /**
     *
     * @param resourceBundle
     *
     * sets the error label to be hidden, adds the types, locations, contact names, and customer names to the combo box during initialization
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Sets the error label to be hidden on initialization
        startTimeErrorLabel.setOpacity(0);

        //Selects the radio button on initialization and deselects the other radio button
        officeLocationTimeRadioBtn.setSelected(true);
        timezoneRadioBtn.setSelected(false);

        //Makes the end date text field not editable. The times are viewed with the start date
        endDateTextField.setEditable(false);

        //Initial setOnAction for the comboBoxes
        handleComboBoxSelection(startTimeComboBox, endTimeComboBox);

        //Adds the types of appointments to the combo box
        addTypesToComboBox();

        try {
            //Gets the contact names and customer names and adds them to the combo boxes
            pullContactNames();
            pullCustomers();

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        //adds locations to the combo box
        locationComboBox.getItems().addAll("Phoenix", "White Plains", "Montreal", "London");

    }


    /**
     * Mouse click handler for the start time combo box.
     * Displays the error label when the date text field is empty.
     */
    public void startTimesComboBoxClicked() {

        if(startDateTextField.getText().isEmpty()) {
            startTimeErrorLabel.setOpacity(1);
        }


    }

    /**
     * Handler for the start time combo box.
     * Runs the handleComboBoxSelection method when a selection is changed.
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
     * Mouse click handler for the end time combo box.
     * Prints an error message to the console when the end date or start date text fields are empty.
     */
    public void endTimesComboBoxClicked() {

        if(endDateTextField.getText().isEmpty() || startDateTextField.getText().isEmpty()) {
            System.out.println("Please enter a start and end date and click the \"Check Times\" button to view times");
        }
    }

    /**
     *Handler for the location combo box.
     */
    public void locationComboBoxHandler() {
    }



    /**
     * Handler for the customer combo box.
     */
    public void customerComboBoxHandler() {
    }

    /**
     * Handler for the view times button.
     * Prints an error to the console if the date field is empty.
     * Gets the start date from the text field and checks if it is valid. If valid, it calls the addLocationTimesToComboBox method and printAppointmentTimes method.
     * Combo box times are displayed depending on the location that is selected.
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
                                addLocationTimesToComboBox(location);
                                printAppointmentTimes(enteredStartDate);

                                //getAvailableLocationTimes(enteredStartDate, enteredEndDate);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("ERROR");
                alert.setContentText("Something went wrong! Try entering a valid date.");
                alert.showAndWait();
            }
        }
    }


    /**
     *
     * @param actionEvent is passed to the nextScreen method to go to the desired screen.
     * @throws SQLException
     * @throws IOException
     * Handler for the save button.
     * Checks if all fields have values.
     * Changes the times and dates into the correct formats.
     * Inserts the record into the database and goes to the appointment screen if everything is valid.
     */
    public void saveButtonHandler(ActionEvent actionEvent) throws SQLException, IOException {
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
            String fullStart = startDateTextField.getText() + " " + startTimeComboBox.getValue() + ":00";
            String fullEnd = endDateTextField.getText() + " " + endTimeComboBox.getValue() + ":00";


            LocalDateTime fullStartLocal = LocalDateTime.parse(fullStart, dateTimeFormatter);
            LocalDateTime fullEndLocal = LocalDateTime.parse(fullEnd, dateTimeFormatter);




            ZonedDateTime fullStartZone;
            ZonedDateTime fullEndZone;
            String location = locationComboBox.getValue();

            ZonedDateTime selectionTimeLocation;

            if(location.contains("Phoenix")) {
                selectionTimeLocation = fullStartLocal.atZone(phoenixZone);
            }
            else if(location.contains("London")) {
                selectionTimeLocation = fullStartLocal.atZone(londonZone);
            }
            else {
                selectionTimeLocation = fullStartLocal.atZone(easternZone);
            }
            ZonedDateTime selectionTimeInLocalTime = selectionTimeLocation.withZoneSameInstant(zoneId);

            String selectionTimeInLocalTimeString = selectionTimeInLocalTime.toString();
            String[] selectionSplit1 = selectionTimeInLocalTimeString.split("T");
            String split1Time = selectionSplit1[1];


            String[] selectionSplit2 = split1Time.split("-");
            String selectionTime = selectionSplit2[0];
            String fullZone = selectionSplit2[1];

            String[] selectionSplit3 = fullZone.split("05:00");
            String zoneOnly = selectionSplit3[1];


            //An alert that displays the selected time in the users local time
            Alert timeAlert = new Alert(Alert.AlertType.CONFIRMATION);
            timeAlert.setHeaderText("Are you sure you want this time?");
            timeAlert.setContentText("The time you selected is " + selectionTime + " " + zoneOnly + " in your local time.");
            Optional<ButtonType> result = timeAlert.showAndWait();

            if(result.get() == ButtonType.OK) {
                //calls the validAppointment method and continues if true
                if(validAppointment(fullStartLocal, fullEndLocal)) {
                    System.out.println("Appointment is valid");

                    LocalDateTime localStartToDatabase;
                    LocalDateTime localEndToDatabase;

                    //if the test appointment button is selected, the time will not be converted using the selected location
                    //the users time zone will be used instead
                    if(testAppointment) {
                        fullStartZone = ZonedDateTime.of(fullStartLocal, zoneId);
                        ZonedDateTime startTimeToDatabase = fullStartZone.withZoneSameInstant(utc);

                        fullEndZone = ZonedDateTime.of(fullEndLocal, zoneId);
                        ZonedDateTime endTimeToDatabase = fullEndZone.withZoneSameInstant(utc);

                        localStartToDatabase = startTimeToDatabase.toLocalDateTime();
                        localEndToDatabase = endTimeToDatabase.toLocalDateTime();
                    }

                    //changes the time depending on which location is selected and then changes that time to UTC
                    else if(locationComboBox.getValue().contains("Phoenix")) {
                        fullStartZone = ZonedDateTime.of(fullStartLocal, phoenixZone);
                        ZonedDateTime startTimeToDatabase = fullStartZone.withZoneSameInstant(utc);

                        fullEndZone = ZonedDateTime.of(fullEndLocal, phoenixZone);
                        ZonedDateTime endTimeToDatabase = fullEndZone.withZoneSameInstant(utc);

                        localStartToDatabase = startTimeToDatabase.toLocalDateTime();
                        localEndToDatabase = endTimeToDatabase.toLocalDateTime();



                    }
                    else if(locationComboBox.getValue().contains("London")) {
                        fullStartZone = ZonedDateTime.of(fullStartLocal, londonZone);
                        ZonedDateTime startTimeToDatabase = fullStartZone.withZoneSameInstant(utc);

                        fullEndZone = ZonedDateTime.of(fullEndLocal, londonZone);
                        ZonedDateTime endTimeToDatabase = fullEndZone.withZoneSameInstant(utc);

                        localStartToDatabase = startTimeToDatabase.toLocalDateTime();
                        localEndToDatabase = endTimeToDatabase.toLocalDateTime();

                    }
                    else {
                        fullStartZone = ZonedDateTime.of(fullStartLocal, easternZone);
                        ZonedDateTime startTimeToDatabase = fullStartZone.withZoneSameInstant(utc);

                        fullEndZone = ZonedDateTime.of(fullEndLocal, easternZone);
                        ZonedDateTime endTimeToDatabase = fullEndZone.withZoneSameInstant(utc);

                        localStartToDatabase = startTimeToDatabase.toLocalDateTime();
                        localEndToDatabase = endTimeToDatabase.toLocalDateTime();
                    }

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
                    String insertStatement = "INSERT into appointments(Title, Description, Location, Type, Start, End, Created_By, Customer_ID, Last_Updated_By, User_ID, Contact_ID) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

                    //gets the contactID from the database based on which name on the form was selected
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



                    DBQuery.setPreparedStatement(connection, insertStatement);
                    PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

                    preparedStatement.setString(1, titleTextField.getText());
                    preparedStatement.setString(2, descriptionTextField.getText());
                    preparedStatement.setString(3, locationComboBox.getValue());
                    preparedStatement.setString(4, typeComboBox.getValue());
                    preparedStatement.setString(5, startToDatabase);
                    preparedStatement.setString(6, endToDatabase);
                    preparedStatement.setString(7, User.getUserName());
                    preparedStatement.setInt(8, customerID);
                    preparedStatement.setString(9, User.getUserName());
                    preparedStatement.setInt(10, User.getUserID());
                    preparedStatement.setInt(11, contactID);

                    preparedStatement.execute();
                    System.out.println("New record added");

                    nextScreen(actionEvent);


                }
                else {
                    //Displays an alert if the times conflict with another appointment
                    //Displays if validAppointment returns false
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not save appointment!");
                    alert.setHeaderText("Appointment times conflict with another scheduled appointment or has invalid time.");
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
     * @param actionEvent is passed to the nextScreen method to go to the desired screen.
     * @throws IOException
     * Cancel button handler.
     * Asks for confirmation and then goes back to the appointments screen if yes is selected.
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
     * Handler for the hidden add test appointment button.
     * Fills in all the fields and selects the time to be 15 min in the future in the users local time.
     * To be used to easily add an appointment to test the appointment alert during log in.
     */
    public void addTestApptButtonHandler() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Confirming....");
        alert.setContentText("Would you like to add a test appointment for 15 min from now?\nThis will ignore the location selection for times");

        Optional<ButtonType> result = alert.showAndWait();


        if(result.get() == ButtonType.OK) {
            testAppointment = true;
            startTimeComboBox.getItems().clear();
            endTimeComboBox.getItems().clear();
            startTimeComboBox.valueProperty().set(null);
            endTimeComboBox.valueProperty().set(null);


            titleTextField.setText("Title");
            descriptionTextField.setText("Description");
            locationComboBox.setValue("White Plains");
            typeComboBox.setValue("Planning Session");

            long minutesToAdd = 15;
            long endMinutesToAdd = 10;
            //long hoursToAdd = 1;

            ZonedDateTime startInstant = Instant.now().atZone(zoneId).truncatedTo(ChronoUnit.MINUTES);
            ZonedDateTime startTimePlusFifteen = startInstant.plus(minutesToAdd, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES);
            ZonedDateTime endInstant = startTimePlusFifteen.plus(endMinutesToAdd, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES);


            System.out.println("Current time: " + startInstant);
            System.out.println("Fifteen Minutes from now: " + startTimePlusFifteen);

            LocalDateTime localStartPlusFifteen = startTimePlusFifteen.toLocalDateTime();
            LocalDateTime localEnd = endInstant.toLocalDateTime();

            String startSplit = localStartPlusFifteen.toString();
            String[] parts = startSplit.split("T");
            String startDate = parts[0];
            String startTime = parts[1];

            String endSplit = localEnd.toString();
            String[] parts1 = endSplit.split("T");
            String endDate = parts1[0];
            String endTime = parts1[1];


            startDateTextField.setText(startDate);
            endDateTextField.setText(endDate);

            startTimeComboBox.setValue(startTime);
            endTimeComboBox.setValue(endTime);
            System.out.println("Start time ComboBox value: " + startTimeComboBox.getValue());
            System.out.println("End time ComboBox value: " + endTimeComboBox.getValue());

        }
        else {
            System.out.println("Appointment is normal");
        }
    }

    /**
     * Handler for the hidden reveal button at the top left on the add appointment screen.
     * Pressing this button will reveal the add test appointment button and make it clickable.
     */
    public void revealTestButtonHandler() {
        addTestApptButton.setOpacity(1);
        addTestApptButton.setDisable(false);
        System.out.println("Test Button Revealed");
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
     * @throws SQLException
     * Gets the contact names from the database and adds them to the combo box.
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
     * Gets the customer names from the database and adds them to the combo box.
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

    //This method is currently disabled. To be added in a later iteration. For removing times from the combo box to make the app more user friendly.

    /*public void getAvailableLocationTimes(LocalDate enteredStartDate, LocalDate enteredEndDate) {

        ZonedDateTime localTime = ZonedDateTime.now(zoneId);
        ZonedDateTime phoenixTime = localTime.withZoneSameInstant(phoenixZone);
        ZonedDateTime easternTime = localTime.withZoneSameInstant(easternZone);
        ZonedDateTime londonTime = localTime.withZoneSameInstant(londonZone);

        if(locationComboBox.getValue().contains("Phoenix") && !timezoneRadioBtn.isSelected()) {
            localTime = phoenixTime;
        }
        else if(locationComboBox.getValue().contains("London") && !timezoneRadioBtn.isSelected()) {
            localTime = londonTime;
        }
        else if(!timezoneRadioBtn.isSelected()) {
            localTime = easternTime;
        }

        String currentTimeSplit = localTime.toString();
        String[] parts = currentTimeSplit.split("T");
        String currentDate = parts[0];
        String currentTime = parts[1];

        String[] parts1 = currentTime.split(":");
        currentTime = parts1[0] + ":00";


        if(currentDate.equals(enteredStartDate.toString())) {
            if(startTimeComboBox.getItems().contains(currentTime)) {
                int currentTimeIndex = startTimeComboBox.getItems().indexOf(currentTime);
                int firstIndex = 0;
                while(startTimeComboBox.getItems().contains(currentTime)) {
                    if(currentTimeIndex >= firstIndex) {
                        System.out.println(startTimeComboBox.getItems().get(0));
                        startTimeComboBox.getItems().remove(0);
                    }
                    else {
                        break;
                    }
                }
            }
        }

        Connection connection = DBConnection.getConnection();

        String selectStatement = "SELECT Start, END from appointments";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

        ResultSet resultSet = preparedStatement.executeQuery();

        startEndTimeSplitFromDatabase(enteredStartDate, enteredEndDate, resultSet, startTimeComboBox, endTimeComboBox);


    }*/

    //This method is currently disabled. To be added in a later iteration.

    /*static void startEndTimeSplitFromDatabase(LocalDate enteredStartDate, LocalDate enteredEndDate, ResultSet resultSet, ComboBox<String> startTimeComboBox, ComboBox<String> endTimeComboBox) throws SQLException {
        Timestamp start;
        Timestamp end;
        while (resultSet.next()) {
            start = resultSet.getTimestamp("Start");
            end = resultSet.getTimestamp("End");
            LocalDateTime localTimeStart = start.toLocalDateTime();
            LocalDateTime localTimeEnd = end.toLocalDateTime();
            String startString = localTimeStart.toString();
            String endString = localTimeEnd.toString();

            String[] startParts = startString.split("T");
            String[] endParts = endString.split("T");


            String startDate = startParts[0];
            String endDate = endParts[0];

            String startTime = startParts[1];
            String endTime = endParts[1];
            String[] startTimeSplit = startTime.split(":");
            String[] endTimeSplit = endTime.split(":");
            String startTimeHour = startTimeSplit[0];
            String endTimeHour = endTimeSplit[0];

            //ComboBox removal to be fixed in a later iteration

            if(startDate.equals(enteredStartDate.toString())) {
                if(startTimeComboBox.getItems().contains(startTimeHour + ":00") || startTimeComboBox.getItems().contains(endTimeHour)) {
                    int beginningIndex = startTimeComboBox.getItems().indexOf(startTimeHour + ":00");
                    int endIndex = startTimeComboBox.getItems().indexOf(endTimeHour + ":00");
                    while(beginningIndex <= endIndex) {
                        startTimeComboBox.getItems().remove(beginningIndex);
                        endIndex = startTimeComboBox.getItems().indexOf(endTimeHour + ":00");
                    }

                    //startTimeComboBox.getItems().removeAll(startTimeHour + ":00");
                }
                if(endTimeComboBox.getItems().contains(endTimeHour + ":00") || endTimeComboBox.getItems().contains(startTimeHour + ":00")) {
                    int beginningIndex = endTimeComboBox.getItems().indexOf(startTimeHour + ":00");
                    int endIndex = endTimeComboBox.getItems().indexOf(endTimeHour + ":00");
                    if(beginningIndex == -1) {
                        endTimeComboBox.getItems().add(0, startTimeHour + ":00");
                        beginningIndex = endTimeComboBox.getItems().indexOf(startTimeHour + ":00");
                        endIndex = endTimeComboBox.getItems().indexOf(endTimeHour + ":00");
                    }
                    while(beginningIndex <= endIndex) {
                        endTimeComboBox.getItems().remove(beginningIndex);
                        endIndex = endTimeComboBox.getItems().indexOf(endTimeHour + ":00");
                    }

                    //endTimeComboBox.getItems().removeAll(endTimeHour + ":00", startTimeHour + ":00");
                }
            }
        }
    }*/

    /**
     *
     * @param location Adds times to the start and end combo boxes depending on which location is selected.
     * The times are all within the EST office hours.
     */
    public void addLocationTimesToComboBox(String location) {
        startTimeComboBox.getItems().clear();
        endTimeComboBox.getItems().clear();

        startTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00");
        endTimeComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");
        startTimes.addAll(startTimeComboBox.getItems());
        endTimes.addAll(endTimeComboBox.getItems());

        if(location.contains("Phoenix")) {
            startTimeComboBox.getItems().add(0, "05:00");
            startTimeComboBox.getItems().add(1, "06:00");
            startTimeComboBox.getItems().add(2, "07:00");

            endTimeComboBox.getItems().add(0, "06:00");
            endTimeComboBox.getItems().add(1, "07:00");

            startTimes.add(0, "05:00");
            startTimes.add(1, "06:00");
            startTimes.add(2, "07:00");

            endTimes.add(0, "06:00");
            endTimes.add(1, "07:00");

            startTimeComboBox.getItems().removeAll("19:00", "20:00", "21:00", "22:00");
            endTimeComboBox.getItems().removeAll("20:00", "21:00", "22:00");
            startTimes.removeAll("19:00", "20:00", "21:00", "22:00");
            endTimes.removeAll("20:00", "21:00", "22:00");
        }
        else if (location.contains("London")) {
            startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
            endTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00");

            startTimeComboBox.getItems().add("23:00");
            endTimeComboBox.getItems().addAll("23:00", "23:59");

            startTimes.add("23:00");
            endTimes.addAll("23:00", "23:59");

            startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
            endTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00");
        }
        else {
            System.out.println("Eastern Times already added");
        }
    }

    /**
     * Adds the appointment types to the types combo box.
     */
    private void addTypesToComboBox() {
        typeComboBox.getItems().addAll("De-Briefing", "Planning Session", "New Account", "Follow Up");
    }

    /**
     *
     * @param actionEvent is passed from other methods.
     * @throws IOException
     * Causes the app to go back to the appointments screen when called.
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
     * onAction for the combo boxes. Removes previous times from the end combo box based on the start time that is selected.
     * Lambda used to reduce the amount of code necessary for the combo box function.
     */
    public static void handleComboBoxSelection(ComboBox<String> startTimeComboBox, ComboBox<String> endTimeComboBox) {

        //lambda used to reduce the amount of code necessary for the function
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
     * @param start appointment start time
     * @param end appointment end time
     * @return
     * Validation for the appointment.
     * Calls the database and checks if the appointment is either during or overlaps an already existing appointment.
     */
    private boolean validAppointment(LocalDateTime start, LocalDateTime end) {
        int appointmentID = -1;
        String userName = User.getUserName();

        try {
            Connection connection = DBConnection.getConnection();
            String selectStatement = "SELECT * FROM appointments WHERE (? BETWEEN Start AND End OR ? BETWEEN Start AND End OR ? < Start AND ? > End) AND " +
                    "(Created_By = ? AND Appointment_ID != ?)";
            DBQuery.setPreparedStatement(connection, selectStatement);
            PreparedStatement preparedStatement = DBQuery.getPreparedStatement();


            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(start));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(end));
            preparedStatement.setString(5, userName);
            preparedStatement.setInt(6, appointmentID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return false;
            }
            ZoneId locationZone;
            if(locationComboBox.getValue().contains("Phoenix")) {
                locationZone = ZoneId.of("America/Phoenix");
            }
            else if(locationComboBox.getValue().contains("London")) {
                locationZone = ZoneId.of("Europe/London");
            }
            else {
                locationZone = ZoneId.of("America/New_York");
            }
            ZonedDateTime currentDate = LocalDateTime.now().atZone(zoneId);
            ZonedDateTime currentDateLocation = currentDate.withZoneSameInstant(locationZone);
            String[] parts = currentDateLocation.toString().split("T");
            String date = parts[0];
            if(startDateTextField.getText().contains(date)) {
                LocalDateTime startDateTime = LocalDateTime.parse(date + "T" + startTimeComboBox.getValue());
                if(startDateTime.isBefore(ChronoLocalDateTime.from(currentDateLocation))) {
                    return false;

                }
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
     * @param enteredDate date entered by the user
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
}
