package test.AcceptanceTests;

import avro.shaded.com.google.common.base.CharMatcher;
import main.Service.IService;
import main.Service.Service;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import test.testUtils.testsFactory;

import java.time.LocalDate;

/**
 * This test-suite tests the new requirement as described in version 4 document.
 */
public class AT_Req2_6_5_SystemStats_Updated {
    Response<String> adminToken;
    Response<String> stats;
    IService service;
    int connections = 50;

    @Before
    public void setUp() throws Exception {
        service = new Service(testsFactory.alwaysSuccessPayment(),testsFactory.alwaysSuccessSupplyer());
        adminToken = service.guestConnect();
        service.login(adminToken.getResult(),"admin","admin");
    }

    @Test
   public void testConnectedGuests(){
        for(int i=0;i<connections;i++)
            service.guestConnect();
        stats=service.getStatsPerDate(adminToken.getResult(),LocalDate.now());

        Assertions.assertEquals(CharMatcher.inRange('0', '9').retainFrom(stats.getResult()),
                generateStatsDigitString(connections+1,0,0,0,1,1,0,0));

    }

    @Test
    public void testRegistrations(){
        for(int i=0;i<connections;i++)
            service.register("u"+i,"123456");
        stats=service.getStatsPerDate(adminToken.getResult(),LocalDate.now());

        Assertions.assertEquals(CharMatcher.inRange('0', '9').retainFrom(stats.getResult()),
                generateStatsDigitString(1,0,0,0,1,1,0,connections));
    }

    @Test
    public void testLogins(){
        for(int i=0;i<connections;i++){
            service.register("u"+i,"123456");
            service.login(service.guestConnect().getResult(),"u"+i,"123456");
        }
        stats=service.getStatsPerDate(adminToken.getResult(),LocalDate.now());


        Assertions.assertEquals(CharMatcher.inRange('0', '9').retainFrom(stats.getResult()),
                generateStatsDigitString(1+connections,connections,0,0,1,1+connections,0,connections));
    }

    @Test
    public void testManagerLogins(){
        service.openStore(adminToken.getResult(),"store");
        Response<String> manager = service.guestConnect();
        service.register("manager","123456");
        service.appointStoreManager(adminToken.getResult(),"manager","store");
        service.login(manager.getResult(),"manager","123456");

        stats=service.getStatsPerDate(adminToken.getResult(),LocalDate.now());

        Assertions.assertEquals(CharMatcher.inRange('0', '9').retainFrom(stats.getResult()),
                generateStatsDigitString(2,0,1,0,1,2,0,1));
    }

    @Test
    public void testOwnerLogins() {
        service.openStore(adminToken.getResult(), "store");
        Response<String> owner = service.guestConnect();
        service.register("owner", "123456");
        service.appointStoreOwner(adminToken.getResult(), "owner", "store");
        service.login(owner.getResult(), "owner", "123456");

        stats = service.getStatsPerDate(adminToken.getResult(), LocalDate.now());

        Assertions.assertEquals(CharMatcher.inRange('0', '9').retainFrom(stats.getResult()),
                generateStatsDigitString(2, 0, 0, 1, 1, 2, 0, 1));
    }

    @Test
    public void testPurchasesRecorded() {
        service.openStore(adminToken.getResult(), "store");
        service.addProductToStore(adminToken.getResult(), "p1", null, null, null, "store", 100, 200.5);
        Response<String> guest = service.guestConnect();
        service.addProductToCart(guest.getResult(), "store", "p1", 2);
        service.purchaseCart(guest.getResult(), new PaymentInformation(), new SupplyingInformation());
        stats = service.getStatsPerDate(adminToken.getResult(), LocalDate.now());

        Assertions.assertEquals(CharMatcher.inRange('0', '9').retainFrom(stats.getResult()),
                generateStatsDigitString(2, 0, 0, 0, 1, 1, 1, 0));

    }

    private String generateStatsDigitString(int connections,int non_staff,int managers,int owners,int admins,int logins,int purchases, int registrations){
        StringBuilder builder = new StringBuilder();
        return builder.append(connections).append(non_staff).append(managers).append(owners).append(admins).append(logins).append(purchases).append(registrations).toString();
    }
}
