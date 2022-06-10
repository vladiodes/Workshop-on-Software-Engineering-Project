package test.RobustnessTests;

import main.DTO.BidDTO;
import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Service.IService;
import main.Service.Service;
import main.Stores.Product;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import test.testUtils.testsFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FaultyExternalSystems {

    @Mock
    ISupplying mockSupplyer;
    @Mock
    IPayment mockPayment;
    @Mock
    PaymentInformation mockPaymentInformation;
    @Mock
    SupplyingInformation mockSupplyingInformation;
    Response<String> founder1token, user1token;
    IService service;
    PaymentInformation pi = testsFactory.getSomePI();
    SupplyingInformation si = testsFactory.getSomeSI();
    double CokePrice = 5;

    @Before
    public void setUp() throws Exception {
        mockSupplyer = mock(SupplyingAdapter.class);
        mockPayment = mock(PaymentAdapter.class);
        mockPaymentInformation = mock(PaymentInformation.class);
        mockSupplyingInformation = mock(SupplyingInformation.class);
        when(mockSupplyer.supply(any(SupplyingInformation.class), any(HashMap.class))).thenReturn(true);
        when(mockPayment.makePayment(any(PaymentInformation.class), any(Double.class))).thenReturn(true);

        service = new Service(mockPayment, mockSupplyer);
        founder1token = service.guestConnect();
        user1token = service.guestConnect();

        service.register("manager1", "12345678");
        service.register("founder1", "12345678");
        service.register("user1", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(user1token.getResult(), "user1", "12345678");

        service.openStore(founder1token.getResult(), "MyStore1");
        service.appointStoreOwner(founder1token.getResult(), "owner1", "MyStore1");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, CokePrice);
        service.addProductToStore(founder1token.getResult(), "Sprite", "Drinks", null, "tasty drink", "MyStore1", 100, CokePrice);
        service.addProductToCart(user1token.getResult(), "MyStore1", "Sprite", 2);
    }

    private void PaymentThrows() {
        when(mockPayment.makePayment(any(PaymentInformation.class), any(Double.class))).thenThrow(new IllegalArgumentException());
    }

    private void DeliveryServiceThrows() {
        when(mockSupplyer.supply(any(SupplyingInformation.class), any(HashMap.class))).thenThrow(new IllegalArgumentException());
    }

    @Test
    public void PaymentSystemBroken(){
        PaymentThrows();
        assertTrue(service.purchaseCart(user1token.getResult(), pi, si).isError_occured());
        //system still works:
        assertFalse(service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1).isError_occured());
        assertFalse(service.addProductToStore(founder1token.getResult(), "Fanta", "Drinks", new ArrayList<>(), "Orange sparkly drink", "MyStore1", 50, 50).isError_occured());
        assertFalse(service.appointStoreOwner(founder1token.getResult(), "user1", "MyStore1").isError_occured());
    }

    @Test
    public void DeliverySystemBroken(){
        DeliveryServiceThrows();
        assertTrue(service.purchaseCart(user1token.getResult(), pi, si).isError_occured());
        //system still works:
        assertFalse(service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1).isError_occured());
        assertFalse(service.addProductToStore(founder1token.getResult(), "Fanta", "Drinks", new ArrayList<>(), "Orange sparkly drink", "MyStore1", 50, 50).isError_occured());
        assertFalse(service.appointStoreOwner(founder1token.getResult(), "user1", "MyStore1").isError_occured());
    }

}
