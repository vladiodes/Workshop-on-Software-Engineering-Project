package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

public class removeStoreOwnerAppointmentCommand extends Command<Boolean>{
    private String name;
    private String userAppointed;
    private String storeName;

    public removeStoreOwnerAppointmentCommand(UserTokens ut, String Token, String userAppointed, String storeName) {
        this.name = ut.getName(Token);
        this.userAppointed = userAppointed;
        this.storeName = storeName;
    }

    public removeStoreOwnerAppointmentCommand() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAppointed() {
        return userAppointed;
    }

    public void setUserAppointed(String userAppointed) {
        this.userAppointed = userAppointed;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public Response<Boolean> execute() {
        return this.service.removeStoreOwnerAppointment(this.userTokens.getToken(this.name), this.userAppointed, this.storeName);
    }
}
