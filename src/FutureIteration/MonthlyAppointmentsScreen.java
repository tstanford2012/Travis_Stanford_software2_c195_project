package FutureIteration;

import Controller.AppointmentsController;
import Model.Appointments;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MonthlyAppointmentsScreen implements Initializable {

    @FXML
    TableView<Appointments> filterMonthTableView;
    @FXML
    ComboBox<String> monthComboBox;
    @FXML
    ComboBox<String> yearComboBox;
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
    private ObservableList<Appointments> appointmentList = FXCollections.observableArrayList();
    String monthSelected;
    String yearSelected;
    LocalDate now = LocalDate.now();
    String nowString = now.toString();
    String[] parts = nowString.split("-");
    String month = parts[1];





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        monthComboBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        yearComboBox.getItems().addAll("2020", "2021", "2022");
        appointmentList.setAll(AppointmentsController.appointmentList);

    }

    public void yearComboBoxHandler(ActionEvent actionEvent) {
        yearSelected = yearComboBox.getValue();

    }

    public void monthComboBoxHandler(ActionEvent actionEvent) {
        String selection = monthComboBox.getValue();
        int selectionIndex = monthComboBox.getItems().indexOf(selection);
        switch (selectionIndex) {
            case 0: monthSelected = "01";
            break;
            case 1: monthSelected = "02";
            break;
            case 2: monthSelected = "03";
            break;
            case 3: monthSelected = "04";
            break;
            case 4: monthSelected = "05";
            break;
            case 5: monthSelected = "06";
            break;
            case 6: monthSelected = "07";
            break;
            case 7: monthSelected = "08";
            break;
            case 8: monthSelected = "09";
            break;
            case 9: monthSelected = "10";
            break;
            case 10: monthSelected = "11";
            break;
            case 11: monthSelected = "12";
            break;
            default: monthSelected = month;
            monthComboBox.setValue(monthSelected);
            break;
        }
    }


    public void filterBtnHandler(ActionEvent actionEvent) throws SQLException, ParseException {
        if(monthComboBox.getSelectionModel().isEmpty()) {
            System.out.println("Please select a month");
        }
        else if(yearComboBox.getSelectionModel().isEmpty()) {
            System.out.println("Please select a year");
        }
        else {
            filterAppointments(appointmentList, monthSelected, yearSelected);
        }

    }

    public void backButtonHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent);
    }

    private void nextScreen(ActionEvent actionEvent) throws IOException {
        Stage stage;
        Parent root;
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/appointments.fxml"));

        root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void displayAppointmentTable() throws SQLException {
        filterMonthTableView.refresh();
        int userID = User.getUserID();


        Connection connection = DBConnection.getConnection();
        String selectStatement = "SELECT * from appointments where User_ID = ?";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

        preparedStatement.setInt(1, userID);


        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSet resultSet1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("SELECT Contact_ID, Contact_Name from contacts");
        ResultSet resultSet2 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("SELECT Customer_ID, Customer_Name from customers");

        String contactName = null;
        String customerName = null;


        while (resultSet.next()) {
            int contactIDName = resultSet.getInt("Contact_ID");
            int customerIDName = resultSet.getInt("Customer_ID");


            while (resultSet1.next()) {
                if (contactIDName == resultSet1.getInt("Contact_ID")) {
                    contactName = resultSet1.getString("Contact_Name");
                    resultSet1.beforeFirst();
                    break;
                }
            }
            while (resultSet2.next()) {
                if (customerIDName == resultSet2.getInt("Customer_ID")) {
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
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("appointmentCustomerName"));

        filterMonthTableView.setItems(appointmentList);
    }

    public void filterAppointments(ObservableList<Appointments> appointmentList, String monthSelected, String yearSelected) {
        LocalDate now = LocalDate.now();

        String nowString = now.toString();
        String[] parts = nowString.split("-");
        String year = parts[0];
        String month = parts[1];
        String day = parts[2];
        //LocalDate monthSelectedToDate = LocalDate.parse(monthSelected);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        FilteredList<Appointments> filteredList = new FilteredList<>(appointmentList);
        filteredList.setPredicate(row -> {
            LocalDate rowDate = LocalDate.parse(row.getStart().toString(), dateTimeFormatter);

            return false;
        });

        filterMonthTableView.setItems(filteredList);


    }

}
