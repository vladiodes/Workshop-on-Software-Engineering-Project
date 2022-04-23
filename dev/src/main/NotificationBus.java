package main;

import main.Logger.Logger;
import main.Stores.Store;
import main.Users.User;
import main.utils.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationBus {
    private ConcurrentHashMap<User, ConcurrentLinkedQueue<String>> usersMessagesMap;
    private ConcurrentHashMap<Store, ConcurrentLinkedQueue<Pair<String,String>>> storesMessagesMap; //(username , message)

    public NotificationBus(){
        usersMessagesMap=new ConcurrentHashMap<>();
        storesMessagesMap = new ConcurrentHashMap<>();
    }

    public void registerUser(User user){
        usersMessagesMap.putIfAbsent(user,new ConcurrentLinkedQueue<>());
    }


    public void registerStore(Store store, String username){
        storesMessagesMap.putIfAbsent(store,new ConcurrentLinkedQueue<>());
    }

    public void addMessage(User toUser,String msg){
        if(!usersMessagesMap.containsKey(toUser)){
            Logger.getInstance().logBug("Notification Bus","A user is not registered to the notification bus, this shouldn't happen");
            return;
        }
        usersMessagesMap.get(toUser).add(msg);

        // in the future - code for sending notifications to user only if he's logged in should be here!
    }

    public void addMessage(Store store, String username, String msg)
    {
        if(!storesMessagesMap.containsKey(store))
        {
            return;
        }
        storesMessagesMap.get(store).add(new Pair<>(username, msg));
    }

    /**
     * This function returns all messages that were gathered in a user's queue up until now
     * @param requestingUser the user that requested to get messages
     * @return returns a list of all the messages that were gathered so far
     */
    public List<String> getMessagesFromUserRequest(User requestingUser){
        LinkedList<String> msgList=new LinkedList<>();
        ConcurrentLinkedQueue<String> msgQueue=usersMessagesMap.get(requestingUser);
        while (!msgQueue.isEmpty())
            msgList.add(msgQueue.remove());

        return msgList;
    }


    public ConcurrentHashMap<User, ConcurrentLinkedQueue<String>> getUsersMessagesMap() {
        return usersMessagesMap;
    }
  
    /**
     * This function returns all messages that were gathered in a stores's queue up until now
     * @param store the store that requested to get messages
     * @return returns a list of all the messages that were gathered so far
     */
    public List<Pair<String,String>> getMessagesFromUserRequest(Store store){
        LinkedList<Pair<String, String>> msgList=new LinkedList<>();
        ConcurrentLinkedQueue<Pair<String, String>> msgQueue=storesMessagesMap.get(store);
        while (!msgQueue.isEmpty())
            msgList.add(msgQueue.remove());

        return msgList;

    }
}
