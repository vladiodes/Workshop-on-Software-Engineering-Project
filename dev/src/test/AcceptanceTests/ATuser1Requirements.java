package test.AcceptanceTests;

import main.DTO.ProductDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.testUtils.testsFactory;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ATuser1Requirements {

    Response<String> usertoken1, usertoken2, usertoken3, usertoken4;
    IService System;
    String userName = "userName";
    String insecurePassword = "123";
    String securePassword = "Ligma123";
    @Before
    public void setUp() throws Exception {
        System = new Service("TestingConfig.json");
        usertoken1 = System.guestConnect();
        usertoken2 = System.guestConnect();
        usertoken3 = System.guestConnect();
        usertoken4 = System.guestConnect();
        assertFalse(usertoken1.isError_occured() || usertoken2.isError_occured() || usertoken3.isError_occured() || usertoken4.isError_occured());
    }

    /***
     * use case: Registering req 1.3:
     */
    @Test
    public void Registering(){
        //Register with insecure password - fail
        assertTrue(System.register(userName, insecurePassword).isError_occured());
        //Register with secured password - success
        assertFalse(System.register(userName, securePassword).isError_occured());
        //Register with username already is use - fail
        assertTrue(System.register(userName, securePassword).isError_occured());
    }
    /***
     * use case: Login req 1.4:
     */
    @Test
    public void Login(){
        System.register(userName, securePassword);
        //Login bad password - fail
        assertTrue(System.login(usertoken1.getResult(), userName, "BadPassword").isError_occured());
        //Login good password - success
        assertFalse(System.login(usertoken1.getResult(), userName, securePassword).isError_occured());
        //Duplicate login - fail
        assertTrue(System.login(usertoken1.getResult(), userName, securePassword).isError_occured());
    }

    @Test
    public void unknownUsernameLogin()
    {
        assertTrue(System.login(usertoken1.getResult(), "UnkownUserName", securePassword).isError_occured());
    }

    @After
    public void tearDown(){

    }

}
