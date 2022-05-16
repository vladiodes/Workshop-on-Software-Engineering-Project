package test.UnitTests;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Shopping.Purchase;
import main.Shopping.ShoppingCart;
import main.Stores.Product;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class PurchaseTest {
    @Mock
    ISupplying mockSupplyer;
    @Mock
    IPayment mockPayment;
    @Mock
    PaymentInformation mockPaymentInformation;
    @Mock
    SupplyingInformation mockSupplyingInformation;
    @Mock
    User mockuser;
    @Mock
    ShoppingCart mockCart;
    double price = 50.0;
    Map<Product, Integer> prods = mock(Map.class);
    @BeforeEach
    void setUp() {
        mockSupplyer = mock(SupplyingAdapter.class);
        mockPayment = mock(PaymentAdapter.class);
        mockPaymentInformation = mock(PaymentInformation.class);
        mockSupplyingInformation = mock(SupplyingInformation.class);
        mockuser = mock(User.class);
        mockCart= mock(ShoppingCart.class);
        when(mockSupplyer.bookDelivery(any(SupplyingInformation.class))).thenReturn(true);
        when(mockPayment.validateCard(any(PaymentInformation.class))).thenReturn(true);
        when(mockSupplyer.supply(any(SupplyingInformation.class), any(HashMap.class))).thenReturn(true);
        when(mockPayment.makePayment(any(PaymentInformation.class), any(Double.class))).thenReturn(true);
        when(prods.size()).thenReturn(5);
        when(mockCart.ValidateCart(mockuser)).thenReturn(true);
        when(mockCart.getPrice()).thenReturn(price);
        when(mockCart.getProductsForPurchase(mockuser)).thenReturn(prods);
        when(mockCart.getBaskets()).thenReturn(new ConcurrentHashMap<>());
    }

    @Test
    void executePurchaseCallsExternalSystems() {
        Purchase subject = new Purchase(mockPaymentInformation, mockSupplyingInformation, mockuser, mockCart, mockPayment,mockSupplyer);
        Assertions.assertDoesNotThrow(subject::executePurchase);
        verify(mockPayment,times(1)).makePayment(mockPaymentInformation, price);
        verify(mockSupplyer,times(1)).supply(mockSupplyingInformation, prods);
    }

    @Test
    void ifPaymentFailsAbortSupply() {
        Purchase subject = new Purchase(mockPaymentInformation, mockSupplyingInformation, mockuser, mockCart, mockPayment,mockSupplyer);
        when(mockPayment.makePayment(any(PaymentInformation.class), any(Double.class))).thenReturn(false);
        Assertions.assertThrows(Exception.class, subject::executePurchase);
        verify(mockPayment,times(1)).abort(mockPaymentInformation);
        verify(mockSupplyer,times(1)).abort(mockSupplyingInformation);
    }

    @Test
    void ifSupplymentFailsAbortPayment() {
        Purchase subject = new Purchase(mockPaymentInformation, mockSupplyingInformation, mockuser, mockCart, mockPayment,mockSupplyer);
        when(mockSupplyer.supply(any(SupplyingInformation.class), any(HashMap.class))).thenReturn(false);
        Assertions.assertThrows(Exception.class, subject::executePurchase);
        verify(mockPayment,times(1)).abort(mockPaymentInformation);
        verify(mockSupplyer,times(1)).abort(mockSupplyingInformation);
    }
}