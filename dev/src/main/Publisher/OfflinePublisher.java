package main.Publisher;

public class OfflinePublisher implements Observer {
    @Override
    public boolean update(Notification newNotification) {
        return false;
    }
}
