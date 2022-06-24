package test.AcceptanceTests;

import main.DTO.UserDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.Before;
import org.junit.Test;
import test.testUtils.testsFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class AT_Req_Constraints {

    Response<String> adminToken, founder1token, user1token, user2token, user3token;
    IService service;
    int threadCount;

    @Before
    public void setUp() throws Exception {
        this.threadCount = 10000;
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

    @Test
    public void ConcurrentCheckUsernameIsUnique() throws InterruptedException{
        AtomicInteger successCounter = new AtomicInteger(0);
        Runnable registerSameUser = () -> {
            Response<Boolean> response = service.register("user11","123456");
            if(!response.isError_occured()) {
                successCounter.getAndIncrement();
            }
        };
        Thread[] registerThreads = new Thread[threadCount];
        for(int i=0;i<threadCount;i++) {
            registerThreads[i] = new Thread(registerSameUser);
        }
        for(int i=0;i<threadCount;i++) {
            registerThreads[i].start();
        }
        for(int i=0;i<threadCount;i++) {
            registerThreads[i].join();
        }

        assertEquals(1, successCounter.get());
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
            ownerExists |= u.contains("Owner of the store");
            ownerExists |= u.contains("Founder of the store");
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
//    @Test
//    public void checkPurchaseWithBadSupplyAndPayment() {
//        Response<Boolean> res = service.addProductToStore(founder1token.getResult(),"Bamba","Snacks",null,"nice snack","MyStore1",20,22);
//        Response<Boolean> res2 = service.addProductToCart(user1token.getResult(),"MyStore1","Bamba",5);
//        Response<Boolean> res3 = service.purchaseCart(user1token.getResult(), testsFactory.getSomePI(), testsFactory.getSomeSI());
//        assertTrue(res3.isError_occured());
//    }
}
