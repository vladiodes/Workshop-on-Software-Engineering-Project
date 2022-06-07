package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

public class AllowManagerUpdateProductsCommand extends Command<Boolean>{
    private String name;
    private String managerName;
    private String storeName;

    public AllowManagerUpdateProductsCommand() {
    }

    public AllowManagerUpdateProductsCommand(UserTokens ut, String token, String managerName, String storeName) {
        this.name = ut.getName(token);
        this.managerName = managerName;
        this.storeName = storeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public Response<Boolean> execute() {
        return this.service.allowManagerUpdateProducts(this.userTokens.getToken(this.name), this.managerName, this.storeName);
    }
}
