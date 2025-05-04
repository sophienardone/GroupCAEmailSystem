package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
