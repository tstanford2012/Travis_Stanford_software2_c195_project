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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class has several buttons used to navigate to the main portions of the application.
 */
public class MainScreenController implements Initializable {
    @FXML
    Label welcomeLabel;
    @FXML
    Button appointmentsButton;
    @FXML
    Button customersButton;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     *
     * @param actionEvent uses the action event to go to the appointments screen.
     * @throws IOException
     *
     * Handler for the appointments button.
     * Goes to the appointments screen when the button is pressed.
     */
    public void appointmentsButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/appointments.fxml");
    }

    /**
     *
     * @param actionEvent uses the action event to go to the customers screen.
     * @throws IOException
     * Handler for the customers button.
     * Goes to the customers screen when the button is pressed.
     */
    public void customersButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/customers.fxml");
    }

    /**
     *
     * @param actionEvent uses the action event to go to the reports screen.
     * @throws IOException
     * Handler for the reports button.
     * Goes to the reports screen when the button is pressed.
     */
    public void reportsButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/reports.fxml");
    }

    /**
     * Handler for the exit button.
     * Exits the application after confirmation.
     */
    public void exitButtonHandler() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Exit Confirmation");
        alert.setContentText("Are you sure you want to exit the program?");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK) {
            System.exit(0);
        }
        else {
            System.out.println("No longer exiting.");
        }
    }

    /**
     *
     * @param actionEvent takes the action event that is passed to go to the specified screen.
     * @param screenName Takes a fxml string and goes to the corresponding screen.
     * @throws IOException
     *
     */
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
