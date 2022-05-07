package main.Communication.Controllers;

import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;
import main.DTO.ShoppingCartDTO;
import main.Service.IService;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;

import java.time.LocalDate;
import java.time.Month;
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
        String exp_date = ctx.formParam("ExpDate");
        String[] exp_date_params = Objects.requireNonNull(exp_date).split("-");
        String sup_date = ctx.formParam("supplyingDate");
        String[] sup_date_params =Objects.requireNonNull(sup_date).split("-");
        //yyyy-mm-dd
        PaymentInformation pi = new PaymentInformation(ctx.formParam("cardNumber"),
                LocalDate.of(Integer.parseInt(exp_date_params[0]),Month.of(Integer.parseInt(exp_date_params[1])),Integer.parseInt(exp_date_params[2])),
                Integer.parseInt(Objects.requireNonNull(ctx.formParam("cvv"))),ctx.formParam("name"),ctx.formParam("email"));

        SupplyingInformation si = new SupplyingInformation(ctx.formParam("address"),
                LocalDate.of(Integer.parseInt(sup_date_params[0]), Month.of(Integer.parseInt(sup_date_params[1])),Integer.parseInt(sup_date_params[2])));

        Response<Boolean> response=service.purchaseCart(ctx.sessionAttribute("userToken"),pi,si);
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else{
            model.put("success",true);
            model.put("response","Successfully purchased the cart!");
        }
        ctx.render(Path.Template.PURCHASE_CART, model);

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

    public Handler handleRemoveProductFromCart = ctx ->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        String p_name = ctx.formParam("productName");
        String s_name = ctx.formParam("storeName");
        Response<Boolean> response = service.RemoveProductFromCart(ctx.sessionAttribute("userToken"),s_name,p_name,Integer.parseInt(Objects.requireNonNull(ctx.formParam("quantity"))));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully deleted %s from cart",p_name));
        }
        Response<ShoppingCartDTO> r=service.getCartInfo(ctx.sessionAttribute("userToken"));
        model.put("cart",r.getResult());
        ctx.render(Path.Template.CART,model);
    };

}
