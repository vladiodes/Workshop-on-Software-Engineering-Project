package main.Publisher;

import java.util.LinkedList;

public interface Observer {
    void setWebSocket(WebSocket webSocket);
    void update(Notification newNotification);
    void update();

    LinkedList<Notification>  getAllNotifications();
}
