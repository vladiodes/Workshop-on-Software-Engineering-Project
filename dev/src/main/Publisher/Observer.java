package main.Publisher;

import io.javalin.websocket.WsContext;
import org.mockito.internal.matchers.Not;

import java.util.LinkedList;

public interface Observer {
    void setWebSocket(WsContext webSocket);
    void update(Notification newNotification);
    void update();

    LinkedList<Notification>  getAllNotifications();
}
