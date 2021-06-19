package Model;

/**
 * This class contains the attributes, constructors, getters, and setters for the user object
 */
public class User {
    private static int userID;
    private static String userName;

    /**
     *
     * @param userID
     * @param userName
     * constructor
     */
    public User(int userID, String userName) {
        User.userID = userID;
        User.userName = userName;
    }

    /**
     *
     * @return
     *
     * getters
     */
    public static int getUserID() {
        return userID;
    }

    public static String getUserName() {
        return userName;
    }


    /**
     *
     * setters
     */
    public void setUserID(int userID) {
        User.userID = userID;
    }

    public void setUserName(String userName) {
        User.userName = userName;
    }
}
