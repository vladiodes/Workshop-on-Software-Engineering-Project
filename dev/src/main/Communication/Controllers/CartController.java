package main.Communication.Controllers;

import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;
import main.DTO.ShoppingCartDTO;
import main.Service.IService;
import main.utils.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartController {
    private IService service;

    public CartController(IService service){
        this.service=service;
    }
    public Handler openCartPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<ShoppingCartDTO> response=service.getCartInfo(ctx.sessionAttribute("userToken"));
        model.put("cart",response.getResult());
        ctx.render(Path.Template.CART,model);
    };
    public Handler openPurchaseCartPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.PURCHASE_CART,model);
    };

    public Handler handlePurchaseCart = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
//        Response<Boolean> response=service.purchaseCart()

    };
    public Handler handleAddToCartPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        List<String> params = List.of(Objects.requireNonNull(ctx.formParam("productStoreName")).split(","));
        Response<Boolean> response=service.addProductToCart(ctx.sessionAttribute("userToken"),params.get(1),params.get(0),
                Integer.parseInt(Objects.requireNonNull(ctx.formParam("quantity"))));
        if(response.isError_occured()){
            model.put("cart_fail",true);
            model.put("cart_response",response.getError_message());
        }
        else{
            model.put("cart_success",true);
            model.put("cart_response","Successfully added to cart");
        }
        ctx.render(Path.Template.SEARCH_PRODUCT, model);
    };

}
