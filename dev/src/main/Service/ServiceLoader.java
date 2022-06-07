package main.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Service.CommandExecutor.Invoker;
import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

import java.nio.file.Paths;
import java.util.Arrays;
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

    //you can use this main to create more startup files.
//    public static void main(String[] args) throws Exception {
//        createRecording("store_with_manager.json", new Function<IService, Void>() {
//            @Override
//            public Void apply(IService service) {
//                Response<String> token = service.guestConnect();
//                service.register("Founder","123456");
//                service.login(token.getResult(), "Founder", "123456");
//                service.openStore(token.getResult(), "Samsung");
//                service.register("Manager", "123456");
//                service.appointStoreManager(token.getResult(), "Manager", "Samsung");
//                service.addProductToStore(token.getResult(), "S7", "Phone", Arrays.asList("TouchScreen", "Cool"), "Very good korean phone.", "Samsung", 10, 2700);
//                service.logout(token.getResult());
//                service.guestDisconnect(token.getResult());
//                return null;
//            }
//        });
//    }
}