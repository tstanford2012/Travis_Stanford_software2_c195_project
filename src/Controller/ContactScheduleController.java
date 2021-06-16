package Controller;

import Model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ContactScheduleController implements Initializable {
    @FXML
    TableView<Appointments> contactScheduleTableView;
    @FXML
    TableColumn<TableView<Appointments>, String> appointmentIDCol;
    @FXML
    TableColumn<TableView<Appointments>, String> titleCol;
    @FXML
    TableColumn<TableView<Appointments>, String> typeCol;
    @FXML
    TableColumn<TableView<Appointments>, String> descriptionCol;
    @FXML
    TableColumn<TableView<Appointments>, String> startCol;
    @FXML
    TableColumn<TableView<Appointments>, String> endCol;
    @FXML
    TableColumn<TableView<Appointments>, String> customerIDCol;
    @FXML
    ComboBox<String> contactComboBox;
    private ObservableList<Appointments> contactAppointmentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillCustomerComboBox();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


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

    public void contactScheduleButtonHandler(ActionEvent actionEvent) throws SQLException {
        String contactName = contactComboBox.getValue();
        if(contactName == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error processing...");
            alert.setContentText("Must select a contact from the box to view schedule!");
            alert.showAndWait();
        }
        else {
            displayTable(contactName);
        }
    }

    private void fillCustomerComboBox() throws SQLException {
        Connection connection = DBConnection.getConnection();
        String selectStatement = "SELECT * from contacts";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String contact = resultSet.getString("Contact_Name");
            contactComboBox.getItems().add(contact);
        }

    }

    private void displayTable(String contactName) throws SQLException {
        contactAppointmentList.clear();
        Connection connection = DBConnection.getConnection();
        String selectStatement = "SELECT Contact_ID from contacts where Contact_Name = ?";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
        preparedStatement.setString(1, contactName);

        ResultSet resultSet = preparedStatement.executeQuery();

        int contactID = -1;
        while(resultSet.next()) {
            contactID = resultSet.getInt("Contact_ID");
        }


        selectStatement = "SELECT * from appointments where Contact_ID = ?";
        DBQuery.setPreparedStatement(connection, selectStatement);
        preparedStatement = DBQuery.getPreparedStatement();

        preparedStatement.setInt(1, contactID);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            contactAppointmentList.add(new Appointments(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"), resultSet.getString("Type"),
                    resultSet.getString("Description"), resultSet.getTimestamp("Start"), resultSet.getTimestamp("End"), resultSet.getInt("Customer_ID")));
        }



        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        contactScheduleTableView.setItems(contactAppointmentList);
    }


}
