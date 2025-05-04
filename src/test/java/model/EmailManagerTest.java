package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailManagerTest {
    private EmailManager emailManager;
    private Email testEmail;
    @BeforeEach
    void setUp() {
        emailManager = new EmailManager();
        testEmail = new Email(1,"Joan", "Peter", "Exam Prep", "Body", LocalDateTime.now());
    }
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