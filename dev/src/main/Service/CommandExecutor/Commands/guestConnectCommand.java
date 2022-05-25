package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.Service.IService;
import main.Users.User;
import main.utils.Response;

public class guestConnectCommand extends Command<String>{
    private String name;

    public guestConnectCommand(){}

    public guestConnectCommand(UserTokens ut, String token){
        name = ut.addNameToken(token);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Response<String> execute() {
        Response<String> r = this.service.guestConnect();
        if(!r.isError_occured())
            this.userTokens.addNameToken(this.name, r.getResult());
        return r;
    }
}
