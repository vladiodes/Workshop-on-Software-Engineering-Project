package test.UnitTests;

import main.Stores.IStore;
import main.Users.*;
import org.junit.jupiter.api.BeforeEach;
import test.Mocks.StoreMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;
    private User admin;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private IStore store_mock;

    @BeforeEach
    void setUp() {
        user = new User(false,"user1","password");
        admin = new User(true,"admin1","password");
        user2 = new User(false,"user2","password");
        user3 = new User(false,"user3","password");
        user4 = new User(false,"user4","password");
        user5 = new User(false,"user5","password");
        store_mock = new StoreMock();
    }

    @Test
    void checkLogin(){
        assertFalse(user.getIsLoggedIn());
        user.LogIn();
        assertTrue(user.getIsLoggedIn());
    }

    @Test
    void addProductToStoreWithFounderPermissions(){
        user.getFoundedStores().add(store_mock);
        boolean res = user.addProductToStore(store_mock,"product","category",null,null,5,15);
        assertTrue(res);
    }

    @Test
    void addProductToStoreWithoutPermissions(){
        assertThrows(IllegalArgumentException.class,() ->user.addProductToStore(store_mock,"product","category",null,null,5,15));
    }

    @Test
    void updateProductToStoreWithFounderPermissions(){
        user.getFoundedStores().add(store_mock);
        boolean res = user.updateProductToStore(store_mock,"product","product","category",null,null,5,15);
        assertTrue(res);
    }

    @Test
    void updateProductToStoreWithoutPermissions(){
        assertThrows(IllegalArgumentException.class,() ->user.updateProductToStore(store_mock,"product","product","category",null,null,5,15));
    }

    @Test
    void appointOwnerToStoreAlreadyStaff(){
        user2.getFoundedStores().add(store_mock);
        user.getFoundedStores().add(store_mock);
        assertThrows(IllegalArgumentException.class,()->user.appointOwnerToStore(store_mock,user2));
    }

    @Test
    void appointOwnerToStoreSuccess(){
        user.getFoundedStores().add(store_mock);
        assertTrue(user.appointOwnerToStore(store_mock,user2));
        assertTrue(user2.getOwnedStores().contains(store_mock));
    }

    @Test
    void appointOwnerToStoreOwnerAppointing(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        appointStoreOwner(user2,user3,store_mock);
        assertTrue(user3.getOwnedStores().contains(store_mock));
    }

    @Test
    void appointOwnerCircularity(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        appointStoreOwner(user2,user3,store_mock);
        appointStoreOwner(user3,user4,store_mock);
        assertThrows(IllegalArgumentException.class,()->appointStoreOwner(user4,user,store_mock));
    }

    @Test
    void removeOwnerAppointmentNonOwner(){
        user.getFoundedStores().add(store_mock);
        assertThrows(IllegalArgumentException.class,()->user.removeOwnerAppointment(store_mock,user2));
    }

    @Test
    void removeOwnerAppointmentGoodNoChain(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        assertTrue(user.removeOwnerAppointment(store_mock,user2));
        assertFalse(user.getOwnedStores().contains(store_mock));
    }

    private void appointStoreOwner(User appointing,User appointed,IStore in_store) {
        appointing.appointOwnerToStore(in_store,appointed);
        in_store.getOwnersAppointments().add(new OwnerPermissions(appointed,appointing,in_store));
    }

    private void appointStoreManager(User appointing,User appointed,IStore in_store) {
        appointing.appointManagerToStore(in_store,appointed);
        in_store.getManagersAppointments().add(new ManagerPermissions(appointed,appointing,in_store));
    }

    @Test
    void removeOwnerAppointmentNotAppointing(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        appointStoreOwner(user,user3,store_mock);
        assertThrows(IllegalArgumentException.class,()->user2.removeOwnerAppointment(store_mock,user3));
    }
    @Test
    void removeOwnerAppointmentGoodWithChain(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        appointStoreOwner(user2,user3,store_mock);
        appointStoreOwner(user3,user4,store_mock);
        appointStoreOwner(user4,user5,store_mock);
        user.removeOwnerAppointment(store_mock,user2);
        assertEquals(0, user2.getOwnedStores().size());
        assertEquals(0, user3.getOwnedStores().size());
        assertEquals(0, user4.getOwnedStores().size());
        assertEquals(0, user5.getOwnedStores().size());
    }

    @Test
    void removeOwnerAppointmentGoodWithForkedChain(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        appointStoreOwner(user,user3,store_mock);
        appointStoreOwner(user2,user4,store_mock);
        appointStoreOwner(user3,user5,store_mock);
        assertTrue(user.removeOwnerAppointment(store_mock,user2));
        assertEquals(0, user2.getOwnedStores().size());
        assertEquals(1, user3.getOwnedStores().size());
        assertEquals(0, user4.getOwnedStores().size());
        assertEquals(1, user5.getOwnedStores().size());
    }

    @Test
    void removeOwnerAppointmentGoodWithChainIncludingManagers(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        appointStoreOwner(user2,user3,store_mock);
        appointStoreManager(user3,user4,store_mock);
        assertTrue(user.removeOwnerAppointment(store_mock,user2));
        assertEquals(0, user2.getOwnedStores().size());
        assertEquals(0, user3.getOwnedStores().size());
        assertEquals(0, user4.getManagedStores().size());
    }

    @Test
    void appointManagerToStore(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertTrue(user2.getManagedStores().contains(store_mock));
    }

    @Test
    void appointManagerAlreadyManager(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertThrows(IllegalArgumentException.class,()->appointStoreManager(user,user2,store_mock));
    }

    @Test
    void RemoveManagerAppointmentGood(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertTrue(user.removeManagerAppointment(store_mock,user2));
        assertEquals(0, user2.getManagedStores().size());
    }

    @Test
    void RemoveManagerNotManager(){
        user.getFoundedStores().add(store_mock);
        assertThrows(IllegalArgumentException.class,()->user.removeManagerAppointment(store_mock,user2));
    }

    @Test
    void RemoveManagerOwner(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        assertThrows(IllegalArgumentException.class,()->user.removeManagerAppointment(store_mock,user2));
    }

    @Test
    void grantPermissionNotStaff(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertThrows(IllegalArgumentException.class,()->user3.grantOrDeletePermission(user2,store_mock,true, StorePermission.UpdateAddProducts));
    }

    @Test
    void grantPermissionNotToManager(){
        user.getFoundedStores().add(store_mock);
        assertThrows(IllegalArgumentException.class,()->user.grantOrDeletePermission(user2,store_mock,true,StorePermission.UpdateAddProducts));
    }

    @Test
    void grantPermissionNotByAppointing(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        appointStoreManager(user,user3,store_mock);
        assertThrows(IllegalArgumentException.class,()->user2.grantOrDeletePermission(user3,store_mock,true,StorePermission.UpdateAddProducts));
    }

    @Test
    void grantPermissionGood(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        appointStoreManager(user2,user3,store_mock);
        assertTrue(user2.grantOrDeletePermission(user3,store_mock,true,StorePermission.UpdateAddProducts));
    }

    @Test
    void closeStoreGood(){
        user.getFoundedStores().add(store_mock);
        assertTrue(user.closeStore(store_mock,null));
    }

    @Test
    void closeStoreNotFounder(){
        assertThrows(IllegalArgumentException.class,()->user.closeStore(store_mock,null));
    }

    @Test
    void reopenStoreGood(){
        user.getFoundedStores().add(store_mock);
        assertTrue(user.reOpenStore(store_mock,null));
    }

    @Test
    void reopenStoreNotFounder(){
        assertThrows(IllegalArgumentException.class,()->user.reOpenStore(store_mock,null));
    }

    @Test
    void getStoreStaffNotOwnerOrFounder(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertThrows(IllegalArgumentException.class,()->user2.getStoreStaff(store_mock));
        assertThrows(IllegalArgumentException.class,()->user3.getStoreStaff(store_mock));
    }

    @Test
    void getStoreStaffOwnerOrFounderGood(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        assertDoesNotThrow(()->user2.getStoreStaff(store_mock));
        assertDoesNotThrow(()->user.getStoreStaff(store_mock));
    }

    @Test
    void receiveQuestionsFromStoreOwnerOrFounderGood(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        assertDoesNotThrow(()->user2.receiveQuestionsFromStore(store_mock));
        assertDoesNotThrow(()->user.receiveQuestionsFromStore(store_mock));
    }

    @Test
    void receiveQuestionsFromStoreManagerWithPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertDoesNotThrow(()->user2.receiveQuestionsFromStore(store_mock));
    }

    @Test
    void receiveQuestionsFromStoreManagerWithoutPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        user.grantOrDeletePermission(user2,store_mock,false,StorePermission.AnswerAndTakeRequests);
        assertThrows(IllegalArgumentException.class,()->user2.receiveQuestionsFromStore(store_mock));
    }

    @Test
    void sendRespondsFromStoreOwnerOrFounderGood(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        assertDoesNotThrow(()->user2.sendRespondFromStore(store_mock,user3,"asdasd",null));
        assertDoesNotThrow(()->user.sendRespondFromStore(store_mock,user3,"asdasd",null));
    }

    @Test
    void sendRespondsFromStoreManagerWithPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertDoesNotThrow(()->user2.sendRespondFromStore(store_mock,user3,"asdasd",null));
    }

    @Test
    void sendRespondsFromStoreManagerWithoutPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        user.grantOrDeletePermission(user2,store_mock,false,StorePermission.AnswerAndTakeRequests);
        assertThrows(IllegalArgumentException.class,()->user2.sendRespondFromStore(store_mock,user3,"asdasd",null));
    }

    @Test
    void getStorePurchaseHistoryOwnerOrFounderGood(){
        user.getFoundedStores().add(store_mock);
        appointStoreOwner(user,user2,store_mock);
        assertDoesNotThrow(()->user2.getStorePurchaseHistory(store_mock));
        assertDoesNotThrow(()->user.getStorePurchaseHistory(store_mock));
    }

    @Test
    void getStorePurchaseHistoryManagerWithPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertDoesNotThrow(()->user2.getStorePurchaseHistory(store_mock));
    }

    @Test
    void getStorePurchaseHistoryManagerWithoutPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        user.grantOrDeletePermission(user2,store_mock,false,StorePermission.ViewStoreHistory);
        assertThrows(IllegalArgumentException.class,()->user2.getStorePurchaseHistory(store_mock));
    }

    @Test
    void removeStoreNotAdmin(){
        assertThrows(IllegalArgumentException.class,()->user.removeStore(store_mock));
    }

    @Test
    void removeStoreAdmin(){
        assertTrue(admin.removeStore(store_mock));
    }

    @Test
    void removeUserAdmin(){
        assertTrue(admin.deleteUser(user));
    }

    @Test
    void removeUserNotAdmin(){
        assertThrows(IllegalArgumentException.class,()->user.deleteUser(user2));
    }

    @Test
    void openStoreGood(){
        IStore store=user.openStore("store1");
        assertNotNull(store);
        assertEquals(1,user.getFoundedStores().size());
        assertTrue(user.getFoundedStores().contains(store));
    }

    @Test
    void removeProductFromStoreNoPermission(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertThrows(IllegalArgumentException.class,()->user2.removeProductFromStore("product",store_mock));
    }

    @Test
    void removeProductFromStoreNotStaff(){
        assertThrows(IllegalArgumentException.class,()->user2.removeProductFromStore("product",store_mock));
    }

    @Test
    void removeProductFromStoreOwnerOrFounderOrManagerWithPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        appointStoreOwner(user,user3,store_mock);
        user.grantOrDeletePermission(user2,store_mock,true,StorePermission.UpdateAddProducts);
        assertTrue(user.removeProductFromStore("product",store_mock));
        assertTrue(user2.removeProductFromStore("product",store_mock));
        assertTrue(user3.removeProductFromStore("product",store_mock));
    }
}