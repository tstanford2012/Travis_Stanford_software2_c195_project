package Controller;

import DBAccess.DBCountries;
import Model.Countries;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TableColumn idCol;
    public TableColumn NameCol;
    public TableView dataTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void showMe(ActionEvent event) {
        ObservableList<Countries> countryDataList = DBCountries.getAllCountries();
        for(Countries countries : countryDataList) {
            System.out.println("Country ID: " + countries.getCountryID() + " Country Name: " + countries.getCountryName());
        }
    }
}
