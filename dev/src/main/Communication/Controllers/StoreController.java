package main.Communication.Controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;
import main.DTO.StoreDTO;
import main.Service.IService;
import main.utils.Response;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class StoreController {



    private IService service;

    public StoreController(IService service){
        this.service=service;
    }

    public Handler openStorePage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.OPEN_STORE, model);
    };

    public Handler openAddProductToStorePage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx, model);
        ctx.render(Path.Template.ADD_PRODUCT_TO_STORE, model);
    };
    public Handler viewStoreHistoryPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        Response<List<String>> response=service.getStorePurchaseHistory(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            StringBuilder htmlQuery=new StringBuilder();
            for(String str: response.getResult())
                htmlQuery.append("<p>" + str + "</p>");
            model.put("response",htmlQuery.toString());
        }
        ctx.render(Path.Template.VIEW_STORE_HISTORY,model);
    };
    public Handler openUpdateProductInStorePage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx, model);
        ctx.render(Path.Template.UPDATE_PRODUCT_IN_STORE, model);
    };

    private void getUserStores(Context ctx, Map<String, Object> model) {
        Response<List<StoreDTO>> response = service.getAllStoresOfUser(ctx.sessionAttribute("userToken"));
        model.put("stores",response.getResult());
    }


    public Handler handleAddProductToStorePost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        String [] keys = Objects.requireNonNull(ctx.formParam("keyWords")).split(",");
        List<String> keyWords = new LinkedList<>(Arrays.asList(keys));


        Response<Boolean> response = service.addProductToStore(ctx.sessionAttribute("userToken"),ctx.formParam("productName"),
                ctx.formParam("category"),keyWords,ctx.formParam("description"),ctx.formParam("storeName"),
                Integer.valueOf(ctx.formParam("quantity")),Double.valueOf(ctx.formParam("price")));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully added a product to the store %s",ctx.formParam("storeName")));
        }
        getUserStores(ctx, model);
        ctx.render(Path.Template.ADD_PRODUCT_TO_STORE,model);
    };

    public Handler handleUpdateProductInStorePost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        String [] keys = Objects.requireNonNull(ctx.formParam("keyWords")).split(",");
        List<String> keyWords = new LinkedList<>(Arrays.asList(keys));

        Response<Boolean> response = service.updateProduct(ctx.sessionAttribute("userToken"),ctx.formParam("productName"),ctx.formParam("productName"),
                ctx.formParam("category"),keyWords,ctx.formParam("description"),ctx.formParam("storeName"),
                Integer.valueOf(ctx.formParam("quantity")),Double.valueOf(ctx.formParam("price")));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully updated a product in the store %s",ctx.formParam("storeName")));
        }
        getUserStores(ctx, model);
        ctx.render(Path.Template.UPDATE_PRODUCT_IN_STORE,model);
    };

    public Handler openManageStoreInventoryPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.MANAGE_STORE_INVENTORY, model);
    };

    public Handler openCloseStorePage = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        ctx.render(Path.Template.OPEN_CLOSE_STORE,model);
    };




    public Handler handleOpenStorePost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.openStore(ctx.formParam("userToken"),ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully opened the store: %s",ctx.formParam("storeName")));
        }
        ctx.render(Path.Template.OPEN_STORE, model);
    };

    public Handler handleOpenCloseStorePost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        String open_close = ctx.formParam("closeOrOpen");
        if(open_close.equals("open")){
            reOpenStore(ctx, model);
        }
        else {
            closeStore(ctx, model);
        }
        ctx.render(Path.Template.OPEN_CLOSE_STORE,model);
    };

    private void closeStore(Context ctx, Map<String, Object> model) {
        Response<Boolean> response = service.closeStore(ctx.sessionAttribute("userToken"), ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else{
            model.put("success",true);
            model.put("response","Successfully closed the store");
        }
    }

    private void reOpenStore(Context ctx, Map<String, Object> model) {
        Response<Boolean> response = service.reopenStore(ctx.sessionAttribute("userToken"), ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else{
            model.put("success",true);
            model.put("response","Successfully re-opened the store");
        }
    }

    public Handler openManageStoreStaffPage = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        ctx.render(Path.Template.MANAGE_STORE_STAFF,model);
    };

    public Handler viewStoreStaffPost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        Response<List<String>> response=service.getStoreStaff(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("staff",response.getResult());
            model.put("response","The store staff are:");
        }

        ctx.render(Path.Template.MANAGE_STORE_STAFF,model);
    };

    public Handler appointOrDeletePostHandle = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        switch (Objects.requireNonNull(ctx.formParam("appointOrDelete"))){
            case "appManager":
                appManager(model,ctx);
                break;
            case "remManager":
                remManager(model,ctx);
                break;
            case "appOwner":
                appOwner(model,ctx);
                break;
            case "remOwner":
                remOwner(model,ctx);
                break;
        }
        ctx.render(Path.Template.MANAGE_STORE_STAFF,model);
    };

    private void remManager(Map<String, Object> model, Context ctx) {
        Response<Boolean> response=service.removeStoreManagerAppointment(ctx.sessionAttribute("userToken"),ctx.formParam("userName"),ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else{
            model.put("success",true);
            model.put("response",String.format("Successfully removed manager %s from the store %s",ctx.formParam("userName"),ctx.formParam("storeName")));
        }
    }

    private void appOwner(Map<String, Object> model, Context ctx) {
        Response<Boolean> response = service.appointStoreOwner(ctx.sessionAttribute("userToken"), ctx.formParam("userName"), ctx.formParam("storeName"));
        if (response.isError_occured()) {
            model.put("fail", true);
            model.put("response", response.getError_message());
        } else {
            model.put("success", true);
            model.put("response", String.format("Successfully promoted %s to owner in the store %s", ctx.formParam("userName"), ctx.formParam("storeName")));
        }
    }

    private void remOwner(Map<String, Object> model, Context ctx) {
        Response<Boolean> response = service.removeStoreOwnerAppointment(ctx.sessionAttribute("userToken"), ctx.formParam("userName"), ctx.formParam("storeName"));
        if (response.isError_occured()) {
            model.put("fail", true);
            model.put("response", response.getError_message());
        } else {
            model.put("success", true);
            model.put("response", String.format("Successfully removed owner %s from the store %s", ctx.formParam("userName"), ctx.formParam("storeName")));
        }
    }

    private void appManager(Map<String, Object> model, Context ctx) {
        Response<Boolean> response = service.appointStoreManager(ctx.sessionAttribute("userToken"), ctx.formParam("userName"), ctx.formParam("storeName"));
        if (response.isError_occured()) {
            model.put("fail", true);
            model.put("response", response.getError_message());
        } else {
            model.put("success", true);
            model.put("response", String.format("Successfully promoted %s to manager in the store %s", ctx.formParam("userName"), ctx.formParam("storeName")));
        }
    }

    public Handler allowDisallowPermissionPostHandle = ctx->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        switch (Objects.requireNonNull(ctx.formParam("allowOrDisallow"))){
            case "allow":
                allowPerm(model,ctx);
                break;
            case "disallow":
                disallowPerm(model,ctx);
                break;
        }
        ctx.render(Path.Template.MANAGE_STORE_STAFF,model);
    };

    private void disallowPerm(Map<String, Object> model, Context ctx) {
        Response<Boolean> response = null;
        switch (Objects.requireNonNull(ctx.formParam("permissionType"))){
            case "Update_Products":
                response=service.disAllowManagerUpdateProducts(ctx.sessionAttribute("userToken"),ctx.formParam("userName"),ctx.formParam("storeName"));
                break;
            case "Purchase_History":
                response=service.disAllowManagerGetHistory(ctx.sessionAttribute("userToken"),ctx.formParam("userName"),ctx.formParam("storeName"));
                break;
            case "Requests":
                response=service.disAllowManagerAnswerAndTakeRequests(ctx.sessionAttribute("userToken"),ctx.formParam("userName"),ctx.formParam("storeName"));
                break;
        }
        if(response.isError_occured()){
            model.put("response",response.getError_message());
            model.put("fail",true);
        }
        else {
            model.put("response","Successfully disallowed permission");
            model.put("success",true);
        }
    }

    private void allowPerm(Map<String, Object> model, Context ctx) {
        Response<Boolean> response = null;
        switch (Objects.requireNonNull(ctx.formParam("permissionType"))){
            case "Update_Products":
                response=service.allowManagerUpdateProducts(ctx.sessionAttribute("userToken"),ctx.formParam("userName"),ctx.formParam("storeName"));
                break;
            case "Purchase_History":
                response=service.allowManagerGetHistory(ctx.sessionAttribute("userToken"),ctx.formParam("userName"),ctx.formParam("storeName"));
                break;
            case "Requests":
                response=service.allowManagerAnswerAndTakeRequests(ctx.sessionAttribute("userToken"),ctx.formParam("userName"),ctx.formParam("storeName"));
                break;
        }
        if(response.isError_occured()){
            model.put("response",response.getError_message());
            model.put("fail",true);
        }
        else {
            model.put("response","Successfully granted permission");
            model.put("success",true);
        }
    }

    public Handler viewStoreInventoryPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        ctx.render(Path.Template.VIEW_STORE_INVENTORY,model);

    };

    public Handler viewStoreInventoryPost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        Response<StoreDTO> response = service.getStoreInfo(ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("response",response.getError_message());
            model.put("fail",true);
        }
        else {
            model.put("success",true);
            model.put("response",String.format("The inventory of %s:",ctx.formParam("storeName")));
            model.put("store",response.getResult());
        }
        ctx.render(Path.Template.VIEW_STORE_INVENTORY,model);
    };

    public Handler deleteStoreGet = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.DELETE_STORE,model);
    };

    public Handler deleteStorePost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.deleteStore(ctx.sessionAttribute("storeToken"),ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully deleted the store %s",ctx.formParam("storeName")));
        }
        ctx.render(Path.Template.DELETE_STORE,model);
    };

    public Handler viewStorePurchaseHistoryPage = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        ctx.render(Path.Template.VIEW_STORE_HISTORY,model);
    };

    public Handler storeSearchPage = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.SEARCH_STORE,model);
    };

    public Handler handleStoreSearch = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<StoreDTO> response = service.getStoreInfo(ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            if(response.getResult().getProductsByName().size()==0){
                model.put("fail",true);
                model.put("response","There are no products in this store for sale");
            }
            else {
                model.put("success", true);
                model.put("store", response.getResult());
            }
        }
        ctx.render(Path.Template.SEARCH_STORE,model);
    };

    public Handler handleDeleteProductPost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.removeProductFromStore(ctx.sessionAttribute("userToken"),ctx.formParam("productName"),ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully removed a product from the store");
        }
        getUserStores(ctx,model);
        ctx.render(Path.Template.UPDATE_PRODUCT_IN_STORE,model);
    };

    public Handler answerQueriesPage = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        ctx.render(Path.Template.ANSWER_QUERIES,model);

    };
    public Handler answerQueriesPost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.sendRespondToBuyers(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"),ctx.formParam("userName"),
                ctx.formParam("respond"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully send a respond to user %s",ctx.formParam("userName")));
        }
        getUserStores(ctx,model);
        ctx.render(Path.Template.ANSWER_QUERIES,model);
    };

    public Handler askQueriesPage = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.ASK_QUERIES,model);

    };
    public Handler askQueriesPost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.sendQuestionsToStore(ctx.sessionAttribute("userToken"),
                ctx.formParam("storeName"),
                ctx.formParam("question"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully send a question to the store staff");
        }
        ctx.render(Path.Template.ASK_QUERIES,model);
    };

    public Handler addPolicyPage = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        getUserStores(ctx,model);
        ctx.render(Path.Template.ADD_POLICY,model);

    };

    public Handler addPolicySelectStorePost = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<StoreDTO> response = service.getStoreInfo(ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("response",response.getError_message());
            model.put("fail",true);
        }
        else {
            model.put("success",true);
            model.put("store",response.getResult());
        }
        getUserStores(ctx,model);
        ctx.render(Path.Template.ADD_POLICY,model);
    };

    public Handler addRafflePolicyPost = ctx ->{
        Map<String, Object> model = getParams(ctx);
        ctx.render(Path.Template.ADD_RAFFLE_POLICY,model);
    };

    public Handler handleAddRafflePolicy = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.addRafflePolicy(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"),ctx.formParam("productName"),
                Double.valueOf(Objects.requireNonNull(ctx.formParam("price"))));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully added raffle policy");
        }
        ctx.render(Path.Template.ADD_RAFFLE_POLICY,model);
    };

    public Handler addAuctionPolicyPost = ctx ->{
        Map<String, Object> model = getParams(ctx);
        ctx.render(Path.Template.ADD_AUCTION_POLICY,model);
    };

    @NotNull
    private Map<String, Object> getParams(Context ctx) {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("storeName", ctx.formParam("storeName"));
        model.put("productName", ctx.formParam("productName"));
        return model;
    }

    public Handler handleAddAuctionPolicy = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        String[] until_date_params = Objects.requireNonNull(ctx.formParam("untilDate")).split("-");

        Response<Boolean> response = service.addAuctionPolicy(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"),ctx.formParam("productName"),
                Double.valueOf(Objects.requireNonNull(ctx.formParam("price"))),
                LocalDate.of(Integer.parseInt(until_date_params[0]), Month.of(Integer.parseInt(until_date_params[1])),Integer.parseInt(until_date_params[2])));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully added auction policy");
        }
        ctx.render(Path.Template.ADD_AUCTION_POLICY,model);
    };

    public Handler addBargainPolicyPost = ctx ->{
        Map<String, Object> model = getParams(ctx);
        ctx.render(Path.Template.ADD_BARGAIN_POLICY,model);
    };
    public Handler handleAddBargainPolicy = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);

        Response<Boolean> response = service.addBargainPolicy(ctx.sessionAttribute("userToken"),
                ctx.formParam("storeName"),
                ctx.formParam("productName"),
                Double.valueOf(Objects.requireNonNull(ctx.formParam("price"))));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully added auction policy");
        }
        ctx.render(Path.Template.ADD_BARGAIN_POLICY,model);

    };

    public Handler resetPolicy = ctx ->{
        Map<String, Object> model = getParams(ctx);
        ctx.render(Path.Template.RESET_POLICIES,model);
    };

    public Handler handleResetPolicy = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);

        Response<Boolean> response = service.addNormalPolicy(ctx.sessionAttribute("userToken"),
                ctx.formParam("storeName"),
                ctx.formParam("productName"),
                Double.valueOf(Objects.requireNonNull(ctx.formParam("price"))));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully added regular policy");
        }
        ctx.render(Path.Template.RESET_POLICIES,model);
    };
}
