package test.UnitTests;


import main.Publisher.Observer;
import main.Publisher.Publisher;

import main.Users.User;

import main.utils.SystemStats;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;


import java.time.LocalDate;

import static org.mockito.Mockito.*;

class SystemStatsTest {

    SystemStats stats;
    @Mock User u1;
    @Mock User u2;
    @Mock Observer observer1;
    @Mock Observer observer2;

    @BeforeEach
    void setup(){
        stats=new SystemStats(LocalDate.now());
        u1 = mock(User.class);
        u2 = mock(User.class);
        observer1 = mock(Publisher.class);
        observer2 = mock(Publisher.class);
        when(u1.getObserver()).thenReturn(observer1);
        when(u2.getObserver()).thenReturn(observer2);
        stats.registerObserver(u1);
        stats.registerObserver(u2);
    }

    @Test
    void testAddLogin(){
        stats.addLogIn();
        verify(observer1,times(1)).update(any());
        verify(observer2,times(1)).update(any());
        Assertions.assertEquals(stats.getNumOfLoggedIn(),1);
    }

    @Test
    void testAddRegister(){
        stats.addRegister();
        verify(observer1,times(1)).update(any());
        verify(observer2,times(1)).update(any());
        Assertions.assertEquals(stats.getNumOfRegistered(),1);
    }

    @Test
    void testAddPurchase(){
        stats.addPurchase();
        verify(observer1,times(1)).update(any());
        verify(observer2,times(1)).update(any());
        Assertions.assertEquals(stats.getNumOfPurchases(),1);
    }

    @Test
    void addGuestVisitor(){
        stats.addGuestVisitor();
        verify(observer1,times(1)).update(any());
        verify(observer2,times(1)).update(any());
        Assertions.assertEquals(stats.getGuestsVisitors(),1);
    }

    @Test
    void addNonStaffVisitor(){
        stats.addNonStaffVisitor();
        verify(observer1,times(1)).update(any());
        verify(observer2,times(1)).update(any());
        Assertions.assertEquals(stats.getNonStaffVisitors(),1);
    }

    @Test
    void addManagerVisitor(){
        stats.addManagerVisitor();
        verify(observer1,times(1)).update(any());
        verify(observer2,times(1)).update(any());
        Assertions.assertEquals(stats.getManagersVisitors(),1);
    }

    @Test
    void addOwnerVisitor(){
        stats.addOwnerVisitor();
        verify(observer1,times(1)).update(any());
        verify(observer2,times(1)).update(any());
        Assertions.assertEquals(stats.getOwnerVisitors(),1);
    }

    @Test
    void addAdminVisitor(){
        stats.addAdminVisitor();
        verify(observer1,times(1)).update(any());
        verify(observer2,times(1)).update(any());
        Assertions.assertEquals(stats.getAdminVisitors(),1);
    }

    @Test
    void concurrentAddGuest() throws InterruptedException {
        int numOfThreads=100;
        Thread[] threads = new Thread[numOfThreads];
        for(int i=0;i<numOfThreads;i++){
           threads[i]=new Thread(() -> stats.addGuestVisitor());
        }
        for(Thread thread:threads){
            thread.start();
        }
        for(Thread thread:threads){
            thread.join();
        }

        verify(observer1,times(numOfThreads)).update(any());
        verify(observer2,times(numOfThreads)).update(any());
        Assertions.assertEquals(stats.getGuestsVisitors(),numOfThreads);

    }

}