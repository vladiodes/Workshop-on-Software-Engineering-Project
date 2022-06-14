package main.Publisher;

public interface Observable {
    void registerObserver(Observer observer);
    boolean notifyObserver(Notification notification);
    void notifyObserver();

}
