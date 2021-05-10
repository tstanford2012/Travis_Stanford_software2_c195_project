package FutureIteration;

import Model.Appointments;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class WeeklyAppointmentsScreen {

    @FXML
    TableView<Appointments> weekApptTableView;
    @FXML
    TableColumn<TableView<Appointments>, String> appointmentIDCol;
    @FXML
    TableColumn<TableView<Appointments>, String> titleCol;
    @FXML
    TableColumn<TableView<Appointments>, String> descriptionCol;
    @FXML
    TableColumn<TableView<Appointments>, String> locationCol;
    @FXML
    TableColumn<TableView<Appointments>, String> contactCol;
    @FXML
    TableColumn<TableView<Appointments>, String> typeCol;
    @FXML
    TableColumn<TableView<Appointments>, String> startCol;
    @FXML
    TableColumn<TableView<Appointments>, String> endCol;
    @FXML
    TableColumn<TableView<Appointments>, String> customerCol;





    public void backButtonHandler(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/appointments.fxml"));

        root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void nextWeekBtnHandler(ActionEvent actionEvent) {
    }

    public void previousWeekBtnHandler(ActionEvent actionEvent) {
    }
}
