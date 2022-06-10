package test.RobustnessTests;

import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Stores.Product;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SupplyingAdapterTest {
    private SupplyingAdapter supplyingAdapter;
    private SupplyingInformation mockSI;
    private SupplyingInformation badMockSI;

    private Map<Product, Integer> mockProductToSupply;

    @BeforeEach
    void setUp() {
        this.supplyingAdapter = new SupplyingAdapter();
        mockSI = mock(SupplyingInformation.class);
        badMockSI = mock(SupplyingInformation.class);
        mockProductToSupply = mock(HashMap.class);

        //Prepare mockSI
        when(mockSI.getAddress()).thenReturn("Cool Address");
        when(mockSI.getName()).thenReturn("Oded Gal");
        when(mockSI.getCity()).thenReturn("Haifa");
        when(mockSI.getCountry()).thenReturn("Israel");
        when(mockSI.getZip()).thenReturn("123123");


        //Prepare badMockSI
        when(badMockSI.getAddress()).thenReturn(null);
        when(badMockSI.getName()).thenReturn(null);
        when(badMockSI.getCity()).thenThrow(Exception.class);
        when(badMockSI.getCountry()).thenReturn(null);
        when(badMockSI.getZip()).thenReturn(null);


    }

    @Test
    void supplyTest()
    {
        Assertions.assertTrue(supplyingAdapter.supply(mockSI, mockProductToSupply));
    }
    @Test
    void badSupplyTest()
    {
        Assertions.assertFalse(supplyingAdapter.supply(badMockSI, mockProductToSupply));
    }
    @Test
    void abortTestNoSupply()
    {
        Assertions.assertThrows(Exception.class, ()->supplyingAdapter.abort(mockSI));
    }
    @Test
    void badAbortTest()
    {
        Assertions.assertThrows(Exception.class, ()->supplyingAdapter.abort(badMockSI));
    }

}
