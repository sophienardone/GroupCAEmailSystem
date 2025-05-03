package email_service;

import model.Email;
import model.EmailManager;
import model.User;
import model.UserManager;
import service.ServiceClientHandler;
import utils.SecurityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailTCPServer {

    private static final int PORT = EmailUtilities.PORT;
    private static UserManager userManager = new UserManager();
    private static EmailManager emailManager = new EmailManager();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // creating a new thread for each client connection
//                new ClientHandler(clientSocket).start();
                ServiceClientHandler clientHandler = new ServiceClientHandler(clientSocket, userManager, emailManager);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Error establishing server connection: " + e.getMessage());
        }
    }

//    private static class ClientHandler extends Thread {
//        private Socket clientSocket;
//        private BufferedReader in;
//        private PrintWriter out;
//        private boolean loggedIn = false;
//        private String currentUser = null;
//
//        public ClientHandler(Socket clientSocket) {
//            this.clientSocket = clientSocket;
//        }
//
//        @Override
//        public void run() {
//            try {
//                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                out = new PrintWriter(clientSocket.getOutputStream(), true);
//
//                String clientRequest;
//                while ((clientRequest = in.readLine()) != null) {
//                    System.out.println("Received: " + clientRequest);
//                    String response = handleRequest(clientRequest);
//                    out.println(response);
//                }
//            } catch (IOException e) {
//                System.out.println("Error in client communication: " + e.getMessage());
//            } finally {
//                try {
//                    clientSocket.close();  // Close the connection to client socket
//                } catch (IOException e) {
//                    System.out.println("Error closing client socket: " + e.getMessage());
//                }
//            }
//        }
//
//        private String handleRequest(String request) {
//            String[] components = request.split(EmailUtilities.DELIMITER);  // splits the incoming request
//            String command = components[0];
//
//            switch (command) {
//                case EmailUtilities.REGISTER:
//                    return handleRegister(components);
//                case EmailUtilities.LOGIN:
//                    return handleLogin(components);
//                case EmailUtilities.SEND:
//                    return handleSendEmail(components);
//                case EmailUtilities.LIST_RECEIVED:
//                    return handleListReceivedEmails();
//                case EmailUtilities.READ:
//                    return handleReadEmail(components);
//                case EmailUtilities.LOGOUT:
//                    return handleLogout();
//                default:
//                    return EmailUtilities.INVALID_REQUEST;
//            }
//        }
//
//        private String handleRegister(String[] components) {
//            if (components.length != 3) {
//                return EmailUtilities.INVALID_REQUEST;
//            }
//
//            String username = components[1];
//            String password = components[2];
//
//            // Attempt to register the user using the UserManager
//            // If the username already exists, registration fails
//            if (!userManager.registerUser(username, password)) {
//                return EmailUtilities.USERNAME_TAKEN;
//            }
//
//            return EmailUtilities.ADDED;
//        }
//
//
//
//        private String handleLogin(String[] components) {
//            if (components.length != 3) {
//                return EmailUtilities.INVALID_REQUEST;
//            }
//
//            String username = components[1];
//            String password = components[2];
//
//            // Check if the username exists in the system
//            if (!userManager.userExists(username)) {
//                return EmailUtilities.USER_NOT_FOUND;
//            }
//
//            // Verify the password against the stored (hashed) password
//            if (!userManager.authenticate(username, password)) {
//                return EmailUtilities.FAILED;
//            }
//
//            loggedIn = true;
//            currentUser = username;
//            return EmailUtilities.SUCCESSFUL;
//        }
//
//
//
//        private String handleSendEmail(String[] components) {
//            if (!loggedIn) {
//                return EmailUtilities.INVALID_REQUEST;
//            }
//
//            if (components.length != 4) {
//                return EmailUtilities.INVALID_REQUEST;
//            }
//
//            String recipient = components[1];
//            String subject = components[2];
//            String message = components[3];
//
//            //  Use userManager to check if recipient exists
//            if (!userManager.userExists(recipient)) {
//                return EmailUtilities.USER_NOT_FOUND;
//            }
//
//            // Create the email and store it
//            Email email = new Email(++EmailManager.emailCount, currentUser, recipient, subject, message, LocalDateTime.now());
//
//            // Save in both sent and received
//            emailManager.addToSentEmail(email);
//            emailManager.addToReceivedEmail(email);
//
//            return EmailUtilities.EMAIL_SENT;
//        }
//
//
//        private String handleListReceivedEmails() {
//            if (!loggedIn) {
//                return EmailUtilities.INVALID_REQUEST;
//            }
//
//
//            return serializeEmails(emailManager.getReceivedEmailsForUser(currentUser));
//        }
//
//        private String handleReadEmail(String[] components) {
//            if (!loggedIn) {
//                return EmailUtilities.INVALID_REQUEST;
//            }
//
//            if (components.length != 2) {
//                return EmailUtilities.INVALID_REQUEST;
//            }
//
//            int emailId = Integer.parseInt(components[1]);
//            Email email = findEmailById(emailId);
//            if (email == null) {
//                return EmailUtilities.EMAIL_NOT_FOUND;
//            }
//
//            return "EMAIL_CONTENT" + EmailUtilities.DELIMITER + email.getMessage();
//        }
//
//        private String handleLogout() {
//            if (!loggedIn) {
//                return EmailUtilities.INVALID_REQUEST;
//            }
//
//            loggedIn = false;
//            currentUser = null;
//            return EmailUtilities.LOGGED_OUT;
//        }
//
//        private Email findEmailById(int emailId) {
//            for (Email email : emailManager.getReceivedEmailsForUser(currentUser)) {
//                if (email.getId() == emailId) {
//                    return email;
//                }
//            }
//            return null;
//        }
//
//
//        private String serializeEmails(List<Email> emails) {
//            if (emails == null || emails.isEmpty()) {
//                return EmailUtilities.NO_EMAILS_FOUND;
//            }
//
//            StringBuilder serialized = new StringBuilder();
//            for (Email email : emails) {
//                serialized.append(serializeEmail(email)).append(EmailUtilities.DELIMITER);
//            }
//            return serialized.toString();
//        }
//
//        private String serializeEmail(Email email) {
//            return email.getId() + EmailUtilities.DELIMITER + email.getSender() + EmailUtilities.DELIMITER
//                    + email.getReceipiant() + EmailUtilities.DELIMITER + email.getSubject() + EmailUtilities.DELIMITER
//                    + email.getTimeStamp();
//        }

    }

