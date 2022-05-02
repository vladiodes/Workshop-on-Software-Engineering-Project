package main.Communication;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.staticfiles.Location;
import main.Communication.Controllers.*;
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
        ProductController productController=new ProductController(service);
        CartController cartController=new CartController(service);

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public", Location.CLASSPATH);
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
        }).start(HerokuUtil.getHerokuAssignedPort());

        app.ws("/notifications", ws -> {
            ws.onMessage(ctx -> {
                Thread t = new Thread(()->{
                    while (true){
                        ctx.send("This is a real time notification using web-sockets, very very cool!");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                t.start();
            });
        });

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
            get("/productSearch",productController.openSearchProductPage);
            post("/productSearch",productController.handleSearchProductPost);
            post("/addToCart",cartController.handleAddToCartPost);
            get("/cart",cartController.openCartPage);
            get("/purchaseCart",cartController.openPurchaseCartPage);
            post("/purchaseCart", cartController.handlePurchaseCart);
            post("/removeProductFromCart", cartController.handleRemoveProductFromCart);
            get("/openCloseStore",storeController.openCloseStorePage);
            post("/openCloseStore",storeController.handleOpenCloseStorePost);
        });

        app.error(404, registerController.handleSystemConnect);
    }

}
