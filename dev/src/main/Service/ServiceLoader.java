package main.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Service.CommandExecutor.Invoker;
import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ServiceLoader {

    public static IService loadFromFile(String path, IService service ) throws Exception {
        System.out.println("Starting script:");
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
        System.out.println("Done script.");
        return service;
    }

    public static void createRecording(String path, Function<IService, Void> scenario) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Service service = new Service("TestingConfig.json");
        service.setLogCommandsFlag(true);
        service.setLogFileName(path);
        scenario.apply(service);
        service.SaveRecording();
    }

//    you can use this main to create more startup files.
    public static void main(String[] args) throws Exception {
        createRecording("Bar_final_meeting_startup.json", new Function<IService, Void>() {
            @Override
            public Void apply(IService service) {
                String username = "u";
                String password = "123456";
                String tokens[] = new String[5];
                for(int i = 1; i < 5; i ++){
                    service.register(username + i, password);
                    tokens[i] = service.guestConnect().getResult();
                    service.login(tokens[i], username + i, password);
                }
                service.openStore(tokens[2], "s");
                service.addProductToStore(tokens[2], "Bamba", "Snack", new ArrayList<>(), "Peanut snack", "s", 20, 30);
                service.appointStoreManager(tokens[2], "u3", "s");
                service.allowManagerUpdateProducts(tokens[2], "u3", "s");
                for(int i = 1; i < 5; i ++){
                    service.logout(tokens[i]);
                }
                return null;
            }
        });
//        ObjectMapper objectMapper = new ObjectMapper();
//        Configuration c = new Configuration();
//        c.setPersistence_unit("Market");
//        c.setShouldPersist(true);
//        c.setAdminUsername("admin");
//        c.setAdminPassword("admin");
//        c.setPaymentSystem(PaymentAdapter.class);
//        c.setSupplyingSystem(SupplyingAdapter.class);
//        objectMapper.writeValue(new File("RealConfig.json"), c);
    }

}