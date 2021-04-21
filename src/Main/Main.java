package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.sql.*;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/loginPage.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) throws SQLException {
       Connection connection = DBConnection.startConnection();
       //insertRecords();
       //updateRecords();
       //deleteRecords();


        launch(args);

        DBConnection.closeConnection();
    }









    private static void insertRecords() throws SQLException{
        Connection connection = DBConnection.getConnection();

        //SQL insert statement
        String insertStatement = "INSERT INTO countries(Country, Create_Date, Created_By, Last_Updated_By) VALUES(?,?,?,?)";
        DBQuery.setPreparedStatement(connection, insertStatement); //Create prepared statement object
        PreparedStatement preparedStatement = DBQuery.getPreparedStatement();

        String countryName;
        String createDate = "2021-04-06 00:00:00";
        String createdBy = "Admin";
        String lastUpdateBy = "Admin";

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a country name to insert into the database: ");
        countryName = scanner.nextLine();


        //key value mapping
        preparedStatement.setString(1, countryName);
        preparedStatement.setString(2, createDate);
        preparedStatement.setString(3, createdBy);
        preparedStatement.setString(4, lastUpdateBy);

        preparedStatement.execute();

        //check rows affected
        if(preparedStatement.getUpdateCount() > 0) {
            System.out.println(preparedStatement.getUpdateCount() + " row(s) affected");
        }
        else {
            System.out.println("No changes made.");
        }
    }

    private static void updateRecords() throws SQLException{
        Connection connection = DBConnection.getConnection();
        String updateStatement = "UPDATE countries SET Country = ?, Created_By = ? WHERE Country = ?";
        DBQuery.setPreparedStatement(connection, updateStatement);
        PreparedStatement preparedUpdateStatement = DBQuery.getPreparedStatement();

        String countryName, newCountry, createdBy;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a country to update: ");
        countryName = scanner.nextLine();

        System.out.println("Enter new country name: ");
        newCountry = scanner.nextLine();

        System.out.println("Enter user: ");
        createdBy = scanner.nextLine();

        preparedUpdateStatement.setString(1, newCountry);
        preparedUpdateStatement.setString(2, createdBy);
        preparedUpdateStatement.setString(3, countryName);

        preparedUpdateStatement.execute();



        if(preparedUpdateStatement.getUpdateCount() > 0) {
            System.out.println("Number of rows affected: " + preparedUpdateStatement.getUpdateCount());
        }
        else {
            System.out.println("Values not inserted/updated");
        }
    }


    private static void deleteRecords() throws SQLException{
        Connection connection = DBConnection.getConnection();
        String deleteStatement = "DELETE from countries where Country_ID = ?";

        //SQL insert statement
        DBQuery.setPreparedStatement(connection, deleteStatement); //Create prepared statement object
        PreparedStatement preparedDeleteStatement = DBQuery.getPreparedStatement();
        preparedDeleteStatement.setInt(1, 6);

        preparedDeleteStatement.execute();

        if(preparedDeleteStatement.getUpdateCount() > 0) {
            System.out.println("Number of rows affected: " + preparedDeleteStatement.getUpdateCount());
        }
        else {
            System.out.println("Values not deleted");
        }
    }
}
