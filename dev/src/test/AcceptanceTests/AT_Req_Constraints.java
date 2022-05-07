package test.AcceptanceTests;

import main.DTO.ShoppingCartDTO;
import main.DTO.UserDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import test.testUtils.testsFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AT_Req_Constraints {

    Response<String> adminToken, founder1token, user1token, user2token, user3token;
    IService service;

    @Before
    public void setUp() {
        service = new Service(testsFactory.alwaysSuccessPayment(), testsFactory.alwaysSuccessSupplyer());
        adminToken = service.guestConnect();
        founder1token = service.guestConnect();
        user1token = service.guestConnect();
        user2token = service.guestConnect();

        service.register("founder1", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.openStore(founder1token.getResult(), "MyStore1");
    }

        /*
    Truth Constraint number 1
     */

    @Test
    public void checkOnlyOneUsernameIdentifierInSystem(){
        Response<List<String>> res = service.getPurchaseHistory(founder1token.getResult(),"founder");
        Response<List<String>> res2 = service.getPurchaseHistory(founder1token.getResult(),"founder1");
        assertTrue(res.isError_occured() && !res2.isError_occured());

    }

    /*
    Truth Constraint number 2
     */
    @Test
    public void checkAdminExistence() {
        Response<UserDTO> res = service.login(adminToken.getResult(),"admin","admin");
        assertFalse(res.isError_occured() && res.getResult() == null);
    }

    /*
    Truth Constraint number 3
     */
    @Test
    public void checkOwnerOrFounderAreMembers() {
        Response<Boolean> res = service.appointStoreManager(user1token.getResult(),"user1","MyStore");
        Response<Boolean> res2 = service.appointStoreOwner(user1token.getResult(),"user1","MyStore");
        assertTrue(res.isError_occured() && res2.isError_occured());
    }

    /*
    Truth Constraint number 5
     */

    @Test
    public void checkAtLeaseOneOwnerForStore(){
        boolean ownerExists = false;
       Response<List<String>> staff = service.getStoreStaff(founder1token.getResult(), "MyStore1");
        for (String u : staff.getResult()) {
            ownerExists |= staff.getResult().contains("Owner of the store");
            ownerExists |= staff.getResult().contains("Founder of the store");
        }
        assertTrue(ownerExists);
    }

    /*
    Truth Constraint number 9 (Inventory Size)
     */
    @Test
    public void checkInventorySize() {
        Response<Boolean> res = service.addProductToStore(founder1token.getResult(),"Bamba","Snacks",null,"nice snack","MyStore1",-5,22);
        assertTrue(res.isError_occured());
    }
    /*
    Truth Constraint number 9 (Buy Quantity has to be less or equal to inventory quantity)
     */
    @Test
    public void checkBuyQuantityLessThanInventory(){
        Response<Boolean> res = service.addProductToStore(founder1token.getResult(),"Bamba","Snacks",null,"nice snack","MyStore1",20,22);
        Response<Boolean> res2 = service.addProductToCart(user1token.getResult(),"MyStore1","Bamba",21);
        assertTrue(res2.isError_occured());
    }
    /*
    Truth Constraint number 10.2
     */
    @Test
    public void checkPurchaseWithBadSupplyAndPayment() {
        Response<Boolean> res = service.addProductToStore(founder1token.getResult(),"Bamba","Snacks",null,"nice snack","MyStore1",20,22);
        Response<Boolean> res2 = service.addProductToCart(user1token.getResult(),"MyStore1","Bamba",5);
        Response<Boolean> res3 = service.purchaseCart(user1token.getResult(), testsFactory.getSomePI(), testsFactory.getSomeSI());
        assertTrue(res3.isError_occured());
    }
}
