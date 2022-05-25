package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.Service.IService;
import main.utils.Response;

public abstract class Command<T> {
    protected IService service;
    protected UserTokens userTokens;

    public Command() {
    }

    public void setService(IService service) {
        this.service = service;
    }

    public void setUserTokens(UserTokens userTokens) {
        this.userTokens = userTokens;
    }

    public abstract Response<T> execute();

}
