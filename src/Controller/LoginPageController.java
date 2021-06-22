package Controller;

import Model.Appointments;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * This class checks entered credentials to log into the application.
 * The labels and error messages will be translated into either english or french depending on the users system language.
 * This class also alerts the user of an upcoming appointment if it is scheduled in the next 15 min from the users local time.
 */
public class LoginPageController implements Initializable {
    @FXML
    Label usernameLabel;
    @FXML
    Label passwordLabel;
    @FXML
    Label userLocationLabel;
    @FXML
    TextField usernameTextField;
    @FXML
    TextField passwordTextField;
    @FXML
    Button loginButton;
    @FXML
    Label loginFailLabel;
    private ZoneId zoneID = ZoneId.systemDefault();
    private static final String fileName = "ZoneCode/login_page";
    ObservableList<Appointments> appointmentsAlertList = FXCollections.observableArrayList();
    private DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");
    ResourceBundle resourceBundle = ResourceBundle.getBundle(fileName, Locale.getDefault());

    /**
     *
     * @param url
     * @param rb
     * Sets the labels on the login page to the system language.
     * Displays the time zone of the user on the login page.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            //sets the labels on the login page to the language of the users system
            usernameLabel.setText(resourceBundle.getString("username"));
            usernameTextField.setPromptText(resourceBundle.getString("username"));
            passwordLabel.setText(resourceBundle.getString("password"));
            passwordTextField.setPromptText(resourceBundle.getString("password"));
            loginButton.setText(resourceBundle.getString("login"));

            //displays the time zone of the user on the login page
            userLocationLabel.setText("Location ID: " + zoneID);

            //sets the initial login fail label to be empty
            loginFailLabel.setText("");
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace(); // to be removed later

        }

    }

    /**
     *
     * @param actionEvent uses the action event to go to the main screen after logging in.
     * Handler for the log in button.
     * Validates the username and password and creates a new User object based on the userID.
     * Goes to the application if the username and password is correct.
     */
    public void loginButtonHandler(ActionEvent actionEvent) {

        String tempUsername = usernameTextField.getText();
        String tempPassword = passwordTextField.getText();
        try {
            //calls the getUserID method and sets the value of userID
            int userID = getUserID(tempUsername);
            User user = new User(userID, tempUsername);
            if(tempUsername.isEmpty() || tempPassword.isEmpty()) {
                System.out.println("Enter text");
                loginFailLabel.setText(resourceBundle.getString("empty"));
            }
            else {
                //validation for the username and password
                if (validatePassword(userID, tempPassword)) {
                    loginFailLabel.setText(" ");

                    //calls the methods for the appointment reminders
                    addRemindersToList();
                    appointmentIn15MinAlert();
                    //calls the loginActivity method to add a record to the text file
                    loginActivity(true, tempUsername);
                    System.out.println("Log in successful!");

                    Stage stage;
                    Parent root;
                    stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/mainScreen.fxml"));

                    root = loader.load();

                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    //prints an error label in the users system language if authentication failed
                    loginActivity(false, tempUsername);
                    loginFailLabel.setText(resourceBundle.getString("login_Fail"));
                }
            }
        } catch (Exception exception) {
            System.out.println("Exception thrown");
            System.out.println(exception.getMessage());
            exception.printStackTrace(); //to be removed later
        }

    }

    /**
     * Filters the appointments and displays an alert if there is an appointment in the next 15 min of the users local time.
     */
    private void appointmentIn15MinAlert() {
        System.out.println("Starting appointment alert......");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime fifteenMinFromNow = currentTime.plusMinutes(15);
        System.out.println("Current time is: " + currentTime);
        System.out.println("Fifteen minutes from the current time is: " + fifteenMinFromNow);

        FilteredList<Appointments> filteredList = new FilteredList<>(appointmentsAlertList);

        //Lambda expression used to reduce the code required to filter the appointments
        filteredList.setPredicate(row -> {
            LocalDateTime date = LocalDateTime.parse(row.getStartZonedDateTime().toString().substring(0, 16), formatDateTime);
            return date.isAfter(currentTime.minusMinutes(1)) && date.isBefore(fifteenMinFromNow);
        });
        //displays an alert to the user that there are no upcoming appointments
        if(filteredList.isEmpty()) {
            Alert noApptAlert = new Alert(Alert.AlertType.INFORMATION);
            noApptAlert.setContentText("No immediate appointments found");
            noApptAlert.show();
            System.out.println("No upcoming appointments");
        }
        else {
            //gets the data and displays an alert for the user when an appointment is in 15 min of the users local time
            String description = filteredList.get(0).getDescription();
            String customerName = filteredList.get(0).getAppointmentCustomerName();
            String type = filteredList.get(0).getType();

            String start = filteredList.get(0).getStartZonedDateTime().toString().substring(0, 16);


            String[] parts = start.split("T");
            String splitDate = parts[0];
            String splitTime = parts[1];
            Date tempDate = null;
            String tempTime = null;
                System.out.println(splitTime);


            Alert appointmentAlert = new Alert(Alert.AlertType.INFORMATION);
            appointmentAlert.setTitle("Upcoming appointment");
            appointmentAlert.setHeaderText("You have an appointment within the next 15 min!");
            appointmentAlert.setContentText("Your appointment with " + customerName + " will start at " + splitTime + "\nDetails: " + type + " " + "\nDescription: " + description);
            appointmentAlert.showAndWait();
        }
        System.out.println("Ending appointment alert.........");
    }

    /**
     *
     * @throws SQLException
     * Gets the appointments from the database and adds them to the observable list.
     */
    private void addRemindersToList() throws SQLException {
        System.out.println("Username: " + User.getUserName());
        try {
            Connection connection = DBConnection.getConnection();
            DBQuery.setPreparedStatement(connection, "SELECT appointments.Appointment_ID, appointments.Customer_ID, appointments.Title, appointments.Description, " +
                    "appointments.Start, appointments.Type, appointments.End, customers.Customer_ID, customers.Customer_Name FROM appointments, customers" +
                    " WHERE appointments.Customer_ID = customers.Customer_ID ORDER BY Start");
            PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {


                Timestamp startTimestamp = resultSet.getTimestamp("Start");
                ZonedDateTime startTimeInUTC = startTimestamp.toInstant().atZone(ZoneId.of("UTC"));
                ZonedDateTime localStartTime = startTimeInUTC.withZoneSameInstant(zoneID);

                Timestamp endTimestamp = resultSet.getTimestamp("End");
                ZonedDateTime endTimeInUTC = endTimestamp.toInstant().atZone(ZoneId.of("UTC"));
                ZonedDateTime localEndTime = endTimeInUTC.withZoneSameInstant(zoneID);


                int appointmentID = resultSet.getInt("Appointment_ID");
                int customerID = resultSet.getInt("Customer_ID");
                String type = resultSet.getString("Type");
                String description = resultSet.getString("Description");
                String customerName = resultSet.getString("Customer_Name");

                appointmentsAlertList.add(new Appointments(appointmentID, type, description, localStartTime, localEndTime, customerID, customerName));


            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param userID takes the user ID to query the database for the correct password.
     * @param password takes the entered password that is passed to perform the validation.
     * @return
     * @throws SQLException
     *
     * Validates the entered password by checking it against the password stored in the database.
     */
    private boolean validatePassword(int userID, String password) throws SQLException {
        Connection connection = DBConnection.getConnection();
        try {
            //sql statement to be executed
            String passwordStatement = "SELECT Password FROM users WHERE User_ID = ?";

            //creates the prepared statement and sets the parameters
            DBQuery.setPreparedStatement(connection, passwordStatement);

            //gets the statement that was created
            PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

            //replaces the ? in the statement with the specified index and value
            preparedStatement.setInt(1, userID);

            //creates a result set from the sql statement
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                //returns true if the password passed validation
                if(resultSet.getString("Password").equals(password)) {
                    return true;
                }
            }
        } catch (SQLException exception) {
            exception.getMessage();
            exception.printStackTrace(); // to be removed later
        }

        return false;
    }

    /**
     *
     * @param username takes the username that was entered and gets the User ID.
     * @return returns the User ID associated with the name.
     * @throws SQLException
     *
     * Gets the userID from the entered username.
     */
    private int getUserID(String username) throws SQLException {
        Connection connection = DBConnection.getConnection();
        int userID = -1;
        try {

            //sql statement to be executed
            String userNameStatement = "SELECT USER_ID FROM users WHERE User_Name = ?";
            //creates statement
            DBQuery.setPreparedStatement(connection, userNameStatement);

            //gets statement
            PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

            //replaces the ? in the statement with the specified index and value
            preparedStatement.setString(1, username);

            //creates a result set from the executed sql statement
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                //gets user id
                userID = resultSet.getInt("User_ID");
            }
        } catch (SQLException exception) {
            exception.getMessage();
            exception.printStackTrace(); // to be removed later
        }
        return userID;
    }

    /**
     * @param successful takes the successful boolean to determine which version to write to the file.
     * @throws IOException
     *
     * Adds a log in attempt to the login_activity text file whether the attempt was successful of not.
     */
    private void loginActivity(boolean successful, String userName) throws IOException {
        String fileName = "login_activity.txt";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));
        if(successful) {
            bufferedWriter.append(String.valueOf(LocalDateTime.now())).append(" ").append("Username: ").append(userName).append(" successful login attempt.\n");
        }
        else {
            bufferedWriter.append(String.valueOf(LocalDateTime.now())).append(" ").append("Username: ").append(userName).append(" login fail.\n");
        }

        System.out.println("Login added to " + fileName);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

}
