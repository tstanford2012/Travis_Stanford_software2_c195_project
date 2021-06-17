package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;

import java.sql.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //opens the log in page at the beginning of the application
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/loginPage.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) throws SQLException {
        //Starts the database connection
       Connection connection = DBConnection.startConnection();


        launch(args);

        DBConnection.closeConnection();
    }
}
