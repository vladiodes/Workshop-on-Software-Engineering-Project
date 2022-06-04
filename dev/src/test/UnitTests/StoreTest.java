package test.UnitTests;

import main.Stores.Product;
import main.Stores.Store;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {

    private Store store;
    private User founder;

    @BeforeEach
    void setUp(){
        founder=new User(false,"founder","password");
        store=new Store("store1",founder);
    }

    @Test
    void addProductGoodTest(){
        assertTrue(store.addProduct("product1","category",null,"desc",120,200.5));
        assertNotNull(store.getProduct("product1"));
        assertEquals(1, store.getProductsByName().size());
    }

    @Test
    void addProductDuplicate(){
        store.addProduct("product1","category",null,"desc",120,200.5);
        assertThrows(IllegalArgumentException.class,()->store.addProduct("product1","category",null,"desc",120,200.5));
    }

    @Test
    void updateProductGoodTest(){
        store.addProduct("product1","category",null,"desc",120,200.5);
        Product before = store.getProduct("product1");
        assertTrue(store.updateProduct("product1","new_product1","new_cat",null,"new_description",12,200));
        Product after = store.getProduct("new_product1");
        assertSame(before, after);
        assertEquals(after.getName(),"new_product1");
        assertEquals(after.getCategory(),"new_cat");
        assertEquals(after.getDescription(),"new_description");
        assertEquals(after.getQuantity(),12);
        assertEquals(after.getCleanPrice(),200);
    }

    @Test
    void updateProductTakenName(){
        store.addProduct("product1","category",null,"desc",120,200.5);
        store.addProduct("product2","category",null,"desc",120,200.5);
        assertThrows(IllegalArgumentException.class,()->store.updateProduct("product2","product1","cat",null,"dsc",120,200.5));
    }

    @Test
    void closeStoreAlreadyClosed(){
        store.closeStore();
        assertThrows(IllegalArgumentException.class,()->store.closeStore());
    }

    @Test
    void closeStoreGood(){
        User owner = new User(false,"owner","password");
        User manager = new User(false,"manager","password");
        OwnerPermissions op = new OwnerPermissions(owner,founder,store);
        ManagerPermissions mp = new ManagerPermissions(manager,owner,store);
        store.addOwnerToStore(op);
        store.addManager(mp);
        store.closeStore();
//        assertEquals(1, bus.getMessagesFromUserRequest(owner).size());
//        assertEquals(1, bus.getMessagesFromUserRequest(founder).size());
//        assertEquals(1, bus.getMessagesFromUserRequest(manager).size());
    }

    @Test
    void reOpenAlreadyOpened(){
        assertThrows(IllegalArgumentException.class,()->store.reOpen());
    }

    @Test
    void reOpenGood(){
        User owner = new User(false,"owner","password");
        User manager = new User(false,"manager","password");
        OwnerPermissions op = new OwnerPermissions(owner,founder,store);
        ManagerPermissions mp = new ManagerPermissions(manager,owner,store);
        store.addOwnerToStore(op);
        store.addManager(mp);
        store.closeStore();
        store.reOpen();
//        assertEquals(2, bus.getMessagesFromUserRequest(owner).size());
//        assertEquals(2, bus.getMessagesFromUserRequest(founder).size());
//        assertEquals(2, bus.getMessagesFromUserRequest(manager).size());
    }

    @Test
    void getStoreStaffTest(){
        User owner = new User(false,"owner","password");
        User manager = new User(false,"manager","password");
        OwnerPermissions op = new OwnerPermissions(owner,founder,store);
        ManagerPermissions mp = new ManagerPermissions(manager,owner,store);
        store.addOwnerToStore(op);
        store.addManager(mp);
        HashMap<User,String> map = store.getStoreStaff();
        assertEquals(map.get(founder),"Founder of the store");
        assertEquals(map.get(owner),"Owner of the store");
        assertEquals(map.get(manager),"Manager of the store, has permissions: Answer and take requests, View store history, ");
        assertEquals(map.size(),3);
    }

    //TODO: add test for get questions from buyers function

    @Test
    void removeRolesTest(){
        User owner = new User(false,"owner","password");
        User manager = new User(false,"manager","password");
        OwnerPermissions op = new OwnerPermissions(owner,founder,store);
        ManagerPermissions mp = new ManagerPermissions(manager,owner,store);
        store.addOwnerToStore(op);
        store.addManager(mp);
        store.CancelStaffRoles();
        assertEquals(store.getManagersOfStore().size(),0);
        assertEquals(store.getOwnersOfStore().size(),0);
    }

    @Test
    void removeProductTest(){
        store.addProduct("product1","cat",null,"desc",120,120);
        assertTrue(store.removeProduct("product1"));
    }

    @Test
    void removeProductNonExist(){
        assertThrows(IllegalArgumentException.class,()->store.removeProduct("aaa"));
    }


}