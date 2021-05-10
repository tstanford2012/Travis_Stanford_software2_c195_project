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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditCustomerScreen implements Initializable {
    @FXML
    TextField customerIDTextField;
    @FXML
    TextField customerNameTextField;
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
        countryComboBox.getItems().addAll("United States", "United Kingdom", "Canada");
    }

    public void countryComboBoxHandler(ActionEvent actionEvent) {
        String country = countryComboBox.getValue();
        if(country.contains("United States")) {
            stateComboBox.getItems().clear();
            stateComboBox.getItems().addAll("Alabama", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia",
                    "Florida", "Georgia", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts",
                    "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
                    "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
                    "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virgina", "Wisconsin", "Wyoming", "Hawaii", "Alaska");
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

    public void editCustomerSaveBtnHandler(ActionEvent actionEvent) {

        int customerID;
        String customerName;
        String address;
        String stateProvince;
        String country;
        String postalCode;
        String phoneNumber;
        int divisionID = 0;




        try {
            customerID = Integer.parseInt(customerIDTextField.getText());
            customerName = customerNameTextField.getText();
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



            if(customerNameTextField.getText().isEmpty()) {
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
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

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

    public void setCustomer(Customer customer) {
        this.customer = customer;
        String fullAddress = customer.getCustomerAddress();
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
        customerNameTextField.setText(customer.getCustomerName());
        addressTextField.setText(address);
        cityTextField.setText(city);
        countryComboBox.setValue(customer.getCountry());
        stateComboBox.setValue(customer.getStateProvince());
        zipCodeTextField.setText(customer.getPostalCode());
        phoneNumberTextField.setText(customer.getPhoneNumber());

    }
}
