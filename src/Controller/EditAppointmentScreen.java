package Controller;

import Model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import utils.DBConnection;
import utils.DBQuery;

import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class EditAppointmentScreen implements Initializable {
    Appointments appointments;
    @FXML
    TextField titleTextField;
    @FXML
    TextField descriptionTextField;
    @FXML
    TextField locationTextField;
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
    Label startTimeErrorLabel;
    @FXML
    Label endTimeErrorLabel;
    @FXML
    Label appointmentIDLabel;
    private ObservableList<String> contactNames = FXCollections.observableArrayList();
    private final ZoneId zoneId = ZoneId.systemDefault();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startTimeErrorLabel.setOpacity(0);
        endTimeErrorLabel.setOpacity(0);
        appointmentIDLabel.setText("");
        try {
            pullContactNames();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


    public void startTimesComboBoxClicked(MouseEvent mouseEvent) {
        if(startDateTextField.getText().isEmpty()) {
            startTimeErrorLabel.setOpacity(1);
        }
    }

    public void startTimesButtonHandler(ActionEvent actionEvent) throws SQLException {
        if(startDateTextField.getText().isEmpty()) {
            System.out.println("Must enter a date");
        }
        else {
            startTimeErrorLabel.setOpacity(0);
            startTimeComboBox.setValue(null);
            String enteredStartDateString = startDateTextField.getText();
            endDateTextField.setText(enteredStartDateString);
            String enteredEndDateString = endDateTextField.getText();
            startTimeComboBox.getItems().clear();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate enteredStartDate = LocalDate.parse(enteredStartDateString, formatter);
            LocalDate enteredEndDate = LocalDate.parse(enteredEndDateString, formatter);
            getAvailableTimes(enteredStartDate, enteredEndDate);
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
           getAvailableTimes(enteredStartDate, enteredEndDate);

       }
    }

    public void endTimesComboBoxClicked(MouseEvent mouseEvent) {
        if(endDateTextField.getText().isEmpty()) {
            endTimeErrorLabel.setOpacity(1);
        }
    }

    public void saveButtonHandler(ActionEvent actionEvent) {
    }

    public void cancelButtonHandler(ActionEvent actionEvent) {
    }

    public void setAppointment(Appointments appointments) {
        this.appointments = appointments;

        appointmentIDLabel.setText(Integer.toString(appointments.getAppointmentID()));
        titleTextField.setText(appointments.getTitle());
        descriptionTextField.setText(appointments.getDescription());
        locationTextField.setText(appointments.getLocation());
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
            String startTimeHour = startTimeSplit[0];

            if(startDate.equals(enteredStartDate.toString())) {
                if(startTimeComboBox.getItems().contains(startTimeHour + ":00")) {
                    startTimeComboBox.getItems().removeAll(startTimeHour + ":00");
                }
            }
            else if(endDate.equals(enteredEndDate.toString())) {
                endTimeComboBox.getItems().removeAll(endTime);
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
}
