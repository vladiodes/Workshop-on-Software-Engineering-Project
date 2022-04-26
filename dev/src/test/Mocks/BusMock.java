package test.Mocks;

import main.Logger.Logger;
import main.NotificationBus;
import main.Stores.IStore;
import main.Users.User;
import main.utils.Pair;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BusMock extends NotificationBus {
    private ConcurrentHashMap<User, ConcurrentLinkedQueue<String>> usersMessagesMap;
    private ConcurrentHashMap<IStore, ConcurrentLinkedQueue<Pair<String,String>>> storesMessagesMap;

    public BusMock(){
        usersMessagesMap=new ConcurrentHashMap<>();
        storesMessagesMap = new ConcurrentHashMap<>();
    }

    @Override
    public void register(User user){

    }

    @Override
    public void register(IStore store){
    }

    @Override
    public void addMessage(User toUser,String msg){
    }

    @Override
    public void addMessage(IStore IStore, String username, String msg)
    {
    }

    @Override
    public List<String> getMessagesFromUserRequest(User requestingUser){
        return null;
    }


    public ConcurrentHashMap<User, ConcurrentLinkedQueue<String>> getUsersMessagesMap() {
        return null;
    }

    /**
     * This function returns all messages that were gathered in a store's queue up until now
     * @param IStore the store that requested to get messages
     * @return returns a list of all the messages that were gathered so far
     */
    public List<Pair<String,String>> getStoreMessages(IStore IStore){
        return null;
    }
}
