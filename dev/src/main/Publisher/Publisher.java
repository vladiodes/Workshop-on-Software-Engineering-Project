package main.Publisher;

import main.Users.User;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Publisher implements Observer {
    private ConcurrentLinkedQueue<Notification> allNotifications;
    private ConcurrentLinkedQueue<Notification> notPublishedYetNotifications;
    private WebSocket webSocket = null;

    private User observableUser;

    public Publisher(User observableUser) {
        allNotifications = new ConcurrentLinkedQueue<>();
        notPublishedYetNotifications = new ConcurrentLinkedQueue<>();
        this.observableUser=observableUser;
    }

    @Override
    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
        publishNotifications();
    }

    @Override
    public void update(Notification newNotification) {
        notPublishedYetNotifications.add(newNotification);
        allNotifications.add(newNotification);
        if(observableUser.getIsLoggedIn())
            publishNotifications();
    }

    @Override
    public void update() {
        publishNotifications();
    }

    @Override
    public LinkedList<Notification> getAllNotifications() {
        return new LinkedList<>(allNotifications);
    }

    private void publishNotifications() {
        if (webSocket != null) {
            while (!notPublishedYetNotifications.isEmpty()) {
                Notification notification = notPublishedYetNotifications.poll();
                webSocket.send(notification);
            }
        }
    }
}
