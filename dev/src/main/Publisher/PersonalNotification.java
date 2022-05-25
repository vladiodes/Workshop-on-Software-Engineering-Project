package main.Publisher;

public class PersonalNotification implements Notification {
    private String sendingEntity;
    private String content;

    public PersonalNotification(String sendingEntity, String content){
        this.sendingEntity=sendingEntity;
        this.content=content;
    }

    @Override
    public String print() {
        return String.format("A new notification from:%s, Content:%s",sendingEntity,content);
    }


    public String getSendingEntity() {
        return sendingEntity;
    }
}
