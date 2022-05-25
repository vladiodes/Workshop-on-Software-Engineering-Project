package main.Communication.Controllers;

import io.javalin.http.Handler;
import main.Communication.util.ViewUtil;
import main.Service.IService;
import main.utils.Response;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterController {
    private IService service;
    private AtomicBoolean firstStartup;
    public RegisterController(IService service){
        this.service=service;
        firstStartup=new AtomicBoolean(true);
    }

    public Handler serveRegisterPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render("/velocity/register/register.vm", model);
    };

    public Handler handleRegisterPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.register(ctx.formParam("username"),ctx.formParam("password"));
        if(response.isError_occured()) {
            model.put("fail", true);
            model.put("response", response.getError_message());
        }
        else {
            model.put("response", "Successfully registered!");
            model.put("success",true);
        }
        ctx.render("/velocity/register/register.vm",model);
    };

    public Handler handleSystemConnect = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        if(firstStartup.get()){
            model.put("response","Enter admin details for first boot of system!");
            ctx.render("/velocity/boot.vm",model);
        }
        else {
            Response<String> response = service.guestConnect();
            model.put("userToken", response.getResult());
            ctx.sessionAttribute("userToken", response.getResult());
            ctx.render("/velocity/main.vm", model);
        }
    };

    public Handler verifyAdmin = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response=service.verifyAdminDetails(ctx.formParam("username"),ctx.formParam("password"));
        if(response.getResult()){
            firstStartup.set(false);
            Response<String> resp = service.guestConnect();
            model.put("userToken", resp.getResult());
            ctx.sessionAttribute("userToken", resp.getResult());
            ctx.render("/velocity/main.vm", model);
        }
        else {
            model.put("response","Wrong admin details!");
            ctx.render("/velocity/boot.vm",model);
        }
    };
}
