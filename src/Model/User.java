package Model;

public class User {
    private static int userID;
    private static String userName;

    //constructor
    public User(int userID, String userName) {
        User.userID = userID;
        User.userName = userName;
    }

    //getters and setters
    public static int getUserID() {
        return userID;
    }

    public static String getUserName() {
        return userName;
    }

    public void setUserID(int userID) {
        User.userID = userID;
    }

    public void setUserName(String userName) {
        User.userName = userName;
    }
}
