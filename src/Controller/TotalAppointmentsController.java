package Controller;

import Model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class TotalAppointmentsController {
    @FXML
    Label totalWeekAmtLabel;
    @FXML
    Label totalMonthAmtLabel;
    @FXML
    TableView<Customer> customerApptTableView;
    @FXML
    TableColumn<TableView<Customer>, String> customerCol;
    @FXML
    TableColumn<TableView<Customer>, Integer> weekCol;
    @FXML
    TableColumn<TableView<Customer>, Integer> monthCol;




    public void backButtonHandler(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/reports.fxml"));

        root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
