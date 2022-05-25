package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

public class appointStoreManagerCommand extends Command<Boolean>{
    private String name;
    private String userToAppoint;
    private String storeName;

    public appointStoreManagerCommand(UserTokens ut, String Token, String userToAppoint, String storeName) {
        this.name = ut.getName(Token);
        this.userToAppoint = userToAppoint;
        this.storeName = storeName;
    }

    public appointStoreManagerCommand() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserToAppoint() {
        return userToAppoint;
    }

    public void setUserToAppoint(String userToAppoint) {
        this.userToAppoint = userToAppoint;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public Response<Boolean> execute() {
        return service.appointStoreManager(this.userTokens.getToken(name), userToAppoint, storeName);
    }
}
