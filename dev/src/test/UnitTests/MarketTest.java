package test.UnitTests;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Market;
import main.Security.ISecurity;
import main.Shopping.ShoppingCart;
import main.Stores.IStore;
import main.Users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarketTest {
    @Mock
    ISupplying mockSupplyer;
    @Mock
    IPayment mockPayment;
    @Mock
    User guestuserMock;
    @Mock
    User MemberUserMock;
    Market m;
    @Mock
    ConcurrentHashMap<String, User> membersByUserName;
    @Mock
    ConcurrentHashMap<String, User> connectedSessions;
    @Mock
    ConcurrentHashMap<String, IStore> stores;
    @Mock
    ISecurity security_controller;
    String GuestuserToken;
    String MemberUserToken;
    String memberUserName;
    String memberPassword;
    String baduserName;
    String GuestUserName;
    @BeforeEach
    void Setup(){
        MemberUserToken = "Dummy token";
        GuestuserToken = "Dummy token2";
        mockSupplyer = mock(SupplyingAdapter.class);
        MemberUserMock = mock(User.class);
        membersByUserName = mock(ConcurrentHashMap.class);
        connectedSessions = mock(ConcurrentHashMap.class);
        stores = mock(ConcurrentHashMap.class);
        security_controller = mock(ISecurity.class);
        mockPayment = mock(PaymentAdapter.class);
        guestuserMock = mock(User.class);
        m = new Market(mockPayment,mockSupplyer);
        m.setConnectedSessions(connectedSessions);
        m.setMembersByUserName(membersByUserName);
        m.setStores(stores);
        m.setSecurity_controller(security_controller);
        ConfigMocks();
    }

    private void ConfigMocks(){
        memberUserName = "SlayerFan123";
        GuestUserName = "Guest2934723875";
        memberPassword = "RainingBlood";
        baduserName = "Metallica";
        when(connectedSessions.get(GuestuserToken)).thenReturn(guestuserMock);
        when(connectedSessions.containsKey(GuestuserToken)).thenReturn(true);
        when(connectedSessions.get(MemberUserToken)).thenReturn(MemberUserMock);
        when(connectedSessions.containsKey(MemberUserToken)).thenReturn(true);
        when(guestuserMock.getIsLoggedIn()).thenReturn(false);
        when(guestuserMock.getUserName()).thenReturn(GuestUserName);
        when(guestuserMock.getCart()).thenReturn(new ShoppingCart(guestuserMock));
        when(MemberUserMock.getIsLoggedIn()).thenReturn(true);
        when(MemberUserMock.getUserName()).thenReturn(memberUserName);
        when(MemberUserMock.getCart()).thenReturn(new ShoppingCart(MemberUserMock));
        when(connectedSessions.remove(GuestuserToken)).thenReturn(guestuserMock);
        when(connectedSessions.remove(MemberUserToken)).thenReturn(MemberUserMock);
        when(membersByUserName.get(memberUserName)).thenReturn(MemberUserMock);
        when(membersByUserName.containsKey(memberUserName)).thenReturn(true);
        when(membersByUserName.get(GuestUserName)).thenReturn(null);
        when(membersByUserName.containsKey(GuestUserName)).thenReturn(false);
        when(security_controller.isValidPassword(any(String.class), any(String.class))).thenReturn(true);
        when(security_controller.hashPassword(memberPassword)).thenReturn(memberPassword);
        when(MemberUserMock.getHashed_password()).thenReturn(memberPassword);
        doThrow(new IllegalArgumentException()).when(MemberUserMock).changeUsername(baduserName);
    }

    @Test
    void connectGuest() {
        int iterations = (int) Math.floor(Math.random() * 100);
        List<String> accum = new LinkedList<>();
        for(int i = 0; i < iterations; i ++) {
            String toadd = m.ConnectGuest();
            if (accum.contains(toadd))
                fail("same token generated twice.");
            else accum.add(toadd);
        }
        verify(connectedSessions, times(iterations)).put(any(String.class), any(User.class));
    }

    @Test
    void disconnectGuest() {
        m.DisconnectGuest(GuestuserToken);
        verify(connectedSessions, times(1)).remove(GuestuserToken);
    }

    @Test
    void registerValidatesPassword() {
        String username = "sdgjhs";
        String password = "dsgsdg";
        m.Register(username, password);
        verify(security_controller, times(1)).isValidPassword(password, username);
    }

    @Test
    void registerRemembersUser() {
        String username = "sdgjhs";
        String password = "dsgsdg";
        m.Register(username, password);
        verify(membersByUserName, times(1)).put(any(String.class), any(User.class));
    }

    @Test
    void cantRegisterSameUserName(){
        String username = "sdgjhs";
        when(membersByUserName.containsKey(username)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, ()->m.Register(username, "doesn't matter"));
    }

    @Test
    void dontRegisterWithBadPassword(){
        String username = "sdgjhs";
        String password = "dsgsdg";
        when(security_controller.isValidPassword(password, username)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, ()-> m.Register(username, password));
        verify(membersByUserName, times(0)).put(any(String.class), any(User.class));
    }

    @Test
    void openStore() {
        assertDoesNotThrow(()->m.openStore(MemberUserToken, "Mystore1"));
        verify(stores, times(1)).put(any(String.class),any(IStore.class));
    }

    @Test
    void GuestCantopenStore() {
        assertThrows(Exception.class,()->m.openStore(GuestuserToken, "Mystore1"));
        verify(stores, times(0)).put(any(String.class),any(IStore.class));
    }

    @Test
    void openStoreSameName() {
        String storeName = "Mystore1";
        when(stores.containsKey(storeName)).thenReturn(true);
        assertThrows(Exception.class,()->m.openStore(GuestuserToken, storeName));
        verify(stores, times(0)).put(any(String.class),any(IStore.class));
    }

    @Test
    void logout() {
        assertDoesNotThrow(() -> m.logout(MemberUserToken));
        verify(MemberUserMock, times(1)).logout();
    }

    @Test
    void GuestCantlogout() {
        assertThrows(Exception.class,() -> m.logout(GuestuserToken));
    }

    @Test
    void changePassword() {
        String newpass = "bla";
        String hashed = "Blabla";
        when(security_controller.hashPassword(newpass)).thenReturn(hashed);
        assertDoesNotThrow(() -> m.changePassword(MemberUserToken, memberPassword, newpass));
        verify(MemberUserMock, times(1)).changePassword(hashed);
    }

    @Test
    void changePasswordBadOldPassword() {
        assertThrows(Exception.class,() -> m.changePassword(MemberUserToken, "incorrect", "New password"));
        verify(MemberUserMock, times(0)).changePassword(any(String.class));
    }

    @Test
    void BadNewPassword() {
        String password = "bla";
        when(security_controller.isValidPassword(password, memberUserName)).thenReturn(false);
        assertThrows(Exception.class,() -> m.changePassword(MemberUserToken, memberPassword, password));
        verify(MemberUserMock, times(0)).changePassword(any(String.class));
    }
    @Test
    void GuestCantchangePassword() {
        assertThrows(Exception.class,() -> m.changePassword(GuestuserToken, "doesnt", "matter"));
    }

    @Test
    void changeUsername() {
        String newuserName = "Megadeth";
        assertDoesNotThrow(() -> m.changeUsername(MemberUserToken,newuserName));
        verify(MemberUserMock, times(1)).changeUsername(newuserName);
        verify(membersByUserName, times(1)).remove(memberUserName);
        verify(membersByUserName, times(1)).put(newuserName, MemberUserMock);
    }

    @Test
    void changeUsernameBadUserName() {
        assertThrows(IllegalArgumentException.class,() -> m.changeUsername(MemberUserToken, baduserName));
        verify(membersByUserName, times(0)).remove(memberUserName);
        verify(membersByUserName, times(0)).put(baduserName, MemberUserMock);
    }

    @Test
    void GuestCantChangeUserName() {
        assertThrows(IllegalArgumentException.class,() -> m.changeUsername(GuestuserToken, memberUserName));
        verify(MemberUserMock, times(0)).changeUsername(any(String.class));
        verify(membersByUserName, times(0)).remove(any(String.class));
        verify(membersByUserName, times(0)).put(any(String.class), any(User.class));
    }
}