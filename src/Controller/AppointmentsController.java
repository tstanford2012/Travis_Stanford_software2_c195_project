package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {

    @FXML
    private Label appointmentsLabel;
    @FXML
    Label filterByLabel;
    @FXML
    RadioButton monthRadioBtn;
    @FXML
    RadioButton weekRadioBtn;
    @FXML
    Button backBtn;
    @FXML
    Button deleteApptBtn;
    @FXML
    Button editApptBtn;
    @FXML
    Button addApptBtn;
    @FXML
    private TableView<AppointmentsController> appointmentsTableView;

    private boolean isMonth;






    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void monthRadioBtnHandler(ActionEvent actionEvent) {
        isMonth = true;
        weekRadioBtn.setSelected(false);

    }

    public void weekRadioBtnHandler(ActionEvent actionEvent) {
        isMonth = false;
        monthRadioBtn.setSelected(false);
    }

    public void addApptBtnHandler(ActionEvent actionEvent) {
    }

    public void editApptBtnHandler(ActionEvent actionEvent) {
    }

    public void deleteApptBtnHandler(ActionEvent actionEvent) {
    }

    public void backBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/mainScreen.fxml");
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
