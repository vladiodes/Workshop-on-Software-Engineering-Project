package test.UnitTests;

import main.NotificationBus;
import main.Users.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationBusTest {
    private NotificationBus bus;
    private User user1;

    @BeforeEach
    void setUp(){
        bus = new NotificationBus();
        user1=new User(false,"user1","password");
    }

    @Test
    void testRegisterUserToBus(){
        bus.register(user1);
        assertTrue(bus.getUsersMessagesMap().containsKey(user1));
    }

    @Test
    void testAddMessage(){
        bus.register(user1);
        String msg = "This is a message";
        bus.addMessage(user1,msg);
        assertEquals(1, bus.getUsersMessagesMap().get(user1).size());
        assertTrue(bus.getUsersMessagesMap().get(user1).contains(msg));
    }

    @Test
    void testGetMessagesFromUserRequest(){
        bus.register(user1);
        String msg = "This is a message";
        bus.addMessage(user1, msg);
        List<String> msgs = bus.getMessagesFromUserRequest(user1);
        assertEquals(1, msgs.size());
        assertTrue(msgs.contains(msg));
        List<String> new_msgs=bus.getMessagesFromUserRequest(user1);
        assertEquals(0,new_msgs.size());
    }

}