package test.UnitTests;

import main.Market;
import main.Stores.Product;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class PurchaseConcurrencyTest {

    Market market;
    PaymentInformation pi;
    SupplyingInformation si;
    String founder;
    double phonePrice = 50;
    int productQuanitity = 5000;
    String productName = "IPhone";
    String storeName1 = "Apple";
    String storeName2 = "IDigital";
    String tempUser;
    @BeforeEach
    void setUp() throws Exception {
        pi = new PaymentInformation(true);
        si = new SupplyingInformation(true);
        market = new Market();
        founder = market.ConnectGuest();
        market.Register("Founder123", "12345678");
        market.Login(founder, "Founder123", "12345678");
        market.openStore(founder, storeName1);
        market.addProductToStore(founder, productName, "Electronics", new LinkedList<>(), "good phone", storeName1, productQuanitity, phonePrice);
        market.openStore(founder, storeName2);
        market.addProductToStore(founder, productName, "Electronics", new LinkedList<>(), "good phone", storeName2, productQuanitity, phonePrice);
        tempUser = "TEMP";

    }
    String[] generateUsers1Store(int amount){
        String[] output = new String[amount];
        String ValidPassword = "12312456";
        for(int i = 0; i < output.length; i ++){
            output[i] = market.ConnectGuest();
            String UserName =  tempUser + i;
            market.Register(UserName, ValidPassword);
            market.Login(output[i], UserName, ValidPassword);
            market.addProductToCart(output[i], storeName1, productName, 1);
        }
        return output;
    }

    String[] generateUsers2Store(int amount){
        String[] output = new String[amount];
        String ValidPassword = "12312456";
        for(int i = 0; i < output.length; i ++){
            output[i] = market.ConnectGuest();
            String UserName =  tempUser + i;
            market.Register(UserName, ValidPassword);
            market.Login(output[i], UserName, ValidPassword);
            market.addProductToCart(output[i], storeName1, productName, 1);
            market.addProductToCart(output[i], storeName2, productName, 1);
        }
        return output;
    }

    @Test
    void PurchaseConcurrencyCase1Store() throws InterruptedException {
        int userAmount = productQuanitity;
        String[] tokens = generateUsers1Store(userAmount);
        Thread[] ts = new Thread[userAmount];
        for (int i = 0; i < ts.length; i ++){
            int finalI = i;
            ts[i] = new Thread(() -> {
                try {
                    market.purchaseCart(tokens[finalI],pi, si);
                } catch (Exception ignored) {
                }
            });
        }
        for (Thread thread : ts) thread.run();
        for (Thread t : ts) t.join();
        Assertions.assertEquals(userAmount,market.getStorePurchaseHistory(founder, storeName1).size());
        List<Product> ps = market.getStoreProducts(storeName1);
        Assertions.assertEquals(0, ps.size());
    }

    @Test
    void PurchaseConcurrencyCase2Stores() throws InterruptedException {
        int userAmount = productQuanitity * 2;
        String[] store1tokens = generateUsers2Store(userAmount);
        Thread[] ts1 = new Thread[userAmount];
        AtomicInteger counter = new AtomicInteger(0);
        for (int i = 0; i < ts1.length; i ++){
            int finalI = i;
            ts1[i] = new Thread(() -> {
                try {
                    market.purchaseCart(store1tokens[finalI],pi, si);
                } catch (Exception e) {
                    counter.incrementAndGet();
                }
            });
        }
        for (Thread thread : ts1) thread.run();
        for (Thread t : ts1) t.join();
        Assertions.assertEquals(productQuanitity,market.getStorePurchaseHistory(founder, storeName1).size());
        Assertions.assertEquals(productQuanitity,market.getStorePurchaseHistory(founder, storeName2).size());
        Assertions.assertEquals(userAmount - productQuanitity, counter.get());
        List<Product> ps = market.getStoreProducts(storeName1);
        Assertions.assertEquals(0, ps.size());
    }


}