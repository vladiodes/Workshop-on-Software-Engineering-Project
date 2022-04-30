package main.Communication.book;


import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;

import java.util.Map;

import static main.Communication.Main.bookDao;
import static main.Communication.util.RequestUtil.getParamIsbn;


public class BookController {

    public static Handler fetchAllBooks = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("books", bookDao.getAllBooks());
        ctx.render(Path.Template.BOOKS_ALL, model);
    };

    public static Handler fetchOneBook = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("book", bookDao.getBookByIsbn(getParamIsbn(ctx)));
        ctx.render(Path.Template.BOOKS_ONE, model);
    };
}
