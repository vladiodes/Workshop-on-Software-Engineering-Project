package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

public class logoutCommand extends Command<Boolean> {
    private String name;

    public logoutCommand() {
    }

    public logoutCommand(UserTokens ut, String token) {
        this.name = ut.getName(token);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Response<Boolean> execute() {
        return this.service.logout(this.userTokens.getToken(name));
    }
}
