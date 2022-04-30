package main.Communication.Controllers;

import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;
import main.DTO.UserDTO;
import main.Service.IService;
import main.utils.Response;

import java.util.Map;


public class LoginController {
    private IService service;
    public LoginController(IService service){
        this.service=service;
    }

    public Handler serveLoginPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.LOGIN, model);
    };

    public Handler serveLogoutPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render("velocity/login/logout.vm", model);
    };

    public Handler handleLoginPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<UserDTO> response = service.login(ctx.formParam("userToken"),ctx.formParam("username"),ctx.formParam("password"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully logged in, Welcome %s!",response.getResult().getUserName()));
            ctx.sessionAttribute("currentUser",ctx.formParam("username"));
            model.put("currentUser",ctx.formParam("username"));
            model.put("isLoggedIn",true);
            model.remove("isLoggedOut");
        }
        ctx.render(Path.Template.LOGIN,model);
    };

    public Handler handleLogoutPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.logout(ctx.formParam("userToken"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else{
            model.put("success",true);
            model.put("response","Successfully logged out");
            ctx.sessionAttribute("currentUser", null);
            ctx.sessionAttribute("loggedOut", "true");
            model.put("isLoggedOut",true);
            model.remove("isLoggedIn");
        }
        ctx.render(Path.Template.LOGOUT,model);
    };

    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public Handler ensureLoginBeforeViewingBooks = ctx -> {
        if (!ctx.path().startsWith("/books")) {
            return;
        }
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.sessionAttribute("loginRedirect", ctx.path());
            ctx.redirect(Path.Web.LOGIN);
        }
    };

}
