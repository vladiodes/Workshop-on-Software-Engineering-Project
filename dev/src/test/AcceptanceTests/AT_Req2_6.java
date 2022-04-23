package test.AcceptanceTests;

import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AT_Req2_6 {

    Response<String> adminToken, founder1token;
    IService service = new Service();

    @Before
    public void setUp() {
        adminToken = service.guestConnect();
        founder1token = service.guestConnect();

        service.register("founder1", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.openStore(founder1token.getResult(), "MyStore1");
    }

    /***
     * use case: Deleting A Store req 6.1:
     */
    @Test
    public void deleteStore() {
        // this function fails because only an admin can invoke this
        assertTrue(service.deleteStore(adminToken.getResult(), "MyStore1").getResult());
    }

    /***
     * use case: Deleting A Store req 6.1:
     */
    @Test
    public void deleteNotRealStore() {
        assertTrue(service.deleteStore(adminToken.getResult(), "NotARealStore").isWas_expected_error());
    }
}
