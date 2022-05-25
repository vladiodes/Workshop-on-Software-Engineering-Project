package main.Service.CommandExecutor.Commands;

import main.DTO.UserDTO;
import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

public class loginCommand extends Command<UserDTO> {



    public loginCommand(UserTokens ut, String token, String userName, String password) {
        this.tokenName = ut.getName(token);
        this.userName = userName;
        this.password = password;
    }

    public loginCommand() {
    }

    private String tokenName;
    private String userName;
    private String password;

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Response<UserDTO> execute() {
        return this.service.login(this.userTokens.getToken(tokenName), userName, password);
    }
}
