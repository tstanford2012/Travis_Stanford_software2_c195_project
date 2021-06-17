package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBQuery {
    private static PreparedStatement statement; //statement reference

    //Create statement object
    public static void setPreparedStatement(Connection connection, String sqlStatement) throws SQLException {
        statement = connection.prepareStatement(sqlStatement);
    }

    //return statement object
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }
    //returns the prepared statement when called
    public static PreparedStatement getPreparedStatement(int generatedKeys) {
        return statement;
    }
}
