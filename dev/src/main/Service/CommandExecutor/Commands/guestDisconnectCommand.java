package main.Service.CommandExecutor.Commands;

import main.DTO.UserDTO;
import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

public class guestDisconnectCommand extends Command<UserDTO>{
    private String name;

    public guestDisconnectCommand(UserTokens ut, String token){
        name = ut.getName(token);
    }
    public guestDisconnectCommand(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Response<UserDTO> execute() {
        return service.guestDisconnect(userTokens.getToken(this.name));
    }
}
