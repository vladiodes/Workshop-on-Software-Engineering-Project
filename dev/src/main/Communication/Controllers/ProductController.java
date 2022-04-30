package main.Communication.Controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;
import main.DTO.ProductDTO;
import main.Service.IService;
import main.Stores.IStore;
import main.Stores.Product;
import main.utils.Response;

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
}
