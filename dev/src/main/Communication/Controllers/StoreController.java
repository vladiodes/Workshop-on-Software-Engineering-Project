package main.Communication.Controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;
import main.DTO.StoreDTO;
import main.Service.IService;
import main.utils.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        List<String> keyWords = Arrays.stream(Objects.requireNonNull(ctx.formParam("keyWords")).split(",")).toList();

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
        List<String> keyWords = Arrays.stream(Objects.requireNonNull(ctx.formParam("keyWords")).split(",")).toList();

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
}
