package Controller;

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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TotalAppointmentsController implements Initializable {

    @FXML
    RadioButton appointmentTypeRadBtn;
    @FXML
    RadioButton monthRadBtn;
    @FXML
    ComboBox<String> customerComboBox;



    private ObservableList<Appointments> appointmentList = FXCollections.observableArrayList();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");


    /**
     *
     * @param url
     * @param resourceBundle
     * adds the customer names to the combo box during initialization
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillCustomerComboBox();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


    /**
     * -handler for the appointment type radio button
     * -deselects the month radio button when selected
     *
     */
    public void appointmentTypeRadBtnHandler(MouseEvent mouseEvent) {
        monthRadBtn.setSelected(false);
    }

    /**
     *
     * @param mouseEvent
     * -handler for the month radio button
     * -deselects the appointment type radio button when selected
     */
    public void monthRadBtnHandler(MouseEvent mouseEvent) {
        appointmentTypeRadBtn.setSelected(false);
    }


    /**
     * -handler for the back button
     * -goes back to the reports screen when the button is pressed
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
     *
     * @param actionEvent
     * @throws SQLException
     *
     * Calculates the number of appointments for the next month or for each type depending on which customer is selected from the combo box
     */
    public void calculateBtnHandler(ActionEvent actionEvent) throws SQLException {
        String customerName = customerComboBox.getValue();
        appointmentList.clear();

        if(customerName == null) {
            System.out.println("Please select a customer");
        }
        else {
            int userID = User.getUserID();
            Connection connection = DBConnection.getConnection();
            String selectCustomerStatement = "SELECT Customer_ID, Customer_Name from customers where Customer_Name = ?";
            DBQuery.setPreparedStatement(connection, selectCustomerStatement);
            PreparedStatement preparedCustomerStatement = DBQuery.getPreparedStatement();
            preparedCustomerStatement.setString(1, customerName);

            ResultSet customerResultSet = preparedCustomerStatement.executeQuery();

            int customerID = -1;

            while (customerResultSet.next()) {
                customerID = customerResultSet.getInt("Customer_ID");
            }


            if(appointmentTypeRadBtn.isSelected()) {
                ObservableList<Appointments> typeAppointmentList = FXCollections.observableArrayList();

                String selectStatement = "Select * from appointments where User_ID = ? and Customer_ID = ?";
                DBQuery.setPreparedStatement(connection, selectStatement);
                PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, customerID);

                ResultSet resultSet = preparedStatement.executeQuery();


                while (resultSet.next()) {
                    typeAppointmentList.add(new Appointments(resultSet.getInt("Appointment_ID"), resultSet.getString("Type"), resultSet.getInt("Customer_ID"), resultSet.getInt("User_ID")));

                }

                calculateAppointmentTypeTotals(customerID, userID, customerName);

            }
            else if(monthRadBtn.isSelected()) {
                int totalAppointments;
                String selectStatement = "SELECT * from appointments where User_ID = ? and Customer_ID = ?";
                DBQuery.setPreparedStatement(connection, selectStatement);
                PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, customerID);

                ResultSet monthResultSet = preparedStatement.executeQuery();

                while (monthResultSet.next()) {
                    appointmentList.add(new Appointments(monthResultSet.getInt("Appointment_ID"), monthResultSet.getTimestamp("Start"), monthResultSet.getTimestamp("End"),
                            monthResultSet.getInt("Customer_ID"), monthResultSet.getInt("User_ID")));
                }


                totalAppointments = calculateAppointmentsThisMonth(appointmentList);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Showing information...");
                alert.setContentText("The total amount of appointments this month for customer " + customerName + " is:\n" + totalAppointments);
                alert.showAndWait();
            }
            else {
                System.out.println("Please select one of the radio buttons");
            }
        }
    }

    /**
     *
     * @param appointmentList
     * @return
     *
     * uses a filtered list to calculate the amount of appointments for the next month
     */
    public int calculateAppointmentsThisMonth(ObservableList<Appointments> appointmentList) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime monthFromToday = today.plusMonths(1);

        //filtered list lambda used to reduce code required to filter appointments this month
        FilteredList<Appointments> filteredList = new FilteredList<>(appointmentList);
        filteredList.setPredicate(row -> {

            LocalDateTime startDate = LocalDateTime.parse(row.getStart().toString(), dateTimeFormatter);

            return startDate.isAfter(today.minusDays(1)) && startDate.isBefore(monthFromToday);
        });
        return filteredList.size();

    }

    /**
     *
     * @param customerID
     * @param userID
     * @param customerName
     * @throws SQLException
     *
     * Calculates the totals for each type of appointment for the selected customer
     */
    private void calculateAppointmentTypeTotals(int customerID, int userID, String customerName) throws SQLException {
        String debrief = "De-Briefing";
        String planning = "Planning Session";
        String newAccount = "New Account";
        String followUp = "Follow Up";

        int debriefTotal = 0;
        int planningTotal = 0;
        int newAccountTotal = 0;
        int followUpTotal = 0;

        Connection connection = DBConnection.getConnection();
        String selectStatement = "SELECT Type from appointments where User_ID = ? and Customer_ID = ?";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

        preparedStatement.setInt(1, userID);
        preparedStatement.setInt(2, customerID);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String type = resultSet.getString("Type");
            if(type.contains(debrief)) {
                debriefTotal++;
            }
            else if(type.contains(planning)) {
                planningTotal++;
            }
            else if(type.contains(newAccount)) {
                newAccountTotal++;
            }
            else if(type.contains(followUp)) {
                followUpTotal++;
            }
            else {
                debriefTotal = 0;
                planningTotal = 0;
                newAccountTotal = 0;
                followUpTotal = 0;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Displaying information...");
        alert.setContentText("Total appointments for customer " + customerName + ":\n" + "De-briefing = " + debriefTotal + "\n" + "Planning Session = " + planningTotal + "\n" +
                "New Account = " + newAccountTotal + "\n" + "Follow Up = " + followUpTotal);
        alert.showAndWait();

    }

    /**
     *
     * @throws SQLException
     * pulls the customer names from the database and adds them to the customer combo box
     */
    private void fillCustomerComboBox() throws SQLException {
        String customerName = null;
        Connection connection = DBConnection.getConnection();
        String selectStatement = "Select Customer_Name from customers";
        DBQuery.setPreparedStatement(connection, selectStatement);
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            customerName = resultSet.getString("Customer_Name");
            customerComboBox.getItems().add(customerName);
        }
    }
}
