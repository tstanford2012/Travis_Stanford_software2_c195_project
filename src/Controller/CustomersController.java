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
                /*
                switch (divisionID) {
                    case 1: stateProvince = "Alabama"; break; case 2: stateProvince = "Arizona"; break; case 3: stateProvince = "Arkansas"; break;
                    case 4: stateProvince = "California"; break; case 5: stateProvince = "Colorado"; break; case 6: stateProvince = "Connecticut"; break;
                    case 7: stateProvince = "Delaware"; break; case 8: stateProvince = "District of Columbia"; break; case 9: stateProvince = "Florida"; break;
                    case 10: stateProvince = "Georgia"; break; case 11: stateProvince = "Idaho"; break; case 12: stateProvince = "Illinois"; break;
                    case 13: stateProvince = "Indiana"; break; case 14: stateProvince = "Iowa"; break; case 15: stateProvince = "Kansas"; break;
                    case 16: stateProvince = "Kentucky"; break; case 17: stateProvince = "Louisiana"; break; case 18: stateProvince = "Maine"; break;
                    case 19: stateProvince = "Maryland"; break; case 20: stateProvince = "Massachusetts"; break; case 21: stateProvince = "Michigan"; break;
                    case 22: stateProvince = "Minnesota"; break; case 23: stateProvince = "Mississippi"; break; case 24: stateProvince = "Missouri"; break;
                    case 25: stateProvince = "Montana"; break; case 26: stateProvince = "Nebraska"; break; case 27: stateProvince = "Nevada"; break;
                    case 28: stateProvince = "New Hampshire"; break; case 29: stateProvince = "New Jersey"; break; case 30: stateProvince = "New Mexico"; break;
                    case 31: stateProvince = "New York"; break; case 32: stateProvince = "North Carolina"; break; case 33: stateProvince = "North Dakota"; break;
                    case 34: stateProvince = "Ohio"; break; case 35: stateProvince = "Oklahoma"; break; case 36: stateProvince = "Oregon"; break;
                    case 37: stateProvince = "Pennsylvania"; break; case 38: stateProvince = "Rhode Island"; break; case 39: stateProvince = "South Carolina"; break;
                    case 40: stateProvince = "South Dakota"; break; case 41: stateProvince = "Tennessee"; break; case 42: stateProvince = "Texas"; break;
                    case 43: stateProvince = "Utah"; break; case 44: stateProvince = "Vermont"; break; case 45: stateProvince = "Virginia"; break;
                    case 46: stateProvince = "Washington"; break; case 47: stateProvince = "West Virginia"; break; case 48: stateProvince = "Wisconsin"; break;
                    case 49: stateProvince = "Wyoming"; break; case 50: stateProvince = ""; break; case 51: stateProvince = ""; break;
                    case 52: stateProvince = "Hawaii"; break; case 53: stateProvince = ""; break; case 54: stateProvince = "Alaska"; break;
                }*/
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
                /*switch (divisionID) {
                    case 60: stateProvince = "Northwest Territories"; break; case 61: stateProvince = "Alberta"; break; case 62: stateProvince = "British Columbia"; break;
                    case 63: stateProvince = "Manitoba"; break; case 64: stateProvince = "New Brunswick"; break; case 65: stateProvince = "Nova Scotia"; break;
                    case 66: stateProvince = "Prince Edward Island"; break; case 67: stateProvince = "Ontario"; break; case 68: stateProvince = "Quebec"; break;
                    case 69: stateProvince = "Saskatchewan"; break; case 70: stateProvince = "Nunavut"; break; case 71: stateProvince = "Yukon"; break;
                    case 72: stateProvince = "Newfoundland and Labrador"; break;
                }*/
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
                /*switch (divisionID) {
                    case 101: stateProvince = "England"; break;
                    case 102: stateProvince = "Wales"; break;
                    case 103: stateProvince = "Scotland"; break;
                    case 104: stateProvince = "Northern Ireland"; break;
                }*/
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
                String deleteStatement = "DELETE from customers where Customer_ID = ?";

                int deleteID = customer.getCustomerID();


                //SQL delete statement
                DBQuery.setPreparedStatement(connection, deleteStatement); //Create prepared statement object
                PreparedStatement preparedDeleteStatement = DBQuery.getPreparedStatement();
                preparedDeleteStatement.setInt(1, deleteID);

                preparedDeleteStatement.execute();

                if(preparedDeleteStatement.getUpdateCount() > 0) {
                    System.out.println("Number of rows affected: " + preparedDeleteStatement.getUpdateCount());
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
