package main.Publisher;

import main.Users.User;

public class Publisher implements Observer {
    private WebSocket webSocket ;

    private User observableUser;

    public Publisher(User observableUser,WebSocket webSocket) {
        this.observableUser=observableUser;
        this.webSocket=webSocket;
    }

    @Override
    public boolean update(Notification newNotification) {
        if(observableUser.getIsLoggedIn())
            return publishNotifications(newNotification);
        return false;
    }

    private boolean publishNotifications(Notification notification) {
        if (webSocket != null) {
            webSocket.send(notification);
            return true;
        }
        return false;
    }
}
