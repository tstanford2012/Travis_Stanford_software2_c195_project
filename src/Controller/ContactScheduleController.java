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

/**
 * This class displays the appointments in a table that are associated with a contact that is selected by the user
 */
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

    /**
     *
     * @param url
     * @param resourceBundle
     * Calls the fillContactComboBox method to add the names of the contacts to the box.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillContactComboBox();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     *
     * @param actionEvent uses the action event to go back to the reports screen.
     * @throws IOException
     * Handler for the back button.
     * Goes back to the reports screen when the button is pressed.
     */
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

    /**
     * @throws SQLException
     * Handler for the view button.
     * Displays the appointments in the table that are associated with the contact that is selected.
     */
    public void contactScheduleButtonHandler() throws SQLException {
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

    /**
     * @throws SQLException
     * Selects the contact names from the database and adds them to the combo box when called.
     */
    private void fillContactComboBox() throws SQLException {
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

    /**
     *
     * @param contactName Takes the contact name that is selected and displays the information in the table.
     * @throws SQLException
     *
     */
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



        //sets the table column properties
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
