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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.DBConnection;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
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
    public TableView<Appointments> appointmentsTableView;
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
    TableColumn<TableView<Appointments>, String> startTimeCol;
    @FXML
    TableColumn<TableView<Appointments>, String> endTimeCol;
    @FXML
    TableColumn<TableView<Appointments>, String> customerCol;
    @FXML
    Button cancelAppointmentButton;

    private ObservableList<Appointments> appointmentList = FXCollections.observableArrayList();


    private boolean isMonth;






    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            displayAppointmentTable();


        } catch (SQLException exception) {
            System.out.println(exception.getMessage());

            exception.printStackTrace();
        }
    }


    public void monthRadioBtnHandler(ActionEvent actionEvent) {
        isMonth = true;
        weekRadioBtn.setSelected(false);

    }

    public void weekRadioBtnHandler(ActionEvent actionEvent) {
        isMonth = false;
        monthRadioBtn.setSelected(false);
    }

    public void addApptBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/addAppointment.fxml");
    }

    public void editApptBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/editAppointment.fxml");
    }

    public void deleteApptBtnHandler(ActionEvent actionEvent) {
    }

    public void backBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/mainScreen.fxml");
    }

    public void displayAppointmentTable() throws SQLException {
        appointmentsTableView.refresh();


        Connection connection = DBConnection.getConnection();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * from appointments");
        ResultSet resultSet1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("SELECT Contact_ID, Contact_Name from contacts");
        ResultSet resultSet2 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("SELECT Customer_ID, Customer_Name from customers");

        String contactName = null;
        String customerName = null;


        while(resultSet.next()) {
            int contactIDName = resultSet.getInt("Contact_ID");
            int customerIDName = resultSet.getInt("Customer_ID");


            while(resultSet1.next()) {
                if(contactIDName == resultSet1.getInt("Contact_ID")) {
                    contactName = resultSet1.getString("Contact_Name");
                    resultSet1.beforeFirst();
                    break;
                }
            }
            while(resultSet2.next()) {
                if(customerIDName == resultSet2.getInt("Customer_ID")) {
                    customerName = resultSet2.getString("Customer_Name");
                    resultSet2.beforeFirst();
                    break;
                }
            }

            appointmentList.add(new Appointments(resultSet.getInt("Appointment_ID"), resultSet.getString("Title"), resultSet.getString("Description"),
                    resultSet.getString("Location"), contactName, resultSet.getString("Type"), resultSet.getTimestamp("Start"), resultSet.getTimestamp("End"),
                    resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID"), customerName));
        }

        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("appointmentCustomerName"));

        appointmentsTableView.setItems(appointmentList);



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
