package FutureIteration;

import FutureIteration.Countries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DBCountries {
    public static ObservableList<Countries> getAllCountries() {
        ObservableList<Countries> countryList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * from countries";

            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                int countryId = resultSet.getInt("Country_ID");
                String countryName = resultSet.getString("Country");
                Countries countries = new Countries(countryId, countryName);
                countryList.add(countries);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return countryList;
    }

    public static void checkDateConversion() {
        System.out.println("CREATE DATE TEST");
        String sql = "select Create_Date from countries";
        try {
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("Create_Date");
                System.out.println("CD: " + timestamp.toLocalDateTime().toString());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
