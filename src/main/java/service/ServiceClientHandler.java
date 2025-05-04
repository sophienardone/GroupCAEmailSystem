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

    private String handleLogin(String[] parts) {
        if (parts.length < 3) return EmailUtilities.INVALID_REQUEST;
        String username = parts[1];
        String password = parts[2];

        if (!userManager.userExists(username)) return EmailUtilities.INVALID_USERNAME;
        if (!userManager.authenticate(username, password)) return EmailUtilities.INVALID_PASSWORD;

        loggedInUser = userManager.getUser(username);
        return EmailUtilities.SUCCESSFUL;
    }

    private String handleSend(String[] parts) {
        if (!isLoggedIn()) return EmailUtilities.FAILED;
        if (parts.length < 4) return EmailUtilities.INVALID_REQUEST;

        String recipient = parts[1];
        String subject = parts[2];
        String content = parts[3];

        if (!userManager.userExists(recipient)) return EmailUtilities.USER_NOT_FOUND;

        return emailManager.sendEmail(loggedInUser.getUsername(), recipient, subject, content);
    }

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

    private String handleLogout() {
        loggedInUser = null;
        return EmailUtilities.LOGGED_OUT;
    }

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
