package main.Communication;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import main.Communication.Controllers.*;
import main.Communication.util.HerokuUtil;
import main.Communication.util.ViewUtil;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;


public class Main {
    private static final IService service=new Service(new PaymentAdapter(),new SupplyingAdapter());
    public static void main(String[] args) {

        RegisterController registerController=new RegisterController(service);
        LoginController loginController=new LoginController(service);
        StoreController storeController=new StoreController(service);
        ProductController productController=new ProductController(service);
        CartController cartController=new CartController(service);
        UserController userController=new UserController(service);

        Response<String> token=service.guestConnect();
        service.register("vladiodes","123456");
        service.login(token.getResult(),"admin","admin");
        service.openStore(token.getResult(),"Apple store");
        service.appointStoreOwner(token.getResult(),"vladiodes","Apple store");
        service.logout(token.getResult());
        service.guestDisconnect(token.getResult());

        Javalin app = Javalin.create(config -> {
            config.server(() -> {
                Server server = new Server();
                ServerConnector sslConnector = new ServerConnector(server, getSslContextFactory());
                sslConnector.setPort(443);
                ServerConnector connector = new ServerConnector(server);
                connector.setPort(80);
                server.setConnectors(new Connector[]{sslConnector, connector});
                return server;
            });
            config.addStaticFiles("/public");
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
        }).start(HerokuUtil.getHerokuAssignedPort());
        app.ws("/notifications", ws -> {
            ws.onMessage(ctx -> {
                String userToken = ctx.message();
                service.assignWStoUserToken(userToken,ctx);
            });
            ws.onClose(ctx->{
                service.leaveWSforUserToken(ctx.sessionAttribute("userToken"));
            });
        });

        app.routes(() -> {
            get("/", registerController.handleSystemConnect);
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
            post("/deleteProduct",storeController.handleDeleteProductPost);
            get("/productSearch",productController.openSearchProductPage);
            post("/productSearch",productController.handleSearchProductPost);
            post("/addToCart",cartController.handleAddToCartPost);
            get("/cart",cartController.openCartPage);
            get("/purchaseCart",cartController.openPurchaseCartPage);
            post("/purchaseCart", cartController.handlePurchaseCart);
            post("/removeProductFromCart", cartController.handleRemoveProductFromCart);
            get("/openCloseStore",storeController.openCloseStorePage);
            post("/openCloseStore",storeController.handleOpenCloseStorePost);
            get("/profile",userController.openUserProfilePage);
            post("/changeUserName",userController.changeUserNamePost);
            post("/changePassword",userController.changePasswordPost);
            post("/addSecurityQuestion",userController.addSecurityQuestionPost);
            get("/manageStoreStaff",storeController.openManageStoreStaffPage);
            post("/viewStoreStaff",storeController.viewStoreStaffPost);
            post("/appointOrDeleteManager",storeController.appointOrDeletePostHandle);
            post("/grantRemovePermissions",storeController.allowDisallowPermissionPostHandle);
            get("userPurchaseHistory",userController.handleGetPurchaseHistory);
            get("/viewProductsInStore",storeController.viewStoreInventoryPage);
            post("/viewProductsInStore",storeController.viewStoreInventoryPost);
            get("/deleteUser",userController.deleteUserGet);
            post("/deleteUser",userController.deleteUserPost);
            get("/deleteStore",storeController.deleteStoreGet);
            post("/deleteStore",storeController.deleteStorePost);
            get("/systemStats",userController.systemStatsPage);
            post("/systemStats",userController.systemStatsHandlePost);
            get("/answerComplaints",userController.answerComplaintsPage);
            post("/answerComplaints",userController.answerComplaintsPost);
            get("/viewPurchaseHistoryAdmin",userController.viewPurchaseHistoryAdminPage);
            post("/adminViewUserHistory",userController.adminViewUserHistoryPost);
            post("/adminViewStoreHistory",userController.adminViewStoreHistoryPost);
            get("/viewStoreHistory",storeController.viewStorePurchaseHistoryPage);
            post("/viewStoreHistory",storeController.viewStoreHistoryPost);
            get("/sendComplaint",userController.sendComplaintPage);
            post("/sendComplaint",userController.sendComplaintPost);
            get("/storeSearch",storeController.storeSearchPage);
            post("/storeSearch",storeController.handleStoreSearch);
            post("/addToCartAfterStoreSearch",cartController.handleAddToCartAfterStoreSearchPost);
            get("/writeReview",userController.reviewPage);
            post("/writeProductReview",userController.writeProductReviewPost);
            post("/writeStoreReview",userController.writeStoreReviewPost);
            get("/answerQueries",storeController.answerQueriesPage);
            post("/answerQueries",storeController.answerQueriesPost);
            get("/askQueries",storeController.askQueriesPage);
            post("/askQueries",storeController.askQueriesPost);
            get("/addPolicy",storeController.addPolicyPage);
            post("/addPolicySelectStore",storeController.addPolicySelectStorePost);
            post("/addRafflePolicy",storeController.addRafflePolicyPost);
            post("/handleAddRafflePolicy",storeController.handleAddRafflePolicy);
            post("/addAuctionPolicy",storeController.addAuctionPolicyPost);
            post("/handleAddAuctionPolicy",storeController.handleAddAuctionPolicy);
            post("/addBargainPolicy",storeController.addBargainPolicyPost);
            post("/handleAddBargainPolicy",storeController.handleAddBargainPolicy);
            post("/resetPolicy",storeController.resetPolicy);
            post("/resetPolicies",storeController.handleResetPolicy);
            post("/addDiscountSelectStore",storeController.addDiscountSelectStore);
            get("/addDiscount", storeController.addDiscountPage);
            post("/addDiscount",storeController.addDiscountPost);
//            post("/handleAddDirectDiscount",productController.handleAddDirectDiscount); //TODO fix
//            post("/handleAddSecretDiscount",productController.handleAddSecretDiscount);
//            post("/handleAddConditionalDiscount",productController.handleAddCondDiscount);
            post("/insertSecretCode",cartController.handleAddSecretCode);
            post("/makeBid",productController.makeBidPage);
            post("/addBid",productController.handleAddBidToProduct);
            get("/viewBids",storeController.viewBidsPage);
            post("/viewBids",storeController.handleViewBidsPost);
            post("/approveBid",productController.approveBidPost);
            post("/declineBid",productController.declineBidPost);

        });

    }

    private static SslContextFactory getSslContextFactory() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreType("PKCS12");
        sslContextFactory.setKeyStorePath(Main.class.getResource("/keystore/localhost.p12").toExternalForm());
        sslContextFactory.setKeyStorePassword("password");
        return sslContextFactory;
    }

}
