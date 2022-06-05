package main;

import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Persistence.DAO;
import main.Service.IService;
import main.Service.Service;
import main.Users.User;
import main.utils.Response;

import java.util.List;


public class DB_Tests {
    public static void main(String[] args){
//        IService service=new Service(new PaymentAdapter(),new SupplyingAdapter());
//        Response<String> user1Token=service.guestConnect();
//        Response<String> managerToken=service.guestConnect();
//        service.register("user1","123456");
//        service.register("manager","123456");
//        service.login(user1Token.getResult(),"user1","123456");
//        service.login(managerToken.getResult(),"manager","123456");
//
//        service.openStore(user1Token.getResult(),"store1");
//        service.appointStoreManager(user1Token.getResult(),"manager","store1");
//
//        service.addProductToStore(user1Token.getResult(),"product1","category",null,"desc","store1",100,200);
//
//        service.addProductToCart(managerToken.getResult(),"store1","product1",10);
//
//        System.out.println("ok");
        readFromDB();
    }

    public static void readFromDB(){
        List<User> users= DAO.getInstance().getUsers();
        System.out.println("ok");

    }
}
