package main.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Service.CommandExecutor.Invoker;
import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ServiceLoader {

    public static IService loadFromFile(String path, IService service ) throws Exception {
        UserTokens ut = new UserTokens();
        ObjectMapper objectMapper = new ObjectMapper();
        Invoker<?>[] ins = objectMapper.readValue(Paths.get(path).toFile(), Invoker[].class);
        int counter = 0;
        for( Invoker<?> inv : ins){
            Response<?> res= inv.Invoke(service, ut);
            if(res.isError_occured())
                throw new IllegalArgumentException(String.format("Illegal commands. failed at command %d in file %s", counter, path));
            counter ++;
        }
        return service;
    }

    public static void createRecording(String path, Function<IService, Void> scenario){
        ObjectMapper objectMapper = new ObjectMapper();
        Service service = new Service(new PaymentAdapter(), new SupplyingAdapter(), true,false);
        service.setLogFileName(path);
        scenario.apply(service);
        service.SaveRecording();
    }

//    you can use this main to create more startup files.
//    public static void main(String[] args) throws Exception {
//        createRecording("Tal3.json", new Function<IService, Void>() {
//            @Override
//            public Void apply(IService service) {
//                Response<String> admintoken = service.guestConnect();
//                service.register("u1", "123456");
//                service.login(admintoken.getResult(), "admin", "admin");
//                service.deleteUser(admintoken.getResult(), "u1");
//                service.logout(admintoken.getResult());
//                return null;
//            }
//        });
//    }
}