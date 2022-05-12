package main.Publisher;

public interface Observable {
    void registerObserver(Observer observer);
    void notifyObserver(Notification notification);
    void notifyObserver();

}
