package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    //JDBC URL parts
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com:3306/";
    private static final String dbName = "WJ06gVI";

    //JDBC URL
    private static final String jdbcURL = protocol + vendorName + ipAddress + dbName;

    //Driver and connection interface reference
    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    private static final String username = "U06gVI";

    //attempts the connect to the database and prints an error if unsuccessful
    //returns the connection if successful
    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, username, DBPassword.getPassword());
            System.out.println("Connection Successful");
        }
        catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("Connection Unsuccessful");
        }
        return conn;

    }
    //returns the database connection when called
    public static Connection getConnection() {
        return conn;
    }

    //closes the database connection when called
    public static void closeConnection() {
        try {
            conn.close();
        }
        catch (Exception e) {
            //do nothing
        }
    }
}
