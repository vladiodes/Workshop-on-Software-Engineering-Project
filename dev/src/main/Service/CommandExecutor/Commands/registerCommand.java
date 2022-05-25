package main.Service.CommandExecutor.Commands;

import main.utils.Response;

public class registerCommand extends Command<Boolean>{
    private String userName;
    private String password;

    public registerCommand() {
    }

    public registerCommand(String userName, String password) {
        this.userName = userName;
        this.password = password;
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
    public Response<Boolean> execute() {
        return this.service.register(userName, password);
    }
}
