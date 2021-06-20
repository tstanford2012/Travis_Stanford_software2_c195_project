package Controller;

import Model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class allows the user to enter information into combo boxes and text fields in order to save a new customer to the database
 */
public class AddCustomerScreen implements Initializable {
    @FXML
    TextField firstNameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    TextField addressTextField;
    @FXML
    TextField zipCodeTextField;
    @FXML
    ComboBox<String> countryComboBox;
    @FXML
    ComboBox<String> stateComboBox;
    @FXML
    TextField phoneNumberTextField;
    @FXML
    TextField cityTextField;
    @FXML
    TextField boroughTextField;
    @FXML
    Label boroughLabel;
    Customer customer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //clears and adds countries to the country combo box
        countryComboBox.getItems().clear();
        countryComboBox.getItems().addAll("United States", "United Kingdom", "Canada");
        stateComboBox.getItems().clear();

    }

    /**
     *
     * Adds the states/provinces to the combo box depending on which country is selected. Reveals the borough combo box if United kingdom is selected.
     */
    public void countryComboBoxHandler() {
        String country = countryComboBox.getValue();
        if(country.contains("United States")) {
            stateComboBox.getItems().clear();
            stateComboBox.getItems().addAll("Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia",
                    "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts",
                    "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
                    "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
                    "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virgina", "Wisconsin", "Wyoming");
            boroughTextField.setOpacity(0);
            boroughTextField.setPromptText("");
            boroughTextField.setEditable(false);
            boroughLabel.setText("");
        }
        else if(country.contains("United Kingdom")) {
            stateComboBox.getItems().clear();
            stateComboBox.getItems().addAll("England", "Wales", "Scotland", "Northern Ireland");
            boroughTextField.setOpacity(1);
            boroughTextField.setPromptText("Enter Borough name");
            boroughTextField.setEditable(true);
            boroughLabel.setText("Borough");
        }
        else {
            stateComboBox.getItems().clear();
            stateComboBox.getItems().addAll("Northwest Territories", "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Nova Scotia",
                    "Prince Edward Island", "Ontario", "Quebec", "Saskatchewan", "Nunavut", "Yukon", "Newfoundland and Labrador");
            boroughTextField.setOpacity(0);
            boroughTextField.setPromptText("");
            boroughTextField.setEditable(false);
            boroughLabel.setText("");
        }
        System.out.println(country);
    }

    //

    /**
     * Mouse click handler for the state combo box.
     * Displays an alert if the country is empty
     */
    public void stateComboBoxClicked() {
        String country = countryComboBox.getValue();
        stateComboBox.setOnAction(actionEvent -> {
            int selectedIndex = stateComboBox.getSelectionModel().getSelectedIndex();
            Object selectedItem = stateComboBox.getSelectionModel().getSelectedItem();
        });
        if (country == null) {
            System.out.println("Select a country please");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Must select a country");
            alert.show();
        }
    }

    /**
     *
     * Handler for the state combo box.
     */
    public void stateComboBoxHandler() {

    }

    /**
     *
     * @param actionEvent uses the action event to go to the customers screen after the customer is saved to the database.
     * @throws Exception
     * Handler for the save button.
     * Ensures all fields have values and inserts the record into the database if valid.
     */
    public void addCustomerSaveBtnHandler(ActionEvent actionEvent) throws Exception {
        int customerID;
        String customerFirstName;
        String customerLastName;
        String address;
        String stateProvince;
        String country;
        String postalCode;
        String phoneNumber;
        int divisionID = 0;




        try {
            customerFirstName = firstNameTextField.getText();
            customerLastName = lastNameTextField.getText();
            stateProvince = stateComboBox.getValue();
            country = countryComboBox.getValue();
            postalCode = zipCodeTextField.getText();
            phoneNumber = phoneNumberTextField.getText();

            //concatenates the address to include the borough if United Kingdom is selected
            if(country.contains("United Kingdom")) {
                address = addressTextField.getText() + ", " + boroughTextField.getText() + ", " + cityTextField.getText();
            }
            else {
                address = addressTextField.getText() + ", " + cityTextField.getText();
            }


            if(firstNameTextField.getText().isEmpty() || lastNameTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error adding customer");
                alert.setContentText("Please enter a customer name");
                alert.showAndWait();
            }
            else {
                if(addressTextField.getText().isEmpty() || zipCodeTextField.getText().isEmpty() || cityTextField.getText().isEmpty() || countryComboBox.getValue().isEmpty()
                || stateComboBox.getValue().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Missing address information");
                    alert.showAndWait();
                }
                else if(phoneNumberTextField.getText().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please enter a phone Number");
                    alert.showAndWait();
                }
                else {
                    Connection conn = DBConnection.getConnection();
                    ResultSet rs = conn.createStatement().executeQuery("SELECT Division_ID, Division from first_level_divisions");

                    while(rs.next()) {
                        if(rs.getString("Division").contains(stateProvince)) {
                            divisionID = rs.getInt("Division_ID");
                            break;
                        }
                    }

                    //Code below ensures that the entered name is not already in the database
                    String selectStatement = "SELECT Customer_Name from customers";
                    DBQuery.setPreparedStatement(conn, selectStatement);
                    PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

                    ResultSet resultSet = preparedStatement.executeQuery();

                    String customerName = customerFirstName + " " + customerLastName;
                    boolean nameExists = false;
                    while (resultSet.next()) {
                        if(customerName.toLowerCase(Locale.ROOT).contains(resultSet.getString("Customer_Name").toLowerCase(Locale.ROOT))) {
                            nameExists = true;
                            break;
                        }
                    }
                    //Throws an error if the entered name is already in the database
                    if(nameExists) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Could not save...");
                        alert.setContentText("The entered name is the same as a record in the database.\nTry entering another name.");
                        alert.showAndWait();
                    }
                    else {
                        Connection connection = DBConnection.getConnection();
                        String statement = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Division_ID, Created_By, Last_Updated_By) VALUES (?,?,?,?,?,?,?)";
                        preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);

                        preparedStatement.setString(1, customerName);
                        preparedStatement.setString(2, address);
                        preparedStatement.setString(3, postalCode);
                        preparedStatement.setString(4, phoneNumber);
                        preparedStatement.setInt(5, divisionID);
                        preparedStatement.setString(6, "Software 2 Program");
                        preparedStatement.setString(7, "Software 2 Program");

                        preparedStatement.executeUpdate();

                        resultSet = preparedStatement.getGeneratedKeys();


                        if(resultSet.next()) {
                            System.out.println("Auto Generated Key: " + resultSet.getInt(1));
                        }

                        customerID = resultSet.getInt(1);


                        Customer.customerList.remove(customer);
                        Customer newCustomer = new Customer(customerID, customerName, address, stateProvince, country, postalCode, phoneNumber, divisionID);
                        Customer.customerList.add(newCustomer);
                        newCustomer.setCustomerID(customerID);
                        newCustomer.setCustomerName(customerName);
                        newCustomer.setCustomerAddress(address);
                        newCustomer.setStateProvince(stateProvince);
                        newCustomer.setCountry(country);
                        newCustomer.setPostalCode(postalCode);
                        newCustomer.setPhoneNumber(phoneNumber);
                        newCustomer.setDivisionID(divisionID);



                        Stage stage;
                        Parent root;
                        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/customers.fxml"));

                        root = loader.load();

                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    }
                }
            }
        } catch(Exception e) {
            e.getMessage();
            e.printStackTrace();
        }

    }

    /**
     *
     * @param actionEvent uses the action event to go back to the customers screen.
     * @throws IOException
     * Handler for the cancel button. Goes  to the customers screen after confirmation.
     */
    public void cancelBtnHandler(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirming...");
        alert.setContentText("Are you sure you want to cancel? Changes will not be saved.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            Stage stage;
            Parent root;
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/customers.fxml"));

            root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else {
            System.out.println("Not cancelling...");
        }
    }
}
