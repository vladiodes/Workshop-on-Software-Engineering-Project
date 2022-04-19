package test.AcceptanceTests;

import main.DTO.ProductDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AT {

    Response<String> manager1token, manager2token, founder1token, founder2token, owner1token, user1token;
    boolean searchFlag;
    IService service = new Service();

    @Before
    public void setUp() throws Exception {
        manager1token = service.guestConnect();
        manager2token = service.guestConnect();
        founder1token = service.guestConnect();
        founder2token = service.guestConnect();
        owner1token = service.guestConnect();
        user1token = service.guestConnect();

        service.register("manager1", "1234");
        service.register("manager2", "1234");
        service.register("founder1", "1234");
        service.register("owner1", "1234");
        service.register("user1", "1234");

        service.login(manager1token.getResult(), "manager1", "1234");
        service.login(manager2token.getResult(), "manager2", "1234");
        service.login(founder1token.getResult(), "founder1", "1234");
        service.login(founder2token.getResult(), "founder1", "1234");
        service.login(owner1token.getResult(), "owner1", "1234");
        service.login(user1token.getResult(), "user1", "1234");

        service.openStore(founder1token.getResult(), "MyStore1");
        service.openStore(founder2token.getResult(), "MyStore2");
        service.appointStoreOwner(founder1token.getResult(), "owner1", "MyStore1");
        service.appointStoreManager(founder1token.getResult(), "manager1", "MyStore1");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6);
    }

    @Test
    public void ManageStoreInventoryTests() {

    }

    @Test
    public void addProduct() {
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
        assertTrue(service.updateProduct(founder1token.getResult(), "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6).getResult());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertEquals(product.getDescription(), "very tasty drink");
    }

    @Test
    public void notManagerUpdateProductInfo() {
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Drinks", null, "bad drink", "MyStore1", 100, 6).isWas_expected_error());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertNotEquals(product.getDescription(), "bad drink");
    }

    @Test
    public void removeProduct() {
        int size = service.getStoreProducts("MyStore1").size();
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola").getResult());
        assertEquals(service.getStoreProducts("MyStore1").size(), size - 1);
    }

    @Test
    public void removeProductTwice() {
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola").getResult());
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola").isWas_expected_error());
    }

    @Test
    public void notManagerRemoveProduct() {
        int size = service.getStoreProducts("MyStore1").size();
        assertTrue(service.removeProductFromStore(user1token.getResult(), "Coca Cola").isWas_expected_error());
        assertEquals(service.getStoreProducts("MyStore1").size(), size);
    }

    @Test
    public void appointStoreOwner() {
        assertTrue(service.appointStoreOwner(owner1token.getResult(), "manager1", "MyStore1").getResult());
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
        assertTrue(service.removeStoreOwnerAppointment(owner1token.getResult(), "manager1", "MyStore1").getResult());
    }

    @Test
    public void chainRemoveStoreOwnerAppointment() {
        service.appointStoreOwner(founder1token.getResult(), "manager2", "MyStore1");
        service.appointStoreOwner(manager2token.getResult(), "user1", "MyStore1");
        assertTrue(service.removeStoreOwnerAppointment(founder1token.getResult(), "manager2", "MyStore1").getResult());
        // TODO
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

    @Test
    public void chainRemoveStoreManagerAppointment() {
        service.appointStoreManager(founder1token.getResult(), "manager2", "MyStore1");
        service.appointStoreManager(manager2token.getResult(), "user1", "MyStore1");
        assertTrue(service.removeStoreManagerAppointment(founder1token.getResult(), "manager2", "MyStore1").getResult());
        assertEquals("need to check", "if user1 is manager");
    }

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
        assertTrue(service.removeStoreManagerAppointment(founder1token.getResult(), "manager1", "MyStore2").getResult());
        assertTrue(service.removeStoreManagerAppointment(founder2token.getResult(), "manager1", "MyStore2").getResult());
    }

    @Test
    public void allowAndDisallowManagerUpdateProducts() {
        assertTrue(service.allowManagerUpdateProducts(owner1token.getResult(), "manager1", "MyStore1").getResult());
        assertTrue(service.updateProduct(manager1token.getResult(), "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6).getResult());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1");
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertEquals(product.getDescription(), "very tasty drink");

        assertTrue(service.disAllowManagerUpdateProducts(owner1token.getResult(), "manager1", "MyStore1").getResult());
        assertTrue(service.updateProduct(manager1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6).isWas_expected_error());
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
        assertTrue(service.allowManagerGetHistory(owner1token.getResult(), "manager1", "MyStore1").getResult());
        service.getStorePurchaseHistory(manager1token.getResult(), "MyStore1");

        assertTrue(service.disAllowManagerGetHistory(owner1token.getResult(), "manager1", "MyStore1").getResult());
        assertTrue(service.getStorePurchaseHistory(manager1token.getResult(), "MyStore1").isWas_expected_error());
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
        assertTrue(service.allowManagerAnswerAndTakeRequests(owner1token.getResult(), "manager1", "MyStore1").getResult());
        service.receiveQuestionsFromBuyers(manager1token.getResult(), "MyStore1");
        assertTrue(service.sendRespondToBuyers(manager1token.getResult(), "MyStore1", "manager1", "check").getResult());

        assertTrue(service.disAllowManagerAnswerAndTakeRequests(owner1token.getResult(), "manager1", "MyStore1").getResult());
        assertTrue(service.receiveQuestionsFromBuyers(manager1token.getResult(), "MyStore1").isWas_expected_error());
        assertTrue(service.sendRespondToBuyers(manager1token.getResult(), "MyStore1", "manager1", "check").isWas_expected_error());
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

    @Test
    public void deleteStore() {
        assertTrue(service.deleteStore(founder1token.getResult(), "MyStore1").getResult());
    }

    @Test
    public void deleteInvalidStore() {
        assertTrue(service.deleteStore(founder1token.getResult(), "MyStore2").isWas_expected_error());
    }

    @Test
    public void deleteInactiveStore() {
        service.deleteStore(founder1token.getResult(), "MyStore1");
        assertTrue(service.deleteStore(founder1token.getResult(), "MyStore1").isWas_expected_error());
    }

    @Test
    public void deleteNotRealStore() {
        assertTrue(service.deleteStore(founder1token.getResult(), "NotARealStore").isWas_expected_error());
    }

    @Test
    public void concurrentAppointStoreManager() {
        Runnable founder1AppointsUser1 = () -> service.appointStoreManager(founder1token.getResult(), "user1", "MyStore1");
        Runnable owner1AppointsUser1 = () -> service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");

        Thread founder1AppointsUser1Thread = new Thread(founder1AppointsUser1);
        Thread owner1AppointsUser1Thread = new Thread(owner1AppointsUser1);

        founder1AppointsUser1Thread.start();
        owner1AppointsUser1Thread.run();
        // TODO
    }

    @Test
    public void concurrentRemoveAndEditProduct() {
        Runnable founder1RemoveProduct = () -> service.removeProductFromStore(founder1token.getResult(), "Coca Cola");
        Runnable owner1UpdateProduct = () -> service.updateProduct(manager1token.getResult(), "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6);

        Thread founder1RemoveProductThread = new Thread(founder1RemoveProduct);
        Thread owner1UpdateProductThread = new Thread(owner1UpdateProduct);

        founder1RemoveProductThread.start();
        owner1UpdateProductThread.run();
        // TODO
    }

    @After
    public void tearDown() {

    }

    /*
        need TODO:
        • getStoreStaff
        • concurrency tests
        • check if a store is closed
     */
}
