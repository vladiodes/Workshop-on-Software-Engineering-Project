package test.UnitTests;

import main.Security.Security;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityTest {

    private Security security=new Security();

    @Before
    public void setup(){
        security=new Security();
    }


    @Test
    void testHashingFunction() {
        String password="H1E2L3";
        String hashed_password= security.hashPassword(password);
        assertNotEquals(password, hashed_password);
    }

    @Test
    void testHashingFunctionSensitiveCase(){
        hashTwoPasswordsAndTestForInequality("H1E2L3", "H1e2L3");
    }

    private void hashTwoPasswordsAndTestForInequality(String password1, String password2) {
        String hashed1 = security.hashPassword(password1);
        String hashed2 = security.hashPassword(password2);
        assertNotEquals(hashed1, hashed2);
    }

    @Test
    void testHashingSamePrefix(){
        hashTwoPasswordsAndTestForInequality("HelloWorld", "HelloWor");
    }

    @Test
    void testHashingSameSuffix(){
        hashTwoPasswordsAndTestForInequality("HelloWorld", "GoodbyeWorld");
    }

    @Test
    void testHashingSamePassword(){
        String password1="Same_password";
        String password2="Same_password";
        String hashed1=security.hashPassword(password1);
        String hashed2=security.hashPassword(password2);
        assertEquals(hashed1,hashed2);
    }

    @Test
    void testHashingWhiteSpacesAndWhiteSpace(){
        hashTwoPasswordsAndTestForInequality("           "," ");
    }

    @Test
    void testHashingSingleChar(){
        hashTwoPasswordsAndTestForInequality("a","b");
    }


}