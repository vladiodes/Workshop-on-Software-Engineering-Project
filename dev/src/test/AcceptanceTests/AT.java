package test.AcceptanceTests;

import main.DTO.ProductDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class AT {

    Response<String> manager1token, manager2token, founder1token, founder2token, owner1token, user1token;
    boolean searchFlag;
    IService service = new Service();

    @Before
    public void setUp() {
        manager1token = service.guestConnect();
        manager2token = service.guestConnect();
        founder1token = service.guestConnect();
        founder2token = service.guestConnect();
        owner1token = service.guestConnect();
        user1token = service.guestConnect();

        service.register("manager1", "12345678");
        service.register("manager2", "12345678");
        service.register("founder1", "12345678");
        service.register("founder2","12345678");
        service.register("owner1", "12345678");
        service.register("user1", "12345678");

        service.login(manager1token.getResult(), "manager1", "12345678");
        service.login(manager2token.getResult(), "manager2", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(founder2token.getResult(), "founder2", "12345678");
        service.login(owner1token.getResult(), "owner1", "12345678");
        service.login(user1token.getResult(), "user1", "12345678");

        service.openStore(founder1token.getResult(), "MyStore1");
        service.openStore(founder2token.getResult(), "MyStore2");
        service.appointStoreOwner(founder1token.getResult(), "owner1", "MyStore1");
        service.appointStoreManager(founder1token.getResult(), "manager1", "MyStore1");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6);
    }

    @Test
    public void addProduct() {
        //this test fails because getStoreProducts isn't implemented yet
        int size = service.getStoreProducts("MyStore1").size();
        assertTrue(service.addProductToStore(founder1token.getResult(), "Pepsi Cola", "Drinks", null, "less tasty drink", "MyStore1", 50, 5).getResult());
        assertEquals(service.getStoreProducts("MyStore1").size(), size + 1);
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        searchFlag = false;
        for (ProductDTO product : MyStore1Products)
            searchFlag |= product.getProductName().equals("Pepsi Cola");
        assertTrue(searchFlag);
    }

    @Test
    public void addProductTwice() {
        //this test fails because service.getStoreProducts wasn't implemented yet!
        assertTrue(service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6).isWas_expected_error());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        int counter = 0;
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                counter++;
        assertEquals(counter, 1);
    }

    @Test
    public void notManagerAddProduct() {
        //this test fails because service.getStoreProducts wasn't implemented yet!
        int size = service.getStoreProducts("MyStore1").size();
        assertTrue(service.updateProduct(user1token.getResult(), "Crystal Cola", "Drinks", null, "ew", "MyStore1", 100, 6).isWas_expected_error());
        assertEquals(service.getStoreProducts("MyStore1").size(), size);
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        searchFlag = false;
        for (ProductDTO product : MyStore1Products)
            searchFlag |= product.getProductName().equals("Crystal Cola");
        assertFalse(searchFlag);
    }

    @Test
    public void updateProductInfo() {
        //this test fails because service.getStoreProducts wasn't implemented yet!
        assertTrue(service.updateProduct(founder1token.getResult(), "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6).getResult());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertEquals(product.getDescription(), "very tasty drink");
    }

    @Test
    public void notManagerUpdateProductInfo() {
        //this test fails because service.getStoreProducts wasn't implemented yet!
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Drinks", null, "bad drink", "MyStore1", 100, 6).isWas_expected_error());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertNotEquals(product.getDescription(), "bad drink");
    }

    @Test
    public void removeProduct() {
        //this test fails because service.getStoreProducts wasn't implemented yet!
        int size = service.getStoreProducts("MyStore1").size();
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola","MyStore1").getResult());
        assertEquals(service.getStoreProducts("MyStore1").size(), size - 1);
    }

    @Test
    public void removeProductTwice() {
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola","MyStore1").getResult());
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola","MyStore1").isWas_expected_error());
    }

    @Test
    public void notManagerRemoveProduct() {
        //this test fails because service.getStoreProducts wasn't implemented yet!
        int size = service.getStoreProducts("MyStore1").size();
        assertTrue(service.removeProductFromStore(user1token.getResult(), "Coca Cola","MyStore1").isWas_expected_error());
        assertEquals(service.getStoreProducts("MyStore1").size(), size);
    }

    @Test
    public void appointStoreOwner() {
        assertTrue(service.appointStoreOwner(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.appointStoreOwner(founder1token.getResult(), "manager2", "MyStore1").getResult());
    }

    @Test
    public void notOwnerAppointStoreOwner() {
        assertTrue(service.appointStoreOwner(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.appointStoreOwner(manager2token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void appointStoreOwnerAgain() {
        service.appointStoreOwner(founder1token.getResult(), "manager1", "MyStore1");
        assertTrue(service.appointStoreOwner(owner1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void appointStoreOwnerToDifferentStore() {
        assertTrue(service.appointStoreOwner(founder1token.getResult(), "manager1", "MyStore2").isWas_expected_error());
    }

    @Test
    public void removeStoreOwnerAppointment() {
        assertTrue(service.removeStoreOwnerAppointment(founder1token.getResult(), "owner1", "MyStore1").getResult());
    }

    @Test
    public void chainRemoveStoreOwnerAppointment() {
        service.appointStoreOwner(founder1token.getResult(), "manager2", "MyStore1");
        service.appointStoreOwner(manager2token.getResult(), "user1", "MyStore1");
        assertTrue(service.removeStoreOwnerAppointment(founder1token.getResult(), "manager2", "MyStore1").getResult());

        //this should throw an expected error since user1 was appointed by manager2 and it shouldn't be a manager anymore!
        assertTrue(service.updateProduct(user1token.getResult(),"Coca Cola","Something",null,"Something","MyStore1",100000,20000.5).isWas_expected_error());
    }

    @Test
    public void notOwnerRemoveStoreOwnerAppointment() {
        assertTrue(service.removeStoreOwnerAppointment(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(manager2token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(user1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(manager2token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void otherOwnerRemoveStoreOwnerAppointment() {
        // The owner_to_remove was not appointed by Owner
        service.appointStoreOwner(owner1token.getResult(), "manager1", "MyStore1");
        assertTrue(service.removeStoreOwnerAppointment(manager1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void removeStoreOwnerAppointmentFromDifferentStore() {
        assertTrue(service.removeStoreOwnerAppointment(founder1token.getResult(), "manager1", "MyStore2").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(founder2token.getResult(), "manager1", "MyStore2").isWas_expected_error());
    }

    @Test
    public void appointStoreManager() {
        assertTrue(service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.appointStoreManager(founder1token.getResult(), "manager2", "MyStore1").getResult());
    }

    @Test
    public void notOwnerAppointStoreManager() {
        assertTrue(service.appointStoreManager(user1token.getResult(), "user1", "MyStore1").isWas_expected_error());
        assertTrue(service.appointStoreManager(manager2token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void appointStoreManagerAgain() {
        service.appointStoreManager(founder1token.getResult(), "user1", "MyStore1");
        assertTrue(service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void appointOwnerAsStoreManager() {
        assertTrue(service.appointStoreManager(owner1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void appointStoreManagerToDifferentStore() {
        assertTrue(service.appointStoreManager(founder1token.getResult(), "manager1", "MyStore2").isWas_expected_error());
    }

    @Test
    public void removeStoreManagerAppointment() {
        assertTrue(service.removeStoreManagerAppointment(founder1token.getResult(), "manager1", "MyStore1").getResult());
    }

    // need to check if this test is relevant
//    @Test
//    public void chainRemoveStoreManagerAppointment() {
//        service.appointStoreManager(founder1token.getResult(), "manager2", "MyStore1");
//        service.appointStoreManager(manager2token.getResult(), "user1", "MyStore1");
//        assertTrue(service.removeStoreManagerAppointment(founder1token.getResult(), "manager2", "MyStore1").getResult());
//        assertEquals("need to check", "if user1 is manager");
//    }

    @Test
    public void notOwnerRemoveStoreManagerAppointment() {
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(manager2token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(manager2token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void otherOwnerRemoveStoreManagerAppointment() {
        // The manager_to_remove was not appointed by Owner
        service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void removeStoreManagerAppointmentFromDifferentStore() {
        // this should fail - manager1 was appointed to manager by founder1 in store1 and not store 2
        assertTrue(service.removeStoreManagerAppointment(founder1token.getResult(), "manager1", "MyStore2").isWas_expected_error());

        // this should fail aswell - manager 1 isn't a manager in store 2
        assertTrue(service.removeStoreManagerAppointment(founder2token.getResult(), "manager1", "MyStore2").isWas_expected_error());
    }

    @Test
    public void allowAndDisallowManagerUpdateProducts() {
        //this test fails because getStoreProducts wasn't implemented yet
        service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");

        //default permissions include doesn't include update products permission
        assertTrue(service.allowManagerUpdateProducts(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6).getResult());

        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertEquals(product.getDescription(), "very tasty drink");

        assertTrue(service.disAllowManagerUpdateProducts(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6).isWas_expected_error());
        MyStore1Products = service.getStoreProducts("MyStore1");
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertNotEquals(product.getDescription(), "tasty drink");
    }

    @Test
    public void allowManagerUpdateProductsToNotManager() {
        assertTrue(service.allowManagerUpdateProducts(owner1token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void notOwnerAllowManagerUpdateProducts() {
        assertTrue(service.allowManagerUpdateProducts(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void allowAndDisallowManagerGetHistory() {
        service.appointStoreManager(owner1token.getResult(),"user1","MyStore1");
        assertTrue(service.allowManagerGetHistory(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertNotEquals(service.getStorePurchaseHistory(user1token.getResult(), "MyStore1").getResult(),null);

        assertTrue(service.disAllowManagerGetHistory(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.getStorePurchaseHistory(user1token.getResult(), "MyStore1").isWas_expected_error());
    }

    @Test
    public void allowManagerGetHistoryToNotManager() {
        assertTrue(service.allowManagerGetHistory(owner1token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void notOwnerAllowManagerGetHistory() {
        assertTrue(service.allowManagerGetHistory(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void allowManagerAnswerAndTakeRequests() {
        service.appointStoreManager(owner1token.getResult(),"user1","MyStore1");
        assertTrue(service.allowManagerAnswerAndTakeRequests(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertNotEquals(null,service.receiveQuestionsFromBuyers(user1token.getResult(), "MyStore1").getResult());
        assertTrue(service.sendRespondToBuyers(user1token.getResult(), "MyStore1", "manager1", "check").getResult());
        assertEquals("check", service.receiveMessages(manager1token.getResult()).getResult().get(0));

        assertTrue(service.disAllowManagerAnswerAndTakeRequests(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.receiveQuestionsFromBuyers(user1token.getResult(), "MyStore1").isWas_expected_error());
        assertTrue(service.sendRespondToBuyers(user1token.getResult(), "MyStore1", "manager1", "check").isWas_expected_error());
    }

    @Test
    public void allowManagerAnswerAndTakeRequestsToNotManager() {
        assertTrue(service.allowManagerAnswerAndTakeRequests(owner1token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void notOwnerAllowManagerAnswerAndTakeRequests() {
        assertTrue(service.allowManagerAnswerAndTakeRequests(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    @Test
    public void closeStore() {
        assertTrue(service.closeStore(founder1token.getResult(), "MyStore1").getResult());
        // TODO
    }

    @Test
    public void closeInvalidStore() {
        assertTrue(service.closeStore(founder1token.getResult(), "MyStore2").isWas_expected_error());
    }

    @Test
    public void closeInactiveStore() {
        service.closeStore(founder1token.getResult(), "MyStore1");
        assertTrue(service.closeStore(founder1token.getResult(), "MyStore1").isWas_expected_error());
    }

    @Test
    public void closeNotRealStore() {
        assertTrue(service.closeStore(founder1token.getResult(), "NotARealStore").isWas_expected_error());
    }

    @Test
    public void reopenStore() {
        service.closeStore(founder1token.getResult(), "MyStore1");
        assertTrue(service.reopenStore(founder1token.getResult(), "MyStore1").getResult());
    }

    @Test
    public void reopenActiveStore() {
        assertTrue(service.reopenStore(founder1token.getResult(), "MyStore1").isWas_expected_error());
    }

    @Test
    public void reopenInvalidStore() {
        service.closeStore(founder1token.getResult(), "MyStore1");
        assertTrue(service.reopenStore(founder2token.getResult(), "MyStore1").isWas_expected_error());
    }

    @Test
    public void reopenNotRealStore() {
        assertTrue(service.reopenStore(founder1token.getResult(), "NotARealStore").isWas_expected_error());
    }

//    @Test
//    public void deleteStore() {
//        //this function fails because only an admin can invoke this
//        assertTrue(service.deleteStore(founder1token.getResult(), "MyStore1").getResult());
//    }
//
//    @Test
//    public void deleteInvalidStore() {
//        assertTrue(service.deleteStore(founder1token.getResult(), "MyStore2").isWas_expected_error());
//    }
//
//    @Test
//    public void deleteInactiveStore() {
//        service.deleteStore(founder1token.getResult(), "MyStore1");
//        assertTrue(service.deleteStore(founder1token.getResult(), "MyStore1").isWas_expected_error());
//    }
//
//    @Test
//    public void deleteNotRealStore() {
//        assertTrue(service.deleteStore(founder1token.getResult(), "NotARealStore").isWas_expected_error());
//    }

    @Test
    public void concurrentAppointStoreManager() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Runnable founder1AppointsUser1 = () -> {
            Response<Boolean> resp = service.appointStoreManager(founder1token.getResult(), "user1", "MyStore1");
            if(!resp.isError_occured())
                counter.incrementAndGet();
        };

        Runnable owner1AppointsUser1 = () -> {
            Response<Boolean> resp = service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");
            if(!resp.isError_occured())
                counter.incrementAndGet();
        };

        Thread founder1AppointsUser1Thread = new Thread(founder1AppointsUser1);
        Thread owner1AppointsUser1Thread = new Thread(owner1AppointsUser1);

        founder1AppointsUser1Thread.start();
        owner1AppointsUser1Thread.start();

        founder1AppointsUser1Thread.join();
        owner1AppointsUser1Thread.join();
        assertEquals(1,counter.get());
    }

    @Test
    public void concurrentRemoveAndEditProduct() throws InterruptedException {
        AtomicInteger num_of_errors=new AtomicInteger(0);
        AtomicBoolean should_not_happen = new AtomicBoolean(false);
        Runnable founder1RemoveProduct = () -> {
            Response<Boolean> resp = service.removeProductFromStore(founder1token.getResult(), "Coca Cola","MyStore1");
            if(resp.isError_occured()) {
                should_not_happen.set(true);
                num_of_errors.incrementAndGet();
            }
        };
        Runnable owner1UpdateProduct = () -> {
            Response<Boolean> resp = service.updateProduct(manager1token.getResult(), "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6);
            if(resp.isWas_expected_error())
                num_of_errors.incrementAndGet();
        };


        Thread founder1RemoveProductThread = new Thread(founder1RemoveProduct);
        Thread owner1UpdateProductThread = new Thread(owner1UpdateProduct);

        founder1RemoveProductThread.start();
        owner1UpdateProductThread.start();
        founder1RemoveProductThread.join();
        owner1UpdateProductThread.join();

        assertFalse(should_not_happen.get());
        assertTrue(num_of_errors.get()<=1);

    }

    @After
    public void tearDown() {
        service=new Service();
    }

    /*
        need TODO:
        • getStoreStaff
        • concurrency tests
        • check if a store is closed
        - change delete store tests

     */
}
