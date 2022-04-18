package test.AcceptanceTests;

import main.DTO.ProductDTO;
import main.DTO.StoreDTO;
import main.DTO.UserDTO;
import main.Service.IService;
import main.Service.Service;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.NoPermissionException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AT {

    StoreDTO store;
    UserDTO user1, manager, owner, founder;
    IService service = new Service();

    @Before
    public void setUp() throws Exception {
        service.register("manager1", "1234");
        service.register("founder1", "1234");
        service.login("manager", "manager1", "1234");
        service.login("founder", "founder1", "1234");
        service.addProductToStore(manager.getUserName(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6);
    }

    @Test
    public void ManageStoreInventoryTests() {

    }

    @Test
    public void addProduct() {
        int size = service.getStoreProducts("MyStore").size();
        assertDoesNotThrow(() -> service.addProductToStore("manager", "Coca Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6));
        assertEquals(service.getStoreProducts("MyStore").size(), size + 1);
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
    }

    @Test
    public void addProductTwice() {
        assertDoesNotThrow(() -> service.addProductToStore("manager", "Coca Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6));
        assertThrows(IllegalArgumentException.class, () -> service.addProductToStore("manager", "Coca Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6));
    }

    @Test
    public void notManagerAddProduct() {
        int size = service.getStoreProducts("MyStore").size();
        assertThrows(NoPermissionException.class, () -> service.updateProduct("user", "Pepsi Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6));
        assertEquals(service.getStoreProducts("MyStore").size(), size);
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
    }

    @Test
    public void updateProductInfo() {
        assertDoesNotThrow(() -> service.updateProduct("manager", "Coca Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6));
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
        assertTrue(MyStoreProducts.contains("Coca Cola"));
    }

    @Test
    public void notManagerUpdateProductInfo() {
        assertThrows(NoPermissionException.class, () -> service.updateProduct("user", "Coca Cola", "Drinks", null, "tasty drink", "MyStore", 100, 6));
        List<ProductDTO> MyStoreProducts = service.getStoreProducts("MyStore");
    }

    @Test
    public void removeProduct() {
        int size = service.getStoreProducts("MyStore").size();
        assertDoesNotThrow(() -> service.removeProductFromStore("manager", "Coca Cola"));
        assertEquals(service.getStoreProducts("MyStore").size(), size - 1);
    }

    @Test
    public void removeProductTwice() {
        assertDoesNotThrow(() -> service.removeProductFromStore("manager", "Coca Cola"));
        assertThrows(IllegalArgumentException.class, () -> service.removeProductFromStore("manager", "Coca Cola"));
    }

    @Test
    public void notManagerRemoveProduct() {
        int size = service.getStoreProducts("MyStore").size();
        assertThrows(NoPermissionException.class, () -> service.removeProductFromStore("user", "Coca Cola"));
        assertEquals(service.getStoreProducts("MyStore").size(), size);
    }

    @Test
    public void appointStoreOwner() {
        assertDoesNotThrow(() -> service.appointStoreOwner("owner", "new_manager_name", "MyStore"));
    }

    @Test
    public void notOwnerAppointStoreOwner() {
        assertThrows(IllegalArgumentException.class, () -> service.appointStoreOwner("user", "new_manager_name", "MyStore"));
        assertThrows(IllegalArgumentException.class, () -> service.appointStoreOwner("manager", "new_manager_name", "MyStore"));
    }

    @Test
    public void appointStoreOwnerAgain() {
        service.appointStoreOwner("owner", "new_manager_name", "MyStore");
        assertThrows(IllegalArgumentException.class, () -> service.appointStoreOwner("owner", "new_manager_name", "MyStore"));
    }

    @Test
    public void removeStoreOwnerAppointment() {
        assertDoesNotThrow(() -> service.removeStoreOwnerAppointment("owner", "new_manager_name", "MyStore"));
    }

    @Test
    public void notOwnerRemoveStoreOwnerAppointment() {
        assertThrows(IllegalArgumentException.class, () -> service.removeStoreOwnerAppointment("manager", "new_manager_name", "MyStore"));
    }

    @Test
    public void otherOwnerRemoveStoreOwnerAppointment() {
        // The owner_to_remove was not appointed by Owner
        assertThrows(IllegalArgumentException.class, () -> service.removeStoreOwnerAppointment("owner2", "new_manager_name", "MyStore"));
    }

    @Test
    public void appointStoreManager() {
        assertDoesNotThrow(() -> service.appointStoreManager("owner", "new_manager_name", "MyStore"));
    }

    @Test
    public void notOwnerAppointStoreManager() {
        assertThrows(IllegalArgumentException.class, () -> service.appointStoreManager("user", "new_manager_name", "MyStore"));
        assertThrows(IllegalArgumentException.class, () -> service.appointStoreManager("manager", "new_manager_name", "MyStore"));
    }

    @Test
    public void appointStoreManagerAgain() {
        service.appointStoreManager("owner", "new_manager_name", "MyStore");
        assertThrows(IllegalArgumentException.class, () -> service.appointStoreManager("owner", "new_manager_name", "MyStore"));
    }

    @Test
    public void appointOwnerAsStoreManager() {
        assertThrows(IllegalArgumentException.class, () -> service.appointStoreManager("owner", "owner", "MyStore"));
    }

    @Test
    public void removeStoreManagerAppointment() {
        assertDoesNotThrow(() -> service.removeStoreManagerAppointment("owner", "new_manager_name", "MyStore"));
    }

    @Test
    public void notOwnerRemoveStoreManagerAppointment() {
        assertThrows(IllegalArgumentException.class, () -> service.removeStoreManagerAppointment("manager", "new_manager_name", "MyStore"));
    }

    @Test
    public void otherOwnerRemoveStoreManagerAppointment() {
        // The manager_to_remove was not appointed by Owner
        assertThrows(IllegalArgumentException.class, () -> service.removeStoreManagerAppointment("owner2", "new_manager_name", "MyStore"));
    }

    @Test
    public void deleteStore() {
        service.deleteStore("owner", "MyStore");
    }

    @Test
    public void deleteInvalidStore() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteStore("owner", "MyStore1"));
    }

    @Test
    public void deleteInactiveStore() {
        service.deleteStore("owner", "MyStore");
        assertThrows(IllegalArgumentException.class, () -> service.deleteStore("owner", "MyStore"));
    }

    @Test
    public void deleteNotRealStore() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteStore("owner", "NotARealStore"));
    }

    @Test
    public void reopenStore() {
        service.deleteStore("owner", "MyStore");
        assertDoesNotThrow(() -> service.reOpenStore("owner", "MyStore"));
    }

    @Test
    public void reopenActiveStore() {
        assertThrows(IllegalArgumentException.class, () -> service.reOpenStore("owner", "MyStore"));
    }

    @Test
    public void reopenInvalidStore() {
        assertThrows(IllegalArgumentException.class, () -> service.reOpenStore("owner", "MyStore1"));
    }

    @Test
    public void reopenNotRealStore() {
        assertThrows(IllegalArgumentException.class, () -> service.reOpenStore("owner", "NotARealStore"));
    }

    @After
    public void tearDown() {

    }

    /*
        need TODO:
        • manager without permissions
        • change store’s buying / discount policy
        • change store’s consistency rules
        • assert permissions
        • Change store manager permissions
        • chain of remove owner
        • concurrency tests
        • member’s questions
     */
}
