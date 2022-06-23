package test.DBTests;

import main.DTO.OwnerAppointmentRequestDTO;
import main.DTO.ShoppingCartDTO;
import main.Persistence.DAO;
import main.Service.IService;
import main.Service.Service;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DBTests {

    private IService service;
    private String store1,store2;
    private String product1,product2;
    private Response<String> founder1,manager1,owner2,owner1,owner3;

    @BeforeEach
    public void setUp() throws Exception {
        DAO.setPersistence_unit("MarketTests");
        service=new Service("DBTestingConfig.json");
        store1 = "store1";
        store2="store2";
        product1="product1";
        product2="product2";
        founder1 = service.guestConnect();
        manager1 = service.guestConnect();
        owner1 = service.guestConnect();
        owner2 = service.guestConnect();
        owner3 = service.guestConnect();
    }


    @Test
    @Order(1)
    public void addDataToService() {
        boolean wasError;
        wasError = service.register("founder1", "123456").isError_occured();
        wasError |= service.register("manager1", "123456").isError_occured();
        wasError |= service.login(founder1.getResult(), "founder1", "123456").isError_occured();
        wasError |= service.login(manager1.getResult(), "manager1", "123456").isError_occured();

        wasError |= service.openStore(founder1.getResult(), store1).isError_occured();
        wasError |= service.openStore(founder1.getResult(), store2).isError_occured();
        wasError |= service.appointStoreManager(founder1.getResult(), "manager1", store1).isError_occured();

        wasError |= service.addProductToStore(founder1.getResult(), product1, "category", null, "desc", store1, 100, 200).isError_occured();
        wasError |= service.addProductToStore(founder1.getResult(), product2, "category", null, "desc", store2, 100, 200).isError_occured();

        wasError |= service.addProductToCart(manager1.getResult(), store1, product1, 10).isError_occured();
        wasError |= service.purchaseCart(manager1.getResult(), new PaymentInformation("123", LocalDate.now().plusYears(1), 123, "123", "123"), new SupplyingInformation("123", "123", "123", "123","123")).isError_occured();


        Assertions.assertFalse(wasError);
    }
    @Test
    @Order(2)
    public void addMoreData(){
        boolean wasError;
        wasError=service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError|=service.CreateProductAmountCondition(founder1.getResult(), store1,product1,3).isError_occured();
        Response<Integer> response = service.CreateSimpleDiscount(founder1.getResult(),store1, LocalDate.now().plusDays(10),0.5);
        wasError|=response.isError_occured();
        wasError|=service.SetDiscountToStore(founder1.getResult(),store1,response.getResult()).isError_occured();

        Assertions.assertFalse(wasError);
    }

    @Test
    @Order(3)
    public void checkSimpleDiscount(){
        boolean wasError;
        wasError=service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError|=service.addProductToCart(founder1.getResult(),store1,product1,1).isError_occured();
        Response<ShoppingCartDTO> cart=service.getCartInfo(founder1.getResult());
        wasError|=cart.isError_occured();
        Assertions.assertEquals(100,cart.getResult().getTotalPrice());

        Assertions.assertFalse(wasError);
    }


    @Test
    @Order(4)
    public void addBargainPolicyOnProduct2(){
        boolean wasError;
        wasError=service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError |= service.addBargainPolicy(founder1.getResult(),store2,product2,1000.0).isError_occured();
        Assertions.assertFalse(wasError);
    }


    @Test
    @Order(5)
    public void checkBids(){
        boolean wasError;
        PaymentInformation pi = new PaymentInformation("1111222233334444",LocalDate.now().plusYears(3),123,"vvv","123456789");
        SupplyingInformation si = new SupplyingInformation("Vladi", "Vladis Home", "Beer Sheva", "Israel", "1122334");

        wasError = service.login(manager1.getResult(),"manager1","123456").isError_occured();
        wasError|=service.bidOnProduct(manager1.getResult(),store2,product2,2000.0,pi,si).isError_occured();
        Assertions.assertFalse(wasError);
    }

    @Test
    @Order(6)
    public void acceptBidWorks(){
        boolean wasError;
        wasError=service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError |= service.ApproveBid(founder1.getResult(), store2,product2,"manager1").isError_occured();
        wasError |= service.login(manager1.getResult(),"manager1","123456").isError_occured();
        Response<List<String>> r = service.getPurchaseHistory(manager1.getResult(), "manager1");
        wasError |= r.isError_occured();

        Assertions.assertFalse(wasError);
        Assertions.assertEquals(2,r.getResult().size());
    }

    @Test
    @Order(7)
    public void createOwnerAppointmentRequestsChain() {
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError |= service.register("owner1","123456").isError_occured();
        wasError |= service.register("owner2","123456").isError_occured();
        wasError |= service.appointStoreOwner(founder1.getResult(),"owner1",store2).isError_occured();
        wasError |= service.login(owner1.getResult(),"owner1","123456").isError_occured();
        wasError |= service.appointStoreOwner(owner1.getResult(),"owner2",store2).isError_occured();

        Assertions.assertFalse(wasError);
    }
    @Test
    @Order(8)
    public void OwnerAppointmentRequestsAreRecorded(){
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        Response<List<OwnerAppointmentRequestDTO>> resFounder1 = service.getOwnerAppointmentRequests(founder1.getResult(),store2);
        wasError |= resFounder1.isError_occured();
        Assertions.assertEquals(1,resFounder1.getResult().size());

        wasError |= service.approveOwnerAppointment(founder1.getResult(),"owner2",store2).isError_occured();
        Assertions.assertFalse(wasError);
    }

    @Test
    @Order(9)
    public void OwnerAppointmentRequestsAreDiscarded() {
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError |= service.login(owner1.getResult(), "owner1", "123456").isError_occured();
        wasError |= service.login(owner2.getResult(), "owner2", "123456").isError_occured();

        Response<List<OwnerAppointmentRequestDTO>> resFounder1 = service.getOwnerAppointmentRequests(founder1.getResult(),store2);
        Response<List<OwnerAppointmentRequestDTO>> resOwner1 = service.getOwnerAppointmentRequests(owner1.getResult(),store2);
        Response<List<OwnerAppointmentRequestDTO>> resOwner2 = service.getOwnerAppointmentRequests(owner2.getResult(),store2);

        wasError |= resFounder1.isError_occured();
        wasError |= resOwner1.isError_occured();
        wasError |= resOwner2.isError_occured();

        Assertions.assertEquals(0,resFounder1.getResult().size());
        Assertions.assertEquals(0,resOwner1.getResult().size());
        Assertions.assertEquals(0,resOwner2.getResult().size());
    }

    @Test
    @Order(10)
    public void OwnersAreRecorded(){
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        Response<List<String>> r = service.getStoreStaff(founder1.getResult(), store2);
        wasError|=r.isError_occured();
        Assertions.assertFalse(wasError);
        Assertions.assertEquals(3,r.getResult().size());

    }

    @Test
    @Order(11)
    public void DeleteChainOwnersWorks(){
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError|=service.removeStoreOwnerAppointment(founder1.getResult(), "owner1",store2).isError_occured();
        Response<List<String>> r = service.getStoreStaff(founder1.getResult(), store2);
        wasError|=r.isError_occured();
        Assertions.assertFalse(wasError);
        Assertions.assertEquals(1,r.getResult().size());
    }

    @Test
    @Order(12)
    public void AppointOwnerAndDecline(){
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError |= service.appointStoreOwner(founder1.getResult(),"owner1",store2).isError_occured();
        wasError |= service.login(owner1.getResult(),"owner1","123456").isError_occured();
        wasError |= service.appointStoreOwner(owner1.getResult(),"owner2",store2).isError_occured();
        wasError |= service.declineOwnerAppointment(founder1.getResult(),"owner2",store2).isError_occured();
        Response<List<String>> r = service.getStoreStaff(founder1.getResult(), store2);
        wasError|=r.isError_occured();
        Assertions.assertFalse(wasError);
        Assertions.assertEquals(2,r.getResult().size());
    }

    @Test
    @Order(13)
    public void AppointOwnerAgainAfterDecline() {
        boolean wasError;
        wasError = service.login(founder1.getResult(), "founder1", "123456").isError_occured();
        wasError |= service.login(owner1.getResult(), "owner1", "123456").isError_occured();
        wasError |= service.appointStoreOwner(owner1.getResult(), "owner2", store2).isError_occured();
        wasError |= service.approveOwnerAppointment(founder1.getResult(), "owner2", store2).isError_occured();
        Response<List<String>> r = service.getStoreStaff(founder1.getResult(), store2);
        wasError |= r.isError_occured();
        Assertions.assertFalse(wasError);
        Assertions.assertEquals(3, r.getResult().size());
    }

    @Test
    @Order(14)
    public void concurrentDBModificationTest() throws Exception {
        AtomicBoolean wasError = new AtomicBoolean(false);
        int numOfThreads = 30;
        Thread[] threads = new Thread[numOfThreads];
        for (int i = 0; i < threads.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                wasError.set(wasError.get() | service.register("u" + finalI, "123456").isError_occured());
            });
        }

        for (Thread thread : threads) thread.start();

        for (Thread thread : threads) thread.join();

        Assertions.assertFalse(wasError.get());
    }

    @Test
    @Order(15)
    public void concurrentDBModificationAllWorked(){
        AtomicBoolean wasError = new AtomicBoolean(false);
        int numOfThreads = 30;
        List<Response<String>> tokens =new ArrayList<>();
        for(int i=0;i<numOfThreads;i++){
            tokens.add(service.guestConnect());
        }

        for(int i=0;i<numOfThreads;i++){
            wasError.set(wasError.get() | service.login(tokens.get(i).getResult(),"u" + i, "123456").isError_occured());
        }

        Assertions.assertFalse(wasError.get());
    }

    @AfterAll
    static void TearDown(){
        DAO.disablePersist();
    }



}
