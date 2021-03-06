package test.UnitTests;

import main.Market.Market;
import main.Stores.OwnerAppointmentRequest;
import main.Stores.Store;
import main.Users.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserTest {
    private User user;
    private User admin;
    private User user2;
    private User user3;
    private User user4;
    private User user5;

    @Mock
    private Store store_mock;
    @Mock
    private Store store_mock2;



    @BeforeEach
    void setUp() {
        user = new User(false,"user1","password");
        admin = new User(true,"admin1","password");
        user2 = new User(false,"user2","password");
        user3 = new User(false,"user3","password");
        user4 = new User(false,"user4","password");
        user5 = new User(false,"user5","password");
        store_mock = mock(Store.class);
        store_mock2=mock(Store.class);
        when(store_mock.getOwnersAppointments()).thenReturn(new LinkedList<>());
        when(store_mock.addOwnerRequest(any(OwnerAppointmentRequest.class))).thenReturn(true);
        List<ManagerPermissions> managersList = new ArrayList<>();
        when(store_mock.getManagersAppointments()).thenReturn(managersList);
        when(store_mock.getManagersAppointments()).thenReturn(managersList);
    }

    @Test
    void addSecurityQuestionFail()
    {
        assertTrue(user.getSecurityQNA().isEmpty());
        assertThrows(Exception.class, ()->user.addSecurityQuestion("Question?", ""));
        assertThrows(Exception.class, ()->user.addSecurityQuestion("", "Answer!"));
        assertTrue(user.getSecurityQNA().isEmpty());
    }
    @Test
    void addSecurityQuestion()
    {
        assertTrue(user.getSecurityQNA().isEmpty());
        assertDoesNotThrow(()->user.addSecurityQuestion("Question?", "Answer!"));
        assertFalse(user.getSecurityQNA().isEmpty());
    }

    @Test
    void addProductToStoreWithFounderPermissions(){
        user.getFoundedStores().add(store_mock);
        when(store_mock.addProduct("product","category",null,null,5,15)).thenReturn(true);
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
        when(store_mock.updateProduct("product","product","category",null,null,5,15)).thenReturn(true);
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
    }



    @Test
    void removeOwnerAppointmentNonOwner(){
        user.getFoundedStores().add(store_mock);
        assertThrows(IllegalArgumentException.class,()->user.removeOwnerAppointment(store_mock,user2));
    }



    private void appointStoreOwner(User appointing,User appointed,Store in_store) {
        appointing.appointOwnerToStore(in_store,appointed);
        in_store.getOwnersAppointments().add(new OwnerPermissions(appointed,appointing,in_store));
    }

    private void requestToAppointStoreOwner(User appointing,User appointed,Store in_store) {
        appointing.appointOwnerToStore(in_store,appointed);
        in_store.getAllOwnerRequests().add(new OwnerAppointmentRequest(appointing,appointed));
    }



    private void appointStoreManager(User appointing,User appointed,Store in_store) {
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
        user2.addOwnedStore(new OwnerPermissions(user2,user,store_mock));
        appointStoreManager(user2,user3,store_mock);
        assertTrue(user2.grantOrDeletePermission(user3,store_mock,true,StorePermission.UpdateAddProducts));
    }

    @Test
    void closeStoreGood(){
        user.getFoundedStores().add(store_mock);
        assertTrue(user.closeStore(store_mock));
    }

    @Test
    void closeStoreNotFounder(){
        assertThrows(IllegalArgumentException.class,()->user.closeStore(store_mock));
    }

    @Test
    void reopenStoreGood(){
        user.getFoundedStores().add(store_mock);
        assertTrue(user.reOpenStore(store_mock));
    }

    @Test
    void reopenStoreNotFounder(){
        assertThrows(IllegalArgumentException.class,()->user.reOpenStore(store_mock));
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
        user2.addOwnedStore(new OwnerPermissions(user2,user,store_mock));
        assertDoesNotThrow(()->user2.getStoreStaff(store_mock));
        assertDoesNotThrow(()->user.getStoreStaff(store_mock));
    }

    @Test
    void receiveQuestionsFromStoreOwnerOrFounderGood(){
        user.getFoundedStores().add(store_mock);
        user2.addOwnedStore(new OwnerPermissions(user2,user,store_mock));
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
        user2.addOwnedStore(new OwnerPermissions(user2,user,store_mock));
        assertDoesNotThrow(()->user2.sendRespondFromStore(store_mock,user3,"asdasd"));
        assertDoesNotThrow(()->user.sendRespondFromStore(store_mock,user3,"asdasd"));
    }

    @Test
    void sendRespondsFromStoreManagerWithPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertDoesNotThrow(()->user2.sendRespondFromStore(store_mock,user3,"asdasd"));
    }

    @Test
    void sendRespondsFromStoreManagerWithoutPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        user.grantOrDeletePermission(user2,store_mock,false,StorePermission.AnswerAndTakeRequests);
        assertThrows(IllegalArgumentException.class,()->user2.sendRespondFromStore(store_mock,user3,"asdasd"));
    }

    @Test
    void getStorePurchaseHistoryOwnerOrFounderGood(){
        user.getFoundedStores().add(store_mock);
        user2.addOwnedStore(new OwnerPermissions(user2,user,store_mock));
        assertDoesNotThrow(()->user2.getStorePurchaseHistoryByTime(store_mock));
        assertDoesNotThrow(()->user.getStorePurchaseHistoryByTime(store_mock));
    }


    @Test
    void getStorePurchaseHistoryManagerWithPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        assertDoesNotThrow(()->user2.getStorePurchaseHistoryByTime(store_mock));
    }

    @Test
    void getStorePurchaseHistoryManagerWithoutPermissions(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        user.grantOrDeletePermission(user2,store_mock,false,StorePermission.ViewStoreHistory);
        assertThrows(IllegalArgumentException.class,()->user2.getStorePurchaseHistoryByTime(store_mock));
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
    void openStoreGood(){
        Store store=user.openStore("store1");
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
        user3.addOwnedStore(new OwnerPermissions(user3,user,store_mock));
        user.grantOrDeletePermission(user2,store_mock,true,StorePermission.UpdateAddProducts);
        when(store_mock.removeProduct("product")).thenReturn(true);
        assertTrue(user.removeProductFromStore("product",store_mock));
        assertTrue(user2.removeProductFromStore("product",store_mock));
        assertTrue(user3.removeProductFromStore("product",store_mock));
    }

    @Test
    void testVisitorTypeFounder(){
        user.getFoundedStores().add(store_mock);
        Assertions.assertEquals(user.visitorType(), Market.StatsType.OwnerVisitor);
    }

    @Test
    void testVisitorTypeOwner(){
        user.getFoundedStores().add(store_mock);
        user2.addOwnedStore(new OwnerPermissions(user2,user,store_mock));
        Assertions.assertEquals(user2.visitorType(), Market.StatsType.OwnerVisitor);
    }

    @Test
    void testVisitorTypeNonStaff(){
        Assertions.assertEquals(user2.visitorType(), Market.StatsType.NonStaffVisitor);
    }

    @Test
    void testOnlyManagerVisitor(){
        user.getFoundedStores().add(store_mock);
        appointStoreManager(user,user2,store_mock);
        Assertions.assertEquals(user2.visitorType(), Market.StatsType.ManagerVisitor);
    }

    @Test
    void testManagerVisitor(){
        Assertions.assertEquals(admin.visitorType(), Market.StatsType.AdminVisitor);
    }

    @Test
    void testManagerAndOwnerVisitor(){
        user.getFoundedStores().add(store_mock);
        user2.getFoundedStores().add(store_mock2);
        appointStoreManager(user2,user,store_mock2);

        assertNull(user.visitorType());
    }
}