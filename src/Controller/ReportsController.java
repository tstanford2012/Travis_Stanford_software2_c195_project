package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReportsController {

    /**
     *
     * @param actionEvent
     * @throws IOException
     * -handler for the back button
     * -goes back to the main screen when the button is pressed
     */
    public void backButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/mainScreen.fxml");
    }

    /**
     *
     * @param actionEvent
     * @throws IOException
     * -handler for the total appointments button
     * -goes to the total appointments screen when the button is pressed
     */
    public void totalApptButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/totalAppointments.fxml");

    }

    /**
     *
     * @param actionEvent
     * @throws IOException
     * -handler for the contact schedule button
     * -goes to the contact schedule screen when the button is pressed
     */
    public void contactScheduleBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/contactSchedule.fxml");
    }

    /**
     *
     * @param actionEvent
     * @param screenName
     * @throws IOException
     * takes a fxml string and goes to the corresponding screen when called
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
