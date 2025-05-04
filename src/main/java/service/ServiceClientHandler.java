package service;

import email_service.EmailUtilities;
import lombok.extern.slf4j.Slf4j;
import model.Email;
import model.EmailManager;
import model.User;
import model.UserManager;
import network.TCPNetworkLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ServiceClientHandler implements Runnable {
    private Socket clientDataSocket;
    private TCPNetworkLayer networkLayer;
    private EmailManager emailManager;
    private UserManager userManager;
    private BufferedReader in;
    private PrintWriter out;
    private User loggedInUser;

    public ServiceClientHandler(Socket clientSocket, UserManager userManager, EmailManager emailManager) {
        this.clientDataSocket = clientSocket;
        this.userManager = userManager;
        this.emailManager = emailManager;
    }

    /**
     * Runs the client handling loop
     * Listens for requests from the client, processes them, and sends responses
     * Closes the socket when the session ends or an error occurs.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientDataSocket.getInputStream()));
            out = new PrintWriter(clientDataSocket.getOutputStream(), true);

            log.info("Client connected: {}", clientDataSocket.getInetAddress());

            String request;
            while ((request = in.readLine()) != null) {
                String response = handleRequest(request);
                out.println(response);
                if (EmailUtilities.ACK.equals(response)) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        } finally {
            try {
                clientDataSocket.close();
                log.info("Closed connection with client: {}", clientDataSocket);
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }

    /**
     * handleRequest parses and routes a client request to the appropriate handler method
     *
     * @param request the full request string from the client
     * @return the server's response string based on the protocol
     */
    private String handleRequest(String request) {
        String[] parts = request.split(EmailUtilities.DELIMITER);
        String command = parts[0];

        switch (command) {
            case EmailUtilities.REGISTER:
                return handleRegister(parts);
            case EmailUtilities.LOGIN:
                return handleLogin(parts);
            case EmailUtilities.SEND:
                return handleSend(parts);
            case EmailUtilities.LIST_RECEIVED:
                return handleListReceived();
            case EmailUtilities.READ:
                return handleRead(parts);
            case EmailUtilities.LOGOUT:
                return handleLogout();
            case EmailUtilities.LIST_SENT:
                return handleListSent();
            case EmailUtilities.SEARCH_RECEIVED:
                return handleSearchReceived(parts);
            case EmailUtilities.SEARCH_SENT:
                return handleSearchSent(parts);

            case EmailUtilities.EXIT:
                return EmailUtilities.ACK;
            default:
                return EmailUtilities.INVALID_REQUEST;
        }
    }

    /**
     * This method handles user registration
     *
     * @param parts the request parts, parts[1] being the username and parts[2] being the password
     * @return a response indicating success, failure, or invalid format
     */
    private String handleRegister(String[] parts) {
        if (parts.length < 3) return EmailUtilities.INVALID_REQUEST;
        String username = parts[1];
        String password = parts[2];

        if (userManager.userExists(username)) return EmailUtilities.USERNAME_TAKEN;

        boolean success = userManager.registerUser(username, password);
        if (success) {
            emailManager.initializeMailbox(username);
            return EmailUtilities.ADDED;
        } else {
            return EmailUtilities.FAILED;
        }
    }

    /**
     * This method handles user login
     *
     * @param parts the request parts,  parts[1] being the username and parts[2] being the password
     * @return a response indicating login success, or reasons for failure
     */
    private String handleLogin(String[] parts) {
        if (parts.length < 3) return EmailUtilities.INVALID_REQUEST;
        String username = parts[1];
        String password = parts[2];

        if (!userManager.userExists(username)) return EmailUtilities.INVALID_USERNAME;
        if (!userManager.authenticate(username, password)) return EmailUtilities.INVALID_PASSWORD;

        loggedInUser = userManager.getUser(username);
        return EmailUtilities.SUCCESSFUL;
    }

    /**
     * This method handles sending an email from the logged-in user to a recipient
     *
     * @param parts the request parts: sender, recipient, subject, message content
     * @return a response indicating whether the email was sent successfully or not
     */
    private String handleSend(String[] parts) {
        if (!isLoggedIn()) return EmailUtilities.FAILED;
        if (parts.length < 4) return EmailUtilities.INVALID_REQUEST;

        String recipient = parts[1];
        String subject = parts[2];
        String content = parts[3];

        if (!userManager.userExists(recipient)) return EmailUtilities.USER_NOT_FOUND;

        return emailManager.sendEmail(loggedInUser.getUsername(), recipient, subject, content);
    }

    /**
     * This method handles the retrieval of metadata for all emails received by the logged-in user
     *
     * @return a formatted string with email metadata or an error message
     */
    private String handleListReceived() {
        if (!isLoggedIn()) return EmailUtilities.FAILED;
        List<model.Email> emails = emailManager.getReceivedEmailsForUser(loggedInUser.getUsername());
        if (emails.isEmpty()) return EmailUtilities.NO_EMAILS_FOUND;

        StringBuilder builder = new StringBuilder();
        for (model.Email email : emails) {
            builder.append(email.getId()).append(EmailUtilities.DELIMITER)
                    .append(email.getSender()).append(EmailUtilities.DELIMITER)
                    .append(email.getSubject()).append(EmailUtilities.DELIMITER)
                    .append(email.getTimeStamp()).append("\n");
        }
        return builder.toString().trim();
    }

    /**
     * This method handles reading the content of a specific received email by its ID
     *
     * @param parts the request parts, where parts[1] is the email ID
     * @return the full content of the email or an error message if not found or invalid
     */
    private String handleRead(String[] parts) {
        if (!isLoggedIn() || parts.length < 2) return EmailUtilities.INVALID_REQUEST;

        try {
            int emailId = Integer.parseInt(parts[1]);
            List<model.Email> emails = emailManager.getReceivedEmailsForUser(loggedInUser.getUsername());

            for (model.Email email : emails) {
                if (email.getId() == emailId) {
                    return email.getSender() + EmailUtilities.DELIMITER +
                            email.getSubject() + EmailUtilities.DELIMITER +
                            email.getMessage() + EmailUtilities.DELIMITER +
                            email.getTimeStamp();
                }
            }
            return EmailUtilities.EMAIL_NOT_FOUND;

        } catch (NumberFormatException e) {
            return EmailUtilities.INVALID_REQUEST;
        }
    }

    /**
     * This method logs out the currently logged-in user
     *
     * @return a response confirming the user is logged out.
     */
    private String handleLogout() {
        loggedInUser = null;
        return EmailUtilities.LOGGED_OUT;
    }

    /**
     * This method checks if a user is currently logged in for this session
     *
     * @return true if a user is logged in, false otherwise.
     */
    private boolean isLoggedIn() {
        return loggedInUser != null;
    }


    private String handleListSent() {
        if (!isLoggedIn()) return EmailUtilities.FAILED;
        List<Email> emails = emailManager.getSentEmailsForUser(loggedInUser.getUsername());
        if (emails.isEmpty()) return EmailUtilities.NO_EMAILS_FOUND;

        StringBuilder builder = new StringBuilder();
        for (Email email : emails) {
            builder.append(email.getId()).append(EmailUtilities.DELIMITER)
                    .append(email.getReceipiant()).append(EmailUtilities.DELIMITER)
                    .append(email.getSubject()).append(EmailUtilities.DELIMITER)
                    .append(email.getTimeStamp()).append("\n");
        }
        return builder.toString().trim();
    }


    private String handleSearchReceived(String[] parts) {
        if (!isLoggedIn() || parts.length < 3) return EmailUtilities.INVALID_REQUEST;

        String type = parts[1];
        String keyword = parts[2];
        List<Email> results;

        if (type.equalsIgnoreCase("subject")) {
            results = emailManager.listRecievedEmailsBySubject(keyword);
        } else if (type.equalsIgnoreCase("sender")) {
            results = emailManager.listRecievedEmailsBySender(keyword);
        } else {
            return EmailUtilities.INVALID_REQUEST;
        }

        return serializeSearchResults(results);
    }


    private String handleSearchSent(String[] parts) {
        if (!isLoggedIn() || parts.length < 3) return EmailUtilities.INVALID_REQUEST;

        String type = parts[1];
        String keyword = parts[2];
        List<Email> results = new ArrayList<>();

        for (Email email : emailManager.getSentEmailsForUser(loggedInUser.getUsername())) {
            if (type.equalsIgnoreCase("recipient") && email.getReceipiant().equalsIgnoreCase(keyword)) {
                results.add(email);
            } else if (type.equalsIgnoreCase("subject") && email.getSubject().equalsIgnoreCase(keyword)) {
                results.add(email);
            }
        }

        return serializeSearchResults(results);
    }



    private String serializeSearchResults(List<Email> emails) {
        if (emails.isEmpty()) return EmailUtilities.NO_EMAILS_FOUND;
        StringBuilder builder = new StringBuilder();
        for (Email email : emails) {
            builder.append(email.getId()).append(EmailUtilities.DELIMITER)
                    .append(email.getSender()).append(EmailUtilities.DELIMITER)
                    .append(email.getSubject()).append(EmailUtilities.DELIMITER)
                    .append(email.getTimeStamp()).append("\n");
        }
        return builder.toString().trim();
    }


}
