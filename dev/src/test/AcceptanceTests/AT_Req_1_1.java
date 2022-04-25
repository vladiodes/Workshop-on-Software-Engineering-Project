package test.AcceptanceTests;

import main.DTO.UserDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AT_Req_1_1 {

    IService service;
    Response<String> adminToken;
    PaymentInformation pInfo;
    SupplyingInformation sInfo;

    @Before
    public void setUp() {
        service = new Service();
        adminToken = service.guestConnect();
        pInfo = new PaymentInformation("5236045598761023",2025,6,28,520,"admin","admin@gmail.com");
        sInfo = new SupplyingInformation("Kfar Saba", LocalDateTime.now());

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

        Response<Boolean> resPurchase = service.purchaseCart(adminToken.getResult(),pInfo,sInfo);
        assertTrue(resPurchase.isError_occured() == false);
    }
}
