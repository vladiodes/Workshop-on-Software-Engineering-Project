package test.NotificationTests;

import main.Publisher.*;
import main.Users.User;
import main.Users.states.UserStates;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public class PublisherTests {
    Observer publisher;
    Observable observableUser;
    @Mock
    WebSocket mockWebSocket;
    @Mock
    Notification notification;
    @Mock
    Notification notification2;
    @Mock
    UserStates userState;


    @Before
    public void setUp() {
        observableUser=new User(false,"user1","1234");
        publisher=new Publisher((User)observableUser);
        mockWebSocket=mock(WebSocket.class);
        notification=mock(PersonalNotification.class);
        notification2=mock(PersonalNotification.class);
        observableUser.registerObserver(publisher);
        publisher.setWebSocket(mockWebSocket);
        configMocks();
        userState = mock(UserStates.class);
    }

    private void configMocks() {
        when(notification.print()).thenReturn("notification");
        when(notification2.print()).thenReturn("notification");
        when(mockWebSocket.send(any())).thenReturn(true); //doesn't really matter...
    }

    @Test
    public void sendingNotificationWhenLoggedIn(){
        User observableUserUser = ((User)observableUser);
        observableUserUser.setState(userState);
        when(userState.getIsLoggedIn()).thenReturn(true);
        observableUserUser.LogIn(null, null);
        observableUser.notifyObserver(notification);
        verify(mockWebSocket,times(1)).send(notification);
    }

    @Test
    public void notSendingNotificationsWhenNotLoggedIn(){
        observableUser.notifyObserver(notification);
        verify(mockWebSocket,times(0)).send(notification);
    }

    @Test
    public void notSendingNotificationsWhenLoggedOutButSavingThem(){
        int notifications_num=20;
        for(int i=1;i<=notifications_num;i++){
            observableUser.notifyObserver(notification);
        }
        verify(mockWebSocket,times(0)).send(notification);
        Assertions.assertEquals(publisher.getAllNotifications().size(), notifications_num);
    }

    @Test
    public void sendingAllNotSentNotificationsWhenLoggingIn(){
        int notifications_num=20;
        for(int i=1;i<=notifications_num;i++){
            observableUser.notifyObserver(notification);
        }
        User observableUserUser = ((User)observableUser);
        observableUserUser.setState(userState);
        when(userState.getIsLoggedIn()).thenReturn(true);
        observableUserUser.LogIn(null, null);
        verify(mockWebSocket,times(notifications_num)).send(notification);
        Assertions.assertEquals(publisher.getAllNotifications().size(), notifications_num);
    }

    @Test
    public void whenLoggedInSendingAllNotifications(){
        User observableUserUser = ((User)observableUser);
        observableUserUser.setState(userState);
        when(userState.getIsLoggedIn()).thenReturn(true);
        observableUserUser.LogIn(null, null);
        int notifications_num=20;
        for(int i=1;i<=notifications_num;i++){
            observableUser.notifyObserver(notification);
        }
        Assertions.assertEquals(publisher.getAllNotifications().size(), notifications_num);
        verify(mockWebSocket,times(notifications_num)).send(notification);
    }

    @Test
    public void checkingSuspendedNotificationsNotSent(){
        User observableUserUser = ((User)observableUser);
        observableUserUser.setState(userState);
        when(userState.getIsLoggedIn()).thenReturn(true);
        observableUserUser.LogIn(null, null);
        int notifications_received=20,suspended=10;
        for(int i=1;i<=notifications_received;i++){
            observableUser.notifyObserver(notification);
        }
        ((User)observableUser).logout();
        when(userState.getIsLoggedIn()).thenReturn(false);
        for(int i=1;i<=suspended;i++){
            observableUser.notifyObserver(notification2);
        }
        verify(mockWebSocket,times(notifications_received)).send(notification);
        verify(mockWebSocket,times(0)).send(notification2);
    }

    @Test
    public void complexScenarioOfLoggingSequencesReceivingSuspendedNotifications(){
        for(int i=1;i<=100;i++) {
            User observableUserUser = ((User)observableUser);
            observableUserUser.setState(userState);
            when(userState.getIsLoggedIn()).thenReturn(true);
            observableUserUser.LogIn(null, null);
            observableUserUser.logout();
            when(userState.getIsLoggedIn()).thenReturn(false);
        }
        observableUser.notifyObserver(notification);
        for(int i=1;i<=100;i++) {
            User observableUserUser = ((User)observableUser);
            observableUserUser.setState(userState);
            when(userState.getIsLoggedIn()).thenReturn(true);;
            observableUserUser.LogIn(null, null);
            observableUserUser.logout();
            when(userState.getIsLoggedIn()).thenReturn(false);
        }
        verify(mockWebSocket,times(1)).send(notification);
    }

    @Test
    public void notSendingDoubleNotifications(){
        for(int i=1;i<=20;i++){
            observableUser.notifyObserver(notification);
        }
        User observableUserUser = ((User)observableUser);
        observableUserUser.setState(userState);
        when(userState.getIsLoggedIn()).thenReturn(true);
        observableUserUser.LogIn(null, null);
        observableUserUser.logout();
        when(userState.getIsLoggedIn()).thenReturn(false);
        observableUserUser = ((User)observableUser);
        observableUserUser.setState(userState);
        observableUserUser.LogIn(null, null);
        when(userState.getIsLoggedIn()).thenReturn(true);
        verify(mockWebSocket,times(20)).send(notification);
    }

    @Test
    public void sendingLotsOfNotificationsConcurrently() throws InterruptedException {
        int first_thread=100,second_thread=50;
        Thread t1=new Thread(()->{
            for(int i=1;i<=first_thread;i++){
                observableUser.notifyObserver(notification2);
            }
        });
        Thread t2=new Thread(()->{
            for(int i=1;i<=second_thread;i++){
                observableUser.notifyObserver(notification);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        verify(mockWebSocket,times(0)).send(any());
        User observableUserUser = ((User)observableUser);
        observableUserUser.setState(userState);
        observableUserUser.LogIn(null, null);
        when(userState.getIsLoggedIn()).thenReturn(true);
        verify(mockWebSocket,times(first_thread+second_thread)).send(any());
        Assertions.assertEquals(publisher.getAllNotifications().size(), first_thread + second_thread);
    }
}
