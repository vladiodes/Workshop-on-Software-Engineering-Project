package test.DBTests;

import main.DTO.ShoppingCartDTO;
import main.Persistence.DAO;
import main.Service.IService;
import main.Service.Service;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.*;
import org.junit.Test;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DBTests {

    private IService service;
    private String store1,store2;
    private String product1,product2;
    private Response<String> founder1,manager1,owner2,owner1;

    @Before
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
    }

//    @Test
//    public void addDataAndCheckBehaviour() throws Exception {
//        addDataToService();
//        setUp();
//
//        addMoreData();
//        setUp();
//
//        checkSimpleDiscount();
//        setUp();
//
//        addBargainPolicyOnProduct2();
//        setUp();
//
//        checkBids();
//        setUp();

//        acceptBidWorks();
//        setUp();
//
//        appointOwnerChain();
//        setUp();

//        OwnersAreRecorded();
//        setUp();
//
//        DeleteChainOwnersWorks();

//        DAO.disablePersist();
//    }


    @Test
    @Order(5)
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
    @Order(1)
    public void checkBids(){
        boolean wasError;
        PaymentInformation pi = new PaymentInformation("1111222233334444",LocalDate.now().plusYears(3),123,"vvv","123456789");
        SupplyingInformation si = new SupplyingInformation("Vladi", "Vladis Home", "Beer Sheva", "Israel", "1122334");

        wasError = service.login(manager1.getResult(),"manager1","123456").isError_occured();
        wasError|=service.bidOnProduct(manager1.getResult(),store2,product2,2000.0,pi,si).isError_occured();
        Assertions.assertFalse(wasError);
    }

    @Test
    @Order(7)
    public void appointOwnerChain(){
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
    public void OwnersAreRecorded(){
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        Response<List<String>> r = service.getStoreStaff(founder1.getResult(), store2);
        wasError|=r.isError_occured();
        Assertions.assertFalse(wasError);
        Assertions.assertEquals(3,r.getResult().size());

    }

    @Test
    @Order(6)
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
    @Order(9)
    public void ZacceptBidWorks(){
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
    @Order(10)
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
    @Order(11)
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
    public void TearDown(){
        DAO.disablePersist();
    }



}
