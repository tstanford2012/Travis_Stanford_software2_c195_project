package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
    public static PreparedStatement getPreparedStatement(int generatedKeys) {
        return statement;
    }
}
