package client;

import email_service.EmailUtilities;
import network.TCPNetworkLayer;

import java.io.IOException;
import java.util.Scanner;

public class TCPEmailClient {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);

        //  network layer with hostname and port
        TCPNetworkLayer network = new TCPNetworkLayer(EmailUtilities.HOSTNAME, EmailUtilities.PORT);

        try {
            // Connect to the server
            network.connect();

            boolean isRunning = true;

            while (isRunning) {
                System.out.println("\nAvailable Commands:");
                System.out.println("1. REGISTER");
                System.out.println("2. LOGIN");
                System.out.println("3. SEND EMAIL");
                System.out.println("4. LIST RECEIVED EMAILS");
                System.out.println("5. READ EMAIL BY ID");
                System.out.println("6. LOGOUT");
                System.out.println("7. EXIT");

                System.out.print("\nEnter command number: ");
                String choice = userInput.nextLine();

                String message = "";

                switch (choice) {
                    case "1":
                        System.out.print("Username: ");
                        String regUser = userInput.nextLine();
                        System.out.print("Password: ");
                        String regPass = userInput.nextLine();
                        message = EmailUtilities.REGISTER + EmailUtilities.DELIMITER + regUser + EmailUtilities.DELIMITER + regPass;
                        break;

                    case "2":
                        System.out.print("Username: ");
                        String logUser = userInput.nextLine();
                        System.out.print("Password: ");
                        String logPass = userInput.nextLine();
                        message = EmailUtilities.LOGIN + EmailUtilities.DELIMITER + logUser + EmailUtilities.DELIMITER + logPass;
                        break;

                    case "3":
                        System.out.print("Recipient: ");
                        String recipient = userInput.nextLine();
                        System.out.print("Subject: ");
                        String subject = userInput.nextLine();
                        System.out.print("Message: ");
                        String content = userInput.nextLine();
                        message = EmailUtilities.SEND + EmailUtilities.DELIMITER + recipient + EmailUtilities.DELIMITER + subject + EmailUtilities.DELIMITER + content;
                        break;

                    case "4":
                        message = EmailUtilities.LIST_RECEIVED;
                        break;

                    case "5":
                        System.out.print("Enter Email ID to read: ");
                        String emailId = userInput.nextLine();
                        message = EmailUtilities.READ + EmailUtilities.DELIMITER + emailId;
                        break;

                    case "6":
                        message = EmailUtilities.LOGOUT;
                        break;

                    case "7":
                        message = EmailUtilities.EXIT;
                        isRunning = false;
                        break;

                    default:
                        System.out.println("Invalid choice.");
                        continue;
                }

                // Send the message and print the server response
                network.send(message);
                String response = network.receive();
                System.out.println("Response: " + response);
            }

            // Disconnect when done
            network.disconnect();

        } catch (IOException e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        }
    }
}
