package test.UnitTests;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Stores.PurchasePolicy.Discounts.Discount;
import main.Stores.Product;
import main.Stores.PurchasePolicy.ProductPolicy.rafflePolicy;
import main.Stores.Store;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import test.testUtils.testsFactory;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.*;

class rafflePolicyTest {


    @Mock
    ISupplying mockSupplyer;
    @Mock
    IPayment mockPayment;
    PaymentInformation pi = testsFactory.getSomePI();
    SupplyingInformation si = testsFactory.getSomeSI();
    @Mock
    Store storeMock;
    @Mock
    Product productMock;
    @Mock
    User userMock1;
    @Mock
    User userMock2;
    @Mock
    User userMock3;
    @Mock
    Discount discountMock;
    rafflePolicy subject;
    Double originalPrice;
    @BeforeEach
    public void setUp() {
        productMock = mock(Product.class);
        userMock1 = mock(User.class);
        userMock2 = mock(User.class);
        userMock3 = mock(User.class);
        discountMock = mock(Discount.class);
        mockSupplyer = mock(SupplyingAdapter.class);
        mockPayment = mock(PaymentAdapter.class);
        storeMock = mock(Store.class);
        originalPrice = 60.0;
        subject = new rafflePolicy(storeMock, originalPrice);
    }
    @Test
    void IncompletepurchaseDoesntShip() {
        subject.productPurchased(productMock,userMock1, originalPrice / 3 - 1, 1, mockSupplyer, si, pi, mockPayment);
        subject.productPurchased(productMock,userMock2, originalPrice / 3 - 1, 1, mockSupplyer, si, pi, mockPayment);
        subject.productPurchased(productMock,userMock3, originalPrice / 3 - 1, 1, mockSupplyer, si, pi, mockPayment);
        verify(mockSupplyer, times(0)).supply(any(SupplyingInformation.class), anyMapOf(Product.class, Integer.class));
    }

    @Test
    void verify1winner() {
        subject.productPurchased(productMock,userMock1, originalPrice / 3 , 1, mockSupplyer, si, pi, mockPayment);
        subject.productPurchased(productMock,userMock2, originalPrice / 3 , 1, mockSupplyer, si, pi, mockPayment);
        subject.productPurchased(productMock,userMock3, originalPrice / 3 , 1, mockSupplyer, si, pi, mockPayment);
        verify(mockSupplyer, times(1)).supply(any(SupplyingInformation.class), anyMapOf(Product.class, Integer.class));
    }

    @Test
    void closeRefunds() {
        subject.productPurchased(productMock,userMock1, originalPrice / 3 , 1, mockSupplyer, si, pi, mockPayment);
        subject.productPurchased(productMock,userMock2, originalPrice / 3 , 1, mockSupplyer, si, pi, mockPayment);
        subject.close();
        try
        {
            verify(mockPayment, times(2)).abort(pi);
        }
        catch(Exception e)
        {
            Assertions.fail();
        }
    }

    @Test
    void cantDiscount(){
        Assertions.assertThrows(IllegalArgumentException.class, ()->subject.setDiscount(discountMock));
    }
}