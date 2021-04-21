package Controller;

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
import java.net.URL;
import java.sql.*;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;


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
    ResourceBundle resourceBundle = ResourceBundle.getBundle(fileName, Locale.getDefault());

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
            exception.printStackTrace(); // to be removed later
            exception.getMessage();

        }

    }

    public void loginButtonHandler(ActionEvent actionEvent) throws SQLException {
        String tempUsername = usernameTextField.getText();
        String tempPassword = passwordTextField.getText();
        try {
            //calls the getUserID method and sets the value of userID
            int userID = getUserID(tempUsername);
            //Parent root;
            //Stage stage;
            if(tempUsername.isEmpty() || tempPassword.isEmpty()) {
                System.out.println("Enter text");
                loginFailLabel.setText(resourceBundle.getString("empty"));
            }
            else {
                //validation for the username and password
                if (validPassword(userID, tempPassword)) {
                    //add code to update user info
                    System.out.println("Log in successful!");
                    loginFailLabel.setText(" ");

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
                    loginFailLabel.setText(resourceBundle.getString("login_Fail"));
                }
            }
        } catch (Exception exception) {
            exception.getMessage();
            exception.printStackTrace(); //to be removed later
        }

    }

    //validates the entered password
    private boolean validPassword(int userID, String password) throws SQLException {
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

    //gets the userID from the entered username
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

}
