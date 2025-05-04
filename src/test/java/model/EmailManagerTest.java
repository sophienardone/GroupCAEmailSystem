package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit test class for the {@link EmailManager} class.
 * This class tests the functionality related to sending and receiving emails,
 * and ensures emails are correctly added and retrieved from user-specific email lists.
 *
 * <p>Test cases include:</p>
 * <ul>
 *   <li>Adding an email to sent emails</li>
 *   <li>Adding an email to received emails</li>
 *   <li>Handling null emails when adding to received emails</li>
 * </ul>
 */

class EmailManagerTest {
    private EmailManager emailManager;
    private Email testEmail;
    /**
     * Sets up a fresh instance of {@link EmailManager} and a sample {@link Email}
     * object before each test is run.
     */
    @BeforeEach
    void setUp() {
        emailManager = new EmailManager();
        testEmail = new Email(1,"Joan", "Peter", "Exam Prep", "Body", LocalDateTime.now());
    }
    /**
     * Tests that a valid email is successfully added to the sent emails list for a user.
     * Verifies that the email appears in the sender's sent emails list.
     */
    @Test
    void addToSentEmailTest() {
        System.out.println("Testint Email added successfully to sent emails");
        boolean result = emailManager.addToSentEmail(testEmail);
        String sender = "Joan";
        assertTrue(result);
        //Email should be added successfully to sent emails
        List<Email> sent = emailManager.getSentEmailsForUser(sender);
        assertNotNull(sent);
        assertEquals(1, sent.size());
        assertEquals(testEmail, sent.get(0));

    }
    /**
     * Tests that a valid email is successfully added to the received emails list for a user.
     * Verifies that the email appears in the recipient's received emails list.
     */
    @Test
    void addToReceivedEmailTest() {
        System.out.println(" Testing Email added successfully to Recieved emails");
        boolean result = emailManager.addToReceivedEmail(testEmail);
        String reciever = "Peter";
        assertTrue(result);

        List<Email> recieved = emailManager.getReceivedEmailsForUser(reciever);
        assertNotNull(recieved);
        assertEquals(1, recieved.size());
        assertEquals(testEmail, recieved.get(0));
    }
    /**
     * Tests that adding a null email to the received emails list fails.
     * Verifies that no email is added and the method returns false.
     */
    @Test
    void addToReceivedEmailFailedTest() {
        System.out.println(" Testing Adding Email to Recieved emails FAILED");
        Email Null = null;

        boolean result = emailManager.addToReceivedEmail(Null);
        assertFalse(result); // "Adding a null email should return false"

        List<Email> received = emailManager.getReceivedEmailsForUser("Peter");
        assertTrue(received == null || received.isEmpty()); // "No emails should be added for a null email"

    }

    @Test
    void getReceivedEmailsForUserTest() {
    }

    @Test
    void sendEmailTest() {
    }
}