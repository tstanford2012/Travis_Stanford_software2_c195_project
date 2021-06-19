package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class allows the user to use prepared statements to query the database
 */
public class DBQuery {
    private static PreparedStatement statement; //statement reference

    /**
     *
     * @param connection as a parameter
     * @param sqlStatement takes the SQL statement to save into the prepared statement
     * @throws SQLException
     *
     * Create prepared statement
     */
    public static void setPreparedStatement(Connection connection, String sqlStatement) throws SQLException {
        statement = connection.prepareStatement(sqlStatement);
    }

    //

    /**
     *
     * @return returns the prepared statement
     */
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }

    /**
     *
     * @return returns the prepared statement when called
     *
     */
    public static PreparedStatement getPreparedStatement(int generatedKeys) {
        return statement;
    }
}
