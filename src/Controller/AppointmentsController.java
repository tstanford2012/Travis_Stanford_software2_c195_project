package Controller;

import Model.Appointments;
import Model.Customer;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {

    @FXML
    private Label appointmentsLabel;
    @FXML
    Label filterByLabel;
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
    @FXML
    RadioButton weekFilterRadioBtn;
    @FXML
    RadioButton monthFilterRadioBtn;
    boolean isWeek;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    public static ObservableList<Appointments> appointmentList = FXCollections.observableArrayList();








    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            displayAppointmentTable();


        } catch (SQLException exception) {
            System.out.println(exception.getMessage());

            exception.printStackTrace();
        }
    }



    public void weekFilterRadioBtnHandler(ActionEvent actionEvent) throws SQLException {
        isWeek = true;
        monthFilterRadioBtn.setSelected(false);
        displayAppointmentTable();
    }

    public void monthFilterRadioBtnHandler(ActionEvent actionEvent) throws SQLException {
        isWeek = false;
        weekFilterRadioBtn.setSelected(false);
        displayAppointmentTable();

    }




    public void addApptBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/addAppointment.fxml");
    }

    public void editApptBtnHandler(ActionEvent actionEvent) throws IOException {
        Appointments appointments = appointmentsTableView.getSelectionModel().getSelectedItem();
        if(appointmentsTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error modifying appointment");
            alert.setContentText("Must select an appointment to modify");
            alert.showAndWait();
        }
        else {
            Stage stage;
            Parent root;
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/editAppointment.fxml"));
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            EditAppointmentScreen controller = loader.getController();
            controller.setAppointment(appointments);
        }


    }

    public void cancelAppointmentBtnHandler(ActionEvent actionEvent) throws SQLException {

        Appointments appointments = appointmentsTableView.getSelectionModel().getSelectedItem();

        if(appointmentsTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Please select an appointment to cancel");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Cancel Confirmation");
            alert.setContentText("Are you sure you want to cancel the " + appointments.getType() + " appointment # " + appointments.getAppointmentID() + " with " + appointments.getAppointmentCustomerName() + " at " + appointments.getStart().toString() + "?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
                Connection connection = DBConnection.getConnection();
                int deleteID = appointments.getAppointmentID();


                String deleteAppointmentStatement = "DELETE from appointments where Appointment_ID = ?";


                //SQL delete statement
                DBQuery.setPreparedStatement(connection, deleteAppointmentStatement); //Create prepared statement object
                PreparedStatement preparedDeleteStatement = DBQuery.getPreparedStatement();
                preparedDeleteStatement.setInt(1, deleteID);

                preparedDeleteStatement.execute();

                if(preparedDeleteStatement.getUpdateCount() > 0) {
                    System.out.println("Number of rows affected: " + preparedDeleteStatement.getUpdateCount());
                    Alert deleteAlert = new Alert(Alert.AlertType.INFORMATION);
                    deleteAlert.setHeaderText("Cancel successful");
                    deleteAlert.setContentText(appointments.getType() + " Appointment # " + deleteID + " with customer " + appointments.getAppointmentCustomerName() + " at " + appointments.getStart().toString() + " was cancelled!");
                    deleteAlert.showAndWait();
                }
                else {
                    System.out.println("Values not deleted");
                }
                appointmentList.remove(appointments);
            }
            else {
                System.out.println("No longer cancelling");
            }
        }
    }

    public void backBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/mainScreen.fxml");
    }

    public void displayAppointmentTable() throws SQLException {
        appointmentsTableView.refresh();
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
        appointmentList.clear();


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


        if(weekFilterRadioBtn.isSelected() || monthFilterRadioBtn.isSelected()) {

            if(isWeek) {
                showAppointmentsThisWeek(appointmentList);
            }
            else {
                showAppointmentsThisMonth(appointmentList);
            }

        }
        else {
            appointmentsTableView.setItems(appointmentList);
        }
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

    public void showAppointmentsThisWeek(ObservableList<Appointments> appointmentList) {

        LocalDate today = LocalDate.now();
        LocalDate weekFromToday = today.plusWeeks(1);

        //filtered list lambda used to filter appointments this week
        FilteredList<Appointments> filteredList = new FilteredList<>(appointmentList);
        filteredList.setPredicate(row -> {
            LocalDate startDate = LocalDate.parse(row.getStart().toString(), dateTimeFormatter);

            return startDate.isAfter(today.minusDays(1)) && startDate.isBefore(weekFromToday);

        });
        appointmentsTableView.setItems(filteredList);
    }

    public void showAppointmentsThisMonth(ObservableList<Appointments> appointmentList) {
        LocalDate today = LocalDate.now();
        LocalDate monthFromToday = today.plusMonths(1);

        //filtered list lambda used to filter appointments this month
        FilteredList<Appointments> filteredList = new FilteredList<>(appointmentList);
        filteredList.setPredicate(row -> {

            LocalDate startDate = LocalDate.parse(row.getStart().toString(), dateTimeFormatter);

            return startDate.isAfter(today.minusDays(1)) && startDate.isBefore(monthFromToday);
        });
        appointmentsTableView.setItems(filteredList);

    }

}
