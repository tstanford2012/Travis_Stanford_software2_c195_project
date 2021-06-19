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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditCustomerScreen implements Initializable {
    @FXML
    TextField customerIDTextField;
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

    /**
     *
     * @param url
     * @param resourceBundle
     * -adds the countries to the country combo box
     * -adds the appropriate states/provinces to the combo box based on the country
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryComboBox.getItems().addAll("United States", "United Kingdom", "Canada");
    }


    /**
     *
     * @param actionEvent
     * -handler for the country combo box
     * -adds the states/provinces based on which country is associated with the customer that was selected on the table
     */
    public void countryComboBoxHandler(ActionEvent actionEvent) {
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
    }

    /**
     *
     * @param mouseEvent
     * -mouse click handler for the state combo box
     * -displays an error if a country has not been selected
     */
    public void stateComboBoxClicked(MouseEvent mouseEvent) {
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
     * @param actionEvent
     * -handler for the save button
     * -ensures that all fields have a value then saves to the database
     */
    public void editCustomerSaveBtnHandler(ActionEvent actionEvent) {

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
            customerID = Integer.parseInt(customerIDTextField.getText());
            customerFirstName = firstNameTextField.getText();
            customerLastName = lastNameTextField.getText();
            stateProvince = stateComboBox.getValue();
            country = countryComboBox.getValue();
            postalCode = zipCodeTextField.getText();
            phoneNumber = phoneNumberTextField.getText();
            if(country.contains("United Kingdom")) {
                address = addressTextField.getText() + ", " + boroughTextField.getText() + ", " + cityTextField.getText();
            }
            else {
                address = addressTextField.getText() + ", " + cityTextField.getText();
            }



            //Checks all of the fields and displays the appropriate error if a field in empty

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
                else if (phoneNumberTextField.getText().isEmpty()){
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
                    String customerName = customerFirstName + " " + customerLastName;

                    //calls the validName method and sets isValid to the boolean value returned.
                    //Used to ensure that the entered name does not conflict with a name already in the database.
                    boolean isValid = validName(customerName);


                    if(isValid) {
                        //saves to the database if the customer name is valid
                        Connection connection = DBConnection.getConnection();
                        String updateStatement = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ?, Last_Updated_By = ? where Customer_ID = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(updateStatement);

                        preparedStatement.setString(1, customerName);
                        preparedStatement.setString(2, address);
                        preparedStatement.setString(3, postalCode);
                        preparedStatement.setString(4, phoneNumber);
                        preparedStatement.setInt(5, divisionID);
                        preparedStatement.setString(6, "Software 2 Program");
                        preparedStatement.setInt(7, customerID);

                        preparedStatement.executeUpdate();


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



                        //goes to the customers screen after saving
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
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Could not save...");
                        alert.setContentText("The entered name is the same as a record in the database.\nTry entering another name.");
                        alert.showAndWait();
                    }
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     *
     * @param actionEvent
     * @throws IOException
     * -handler for the cancel button
     * -goes back to the customers screen after confirmation when the button is pressed
     */
    public void cancelBtnHandler(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirming...");
        alert.setContentText("Are you sure you want to cancel? Changes will not be saved.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
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

    /**
     *
     * @param customer
     * sets the values for the fields to the values of the customer that was selected in the table
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        String fullAddress = customer.getCustomerAddress();
        String customerName = customer.getCustomerName();
        String[] nameSplit = customerName.split(" ");
        String firstName = nameSplit[0];
        String lastName = nameSplit[1];
        String[] parts = fullAddress.split(", ");
        String address = parts[0];
        String city;
        String borough;
        if(parts.length < 2) {
            city = null;
            borough = null;
        }
        else {
            if(customer.getCountry().contains("United Kingdom")) {
                borough = parts[1];
                city = parts[2];
                boroughTextField.setText(borough);
            }
            else {
                city = parts[1];
            }
        }




        customerIDTextField.setText(Integer.toString(customer.getCustomerID()));
        firstNameTextField.setText(firstName);
        lastNameTextField.setText(lastName);
        addressTextField.setText(address);
        cityTextField.setText(city);
        countryComboBox.setValue(customer.getCountry());
        stateComboBox.setValue(customer.getStateProvince());
        zipCodeTextField.setText(customer.getPostalCode());
        phoneNumberTextField.setText(customer.getPhoneNumber());

    }

    /**
     *
     * @param customerName
     * @return
     * @throws SQLException
     * validation for the customer name. Ensures no two records have the same name.
     */
    private boolean validName(String customerName) throws SQLException {
        Connection connection = DBConnection.getConnection();
        String selectStatement = "SELECT Customer_Name, Customer_ID from customers";
        DBQuery.setPreparedStatement(connection, selectStatement);

        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isValid = false;

        while (resultSet.next()) {
            if(customerName.toLowerCase(Locale.ROOT).contains(resultSet.getString("Customer_Name").toLowerCase(Locale.ROOT)) &&
                    Integer.parseInt(customerIDTextField.getText()) != resultSet.getInt("Customer_ID")) {
                isValid = false;
                break;
            }
            else {
                isValid = true;
            }
        }
        return isValid;
    }
}
