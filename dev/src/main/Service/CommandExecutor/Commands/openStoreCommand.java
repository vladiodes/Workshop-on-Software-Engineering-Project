package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

public class openStoreCommand extends Command<Boolean>{
    private String name;
    private String storeName;

    public openStoreCommand() {
    }

    public openStoreCommand(UserTokens ut, String Token, String storeName) {
        this.name = ut.getName(Token);
        this.storeName = storeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public Response<Boolean> execute() {
        return this.service.openStore(this.userTokens.getToken(name), storeName);
    }
}
