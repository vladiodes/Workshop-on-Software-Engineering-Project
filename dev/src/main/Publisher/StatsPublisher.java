package main.Publisher;

public class StatsPublisher implements Observer{
    private WebSocket webSocket;


    public StatsPublisher(WebSocket webSocket){
        this.webSocket=webSocket;
    }

    @Override
    public boolean update(Notification newNotification) {
        return publishNotifications(newNotification);
    }

    private boolean publishNotifications(Notification notification) {
        if (webSocket != null) {
            webSocket.send(notification);
            return true;
        }
        return false;
    }
}
