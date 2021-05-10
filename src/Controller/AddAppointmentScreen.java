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
import javafx.scene.input.MouseEvent;

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
import java.time.zone.ZoneRules;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddAppointmentScreen implements Initializable {

    private final ZoneId zoneId = ZoneId.systemDefault();

    @FXML
    Label startTimeErrorLabel;
    @FXML
    Label endTimeErrorLabel;
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
    Button startTimesButton;
    @FXML
    Button endTimesButton;
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;
    @FXML
    Button addTestApptButton;
    @FXML
    Button revealTestButton;
    private ObservableList<String> contactNames = FXCollections.observableArrayList();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startTimeErrorLabel.setOpacity(0);
        endTimeErrorLabel.setOpacity(0);

        try {
            pullContactNames();

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }

    }



    public void startTimesComboBoxClicked(MouseEvent mouseEvent) {

        if(startDateTextField.getText().isEmpty()) {
            startTimeErrorLabel.setOpacity(1);
        }


    }

    public void startTimesButtonHandler(ActionEvent actionEvent) throws SQLException, ParseException {
        if(startDateTextField.getText().isEmpty()) {
            System.out.println("Must enter a date");
        }
        else {
            startTimeErrorLabel.setOpacity(0);
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

    public void endTimesButtonHandler(ActionEvent actionEvent) {

        if(!endDateTextField.getText().isEmpty()) {
            endTimeErrorLabel.setOpacity(0);
        }

    }

    public void endTimesComboBoxClicked(MouseEvent mouseEvent) {

        if(endDateTextField.getText().isEmpty()) {
            endTimeErrorLabel.setOpacity(1);
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

    public void addTestApptButtonHandler(ActionEvent actionEvent) {
        titleTextField.setText("Title");
        descriptionTextField.setText("Description");
        locationTextField.setText("Location");
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

    public void revealTestButtonHandler(ActionEvent actionEvent) {
        addTestApptButton.setOpacity(1);
        addTestApptButton.setDisable(false);
        System.out.println("Test Button Revealed");
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

    private void getAvailableTimes(LocalDate enteredStartDate, LocalDate enteredEndDate) throws SQLException {
        boolean isEastern;

        ZonedDateTime localTime = ZonedDateTime.now(zoneId);
        ZonedDateTime easternTime = localTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime timeInUTC = localTime.withZoneSameInstant((ZoneId.of("UTC")));
        ZoneRules zoneRules = zoneId.getRules();

        ZoneId easternZone = ZoneId.of("America/New_York");
        ZoneRules easternRules = easternZone.getRules();


        boolean isLocalDaylightSavings = zoneRules.isDaylightSavings(localTime.toInstant());
        boolean isEasternDaylightSavings = easternRules.isDaylightSavings(easternTime.toInstant());
        //System.out.println(isLocalDaylightSavings);

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
        //localOffset = Math.abs(localOffset);
        //easternOffset = Math.abs(easternOffset);

        LocalTime localStart = processOffset(localOffset, easternOffset, true, isLocalDaylightSavings, isEasternDaylightSavings);
        LocalTime localEnd = processOffset(localOffset, easternOffset, false, isLocalDaylightSavings, isEasternDaylightSavings);

        if(localStart == null) {
            isEastern = true;
            localStart = easternStart;
        }
        else if(localEnd == null) {
            isEastern =true;
            localStart = easternEnd;
        }


        if(localStart.isBefore(easternStart)) {
            addTimesToComboBox(localStart, true, false);
        }
        else if(localStart.isAfter(easternStart)) {
            addTimesToComboBox(localStart, false, false);
        }
        else {
            addTimesToComboBox(localStart, false, true);
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

    public static boolean isBetween(LocalTime localTime, LocalTime easternStart, LocalTime easternEnd) {
        return !localTime.isBefore(easternStart) && !localTime.isAfter(easternEnd);
    }

    public static LocalTime processOffset(int localOffset, int easternOffset, boolean isStart, boolean isLocalDaylightSavings, boolean isEasternDaylightSavings) {
       int localMinutes = localOffset / 60;
       int localHours = localMinutes / 60;
       /*if(isLocalDaylightSavings) {
           localHours -= 1;
       }*/

       System.out.println(localHours + " hours offset");

       int easternMinutes = easternOffset / 60;
       int easternHours = easternMinutes / 60;
       /*if(isEasternDaylightSavings) {
           easternHours -= 1;
       }*/

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
                   int extraTime = (22 + offsetDiff) - 24;
                   return LocalTime.of(extraTime, 0);
               }
               else {
                   return LocalTime.of(22 + offsetDiff, 0);
               }
           }
       }
    }

    public void addTimesToComboBox(LocalTime time, boolean isBefore, boolean isEastern) {
        startTimeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");

        // TODO: 5/10/2021 Fix these times to match the dates


        if(!isEastern) {
            String stringTime = time.toString();
            if(isBefore) {
                if(stringTime.contains("07:00")) {
                    System.out.println("UTC -6");
                    startTimeComboBox.getItems().add(0, "07:00");
                    startTimeComboBox.getItems().removeAll("22:00");
                }
                else if(stringTime.contains("06:00")) {
                    System.out.println("UTC -7");
                    startTimeComboBox.getItems().add(0, "06:00");
                    startTimeComboBox.getItems().add(1, "07:00");
                    startTimeComboBox.getItems().removeAll("21:00", "22:00");
                }
                else if(stringTime.contains("05:00")) {
                    System.out.println("UTC -8");
                    startTimeComboBox.getItems().add(0, "05:00");
                    startTimeComboBox.getItems().add(1, "06:00");
                    startTimeComboBox.getItems().add(2, "07:00");
                    startTimeComboBox.getItems().removeAll("20:00", "21:00", "22:00");
                }
                else if(stringTime.contains("04:00")) {
                    System.out.println("UTC -9");
                    startTimeComboBox.getItems().add(0, "04:00");
                    startTimeComboBox.getItems().add(1, "05:00");
                    startTimeComboBox.getItems().add(2, "06:00");
                    startTimeComboBox.getItems().add(3, "07:00");
                    startTimeComboBox.getItems().removeAll("19:00", "20:00", "21:00", "22:00");
                }
                else if(stringTime.contains("03:00")) {
                    System.out.println("UTC -10");
                    startTimeComboBox.getItems().add(0, "03:00");
                    startTimeComboBox.getItems().add(1, "04:00");
                    startTimeComboBox.getItems().add(2, "05:00");
                    startTimeComboBox.getItems().add(3, "06:00");
                    startTimeComboBox.getItems().add(4, "07:00");
                    startTimeComboBox.getItems().removeAll("18:00", "19:00", "20:00", "21:00", "22:00");
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
                }
            }
            else {
                if(stringTime.contains("09:00")) {
                    System.out.println("UTC -4");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().removeAll("08:00");
                }
                else if(stringTime.contains("10:00")) {
                    System.out.println("UTC -3");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00");

                }
                else if(stringTime.contains("11:00")) {
                    System.out.println("UTC -2");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00");
                }
                else if(stringTime.contains("12:00")) {
                    System.out.println("UTC -1");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00");
                }
                else if(stringTime.contains("13:00")) {
                    System.out.println("UTC Time");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00");
                }
                else if(stringTime.contains("14:00")) {
                    System.out.println("UTC +1");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().add("04:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00");
                }
                else if(stringTime.contains("15:00")) {
                    System.out.println("UTC +2");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().add("04:00");
                    startTimeComboBox.getItems().add("05:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00");
                }
                else if(stringTime.contains("16:00")) {
                    System.out.println("UTC +3");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().add("04:00");
                    startTimeComboBox.getItems().add("05:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00");
                }
                else if(stringTime.contains("17:00")) {
                    System.out.println("UTC +4");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().add("04:00");
                    startTimeComboBox.getItems().add("05:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
                }
                else if(stringTime.contains("18:00")) {
                    System.out.println("UTC +5");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().add("04:00");
                    startTimeComboBox.getItems().add("05:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("07:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
                }
                else if(stringTime.contains("19:00")) {
                    System.out.println("UTC +6");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().add("04:00");
                    startTimeComboBox.getItems().add("05:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("07:00");
                    startTimeComboBox.getItems().add("08:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
                }
                else if(stringTime.contains("20:00")) {
                    System.out.println("UTC +7");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().add("04:00");
                    startTimeComboBox.getItems().add("05:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("07:00");
                    startTimeComboBox.getItems().add("08:00");
                    startTimeComboBox.getItems().add("09:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00");
                }
                else if(stringTime.contains("21:00")) {
                    System.out.println("UTC +8");
                    startTimeComboBox.getItems().add(0, "23:00");
                    startTimeComboBox.getItems().add(1, "00:00");
                    startTimeComboBox.getItems().add(2, "01:00");
                    startTimeComboBox.getItems().add(3, "02:00");
                    startTimeComboBox.getItems().add(4, "03:00");
                    startTimeComboBox.getItems().add(5, "04:00");
                    startTimeComboBox.getItems().add(6, "05:00");
                    startTimeComboBox.getItems().add(7, "06:00");
                    startTimeComboBox.getItems().add(8, "06:00");
                    startTimeComboBox.getItems().add(9, "07:00");
                    //startTimeComboBox.getItems().add(10, "08:00");
                    //startTimeComboBox.getItems().add(11, "09:00");
                    //startTimeComboBox.getItems().add(12, "10:00");
                    startTimeComboBox.getItems().removeAll( "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00");
                }
                else if(stringTime.contains("22:00")) {
                    System.out.println("UTC +9");
                    startTimeComboBox.getItems().add("23:00");
                    startTimeComboBox.getItems().add("00:00");
                    startTimeComboBox.getItems().add("01:00");
                    startTimeComboBox.getItems().add("02:00");
                    startTimeComboBox.getItems().add("03:00");
                    startTimeComboBox.getItems().add("04:00");
                    startTimeComboBox.getItems().add("05:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("06:00");
                    startTimeComboBox.getItems().add("07:00");
                    startTimeComboBox.getItems().add("08:00");
                    startTimeComboBox.getItems().add("09:00");
                    startTimeComboBox.getItems().add("10:00");
                    startTimeComboBox.getItems().add("11:00");
                    startTimeComboBox.getItems().removeAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00");
                }
            }
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
