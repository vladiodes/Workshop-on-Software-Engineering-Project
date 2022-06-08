package test.AcceptanceTests;

import main.DTO.UserDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.Before;
import org.junit.Test;
import test.testUtils.testsFactory;

import static org.junit.jupiter.api.Assertions.*;

public class AT_Req_1_1 {

    IService service;
    Response<String> adminToken;
    PaymentInformation pInfo;
    SupplyingInformation sInfo;
    Response<String> founder1token;

    @Before
    public void setUp() throws Exception {
        service = new Service(testsFactory.alwaysSuccessPayment(), testsFactory.alwaysSuccessSupplyer());
        adminToken = service.guestConnect();
        founder1token = service.guestConnect();
        service.register("founder1", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.openStore(founder1token.getResult(), "MyStore1");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6);


    }
    /*
        Functional Req 1.1 - check that there is a system admin
     */
    @Test
    public void checkAdminExistence() {
        Response<UserDTO> res = service.login(adminToken.getResult(),"admin","admin");
        assertFalse(res.isError_occured() && res.getResult() == null);
    }
    @Test
    public void checkPaymentAndSupplySystemsAreAccesible() {
        Response<UserDTO> resLogin = service.login(adminToken.getResult(),"admin","admin");
        Response<Boolean> resAddToCart = service.addProductToCart(adminToken.getResult(),"MyStore1","Coca Cola", 5);
        Response<Boolean> resPurchase = service.purchaseCart(adminToken.getResult(),testsFactory.getSomePI(), testsFactory.getSomeSI());
        assertFalse(resPurchase.isError_occured());
    }
}
