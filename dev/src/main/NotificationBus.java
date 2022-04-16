package main;

import main.Logger.Logger;
import main.Users.User;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationBus {
    private ConcurrentHashMap<User, ConcurrentLinkedQueue<String>> usersMessagesMap;

    public NotificationBus(){
        usersMessagesMap=new ConcurrentHashMap<>();
    }

    public void register(User user){
        usersMessagesMap.putIfAbsent(user,new ConcurrentLinkedQueue<>());
    }

    public void addMessage(User toUser,String msg){
        if(usersMessagesMap.containsKey(toUser)){
            Logger.getInstance().logBug("Notification Bus","A user is not registered to the notification bus, this shouldn't happen");
            return;
        }
        usersMessagesMap.get(toUser).add(msg);

        // in the future - code for sending notifications to user only if he's logged in should be here!
    }

    public List<String> getMessagesFromUserRequest(User requestingUser){
        LinkedList<String> msgList=new LinkedList<>();
        ConcurrentLinkedQueue<String> msgQueue=usersMessagesMap.get(requestingUser);
        while (!msgQueue.isEmpty())
            msgList.add(msgQueue.remove());

        return msgList;
    }
}
