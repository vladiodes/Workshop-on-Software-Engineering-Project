package main.Communication.util;

public class Path {

    public static class Web {
        public static final String INDEX = "/index";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String BOOKS = "/books";
       // public static final String ONE_BOOK = "/books/{isbn}";
    }

    public static class Template {
        public static final String LOGIN = "/velocity/login/login.vm";
        public static final String LOGOUT = "/velocity/login/logout.vm";
        public static final String OPEN_STORE = "/velocity/store/openStore.vm";


        public static final String NOT_FOUND = "/velocity/notFound.vm";
        public static final String MANAGE_STORE_INVENTORY = "/velocity/store/manageStoreInventory.vm";
        public static final String ADD_PRODUCT_TO_STORE = "/velocity/store/addProductToStore.vm";
        public static final String UPDATE_PRODUCT_IN_STORE = "/velocity/store/updateProductInStore.vm";
        public static final String SEARCH_PRODUCT = "velocity/product/searchProduct.vm";
        public static final String CART = "velocity/cart/cart.vm";
        public static final String PURCHASE_CART = "velocity/cart/purchaseCart.vm";
    }

}
