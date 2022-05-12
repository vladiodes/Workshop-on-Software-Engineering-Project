package main.Publisher;

import io.javalin.websocket.WsContext;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Publisher implements Observer {
    private ConcurrentLinkedQueue<Notification> allNotifications;
    private ConcurrentLinkedQueue<Notification> notPublishedYetNotifications;
    private WsContext webSocket=null;

    public Publisher(){
        allNotifications=new ConcurrentLinkedQueue<>();
        notPublishedYetNotifications=new ConcurrentLinkedQueue<>();
    }

    @Override
    public void setWebSocket(WsContext webSocket) {
        this.webSocket = webSocket;
        publishNotifications();
    }

    @Override
    public void update(Notification newNotification){
        notPublishedYetNotifications.add(newNotification);
        allNotifications.add(newNotification);
        publishNotifications();
    }

    @Override
    public void update(){
        publishNotifications();
    }

    @Override
    public LinkedList<Notification> getAllNotifications() {
        return new LinkedList<>(allNotifications);
    }

    private void publishNotifications() {
        if(webSocket!=null){
            while (!notPublishedYetNotifications.isEmpty()){
                Notification notification = notPublishedYetNotifications.poll();
                webSocket.send(notification.print());
            }
        }
    }
}
