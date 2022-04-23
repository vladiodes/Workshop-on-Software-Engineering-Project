package test.UnitTests;

import main.Stores.IStore;
import main.Users.ManagerPermissions;
import main.Users.StorePermission;
import main.Users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.Mocks.StoreMock;

import static org.junit.jupiter.api.Assertions.*;

class ManagerPermissionsTest {
    private ManagerPermissions mp;
    private IStore mock_store;
    private User appointing_user;
    private User appointed_user;

    @BeforeEach
    void setUp(){
        mock_store=new StoreMock();
        appointing_user =new User(false,"user1","password");
        appointed_user =new User(false,"user2","password");
        mp=new ManagerPermissions(appointing_user, appointed_user,mock_store);
    }

    @Test
    void hasPermissionTest(){
        //testing for default permissions
        assertEquals(mp.getPermissions().size(),2);
        assertTrue(mp.hasPermission(StorePermission.AnswerAndTakeRequests));
        assertTrue(mp.hasPermission(StorePermission.ViewStoreHistory));
    }

    @Test
    void addPermissionTest(){
        mp.addPermission(StorePermission.OwnerPermission);
        assertTrue(mp.hasPermission(StorePermission.OwnerPermission));
    }

    @Test
    void addPermissionDoubleTest(){
        mp.addPermission(StorePermission.OwnerPermission);
        int size = mp.getPermissions().size();
        mp.addPermission(StorePermission.OwnerPermission);
        assertTrue(mp.hasPermission(StorePermission.OwnerPermission));
        assertEquals(size,mp.getPermissions().size());
    }

    @Test
    void removePermissionTest(){
        mp.removePermission(StorePermission.ViewStoreHistory);
        assertFalse(mp.hasPermission(StorePermission.ViewStoreHistory));
    }

    @Test
    void removePermissionNonExist(){
        mp.removePermission(StorePermission.OwnerPermission);
        assertEquals(2, mp.getPermissions().size());
    }

    @Test
    void removePermissionDouble(){
        mp.removePermission(StorePermission.ViewStoreHistory);
        mp.removePermission(StorePermission.ViewStoreHistory);
        assertFalse(mp.hasPermission(StorePermission.ViewStoreHistory));
        assertEquals(mp.getPermissions().size(),1);
    }

    @Test
    void permissionsToStringTest(){
        String expected1 = "Answer and take requests, View store history, ";
        String expected2 = "View store history, Answer and take requests, ";
        String res = mp.permissionsToString();
        assertTrue(expected1.equals(res) || expected2.equals(res));
    }


}