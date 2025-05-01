package email_service;

public class EmailUtilities {
    public static final String HOSTNAME = "localhost";
    public static final int PORT = 11000;

    // REQUESTS
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String SEND = "SEND";
    public static final String LIST_RECEIVED = "LIST_RECEIVED";
    public static final String SEARCH = "SEARCH";
    public static final String READ = "READ";
    public static final String LOGOUT = "LOGOUT";
    public static final String EXIT = "EXIT ";


    // DELIMITERS
    public static final String DELIMITER = "%%";
    //public static final String EMAIL_DELIMITER = "~~";

    // RESPONSES
    public static final String ADDED = "ADDED";
    public static final String USERNAME_TAKEN = "USERNAME_TAKEN";
    public static final String INVALID_PASSWORD = "INVALID_PASSWORD";

    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String FAILED = "FAILED";
    public static final String EMAIL_SENT = "EMAIL_SENT";
    public static final String USER_NOT_FOUND= "USER_NOT_FOUND";
    public static final String EMAIL = "EMAIL";
    public static final String NO_EMAILS_FOUND= "NO_EMAILS_FOUND";
    public static final String ACK = "GOODBYE";
    public static final String END_PROGRAM = "PROGRAM_SHUTTING_DOWN";

    // GENERAL MALFORMED RESPONSE:
    public static final String INVALID_REQUEST = "INVALID_REQUEST";
}
