package main.Publisher;

import javax.persistence.Entity;

@Entity
public class StoreNotification extends Notification {
    private String storeName;
    private String content;


    public StoreNotification(String storeName,String content){
        this.storeName=storeName;
        this.content=content;
    }

    public StoreNotification() {

    }

    @Override
    public String print() {
        return String.format("New notification regarding the store: %s Content:%s",storeName,content);
    }
}
