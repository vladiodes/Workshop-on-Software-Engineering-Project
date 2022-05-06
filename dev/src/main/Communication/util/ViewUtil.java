package main
        .Communication.util;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.HashMap;
import java.util.Map;

import static main.Communication.util.RequestUtil.getSessionCurrentUser;
import static main.Communication.util.RequestUtil.getSessionLocale;


public class ViewUtil {

    public static Map<String, Object> baseModel(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("currentUser", getSessionCurrentUser(ctx));
        if(ctx.sessionAttribute("isAdmin")!=null){
            model.put("isAdmin",ctx.sessionAttribute("isAdmin"));
        }
        if(model.get("currentUser")==null)
            model.put("isLoggedOut",true);
        else
            model.put("isLoggedIn",true);
        return model;
    }

    public static Handler serveHomePage = ctx -> {
        ctx.render(Path.Template.MAIN, baseModel(ctx));
    };

}
