package main.Service.CommandExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.Service.CommandExecutor.Commands.Command;
import main.Service.CommandExecutor.utils.UserTokens;
import main.Service.IService;
import main.utils.Response;
import org.apache.velocity.app.event.implement.EscapeXmlReference;

public class Invoker<T> {
    public Class<? extends Command<T>> command;
    public String params;  //JSON

    public Invoker(){

    }

    public Invoker(Class<? extends Command<T>> command, String params) {
        this.command = command;
        this.params = params;
    }

    public Response<T> Invoke(IService service, UserTokens userTokens) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Command<T> command1 = mapper.readValue(params, command);
        command1.setService(service);
        command1.setUserTokens(userTokens);
        return command1.execute();
    }

    public  Class<? extends Command<T>> getCommand() {
        return command;
    }

    public void setCommand( Class<? extends Command<T>> command) {
        this.command = command;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
