package client;

import email_service.EmailUtilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPEmailClient {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);

        try {
            Socket socket = new Socket(EmailUtilities.HOSTNAME, EmailUtilities.PORT);
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            boolean isRunning = true;
            while (isRunning) {
                System.out.println("\nAvailable Commands:");
                System.out.println("1. REGISTER");
                System.out.println("2. LOGIN");
                System.out.println("3. SEND");
                System.out.println("4. LIST_RECEIVED");
                System.out.println("5. READ");
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

                // Send the message
                out.println(message);

                // Read and print the response
                String response = in.nextLine();
                System.out.println("Response: " + response);
            }

            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}
