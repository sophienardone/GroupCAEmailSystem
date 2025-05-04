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
        boolean result = emailManager.addToSentEmail(testEmail);
        String sender = "Joan";
        assertTrue(result, "Email should be added successfully to sent emails");

        List<Email> sent = emailManager.getSentEmailsForUser("Joan");
        assertNotNull(sent);
        assertEquals(1, sent.size());
        assertEquals(testEmail, sent.get(0));

    }

    @Test
    void addToReceivedEmailVTest() {
    }

    @Test
    void getReceivedEmailsForUserTest() {
    }

    @Test
    void sendEmailTest() {
    }
}