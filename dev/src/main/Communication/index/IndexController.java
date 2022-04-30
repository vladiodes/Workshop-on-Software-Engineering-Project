package main.Communication.index;

import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;

import java.util.Map;

import static main.Communication.Main.bookDao;
import static main.Communication.Main.userDao;

public class IndexController {
    public static Handler serveIndexPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("users", userDao.getAllUserNames());
        model.put("book", bookDao.getRandomBook());
        ctx.render(Path.Template.INDEX, model);
    };
}
