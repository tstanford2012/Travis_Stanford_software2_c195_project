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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditAppointmentScreen implements Initializable {
    Appointments appointments;
    @FXML
    TextField titleTextField;
    @FXML
    TextField descriptionTextField;
    @FXML
    TextField typeTextField;
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
    private final ZoneId zoneId = ZoneId.systemDefault();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startTimeErrorLabel.setOpacity(0);
        endTimeErrorLabel.setOpacity(0);
        appointmentIDLabel.setText("");
        officeLocationTimeRadioBtn.setSelected(true);
        timezoneRadioBtn.setSelected(false);


        //Initial setOnAction for the comboBoxes
        handleComboBoxSelection(startTimeComboBox, endTimeComboBox);

        try {
            pullContactNames();
            pullCustomers();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        locationComboBox.getItems().addAll("Phoenix", "White Plains", "Montreal", "London");
    }


    public void startTimesComboBoxClicked() {
        if(startDateTextField.getText().isEmpty()) {
            startTimeErrorLabel.setOpacity(1);
        }
    }

    public void startTimeComboBoxHandler() {
        handleComboBoxSelection(startTimeComboBox, endTimeComboBox);


        System.out.println(startTimeComboBox.getValue());

    }


    public void endTimeComboBoxHandler() {
    }

    public void endTimesComboBoxClicked() {
        if(endDateTextField.getText().isEmpty() || startDateTextField.getText().isEmpty()) {
            System.out.println("Please enter a start and end date and click the \"Check Times\" button to view times");
        }
        else {
            System.out.println(endTimeComboBox.getValue());
        }
    }

    public void locationComboBoxHandler() {
    }

    public void customerComboBoxHandler() {
    }

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
                                getAvailableLocationTimes(enteredStartDate, enteredEndDate);
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

            //Prevents the user from attempting to view location times with no location selected
            if(locationComboBox.getValue().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Location not selected");
                alert.setContentText("Please select a location and try again");
                alert.show();
            }
        }
    }

    public void endTimesButtonHandler() {
       if(endDateTextField.getText().isEmpty()) {
           System.out.println("Must enter an end date");
       }
       else {
           endTimeErrorLabel.setOpacity(0);
           endTimeComboBox.getItems().clear();
           String enteredEndDateString = endDateTextField.getText();
           String enteredStartDateString = startDateTextField.getText();

           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
           LocalDate enteredStartDate = LocalDate.parse(enteredStartDateString, formatter);
           LocalDate enteredEndDate = LocalDate.parse(enteredEndDateString, formatter);

           //Alerts the user that only Monday-Friday days are allowed
           if(enteredEndDate.getDayOfWeek() == DayOfWeek.SATURDAY || enteredEndDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
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
    }

    public void saveButtonHandler(ActionEvent actionEvent) throws SQLException, IOException {
        if(titleTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty() || locationComboBox.getValue() == null || typeTextField.getText().isEmpty() ||
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

            System.out.println(fullStartLocal);
            System.out.println(fullEndLocal);

            if(validAppointment(fullStartLocal, fullEndLocal)) {
                System.out.println("Appointment is valid");
                Connection connection = DBConnection.getConnection();
                String selectStatement = "";
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


                System.out.println(titleTextField.getText());
                System.out.println(descriptionTextField.getText());
                System.out.println(locationComboBox.getValue());
                System.out.println(typeTextField.getText());
                System.out.println(Timestamp.valueOf(fullStart));
                System.out.println(Timestamp.valueOf(fullEnd));
                System.out.println(User.getUserName());
                System.out.println(customerID);
                System.out.println(User.getUserID());
                System.out.println(contactID);
                DBQuery.setPreparedStatement(connection, updateStatement);
                PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

                preparedStatement.setString(1, titleTextField.getText());
                preparedStatement.setString(2, descriptionTextField.getText());
                preparedStatement.setString(3, locationComboBox.getValue());
                preparedStatement.setString(4, typeTextField.getText());
                preparedStatement.setTimestamp(5, Timestamp.valueOf(fullStart));
                preparedStatement.setTimestamp(6, Timestamp.valueOf(fullEnd));
                preparedStatement.setString(7, User.getUserName());
                preparedStatement.setInt(8, customerID);
                preparedStatement.setInt(9, User.getUserID());
                preparedStatement.setInt(10, contactID);
                preparedStatement.setInt(11, Integer.parseInt(appointmentIDLabel.getText()));

                preparedStatement.execute();
                System.out.println("Record Updated");

                nextScreen(actionEvent, "../View/appointments.fxml");


            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could not save appointment!");
                alert.setHeaderText("Appointment times conflict with another scheduled appointment.");
                alert.showAndWait();
            }
        }
    }

    public void cancelButtonHandler(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("Changes will not be saved");
        Optional<ButtonType> result = alert.showAndWait();


        if(result.get() == ButtonType.OK) {
            nextScreen(actionEvent, "../View/appointments.fxml");
        }
        else {
            System.out.println("No longer cancelling");
        }
    }

    public void officeLocationTimeRadioBtnHandler() {
        timezoneRadioBtn.setSelected(false);
    }

    public void timezoneRadioBtnHandler() {
        officeLocationTimeRadioBtn.setSelected(false);
    }

    public void setAppointment(Appointments appointments) {
        this.appointments = appointments;

        appointmentIDLabel.setText(Integer.toString(appointments.getAppointmentID()));
        titleTextField.setText(appointments.getTitle());
        descriptionTextField.setText(appointments.getDescription());
        locationComboBox.setValue(appointments.getLocation());
        customerComboBox.setValue(appointments.getAppointmentCustomerName());
        typeTextField.setText(appointments.getType());

        String start = appointments.getStart().toString();
        String end = appointments.getEnd().toString();
        System.out.println(start);
        System.out.println(end);

        String[] parts = start.split(" ");
        String startDate = parts[0];
        String startTime = parts[1];

        String[] endParts = end.split(" ");
        String endDate = endParts[0];
        String endTime = endParts[1];




        startDateTextField.setText(startDate);
        startTimeComboBox.setValue(startTime);

        endDateTextField.setText(endDate);
        endTimeComboBox.setValue(endTime);

        contactNameComboBox.setValue(appointments.getStringContact());


    }

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


    /*public static boolean isBetween(LocalTime localTime, LocalTime easternStart, LocalTime easternEnd) {
        return !localTime.isBefore(easternStart) && !localTime.isAfter(easternEnd);
    }*/

    /*private void getAvailableTimes(LocalDate enteredStartDate, LocalDate enteredEndDate) throws SQLException {

        ZonedDateTime localTime = LocalDateTime.now().atZone(zoneId);
        ZonedDateTime easternTime = localTime.toInstant().atZone(ZoneId.of("America/New_York"));

        LocalDateTime localDateTime = localTime.toLocalDateTime();
        Instant instant = localDateTime.toInstant(localTime.getOffset());
        Instant instantTruncated = instant.truncatedTo(ChronoUnit.MINUTES);
        System.out.println(instantTruncated);


        String currentTimeSplit = localTime.toString();
        String[] parts = currentTimeSplit.split("T");
        String currentDate = parts[0];
        String currentTime = parts[1];

        String[] parts1 = currentTime.split(":");
        currentTime = parts1[0] + ":00";


        LocalTime easternTimeConversion = easternTime.toLocalTime();
        LocalTime easternStart = LocalTime.of(8, 0);
        LocalTime easternEnd = LocalTime.of(22, 0);

        //String enteredDateSplit = enteredStartDate.toString();



        System.out.println(isBetween(easternTimeConversion, easternStart, easternEnd));


        int localOffset = localTime.getOffset().getTotalSeconds();
        int easternOffset = easternTime.getOffset().getTotalSeconds();

        System.out.println(easternOffset - localOffset);
        localOffset = Math.abs(localOffset);
        easternOffset = Math.abs(easternOffset);

        LocalTime localStart = processOffset(localOffset, easternOffset, true);
        LocalTime localEnd = processOffset(localOffset, easternOffset, false);

        if(localStart == null) {
            localStart = easternStart;
        }
        else if(localEnd == null) {
            localStart = easternEnd;
        }


        if(localStart.isBefore(easternStart)) {
            addTimesToComboBox(localStart, true);
        }
        else if(localStart.isAfter(easternStart)) {
            addTimesToComboBox(localStart, false);
        }


        if(currentDate.equals(enteredStartDate.toString())) {
            if(startTimeComboBox.getItems().contains(currentTime)) {
                int enteredTimeIndex = startTimeComboBox.getItems().indexOf(currentTime);
                int firstIndex = 0;
                while(startTimeComboBox.getItems().contains(currentTime)) {
                    if(enteredTimeIndex > firstIndex) {
                        System.out.println(startTimeComboBox.getItems().get(0));
                        startTimeComboBox.getItems().remove(0);
                    }
                }
            }
        }





        Connection connection = DBConnection.getConnection();

        String selectStatement = "SELECT Start, END from appointments";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

        ResultSet resultSet = preparedStatement.executeQuery();
        Timestamp start = null;
        Timestamp end = null;

        AddAppointmentScreen.startEndTimeSplitFromDatabase(enteredStartDate, enteredEndDate, resultSet, startTimeComboBox, endTimeComboBox);
    }*/

    public void getAvailableLocationTimes(LocalDate enteredStartDate, LocalDate enteredEndDate) throws SQLException {
        ZonedDateTime localTime = ZonedDateTime.now(zoneId);

        String currentTimeSplit = localTime.toString();
        String[] parts = currentTimeSplit.split("T");
        String currentDate = parts[0];
        String currentTime = parts[1];

        String[] parts1 = currentTime.split(":");
        currentTime = parts1[0] + ":00";


        if(currentDate.equals(enteredStartDate.toString())) {
            if(startTimeComboBox.getItems().contains(currentTime)) {
                int enteredTimeIndex = startTimeComboBox.getItems().indexOf(currentTime);
                int firstIndex = 0;
                while(startTimeComboBox.getItems().contains(currentTime)) {
                    if(enteredTimeIndex > firstIndex) {
                        System.out.println(startTimeComboBox.getItems().get(0));
                        startTimeComboBox.getItems().remove(0);
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


    }

    static void startEndTimeSplitFromDatabase(LocalDate enteredStartDate, LocalDate enteredEndDate, ResultSet resultSet, ComboBox<String> startTimeComboBox, ComboBox<String> endTimeComboBox) throws SQLException {
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
                    while(beginningIndex <= endIndex) {
                        endTimeComboBox.getItems().remove(beginningIndex);
                        endIndex = endTimeComboBox.getItems().indexOf(endTimeHour + ":00");
                    }

                    //endTimeComboBox.getItems().removeAll(endTimeHour + ":00", startTimeHour + ":00");
                }
            }
        }
    }


    public static LocalTime processOffset(int localOffset, int easternOffset, boolean isStart) {
        int localMinutes = localOffset / 60;
        int localHours = localMinutes / 60;

        System.out.println(localHours + " hours offset");

        int easternMinutes = easternOffset / 60;
        int easternHours = easternMinutes / 60;

        System.out.println(easternHours + " hours offset");

        int offsetDiff = easternHours - localHours;

        if(offsetDiff == 0) {
            System.out.println("Already in eastern timezone");
            return null;
        }
        else if(offsetDiff < 0) {
            if(isStart) {
                return LocalTime.of(8 + offsetDiff, 0);
            }
            else {
                return LocalTime.of(22 + offsetDiff, 0);
            }
        }
        else {
            if(isStart) {
                return LocalTime.of(8 - offsetDiff, 0);
            }
            else {
                return LocalTime.of(22 - offsetDiff, 0);
            }
        }
    }

    public void addTimesToComboBox(LocalTime time, boolean isBefore) {
        startTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");
        endTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");
        String stringTime = time.toString();
        if(isBefore) {
            if(stringTime.contains("07:00")) {
                startTimeComboBox.getItems().add(0, "07:00");
                startTimeComboBox.getItems().removeAll("22:00");
            }
        }
        else {

        }
    }

    public void addLocationTimesToComboBox(String location) {

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
            //startTimes.addAll("05:00", "06:00", "07:00");

            endTimes.add(0, "06:00");
            endTimes.add(1, "07:00");
            //endTimes.addAll("06:00", "07:00");

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
            endTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
        }
        else {
            System.out.println("Eastern Times already added");
        }
    }


    private void nextScreen(ActionEvent actionEvent, String screenName) throws IOException {
        Stage stage;
        Parent root;
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(screenName));

        root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void handleComboBoxSelection(ComboBox<String> startTimeComboBox, ComboBox<String> endTimeComboBox) {
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

    private boolean validAppointment(LocalDateTime start, LocalDateTime end) {
        int appointmentID = -1;
        String userName = User.getUserName();
        int userID = User.getUserID();
        int tempAppointmentID = appointments.getAppointmentID();

        try {
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
                        preparedStatement.setInt(6, appointmentID);
                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            return false;
                        }
                    }
                }
            }

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

        } catch (SQLException sqe) {
            System.out.println("SQL error while calling validAppointment");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("An unknown nonSQL error occurred");
            e.printStackTrace();
        }
        return true;
    }


}
