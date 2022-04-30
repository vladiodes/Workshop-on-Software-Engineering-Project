package main.Communication.Controllers;

import io.javalin.http.Handler;
import main.Communication.util.ViewUtil;
import main.Service.IService;
import main.utils.Response;

import java.util.Map;

public class RegisterController {
    private IService service;
    public RegisterController(IService service){
        this.service=service;
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
        Response<String> response=service.guestConnect();
        model.put("userToken",response.getResult());
        ctx.sessionAttribute("userToken",response.getResult());
        ctx.render("/velocity/notFound.vm",model);
    };
}
