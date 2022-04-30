package main.Communication;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.staticfiles.Location;
import main.Communication.book.BookController;
import main.Communication.book.BookDao;
import main.Communication.index.IndexController;
import main.Communication.login.LoginController;
import main.Communication.user.UserDao;
import main.Communication.util.Filters;
import main.Communication.util.HerokuUtil;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;

import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;


public class Main {

    // Declare dependencies
    public static BookDao bookDao;
    public static UserDao userDao;

    public static void main(String[] args) {

        // Instantiate your dependencies
        bookDao = new BookDao();
        userDao = new UserDao();

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public", Location.CLASSPATH);
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
        }).start(HerokuUtil.getHerokuAssignedPort());

        app.routes(() -> {
            before(Filters.handleLocaleChange);
            before(LoginController.ensureLoginBeforeViewingBooks);
            get(Path.Web.INDEX, IndexController.serveIndexPage);
            get(Path.Web.BOOKS, BookController.fetchAllBooks);
            //get(Path.Web.ONE_BOOK, BookController.fetchOneBook);
            get(Path.Web.LOGIN, LoginController.serveLoginPage);
            post(Path.Web.LOGIN, LoginController.handleLoginPost);
            post(Path.Web.LOGOUT, LoginController.handleLogoutPost);
        });

        app.error(404, ViewUtil.notFound);
    }

}
