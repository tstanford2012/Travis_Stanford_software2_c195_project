package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MonthlyAppointmentsScreen {
    public void yearComboBoxHandler(MouseEvent mouseEvent) {
    }

    public void monthComboBoxHandler(MouseEvent mouseEvent) {
    }

    public void backButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/appointments.fxml");
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
