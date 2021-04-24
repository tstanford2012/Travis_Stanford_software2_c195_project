package Controller;

import Model.Customer;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    @FXML
    TableView<Customer> customersTableView;
    @FXML
    Button backBtn;
    @FXML
    Button addCustomerBtn;
    @FXML
    Button editCustomerBtn;
    @FXML
    Button deleteCustomerBtn;
    @FXML
    public TableColumn<TableView<Customer>, String> customerIDCol;

    public TableColumn<TableView<Customer>, String> customerNameCol;
    public TableColumn<TableView<Customer>, String> addressCol;
    public TableColumn<TableView<Customer>, String> stateProvinceCol;
    public TableColumn<TableView<Customer>, String> countryCol;
    public TableColumn<TableView<Customer>, String> postalCodeCol;
    public TableColumn<TableView<Customer>, String> phoneNumberCol;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            displayCustomerTable();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void displayCustomerTable() throws SQLException {
        customersTableView.refresh();
        String stateProvince = null;
        String country = null;



        Connection connection = DBConnection.getConnection();

        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * from customers");
        ResultSet resultSet1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery("SELECT Division_ID, Division from first_level_divisions");


        while(resultSet.next()) {
            int setOneDivisionID = resultSet.getInt("Division_ID");
            int setTwoDivisionID = setOneDivisionID;


            if((setOneDivisionID > 0 && setOneDivisionID < 55)) {
                country = "United States";
                resultSet1.beforeFirst();
                while(resultSet1.next()) {
                    if(setTwoDivisionID == resultSet1.getInt("Division_ID")) {
                        stateProvince = resultSet1.getString("Division");
                        break;
                    }
                }
            }
            else if(setOneDivisionID > 54 && setOneDivisionID < 73) {
                country = "Canada";
                resultSet1.beforeFirst();
                while(resultSet1.next()) {
                    if(setTwoDivisionID == resultSet1.getInt("Division_ID")) {
                        stateProvince = resultSet1.getString("Division");
                        break;
                    }
                }
            }
            else if (setOneDivisionID > 100 && setOneDivisionID < 105) {
                country = "United Kingdom";
                resultSet1.beforeFirst();
                while(resultSet1.next()) {
                    if((setTwoDivisionID == resultSet1.getInt("Division_ID")) ) {
                        stateProvince = resultSet1.getString("Division");
                        break;
                    }
                }
            }
            customerList.add(new Customer(resultSet.getInt("Customer_ID"), resultSet.getString("Customer_Name"),
                    resultSet.getString("Address"), stateProvince, country, resultSet.getString("Postal_Code"),
                    resultSet.getString("Phone"), resultSet.getInt("Division_ID")));
        }



        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        stateProvinceCol.setCellValueFactory(new PropertyValueFactory<>("stateProvince"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customersTableView.setItems(customerList);
    }


    public void addCustomerBtnHandler(ActionEvent actionEvent) throws IOException {
        nextScreen(actionEvent, "../View/addCustomer.fxml");
    }

    public void editCustomerBtnHandler(ActionEvent actionEvent) throws IOException {
        Customer customer = customersTableView.getSelectionModel().getSelectedItem();
        if(customersTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error modifying customer");
            alert.setContentText("You did not select a customer to modify");
            alert.showAndWait();
        }
        else {
            Stage stage;
            Parent root;
            stage=(Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            FXMLLoader loader=new FXMLLoader(getClass().getResource(
                    "../View/editCustomer.fxml"));
            root =loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            EditCustomerScreen controller = loader.getController();
            controller.setCustomer(customer);
        }
    }

    public void deleteCustomerBtnHandler(ActionEvent actionEvent) throws SQLException {

        Customer customer = customersTableView.getSelectionModel().getSelectedItem();

        if(customersTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Please select a record to delete");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Delete Confirmation");
            alert.setContentText("Are you sure you want to delete the record?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
                Connection connection = DBConnection.getConnection();
                ResultSet appointmentResultSet = connection.createStatement().executeQuery("SELECT Customer_ID from appointments");
                int deleteID = customer.getCustomerID();


                String deleteCustomerStatement = "DELETE from customers where Customer_ID = ?";
                String deleteAppointmentStatement = "DELETE from appointments where Customer_ID = ?";

                while(appointmentResultSet.next()) {
                    int appointmentResultID = appointmentResultSet.getInt("Customer_ID");


                        if(appointmentResultID == deleteID) {
                            DBQuery.setPreparedStatement(connection, deleteAppointmentStatement);
                            PreparedStatement preparedStatement = DBQuery.getPreparedStatement();
                            preparedStatement.setInt(1, deleteID);

                            preparedStatement.execute();
                        }
                }


                //SQL delete statement
                DBQuery.setPreparedStatement(connection, deleteCustomerStatement); //Create prepared statement object
                PreparedStatement preparedDeleteStatement = DBQuery.getPreparedStatement();
                preparedDeleteStatement.setInt(1, deleteID);

                preparedDeleteStatement.execute();

                if(preparedDeleteStatement.getUpdateCount() > 0) {
                    System.out.println("Number of rows affected: " + preparedDeleteStatement.getUpdateCount());
                    Alert deleteAlert = new Alert(Alert.AlertType.INFORMATION);
                    deleteAlert.setHeaderText("Delete successful");
                    deleteAlert.setContentText("Deleting of customer " + customer.getCustomerName() + " was successful!");
                    deleteAlert.showAndWait();
                }
                else {
                    System.out.println("Values not deleted");
                }
                customerList.remove(customer);
            }
            else {
                System.out.println("No longer deleting");
            }
        }
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
