package main.Publisher;

import javax.persistence.Entity;

@Entity
public class PersonalNotification extends Notification {
    private String sendingEntity;
    private String content;

    public PersonalNotification(String sendingEntity, String content){
        this.sendingEntity=sendingEntity;
        this.content=content;
    }

    public PersonalNotification() {

    }

    @Override
    public String print() {
        return String.format("A new notification from:%s, Content:%s",sendingEntity,content);
    }


    public String getSendingEntity() {
        return sendingEntity;
    }
}
