package test.AcceptanceTests;

import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AT_Req2_6 {

    Response<String> adminToken, founder1token, user1token;
    IService service = new Service();

    @Before
    public void setUp() {
        adminToken = service.guestConnect();
        founder1token = service.guestConnect();
        user1token = service.guestConnect();

        service.register("founder1", "12345678");
        service.register("user1", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(user1token.getResult(), "user1", "12345678");
        service.login(adminToken.getResult(), "admin", "admin");
        service.openStore(founder1token.getResult(), "MyStore1");
    }

    /***
     * use case: Deleting A Store req 6.1:
     */
    @Test
    public void deleteStore() {
        // this function fails because only an admin can invoke this
        assertTrue(service.deleteStore(adminToken.getResult(), "MyStore1").getResult());
        assertTrue(service.getStoreInfo("MyStore1").isError_occured());
    }

    /***
     * use case: Deleting A Store req 6.1:
     */
    @Test
    public void deleteNotRealStore() {
        assertTrue(service.deleteStore(adminToken.getResult(), "NotARealStore").isWas_expected_error());
    }

    /***
     * use case: Cancelling A Membership req 6.2:
     */
    @Test
    public void deleteUser() {
        assertTrue(service.deleteUser(adminToken.getResult(), "user1").getResult());
    }

    /***
     * use case: Cancelling A Membership req 6.2:
     */
    @Test
    public void deleteUserWithStore() {
        assertTrue(service.deleteUser(adminToken.getResult(), "founder1").getResult());
        // TODO: What should happen?
    }

    /***
     * use case: Cancelling A Membership req 6.2:
     */
    @Test
    public void deleteNotRealUser() {
        assertTrue(service.deleteUser(adminToken.getResult(), "NotARealUser").isError_occured());
    }

    /***
     * use case: Reading And Commenting Complaints  req 6.3:
     */
    @Test
    public void receiveMessages() {
        service.sendComplaint(user1token.getResult(), "complaint");
        Response<List<String>> resp = service.receiveMessages(adminToken.getResult());
        assertFalse(resp.getResult().isEmpty());
        assertEquals(resp.getResult().get(0), "complaint");
    }

    /***
     * use case: Reading And Commenting Complaints  req 6.3:
     */
    @Test
    public void receiveMessagesNotAdmin() {
        service.sendComplaint(user1token.getResult(), "complaint");
        assertTrue(service.receiveMessages(founder1token.getResult()).isWas_expected_error());
    }

    /***
     * use case: Getting A User Purchase History req 6.4:
     */
    @Test
    public void getUserPurchaseHistory() {
        assertNotNull(service.getPurchaseHistory(adminToken.getResult(), "user1").getResult());
    }

    /***
     * use case: Getting A User Purchase History req 6.4:
     */
    @Test
    public void getUserPurchaseHistoryNotRealUser() {
        assertTrue(service.getPurchaseHistory(adminToken.getResult(), "NotARealUser").isWas_expected_error());
    }

    /***
     * use case: Getting A User Purchase History req 6.4:
     */
    @Test
    public void getUserPurchaseHistoryNotAdmin() {
        assertTrue(service.getPurchaseHistory(founder1token.getResult(), "user1").isWas_expected_error());
    }

    /***
     * use case: Getting A Store Purchase History req 6.4:
     */
    @Test
    public void getStorePurchaseHistory() {
        assertNotNull(service.getStorePurchaseHistory(adminToken.getResult(), "MyStore1").getResult());
    }

    /***
     * use case: Getting A Store Purchase History req 6.4:
     */
    @Test
    public void getStorePurchaseHistoryNotRealStore() {
        assertTrue(service.getStorePurchaseHistory(user1token.getResult(), "NotARealStore").isWas_expected_error());
    }

    /***
     * use case: Getting A Store Purchase History req 6.4:
     */
    @Test
    public void getStorePurchaseHistoryNotAdmin() {
        assertTrue(service.getStorePurchaseHistory(user1token.getResult(), "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Getting System Information And Statistics req 6.5:
     */
    @Test
    public void getNumberOfLoggedInUsersPerDate() {
        assertNotNull(service.getNumberOfLoggedInUsersPerDate(adminToken.getResult(), LocalDate.now()).getResult());
    }

    /***
     * use case: Getting System Information And Statistics req 6.5:
     */
    @Test
    public void getNumberOfLoggedInUsersPerDateNotAdmin() {
        assertTrue(service.getNumberOfLoggedInUsersPerDate(user1token.getResult(), LocalDate.now()).isWas_expected_error());
    }

    /***
     * use case: Getting System Information And Statistics req 6.5:
     */
    @Test
    public void getNumberOfRegisteredUsersPerDate() {
        assertNotNull(service.getNumberOfRegisteredUsersPerDate(adminToken.getResult(), LocalDate.now()).getResult());
    }

    /***
     * use case: Getting System Information And Statistics req 6.5:
     */
    @Test
    public void getNumberOfRegisteredUsersPerDateNotAdmin() {
        assertTrue(service.getNumberOfRegisteredUsersPerDate(user1token.getResult(), LocalDate.now()).isWas_expected_error());
    }

    /***
     * use case: Getting System Information And Statistics req 6.5:
     */
    @Test
    public void getNumberOfPurchasesPerDate() {
        assertNotNull(service.getNumberOfPurchasesPerDate(adminToken.getResult(), LocalDate.now()).getResult());
    }

    /***
     * use case: Getting System Information And Statistics req 6.5:
     */
    @Test
    public void getNumberOfPurchasesPerDateNotAdmin() {
        assertTrue(service.getNumberOfPurchasesPerDate(user1token.getResult(), LocalDate.now()).isWas_expected_error());
    }
}
