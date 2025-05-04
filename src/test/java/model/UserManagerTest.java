package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserManagerTest {

    private UserManager userManager;

    @BeforeEach
    void setUp(){
        userManager = new UserManager();
    }

    @Test
    void testRegisteringUser_Successfully(){
        boolean result = userManager.registerUser("sophietest", "Test123");
        assertTrue(result);
        assertTrue(userManager.userExists("sophietest"));
    }

    @Test
    void test_CorrectUserPassword(){
        userManager.registerUser("sophietest", "Test123");
        boolean result = userManager.authenticate("sophietest","Test123");
        assertTrue(result);
    }


    @Test
    void test_UserExists(){
        userManager.registerUser("sophietest", "Test123");
        assertTrue(userManager.userExists("sophietest"));
        assertFalse(userManager.userExists("nonexistent"));
    }

    @Test
    void test_NonexistentUser(){
     boolean result = userManager.authenticate("fakeuser", "fakeuser123");
     assertFalse(result);
    }

    @Test
    void testRegisterUser_Duplicate() {
        userManager.registerUser("bob", "secret");
        boolean result = userManager.registerUser("bob", "anotherSecret");
        assertFalse(result);
    }

    @Test
    void testAuthenticate_WrongPassword() {
        userManager.registerUser("dave", "pass123");
        boolean result = userManager.authenticate("dave", "wrongpass");
        assertFalse(result);
    }

    @Test
    void testGetUser() {
        userManager.registerUser("frank", "frankpass");
        User user = userManager.getUser("frank");
        assertNotNull(user);
        assertEquals("frank", user.getUsername());
    }
}
