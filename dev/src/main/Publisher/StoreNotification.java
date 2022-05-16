package main.Publisher;

public class StoreNotification implements Notification {
    String storeName;
    String content;

    public StoreNotification(String storeName,String content){
        this.storeName=storeName;
        this.content=content;
    }
    @Override
    public String print() {
        return String.format("New notification regarding the store: %s Content:%s",storeName,content);
    }
}
