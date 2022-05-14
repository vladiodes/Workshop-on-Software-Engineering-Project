package main.Communication.util;

public class Path {

    public static class Template {
        public static final String LOGIN = "/velocity/login/login.vm";
        public static final String LOGOUT = "/velocity/login/logout.vm";
        public static final String OPEN_STORE = "/velocity/store/openStore.vm";


        public static final String MAIN = "/velocity/main.vm";
        public static final String MANAGE_STORE_INVENTORY = "/velocity/store/manageStoreInventory.vm";
        public static final String ADD_PRODUCT_TO_STORE = "/velocity/store/addProductToStore.vm";
        public static final String UPDATE_PRODUCT_IN_STORE = "/velocity/store/updateProductInStore.vm";
        public static final String SEARCH_PRODUCT = "velocity/product/searchProduct.vm";
        public static final String CART = "velocity/cart/cart.vm";
        public static final String PURCHASE_CART = "velocity/cart/purchaseCart.vm";
        public static final String OPEN_CLOSE_STORE = "velocity/store/openCloseStore.vm";
        public static final String PROFILE = "velocity/users/profile.vm";
        public static final String MANAGE_STORE_STAFF = "velocity/store/manageStoreStaff.vm";
        public static final String VIEW_STORE_INVENTORY = "velocity/store/viewStoreInventory.vm";
        public static final String DELETE_USER = "velocity/admin/deleteUser.vm";
        public static final String DELETE_STORE = "velocity/admin/deleteStore.vm";
        public static final String VIEW_SYS_STATS = "velocity/admin/systemStats.vm";
        public static final String ANSWER_COMPLAINTS = "velocity/admin/answerComplaints.vm";
        public static final String VIEW_ADMIN_PURCHASE = "velocity/admin/viewPurchaseHistoryAdmin.vm";
        public static final String VIEW_STORE_HISTORY = "velocity/store/purchaseHistory.vm";
        public static final String SEND_COMPLAINT = "velocity/users/sendComplaint.vm";

        public static final String SEARCH_STORE = "velocity/store/searchStore.vm";
        public static final String WRITE_REVIEW = "velocity/writeReview.vm";
        public static final String ANSWER_QUERIES = "velocity/store/answerQueries.vm";
        public static final String ASK_QUERIES = "velocity/store/askQueries.vm";
        public static final String VIEW_USER_PURCHASE_HISTORY = "velocity/users/viewPurchaseHistory.vm";
        public static final String ADD_POLICY = "velocity/store/addPolicy.vm";
        public static final String ADD_RAFFLE_POLICY = "velocity/product/addRafflePolicy.vm";
        public static final String ADD_AUCTION_POLICY = "velocity/product/addAuctionPolicy.vm";
        public static final String ADD_BARGAIN_POLICY = "velocity/product/addBargainPolicy.vm";
        public static final String RESET_POLICIES = "velocity/product/resetPolicies.vm";
        public static final String ADD_DISCOUNT = "velocity/store/addDiscount.vm";
        public static final String ADD_DIRECT_DISCOUNT = "velocity/product/addDirectDiscount.vm";
        public static final String ADD_SECRET_DISCOUNT = "velocity/product/addSecretDiscount.vm";
        public static final String ADD_COND_DISCOUNT = "velocity/product/addCondDiscount.vm";
        public static final String MAKE_BID_ON_PRODUCT = "velocity/product/addBidOnProduct.vm";
        public static final String VIEW_BIDS = "velocity/store/viewBids.vm";
    }

}
