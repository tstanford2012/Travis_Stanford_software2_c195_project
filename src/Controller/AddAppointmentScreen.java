package Controller;

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
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddAppointmentScreen implements Initializable {

    private final ZoneId zoneId = ZoneId.systemDefault();

    @FXML
    Label startTimeErrorLabel;
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
    private final ObservableList<String> contactNames = FXCollections.observableArrayList();
    private final ObservableList<String> customerNames = FXCollections.observableArrayList();
    private static final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private static final ObservableList<String> endTimes = FXCollections.observableArrayList();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startTimeErrorLabel.setOpacity(0);
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


    public void startTimeComboBoxHandler() {
        handleComboBoxSelection(startTimeComboBox, endTimeComboBox);
    }


    public void endTimeComboBoxHandler() {
    }


    public void startTimesComboBoxClicked() {

        if(startDateTextField.getText().isEmpty()) {
            startTimeErrorLabel.setOpacity(1);
        }


    }

    public void startTimesButtonHandler() throws SQLException {
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
                        getAvailableTimes(enteredStartDate, enteredEndDate);
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

    public void endTimesComboBoxClicked() {

        if(endDateTextField.getText().isEmpty() || startDateTextField.getText().isEmpty()) {
            System.out.println("Please enter a start and end date and click the \"Check Times\" button to view times");
        }
    }

    public void locationComboBoxHandler() {
    }

    public void customerComboBoxHandler() {
    }



    public void saveButtonHandler() throws SQLException {
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
            String fullStart = startDateTextField.getText() + " " + startTimeComboBox.getValue() + ":00";
            String fullEnd = endDateTextField.getText() + " " + endTimeComboBox.getValue() + ":00";


            LocalDateTime fullStartLocal = LocalDateTime.parse(fullStart, dateTimeFormatter);
            LocalDateTime fullEndLocal = LocalDateTime.parse(fullEnd, dateTimeFormatter);

            System.out.println(fullStartLocal);
            System.out.println(fullEndLocal);


            Connection connection = DBConnection.getConnection();
            String selectStatement = "";
            String insertStatement = "INSERT into appointments(Title, Description, Location, Type, Start, End, Created_By, Customer_ID, User_ID, Contact_ID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            DBQuery.setPreparedStatement(connection, insertStatement);
            PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
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

    public void addTestApptButtonHandler() {
        titleTextField.setText("Title");
        descriptionTextField.setText("Description");
        locationComboBox.setValue("New York");
        typeTextField.setText("Appointment Type");

        long minutesToAdd = 15;
        long hoursToAdd = 1;

        ZonedDateTime startInstant = Instant.now().atZone(zoneId);
        ZonedDateTime startTimePlusFifteen = startInstant.plus(minutesToAdd, ChronoUnit.MINUTES);
        ZonedDateTime endInstant = startInstant.plus(hoursToAdd, ChronoUnit.HOURS);


        System.out.println(startInstant);
        System.out.println(startTimePlusFifteen);

        String startSplit = startTimePlusFifteen.toString();
        String[] parts = startSplit.split("T");
        String startDate = parts[0];
        String startTime = parts[1];

        String endSplit = endInstant.toString();
        String[] parts1 = endSplit.split("T");
        String endDate = parts1[0];
        String endTime = parts1[1];


        startDateTextField.setText(startDate);
        endDateTextField.setText(endDate);

        startTimeComboBox.setValue(startTime);
        endTimeComboBox.setValue(endTime);


    }

    public void revealTestButtonHandler() {
        addTestApptButton.setOpacity(1);
        addTestApptButton.setDisable(false);
        System.out.println("Test Button Revealed");
    }

    public void officeLocationTimeRadioBtnHandler() {
        timezoneRadioBtn.setSelected(false);
    }

    public void timezoneRadioBtnHandler() {
        officeLocationTimeRadioBtn.setSelected(false);
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

    private void getAvailableTimes(LocalDate enteredStartDate, LocalDate enteredEndDate) throws SQLException {
        boolean isEastern = false;

        ZonedDateTime localTime = ZonedDateTime.now(zoneId);
        ZonedDateTime easternTime = localTime.withZoneSameInstant(ZoneId.of("America/New_York"));

        ZoneId easternZone = ZoneId.of("America/New_York");


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



        System.out.println(isBetween(easternTimeConversion, easternStart, easternEnd));


        int localOffset = localTime.getOffset().getTotalSeconds();
        int easternOffset = easternTime.getOffset().getTotalSeconds();

        System.out.println(easternOffset - localOffset);

        LocalTime localStart = processOffset(localOffset, easternOffset, true);
        LocalTime localEnd = processOffset(localOffset, easternOffset, false);

        if(localStart == null) {
            isEastern = true;
            localStart = easternStart;
        }
        else if(localEnd == null) {
            isEastern =true;
            localStart = easternEnd;
        }


        if(localStart.isBefore(easternStart)) {
            addTimesToComboBox(localStart, true, isEastern);
        }
        else if(localStart.isAfter(easternStart)) {
            addTimesToComboBox(localStart, false, isEastern);
        }
        else {
            addTimesToComboBox(localStart, false, isEastern);
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

    public static boolean isBetween(LocalTime localTime, LocalTime easternStart, LocalTime easternEnd) {
        return !localTime.isBefore(easternStart) && !localTime.isAfter(easternEnd);
    }

    public static LocalTime processOffset(int localOffset, int easternOffset, boolean isStart) {
       int localMinutes = localOffset / 60;
       int localHours = localMinutes / 60;

       System.out.println(localHours + " hours offset");

       int easternMinutes = easternOffset / 60;
       int easternHours = easternMinutes / 60;

       System.out.println(easternHours + " hours offset");

       int offsetDiff;

        offsetDiff = localHours - easternHours;


        if(offsetDiff == 0) {
           System.out.println("Already in eastern timezone");
           return null;
       }
       /*else if(offsetDiff < 0) {
           if(isStart) {
               return LocalTime.of(8 + offsetDiff, 0);
           }
           else {
               return LocalTime.of(22 + offsetDiff, 0);
           }
       }*/
       else {
           if(isStart) {
               if(8 + offsetDiff > 24) {
                   int extraTime = (8 + offsetDiff) - 24;
                   return LocalTime.of(extraTime, 0);
               }
               return LocalTime.of(8 + offsetDiff, 0);
           }
           else {
               if(22 + offsetDiff > 24) {
                   return LocalTime.of(23, 59);
               }
               else {
                   return LocalTime.of(22 + offsetDiff, 0);
               }
           }
       }
    }

    public void addTimesToComboBox(LocalTime time, boolean isBefore, boolean isEastern) {
        startTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00");
        endTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");
        startTimes.addAll(startTimeComboBox.getItems());
        endTimes.addAll(endTimeComboBox.getItems());

        // TODO: 5/10/2021 Fix these times to match the dates


        if(!isEastern) {
            String stringTime = time.toString();
            if(isBefore) {
                if(stringTime.contains("07:00")) {
                    System.out.println("UTC -6");
                    startTimeComboBox.getItems().add(0, "07:00");
                    startTimeComboBox.getItems().removeAll("22:00");
                    startTimes.removeAll("22:00");
                }
                else if(stringTime.contains("06:00")) {
                    System.out.println("UTC -7");
                    startTimeComboBox.getItems().add(0, "06:00");
                    startTimeComboBox.getItems().add(1, "07:00");
                    startTimeComboBox.getItems().removeAll("21:00", "22:00");
                    startTimes.removeAll("21:00", "22:00");
                }
                else if(stringTime.contains("05:00")) {
                    System.out.println("UTC -8");
                    startTimeComboBox.getItems().add(0, "05:00");
                    startTimeComboBox.getItems().add(1, "06:00");
                    startTimeComboBox.getItems().add(2, "07:00");
                    startTimeComboBox.getItems().removeAll("20:00", "21:00", "22:00");
                    startTimes.removeAll("20:00", "21:00", "22:00");
                }
                else if(stringTime.contains("04:00")) {
                    System.out.println("UTC -9");
                    startTimeComboBox.getItems().add(0, "04:00");
                    startTimeComboBox.getItems().add(1, "05:00");
                    startTimeComboBox.getItems().add(2, "06:00");
                    startTimeComboBox.getItems().add(3, "07:00");
                    startTimeComboBox.getItems().removeAll("19:00", "20:00", "21:00", "22:00");
                    startTimes.removeAll("19:00", "20:00", "21:00", "22:00");
                }
                else if(stringTime.contains("03:00")) {
                    System.out.println("UTC -10");
                    startTimeComboBox.getItems().add(0, "03:00");
                    startTimeComboBox.getItems().add(1, "04:00");
                    startTimeComboBox.getItems().add(2, "05:00");
                    startTimeComboBox.getItems().add(3, "06:00");
                    startTimeComboBox.getItems().add(4, "07:00");
                    startTimeComboBox.getItems().removeAll("18:00", "19:00", "20:00", "21:00", "22:00");
                    startTimes.removeAll("18:00", "19:00", "20:00", "21:00", "22:00");
                }
                else if(stringTime.contains("02:00")) {
                    System.out.println("UTC -11");
                    startTimeComboBox.getItems().add(0, "02:00");
                    startTimeComboBox.getItems().add(1, "03:00");
                    startTimeComboBox.getItems().add(2, "04:00");
                    startTimeComboBox.getItems().add(3, "05:00");
                    startTimeComboBox.getItems().add(4, "06:00");
                    startTimeComboBox.getItems().add(5, "07:00");
                    startTimeComboBox.getItems().removeAll("17:00", "18:00", "19:00", "20:00", "21:00", "22:00");
                    startTimes.removeAll("17:00", "18:00", "19:00", "20:00", "21:00", "22:00");
                }
                else if(stringTime.contains("01:00")) {
                    System.out.println("UTC -12");
                    startTimeComboBox.getItems().add(0, "01:00");
                    startTimeComboBox.getItems().add(1, "02:00");
                    startTimeComboBox.getItems().add(2, "03:00");
                    startTimeComboBox.getItems().add(3, "04:00");
                    startTimeComboBox.getItems().add(4, "05:00");
                    startTimeComboBox.getItems().add(5, "06:00");
                    startTimeComboBox.getItems().add(6, "07:00");
                    startTimeComboBox.getItems().removeAll("16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");
                    startTimes.removeAll("16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");
                }
            }
            else {
                startTimeComboBox.getItems().add("23:00");

                if(stringTime.contains("09:00")) {
                    System.out.println("UTC -4");
                    startTimeComboBox.getItems().removeAll("08:00");
                    startTimes.removeAll("08:00");
                }
                else if(stringTime.contains("10:00")) {
                    System.out.println("UTC -3");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00");
                    startTimes.removeAll("08:00", "09:00");

                }
                else if(stringTime.contains("11:00")) {
                    System.out.println("UTC -2");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00");
                    startTimes.removeAll("08:00", "09:00", "10:00");
                }
                else if(stringTime.contains("12:00")) {
                    System.out.println("UTC -1");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00");
                }
                else if(stringTime.contains("13:00")) {
                    System.out.println("UTC Time");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
                }
                else if(stringTime.contains("14:00")) {
                    System.out.println("UTC +1");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00");
                }
                else if(stringTime.contains("15:00")) {
                    System.out.println("UTC +2");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00");
                }
                else if(stringTime.contains("16:00")) {
                    System.out.println("UTC +3");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00");
                }
                else if(stringTime.contains("17:00")) {
                    System.out.println("UTC +4");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
                }
                else if(stringTime.contains("18:00")) {
                    System.out.println("UTC +5");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
                }
                else if(stringTime.contains("19:00")) {
                    System.out.println("UTC +6");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
                }
                else if(stringTime.contains("20:00")) {
                    System.out.println("UTC +7");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00");
                }
                else if(stringTime.contains("21:00")) {
                    System.out.println("UTC +8");
                    startTimeComboBox.getItems().removeAll( "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00");
                }
                else if(stringTime.contains("22:00")) {
                    System.out.println("UTC +9");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00");
                    startTimes.removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00");
                }
            }
        }
    }

    public void addLocationTimesToComboBox(String location) {

        startTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00");
        endTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
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
}
