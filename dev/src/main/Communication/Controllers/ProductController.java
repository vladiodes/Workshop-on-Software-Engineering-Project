package main.Communication.Controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;
import main.DTO.ProductDTO;
import main.Service.IService;
import main.Stores.Product;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductController {


    private IService service;

    public ProductController(IService service){
        this.service=service;
    }

    public Handler openSearchProductPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.SEARCH_PRODUCT, model);
    };

    public Handler handleSearchProductPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);

        Response<List<ProductDTO>> response = service.getProductsByInfo(ctx.formParam("productName"),ctx.formParam("category"),
                ctx.formParam("keyWord"),getParamOrNull(ctx,"productRating"),
                getParamOrNull(ctx,"storeRating"),getParamOrNull(ctx,"priceMin"),getParamOrNull(ctx,"priceMax"));

        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else if(response.getResult().size()==0){
            model.put("fail",true);
            model.put("response","No products were found matching these details");
        }
        else {
            model.put("success",true);
            model.put("products",response.getResult());
        }
        ctx.render(Path.Template.SEARCH_PRODUCT, model);
    };

    private Double getParamOrNull(Context ctx, String param) {
        if(Objects.requireNonNull(ctx.formParam(param)).isBlank())
            return null;
        return Double.valueOf(Objects.requireNonNull(ctx.formParam(param)));
    }

    public Handler handleAddDirectDiscountToProduct = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        String[] exp_date_params = Objects.requireNonNull(ctx.formParam("expDate")).split("-");
        Response<Integer> response_discount = service.CreateSimpleDiscount(ctx.sessionAttribute("userToken"), ctx.formParam("storeName"),
                LocalDate.of(Integer.parseInt(exp_date_params[0]), Month.of(Integer.parseInt(exp_date_params[1])), Integer.parseInt(exp_date_params[2])),
                Double.valueOf(Objects.requireNonNull(ctx.formParam("percent"))));
        if (response_discount.isError_occured()) {
            model.put("fail", true);
            model.put("response", response_discount.getError_message());
        } else {
            Response<Boolean> response = service.SetDiscountToProduct(ctx.sessionAttribute("userToken"), ctx.formParam("storeName"), response_discount.getResult(), ctx.formParam("productName"));
            if (response.isError_occured()) {
                model.put("fail", true);
                model.put("response", response.getError_message());
            } else {
                model.put("success", true);
                model.put("response", "Successfully added direct discount");
            }
        }
        ctx.render(Path.Template.ADD_DIRECT_DISCOUNT, model);
    };

    public Handler handleAddSecretDiscount = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        String[] exp_date_params = Objects.requireNonNull(ctx.formParam("expDate")).split("-");
        Response<Integer> response_discount = service.CreateSecretDiscount(ctx.sessionAttribute("userToken"), ctx.formParam("storeName"),
                LocalDate.of(Integer.parseInt(exp_date_params[0]), Month.of(Integer.parseInt(exp_date_params[1])), Integer.parseInt(exp_date_params[2])),
                Double.valueOf(Objects.requireNonNull(ctx.formParam("percent"))),
                ctx.formParam("secretCode"));
        if (response_discount.isError_occured()) {
            model.put("fail", true);
            model.put("response", response_discount.getError_message());
        } else {
            Response<Boolean> response = service.SetDiscountToProduct(ctx.sessionAttribute("userToken"), ctx.formParam("storeName"), response_discount.getResult(), ctx.formParam("productName"));
            if (response.isError_occured()) {
                model.put("fail", true);
                model.put("response", response.getError_message());
            } else {
                model.put("success", true);
                model.put("response", "Successfully added secret discount");
            }
        }
        ctx.render(Path.Template.ADD_SECRET_DISCOUNT, model);
    };

    public Handler makeBidPage = ctx->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        String[] s_p = Objects.requireNonNull(ctx.formParam("store_productName")).split(",");
        model.put("storeName",s_p[1]);
        model.put("productName",s_p[0]);
        ctx.render(Path.Template.MAKE_BID_ON_PRODUCT,model);
    };

    public Handler handleAddBidToProduct = ctx->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        String exp_date = ctx.formParam("ExpDate");
        String[] exp_date_params = Objects.requireNonNull(exp_date).split("-");
        //yyyy-mm-dd
        PaymentInformation pi = new PaymentInformation(ctx.formParam("cardNumber"),
                LocalDate.of(Integer.parseInt(exp_date_params[0]),Month.of(Integer.parseInt(exp_date_params[1])),Integer.parseInt(exp_date_params[2])),
                Integer.parseInt(Objects.requireNonNull(ctx.formParam("cvv"))),ctx.formParam("name"),ctx.formParam("id"));

        SupplyingInformation si = new SupplyingInformation(ctx.formParam("name"), ctx.formParam("address"), ctx.formParam("city"),
                ctx.formParam("country"), ctx.formParam("zip"));

        Response<Boolean> response = service.bidOnProduct(ctx.sessionAttribute("userToken"),
                ctx.formParam("storeName"),
                ctx.formParam("productName"),
                Double.valueOf(Objects.requireNonNull(ctx.formParam("bidPrice"))),
                pi,
                si);
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully added a bid on the product");
        }
        ctx.render(Path.Template.MAKE_BID_ON_PRODUCT,model);
    };

    public Handler approveBidPost = ctx->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response=service.ApproveBid(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"),ctx.formParam("productName"),ctx.formParam("userName"));
        if(response.isError_occured()){
            model.put("bid_fail",true);
            model.put("response_bid",response.getError_message());
        }
        else {
            model.put("bid_success",true);
            model.put("response_bid","Bid successfully approved");
        }
        ctx.render(Path.Template.VIEW_BIDS,model);
    };
    public Handler declineBidPost = ctx->{
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response=service.DeclineBid(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"),ctx.formParam("productName"),ctx.formParam("userName"));
        if(response.isError_occured()){
            model.put("bid_fail",true);
            model.put("response_bid",response.getError_message());
        }
        else {
            model.put("bid_success",true);
            model.put("response_bid","Bid successfully declined");
        }
        ctx.render(Path.Template.VIEW_BIDS,model);
    };
}
