package test.RobustnessTests;

import main.ExternalServices.Payment.PaymentAdapter;
import main.utils.PaymentInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentAdapterTest {
    private PaymentAdapter paymentAdapter;
    private PaymentInformation mockPI;
    private PaymentInformation badMockPI;

    @BeforeEach
    void setUp() {
        this.paymentAdapter = new PaymentAdapter();
        mockPI = mock(PaymentInformation.class);
        badMockPI = mock(PaymentInformation.class);

        //Prepare mockPI
        when(mockPI.getCardNumber()).thenReturn("1111222233334444");
        when(mockPI.getName()).thenReturn("Oded Gal");
        when(mockPI.getCvv()).thenReturn(777);
        when(mockPI.getExpDate()).thenReturn(LocalDate.of(2023, 7, 5));
        when(mockPI.getUserId()).thenReturn("123456789");
        when(mockPI.getTransactionId()).thenReturn(50000);


        //Prepare badMockPI
        when(badMockPI.getCardNumber()).thenReturn(null);
        when(badMockPI.getName()).thenThrow(Exception.class);
        when(badMockPI.getCvv()).thenReturn(-50);
        when(badMockPI.getExpDate()).thenReturn(LocalDate.of(1995, 7, 5));
        when(badMockPI.getUserId()).thenReturn(null);
        when(mockPI.getTransactionId()).thenReturn(0);


    }

    @Test
    void makePaymentTest()
    {
        Assertions.assertTrue(paymentAdapter.makePayment(mockPI, 500.00));
    }
    @Test
    void badMakePaymentTest()
    {
        Assertions.assertFalse(paymentAdapter.makePayment(badMockPI, 500.00));
    }
    @Test
    void abortTestNoPayment()
    {
        Assertions.assertThrows(Exception.class, ()->paymentAdapter.abort(mockPI));
    }
    @Test
    void badAbortTest()
    {
        Assertions.assertThrows(Exception.class, ()->paymentAdapter.abort(badMockPI));
    }

}
