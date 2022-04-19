package test.AcceptanceTests;

import main.DTO.ProductDTO;
import main.DTO.StoreDTO;
import main.DTO.UserDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AT {

    StoreDTO store;
    UserDTO user1, manager, owner, founder;
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

        service.openStore(founder1token.getResult(), "MyStore");
        service.openStore(founder2token.getResult(), "MyStore2");
        service.addProductToStore(manager1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6);
    }

    @Test
    public void ManageStoreInventoryTests() {

    }

    @Test
    public void addProduct() {
        int size = service.getStoreProducts("MyStore").size();
        assertTrue(service.addProductToStore(manager1token.getResult(), "Pepsi Cola", "Drinks", null, "less tasty drink", "MyStore", 50, 5).getResult());
        assertEquals(service.getStoreProducts("MyStore").size(), size + 1);
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
        searchFlag = false;
        for(ProductDTO product : MyStoreProducts)
            searchFlag |= product.getProductName().equals("Pepsi Cola");
        assertTrue(searchFlag);
    }

    @Test
    public void addProductTwice() {
        assertTrue(service.addProductToStore(manager1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6).isWas_expected_error());
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
        int counter = 0;
        for(ProductDTO product : MyStoreProducts)
            if(product.getProductName().equals("Coca Cola"))
                counter++;
        assertEquals(counter, 1);
    }

    @Test
    public void notManagerAddProduct() {
        int size = service.getStoreProducts("MyStore").size();
        assertTrue(service.updateProduct(user1token.getResult(), "Crystal Cola", "Drinks", null, "ew", "MyStore", 100, 6).isWas_expected_error());
        assertEquals(service.getStoreProducts("MyStore").size(), size);
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
        searchFlag = false;
        for(ProductDTO product : MyStoreProducts)
            searchFlag |= product.getProductName().equals("Crystal Cola");
        assertFalse(searchFlag);
    }

    @Test
    public void updateProductInfo() {
        assertTrue(service.updateProduct(manager1token.getResult(), "Coca Cola", "Drinks", null, "very tasty drink", "MyStore", 200, 6).getResult());
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
        for(ProductDTO product : MyStoreProducts)
            if(product.getProductName().equals("Coca Cola"))
                assertEquals(product.getDescription(), "very tasty drink");
    }

    @Test
    public void notManagerUpdateProductInfo() {
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Drinks", null, "bad drink", "MyStore", 100, 6).isWas_expected_error());
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
        for(ProductDTO product : MyStoreProducts)
            if(product.getProductName().equals("Coca Cola"))
                assertNotEquals(product.getDescription(), "bad drink");
    }

    @Test
    public void removeProduct() {
        int size = service.getStoreProducts("MyStore").size();
        assertTrue(service.removeProductFromStore(manager1token.getResult(), "Coca Cola").getResult());
        assertEquals(service.getStoreProducts("MyStore").size(), size - 1);
    }

    @Test
    public void removeProductTwice() {
        assertTrue(service.removeProductFromStore(manager1token.getResult(), "Coca Cola").getResult());
        assertTrue(service.removeProductFromStore(manager1token.getResult(), "Coca Cola").isWas_expected_error());
    }

    @Test
    public void notManagerRemoveProduct() {
        int size = service.getStoreProducts("MyStore").size();
        assertTrue(service.removeProductFromStore(user1token.getResult(), "Coca Cola").isWas_expected_error());
        assertEquals(service.getStoreProducts("MyStore").size(), size);
    }

    @Test
    public void appointStoreOwner() {
        assertTrue(service.appointStoreOwner(owner1token.getResult(), "manager1", "MyStore").getResult());
        assertTrue(service.appointStoreOwner(founder1token.getResult(), "manager2", "MyStore").getResult());
    }

    @Test
    public void notOwnerAppointStoreOwner() {
        assertTrue(service.appointStoreOwner(user1token.getResult(), "manager1", "MyStore").isWas_expected_error());
        assertTrue(service.appointStoreOwner(manager2token.getResult(), "manager1", "MyStore").isWas_expected_error());
    }

    @Test
    public void appointStoreOwnerAgain() {
        service.appointStoreOwner(founder1token.getResult(), "manager1", "MyStore");
        assertTrue(service.appointStoreOwner(owner1token.getResult(), "manager1", "MyStore").isWas_expected_error());
    }

    @Test
    public void removeStoreOwnerAppointment() {
        assertTrue(service.removeStoreOwnerAppointment(owner1token.getResult(), "manager1", "MyStore").getResult());
    }

    @Test
    public void chainRemoveStoreOwnerAppointment() {
        service.appointStoreOwner(founder1token.getResult(), "manager2", "MyStore");
        service.appointStoreOwner(manager2token.getResult(), "user1", "MyStore");
        assertTrue(service.removeStoreOwnerAppointment(founder1token.getResult(), "manager2", "MyStore").getResult());

    }

    @Test
    public void notOwnerRemoveStoreOwnerAppointment() {
        assertTrue(service.removeStoreOwnerAppointment(user1token.getResult(), "manager1", "MyStore").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(manager2token.getResult(), "manager1", "MyStore").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(user1token.getResult(), "owner1", "MyStore").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(manager2token.getResult(), "owner1", "MyStore").isWas_expected_error());
    }

    @Test
    public void otherOwnerRemoveStoreOwnerAppointment() {
        // The owner_to_remove was not appointed by Owner
        service.appointStoreOwner(owner1token.getResult(), "manager1", "MyStore");
        assertTrue(service.removeStoreOwnerAppointment(manager1token.getResult(), "owner1", "MyStore").isWas_expected_error());
    }

    @Test
    public void appointStoreManager() {
        assertTrue(service.appointStoreManager(owner1token.getResult(), "user1", "MyStore").getResult());
        assertTrue(service.appointStoreManager(founder1token.getResult(), "manager2", "MyStore").getResult());
    }

    @Test
    public void notOwnerAppointStoreManager() {
        assertTrue(service.appointStoreManager(user1token.getResult(), "user1", "MyStore").isWas_expected_error());
        assertTrue(service.appointStoreManager(manager2token.getResult(), "user1", "MyStore").isWas_expected_error());
    }

    @Test
    public void appointStoreManagerAgain() {
        service.appointStoreManager(founder1token.getResult(), "user1", "MyStore");
        assertTrue(service.appointStoreManager(owner1token.getResult(), "user1", "MyStore").isWas_expected_error());
    }

    @Test
    public void appointOwnerAsStoreManager() {
        assertTrue(service.appointStoreManager(owner1token.getResult(), "owner1", "MyStore").isWas_expected_error());
    }

    @Test
    public void removeStoreManagerAppointment() {
        assertTrue(service.removeStoreManagerAppointment(owner1token.getResult(), "manager1", "MyStore").getResult());
    }

    @Test
    public void chainRemoveStoreManagerAppointment() {
        service.appointStoreManager(founder1token.getResult(), "manager2", "MyStore");
        service.appointStoreManager(manager2token.getResult(), "user1", "MyStore");
        assertTrue(service.removeStoreManagerAppointment(founder1token.getResult(), "manager2", "MyStore").getResult());

    }

    @Test
    public void notOwnerRemoveStoreManagerAppointment() {
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "manager1", "MyStore").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(manager2token.getResult(), "manager1", "MyStore").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "owner1", "MyStore").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(manager2token.getResult(), "owner1", "MyStore").isWas_expected_error());
    }

    @Test
    public void otherOwnerRemoveStoreManagerAppointment() {
        // The manager_to_remove was not appointed by Owner
        service.appointStoreManager(owner1token.getResult(), "user1", "MyStore");
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "owner1", "MyStore").isWas_expected_error());
    }

    @Test
    public void deleteStore() {
        service.deleteStore(owner1token.getResult(), "MyStore");
    }

    @Test
    public void deleteInvalidStore() {
        assertTrue(service.deleteStore(owner1token.getResult(), "MyStore2").isWas_expected_error());
    }

    @Test
    public void deleteInactiveStore() {
        service.deleteStore(owner1token.getResult(), "MyStore");
        assertTrue(service.deleteStore(owner1token.getResult(), "MyStore").isWas_expected_error());
    }

    @Test
    public void deleteNotRealStore() {
        assertTrue(service.deleteStore(owner1token.getResult(), "NotARealStore").isWas_expected_error());
    }

    @Test
    public void reopenStore() {
        service.deleteStore(owner1token.getResult(), "MyStore");
        assertTrue(service.reopenStore(owner1token.getResult(), "MyStore").getResult());
    }

    @Test
    public void reopenActiveStore() {
        assertTrue(service.reopenStore(owner1token.getResult(), "MyStore").isWas_expected_error());
    }

    @Test
    public void reopenInvalidStore() {
        assertTrue(service.reopenStore(owner1token.getResult(), "MyStore2").isWas_expected_error());
    }

    @Test
    public void reopenNotRealStore() {
        assertTrue(service.reopenStore(owner1token.getResult(), "NotARealStore").isWas_expected_error());
    }

    @After
    public void tearDown() {

    }

    /*
        need TODO:
        • manager without permissions
        • assert permissions
        • Change store manager permissions
        • concurrency tests
        • member’s questions
     */
}
