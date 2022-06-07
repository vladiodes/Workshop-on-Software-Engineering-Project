package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

public class deleteUserCommand extends Command<Boolean>{

    private String name;
    private String userName;

    public deleteUserCommand(UserTokens ut, String Token, String userName) {
        this.name = ut.getName(Token);
        this.userName = userName;
    }

    public deleteUserCommand() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Response<Boolean> execute() {
        return this.service.deleteUser(userTokens.getToken(this.name), this.userName);
    }
}
