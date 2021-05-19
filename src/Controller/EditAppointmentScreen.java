package Controller;

import Model.Appointments;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    private ObservableList<String> contactNames = FXCollections.observableArrayList();
    private ObservableList<String> customerNames = FXCollections.observableArrayList();
    private final ZoneId zoneId = ZoneId.systemDefault();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startTimeErrorLabel.setOpacity(0);
        endTimeErrorLabel.setOpacity(0);
        appointmentIDLabel.setText("");
        officeLocationTimeRadioBtn.setSelected(true);
        timezoneRadioBtn.setSelected(false);
        try {
            pullContactNames();
            pullCustomers();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        locationComboBox.getItems().addAll("Phoenix", "White Plains", "Montreal", "London");
    }


    public void startTimesComboBoxClicked(MouseEvent mouseEvent) {
        if(startDateTextField.getText().isEmpty()) {
            startTimeErrorLabel.setOpacity(1);
        }
    }

    public void startTimeComboBoxHandler(ActionEvent actionEvent) {
        AddAppointmentScreen.handleComboBoxSelection(startTimeComboBox, endTimeComboBox);


        System.out.println(startTimeComboBox.getValue());

    }


    public void endTimeComboBoxHandler(ActionEvent actionEvent) {
        System.out.println(endTimeComboBox.getValue());
    }

    public void locationComboBoxHandler(ActionEvent actionEvent) {
    }

    public void customerComboBoxHandler(ActionEvent actionEvent) {
    }

    public void startTimesButtonHandler(ActionEvent actionEvent) throws SQLException {
        if(startDateTextField.getText().isEmpty()) {
            System.out.println("Must enter a date");
        }
        else {
            startTimeErrorLabel.setOpacity(0);
            String enteredStartDateString = startDateTextField.getText();
            endDateTextField.setText(enteredStartDateString);
            String enteredEndDateString = endDateTextField.getText();
            startTimeComboBox.getItems().clear();

            //Prevents the user from attempting to view location times with no location selected
            if(locationComboBox.getValue().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Location not selected");
                alert.setContentText("Please select a location and try again");
                alert.show();
            }
            else {
                String location = locationComboBox.getValue();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate enteredStartDate = LocalDate.parse(enteredStartDateString, formatter);
                LocalDate enteredEndDate = LocalDate.parse(enteredEndDateString, formatter);


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
                        //AddAppointmentScreen.addLocationTimesToComboBox(location);
                    }
                }
                else {
                    getAvailableTimes(enteredStartDate, enteredEndDate);
                }
            }
        }
    }

    public void endTimesButtonHandler(ActionEvent actionEvent) throws SQLException {
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
               getAvailableTimes(enteredStartDate, enteredEndDate);
           }
       }
    }

    public void endTimesComboBoxClicked(MouseEvent mouseEvent) {
        if(endDateTextField.getText().isEmpty()) {
            endTimeErrorLabel.setOpacity(1);
        }
        else {

        }
    }

    public void saveButtonHandler(ActionEvent actionEvent) {
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

    public void officeLocationTimeRadioBtnHandler(ActionEvent actionEvent) {
        timezoneRadioBtn.setSelected(false);
    }

    public void timezoneRadioBtnHandler(ActionEvent actionEvent) {
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
        String contactName = null;

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
        String customerName = null;

        while (resultSet.next()) {
            customerName = resultSet.getString("Customer_Name");
            customerNames.add(customerName);
            customerComboBox.getItems().add(customerName);
        }
    }


    public static boolean isBetween(LocalTime localTime, LocalTime easternStart, LocalTime easternEnd) {
        return !localTime.isBefore(easternStart) && !localTime.isAfter(easternEnd);
    }

    private void getAvailableTimes(LocalDate enteredStartDate, LocalDate enteredEndDate) throws SQLException {

        ZonedDateTime localTime = LocalDateTime.now().atZone(zoneId);
        ZonedDateTime easternTime = localTime.toInstant().atZone(ZoneId.of("America/New_York"));
        ZonedDateTime timeInUTC = localTime.toInstant().atZone(ZoneId.of("UTC"));

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


        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        //LocalTime localTimeConversion = LocalTime.parse(currentTime, dateTimeFormatter);

        LocalTime easternTimeConversion = easternTime.toLocalTime();
        LocalTime easternStart = LocalTime.of(8, 0);
        LocalTime easternEnd = LocalTime.of(22, 0);

        String enteredDateSplit = enteredStartDate.toString();
        //String[] parts1 = enteredDateSplit.split("T");
        //String splitEnteredDate = parts1[0];
        //String splitEnteredTime = parts1[1];



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
}
