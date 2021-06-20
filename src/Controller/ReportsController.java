package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class has several buttons that allow the user to navigate to another screen depending on which report they would like to view.
 */
public class ReportsController {

    /**
     *
     * @param actionEvent uses the action event to go back to the main screen.
     * @throws IOException
     * Handler for the back button.
     * Goes back to the main screen when the button is pressed.
     */
    public void backButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/mainScreen.fxml");
    }

    /**
     *
     * @param actionEvent uses the action event to go to the total appointments screen.
     * @throws IOException
     * Handler for the total appointments button.
     * Goes to the total appointments screen when the button is pressed.
     */
    public void totalApptButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/totalAppointments.fxml");

    }

    /**
     *
     * @param actionEvent uses the action event to go to the contact schedule screen.
     * @throws IOException
     * Handler for the contact schedule button.
     * Goes to the contact schedule screen when the button is pressed.
     */
    public void contactScheduleBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/contactSchedule.fxml");
    }

    /**
     *
     * @param actionEvent takes the action event that is passed to go to the specified screen.
     * @param screenName Takes a fxml string and goes to the corresponding screen when called.
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
