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
    public static Connection getConnection() {
        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
        }
        catch (Exception e) {
            //do nothing
        }
    }

    /*
    Info about working with the database:
    Using the execute() method we can run SQL queries
    It accepts a string argument. The select statement is the argument (ex execute(select * from customers))
    The method returns a boolean. If true is returned, a select statement was executed.
    If false was returned, an insert into, update, or delete statement was executed.

    getUpdateCount() method returns the number of rows affected by an insert into, update, or delete command.
    getUpdateCount should only be called if execute() returns false.


     */

}
