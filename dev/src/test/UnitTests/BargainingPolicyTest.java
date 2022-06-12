package test.UnitTests;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Stores.PurchasePolicy.ProductPolicy.BargainingPolicy;
import main.Stores.Store;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BargainingPolicyTest {

    BargainingPolicy subject;
    Double originalprice = 50.0;
    @Mock
    Store store;
    @Mock
    Product product;
    @Mock
    SupplyingInformation si;
    @Mock
    PaymentInformation pi;
    Double userPrice = 30.0;
    @Mock
    User usermock;
    @Mock
    User approvingUser1;
    User approvingUser2;
    @Mock
    IPayment mockPaymentSystem;
    @Mock
    ISupplying mockSupplyingSystem;
    HashMap<String, Product> storeInventory;
    Bid bid;
    @BeforeEach
    void setUp() {
        mockPaymentSystem = mock(IPayment.class);
        mockSupplyingSystem = mock(ISupplying.class);
        store = mock(Store.class);
        product = mock(Product.class);
        when(product.getName()).thenReturn("Product1");
        when(product.isPurchasableForAmount(1)).thenReturn(true);
        pi = mock(PaymentInformation.class);
        si = mock(SupplyingInformation.class);
        usermock = mock(User.class);
        approvingUser1 = mock(User.class);
        approvingUser2 = mock(User.class);
        storeInventory = new HashMap<>();
        storeInventory.put(product.getName(), product);
        HashMap<User, String> storeStaff = new HashMap<>();
        storeStaff.put(approvingUser1, "approving user1");
        storeStaff.put(approvingUser2, "approving user2");
        when(store.getStoreStaff()).thenReturn(storeStaff);
        when(approvingUser1.ShouldBeNotfiedForBargaining(store)).thenReturn(true);
        when(approvingUser2.ShouldBeNotfiedForBargaining(store)).thenReturn(true);
        when(approvingUser1.getUserName()).thenReturn("ApprovingUser1");
        when(approvingUser2.getUserName()).thenReturn("ApprovingUser2");
        when(usermock.getUserName()).thenReturn("user1");
        when(store.getIsActive()).thenReturn(true);
        when(store.getName()).thenReturn("s1");
        when(store.getProductsByName()).thenReturn(storeInventory);
        when(store.getProduct(product.getName())).thenReturn(product);
        when(store.ValidateBasket(any(User.class), any(ShoppingBasket.class))).thenReturn(true);
        when(store.getPriceForProduct(product, usermock)).thenReturn(userPrice);
        when(mockPaymentSystem.makePayment(any(PaymentInformation.class), any(Double.class))).thenReturn(true);
        when(mockSupplyingSystem.supply(any(SupplyingInformation.class), any())).thenReturn(true);
        bid = new Bid(product, usermock, userPrice, pi, si);
        subject = new BargainingPolicy(store, originalprice, product);
        when(product.deliveredImmediately(usermock)).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return subject.deliveredImmediately(usermock);
            }
        });
    }

    void verifyNotBought(){
        verify(mockPaymentSystem, times(0)).makePayment(pi, userPrice);
        verify(mockSupplyingSystem, times(0)).supply(any(), any());
        verify(store, times(0)).purchaseBasket(any(), any(), any(), any(), any(), any());
    }

    void verifyBought(){
        verify(mockPaymentSystem, times(1)).makePayment(pi, userPrice);
        verify(mockSupplyingSystem, times(1)).supply(any(), any());
        verify(store, times(1)).purchaseBasket(any(), any(), any(), any(), any(), any());
    }

    @Test
    @Order(7)
    void productPurchased() {
        subject.productPurchased(product, usermock, userPrice, 1, mockSupplyingSystem, si, pi, mockPaymentSystem);
        verify(product, times(1)).subtractQuantity(1);
    }

    @Test
    @Order(1)
    void bid() {
        assertTrue(subject.bid(bid));
        assertTrue(subject.getBids().contains(bid));
    }

    @Test
    @Order(2)
    void approveBidAllows() {
        assertTrue(subject.bid(bid));
        assertDoesNotThrow(()->subject.approveBid(usermock, approvingUser1, mockPaymentSystem, mockSupplyingSystem));
        assertDoesNotThrow(()->subject.approveBid(usermock, approvingUser2, mockPaymentSystem, mockSupplyingSystem));
        verifyBought();
    }

    @Test
    @Order(3)
    void CantapproveBidNotExisting() {
        Assertions.assertThrows(Exception.class,()->subject.approveBid(usermock, approvingUser1, mockPaymentSystem, mockSupplyingSystem));
    }

    @Test
    @Order(4)
    void AllApprovesRequired() {
        assertTrue(subject.bid(bid));
        assertDoesNotThrow(()->subject.approveBid(usermock, approvingUser1, mockPaymentSystem, mockSupplyingSystem));
        verifyNotBought();
    }

    @Test
    @Order(5)
    void declineBid() {
        assertTrue(subject.bid(bid));
        assertDoesNotThrow(()->subject.approveBid(usermock, approvingUser1, mockPaymentSystem, mockSupplyingSystem));
        assertDoesNotThrow(()->subject.declineBid(usermock));
        verifyNotBought();
    }

    @Test
    @Order(6)
    void CantDeclineBidNotExisting() {
        Assertions.assertThrows(Exception.class,()->subject.declineBid(usermock));
    }

    @Test
    @Order(8)
    void isAddableToBasket() {
        assertFalse(subject.isAddableToBasket());
    }
}