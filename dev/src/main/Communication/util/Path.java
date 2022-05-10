package main.Communication.util;

public class Path {

    public static class Web {
        public static final String LOGIN = "/login";
       // public static final String ONE_BOOK = "/books/{isbn}";
    }

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
    }

}
