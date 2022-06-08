package test.DBTests;

import main.DTO.ShoppingCartDTO;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Persistence.DAO;
import main.Service.IService;
import main.Service.Service;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;

import java.time.LocalDate;
import java.util.List;


public class DBTests {

    private IService service;
    private String store1,store2;
    private String product1,product2;
    private Response<String> founder1,manager1,owner2,owner1;

    @Before
    public void setUp() {
        DAO.setPersistence_unit("MarketTests");
        service=new Service(new PaymentAdapter(),new SupplyingAdapter(),false,true);
        store1 = "store1";
        store2="store2";
        product1="product1";
        product2="product2";
        founder1 = service.guestConnect();
        manager1 = service.guestConnect();
        owner1 = service.guestConnect();
        owner2 = service.guestConnect();
    }

    @Test
    public void addDataAndCheckBehaviour(){
        addDataToService();
        setUp();

        addMoreData();
        setUp();

        checkSimpleDiscount();
        setUp();

        addBargainPolicyOnProduct2();
        setUp();

        checkBids();
        setUp();

        acceptBidWorks();
        setUp();

        appointOwnerChain();
        setUp();

        OwnersAreRecorded();
        setUp();

        DeleteChainOwnersWorks();

        DAO.disablePersist();
    }


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
        wasError |= service.purchaseCart(manager1.getResult(), new PaymentInformation(null, null, 123, null, null), new SupplyingInformation(null, null, null, null,null)).isError_occured();

        Assertions.assertFalse(wasError);
    }


    public void addMoreData(){
        boolean wasError;
        wasError=service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError|=service.CreateProductAmountCondition(founder1.getResult(), store1,product1,3).isError_occured();
        Response<Integer> response = service.CreateSimpleDiscount(founder1.getResult(),store1, LocalDate.now().plusDays(10),0.5);
        wasError|=response.isError_occured();
        wasError|=service.SetDiscountToStore(founder1.getResult(),store1,response.getResult()).isError_occured();

        Assertions.assertFalse(wasError);
    }

    public void checkSimpleDiscount(){
        boolean wasError;
        wasError=service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError|=service.addProductToCart(founder1.getResult(),store1,product1,1).isError_occured();
        Response<ShoppingCartDTO> cart=service.getCartInfo(founder1.getResult());
        wasError|=cart.isError_occured();
        Assertions.assertEquals(100,cart.getResult().getTotalPrice());

        Assertions.assertFalse(wasError);
    }


    public void addBargainPolicyOnProduct2(){
        boolean wasError;
        wasError=service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError |= service.addBargainPolicy(founder1.getResult(),store2,product2,1000.0).isError_occured();
        Assertions.assertFalse(wasError);
    }


    public void checkBids(){
        boolean wasError;
        PaymentInformation pi = new PaymentInformation("1111222233334444",LocalDate.now().plusYears(3),123,"vvv","123456789");
        SupplyingInformation si = new SupplyingInformation("Vladi", "Vladis Home", "Beer Sheva", "Israel", "1122334");

        wasError = service.login(manager1.getResult(),"manager1","123456").isError_occured();
        wasError|=service.bidOnProduct(manager1.getResult(),store2,product2,2000.0,pi,si).isError_occured();

        Assertions.assertFalse(wasError);
    }


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

    public void OwnersAreRecorded(){
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        Response<List<String>> r = service.getStoreStaff(founder1.getResult(), store2);
        wasError|=r.isError_occured();
        Assertions.assertFalse(wasError);
        Assertions.assertEquals(3,r.getResult().size());

    }

    public void DeleteChainOwnersWorks(){
        boolean wasError;
        wasError = service.login(founder1.getResult(),"founder1","123456").isError_occured();
        wasError|=service.removeStoreOwnerAppointment(founder1.getResult(), "owner1",store2).isError_occured();
        Response<List<String>> r = service.getStoreStaff(founder1.getResult(), store2);
        wasError|=r.isError_occured();
        Assertions.assertFalse(wasError);
        Assertions.assertEquals(1,r.getResult().size());
    }


}
