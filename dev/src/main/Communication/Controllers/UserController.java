package main.Communication.Controllers;

import io.javalin.http.Handler;
import main.Communication.util.Path;
import main.Communication.util.ViewUtil;
import main.DTO.ShoppingCartDTO;
import main.Service.IService;
import main.utils.Response;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserController {



    private IService service;
    public UserController(IService service){
        this.service=service;
    }


    public Handler viewPurchaseHistoryAdminPage = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.VIEW_ADMIN_PURCHASE,model);
    };
    public Handler adminViewUserHistoryPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<List<String>> response=service.getPurchaseHistory(ctx.sessionAttribute("userToken"),ctx.formParam("userName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            StringBuilder htmlQuery=new StringBuilder();
            for(String str: response.getResult())
                htmlQuery.append("<p>str</p>");
            model.put("response",htmlQuery.toString());
        }

        ctx.render(Path.Template.VIEW_ADMIN_PURCHASE,model);
    };

    public Handler adminViewStoreHistoryPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<List<String>> response=service.getStorePurchaseHistory(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            StringBuilder htmlQuery=new StringBuilder();
            for(String str: response.getResult())
                htmlQuery.append("<p>str</p>");
            model.put("response",htmlQuery.toString());
        }
        ctx.render(Path.Template.VIEW_ADMIN_PURCHASE,model);
    };




    public Handler openUserProfilePage = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.PROFILE,model);
    };
    public Handler changeUserNamePost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.changeUsername(ctx.sessionAttribute("userToken"),ctx.formParam("userName"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else{
            ctx.sessionAttribute("currentUser",ctx.formParam("userName"));
            model.put("success",true);
            model.put("response",String.format("Successfully change your username to %s",ctx.formParam("userName")));
            model.put("currentUser",ctx.formParam("userName"));
        }
        ctx.render(Path.Template.PROFILE,model);
    };

    public Handler changePasswordPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.changePassword(ctx.sessionAttribute("userToken"),ctx.formParam("oldPassword"), ctx.formParam("newPassword"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else{
            model.put("success",true);
            model.put("response","You've successfully changed your password");
        }
        ctx.render(Path.Template.PROFILE,model);
    };

    public Handler addSecurityQuestionPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.addSecurityQuestion(ctx.sessionAttribute("userToken"),ctx.formParam("question"),ctx.formParam("answer"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully added a security question!");
        }
        ctx.render(Path.Template.PROFILE,model);
    };

    public Handler handleGetPurchaseHistory = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
         Response<List<String>> response = service.getPurchaseHistory(ctx.sessionAttribute("userToken"),ctx.sessionAttribute("currentUser"));
         if(response.isError_occured()){
             model.put("fail",true);
             model.put("response",response.getError_message());
         }
         else {
             model.put("success",true);
             model.put("response","The history is:");
             model.put("history",response.getResult());
         }
        ctx.render(Path.Template.PROFILE,model);
    };

    public Handler deleteUserGet = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.DELETE_USER,model);
    };

    public Handler deleteUserPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.deleteUser(ctx.sessionAttribute("userToken"),ctx.formParam("username"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully deleted the user %s",ctx.formParam("username")));
        }
        ctx.render(Path.Template.DELETE_USER,model);
    };

    public Handler systemStatsPage = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<String> response = service.getLoggedInVSRegistered(ctx.sessionAttribute("userToken"));
        model.put("logged_in_vs_registered_stats",response.getResult());
        ctx.render(Path.Template.VIEW_SYS_STATS,model);
    };

    public Handler systemStatsHandlePost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        String date = ctx.formParam("date");
        String[] date_params = Objects.requireNonNull(date).split("-");

        Response<String> loggedInUsersPerDate = service.getNumberOfLoggedInUsersPerDate(ctx.sessionAttribute("userToken"), LocalDate.of
                (Integer.parseInt(date_params[0]),
                        Month.of(Integer.parseInt(date_params[1])),
                        Integer.parseInt(date_params[2])));

        Response<String> registeredUsersPerDate = service.getNumberOfRegisteredUsersPerDate(ctx.sessionAttribute("userToken"), LocalDate.of
                (Integer.parseInt(date_params[0]),
                        Month.of(Integer.parseInt(date_params[1])),
                        Integer.parseInt(date_params[2])));

        Response<String> purchasesPerDate = service.getNumberOfPurchasesPerDate(ctx.sessionAttribute("userToken"), LocalDate.of
                (Integer.parseInt(date_params[0]),
                        Month.of(Integer.parseInt(date_params[1])),
                        Integer.parseInt(date_params[2])));



        if(loggedInUsersPerDate.isError_occured() || registeredUsersPerDate.isError_occured() || purchasesPerDate.isError_occured()){
            model.put("fail",true);
            model.put("response","Couldn't get system stats for that date...");
        }
        else{
            model.put("success",true);
            model.put("response",String.format("System stats for %s",ctx.formParam("date").toString()));
            model.put("sys_stats",String.format("<p>Number of users that logged in: %s</p>" +
                    "<p>Number of users that registered: %s</p>" +
                    "<p>Number of purchases: %s</p>",loggedInUsersPerDate.getResult(),registeredUsersPerDate.getResult(),purchasesPerDate.getResult()));
        }
        Response<String> response = service.getLoggedInVSRegistered(ctx.sessionAttribute("userToken"));
        model.put("logged_in_vs_registered_stats",response.getResult());
        ctx.render(Path.Template.VIEW_SYS_STATS,model);
    };

    public Handler answerComplaintsPage= ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.ANSWER_COMPLAINTS,model);
    };

    public Handler answerComplaintsPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.respondToMessage(ctx.sessionAttribute("userToken"),ctx.formParam("userName"),ctx.formParam("answer"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response",String.format("Successfully sent a repond to %s",ctx.formParam("userName")));
        }
        ctx.render(Path.Template.ANSWER_COMPLAINTS,model);
    };
    public Handler sendComplaintPage = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.SEND_COMPLAINT,model);
    };

    public Handler sendComplaintPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.sendComplaint(ctx.sessionAttribute("userToken"),ctx.formParam("desc"));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully sent the complaint, we'll come back to you");
        }
        ctx.render(Path.Template.SEND_COMPLAINT,model);
    };

    public Handler reviewPage = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        ctx.render(Path.Template.WRITE_REVIEW,model);
    };

    public Handler writeProductReviewPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.writeProductReview(ctx.sessionAttribute("userToken"),ctx.formParam("productName"),ctx.formParam("storeName"),
                ctx.formParam("review"),Double.parseDouble(Objects.requireNonNull(ctx.formParam("points"))));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully recorded the review, thank you!");
        }
        ctx.render(Path.Template.WRITE_REVIEW,model);
    };
    public Handler writeStoreReviewPost = ctx ->{
        Map<String,Object> model = ViewUtil.baseModel(ctx);
        Response<Boolean> response = service.writeStoreReview(ctx.sessionAttribute("userToken"),ctx.formParam("storeName"),ctx.formParam("review"),
                Double.parseDouble(Objects.requireNonNull(ctx.formParam("points"))));
        if(response.isError_occured()){
            model.put("fail",true);
            model.put("response",response.getError_message());
        }
        else {
            model.put("success",true);
            model.put("response","Successfully recorded the review, thank you!");
        }
        ctx.render(Path.Template.WRITE_REVIEW,model);
    };
}
