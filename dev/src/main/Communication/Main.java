package main.Communication;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.staticfiles.Location;
import main.Communication.Controllers.RegisterController;
import main.Communication.Controllers.StoreController;
import main.Communication.Controllers.LoginController;
import main.Communication.util.HerokuUtil;
import main.Communication.util.ViewUtil;
import main.Service.IService;
import main.Service.Service;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;


public class Main {
    private static final IService service=new Service();
    public static void main(String[] args) {

        RegisterController registerController=new RegisterController(service);
        LoginController loginController=new LoginController(service);
        StoreController storeController=new StoreController(service);

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public", Location.CLASSPATH);
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
        }).start(HerokuUtil.getHerokuAssignedPort());

        app.routes(() -> {
            get("/home",ViewUtil.serveHomePage);
            get("/register",registerController.serveRegisterPage);
            post("/register",registerController.handleRegisterPost);
            get("/login",loginController.serveLoginPage);
            post("/login",loginController.handleLoginPost);
            get("/logout",loginController.serveLogoutPage);
            post("/logout",loginController.handleLogoutPost);
            get("/openStore", storeController.openStorePage);
            post("/openStore",storeController.handleOpenStorePost);
            get("/manageStoreInventory",storeController.openManageStoreInventoryPage);
            get("/addProductToStore", storeController.openAddProductToStorePage);
            post("/addProductToStore", storeController.handleAddProductToStorePost);
            get("/updateProductInStore", storeController.openUpdateProductInStorePage);
            post("/updateProductInStore", storeController.handleUpdateProductInStorePost);
            //before(Filters.handleLocaleChange);
            //before(LoginController.ensureLoginBeforeViewingBooks);
            //get(Path.Web.INDEX, IndexController.serveIndexPage);
            //get(Path.Web.BOOKS, BookController.fetchAllBooks);
            //get(Path.Web.ONE_BOOK, BookController.fetchOneBook);
            //get(Path.Web.LOGIN, LoginController.serveLoginPage);
            //post(Path.Web.LOGIN, LoginController.handleLoginPost);
            //post(Path.Web.LOGOUT, LoginController.handleLogoutPost);
        });

        app.error(404, registerController.handleSystemConnect);
    }

}
