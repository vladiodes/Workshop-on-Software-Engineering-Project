package test.AcceptanceTests;

import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ATuser2Requirements {

    Response<String> manager1token, founder1token,user1token;
    IService service = new Service();
    @Before
    public void setUp(){
        manager1token = service.guestConnect();
        founder1token = service.guestConnect();
        user1token = service.guestConnect();

        service.register("manager1", "12345678");
        service.register("founder1", "12345678");
        service.register("user1", "12345678");

        service.login(manager1token.getResult(), "manager1", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(user1token.getResult(), "user1", "12345678");

        service.openStore(founder1token.getResult(), "MyStore1");
        service.appointStoreManager(founder1token.getResult(), "manager1", "MyStore1");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6);

    }

    /***
     * use case: Searching for a store req 2.1:
     */
    @Test
    public void SearchingForAStore(){
        Response<StoreDTO> r = service.getStoreInfo("NoneExistent store");
        assertTrue(r.isError_occured());
        Assertions.assertNull(r.getResult());

        r = service.getStoreInfo("MyStore1");
        assertFalse(r.isError_occured());
        Assertions.assertNotNull(r.getResult());
    }
    /***
     * use case:Searching for a product req 2.2:
     */
    @Test
    public void SearchingForAProduct(){
        Response<List<ProductDTO>> r = service.getProductsByInfo("NoneExistent Product", null, null, null, null, null, null);
        assertFalse(r.isError_occured());
        Assertions.assertEquals(r.getResult().size(), 0);

        r = service.getProductsByInfo("Coca Cola", "Drinks", null, null, null, null, null);
        assertFalse(r.isError_occured());
        Assertions.assertEquals(r.getResult().size(), 1);
    }

    /***
     * use case: Adding a Product to the Shopping Cart req 2.3:
     */
    @Test
    public void AddingProductToCart(){
        Response<Boolean> r = service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1);
        Assertions.assertTrue(!r.isError_occured() && r.getResult());
        r = service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 150);
        assertTrue(r.isError_occured());
        r = service.addProductToCart(user1token.getResult(), "NonExistentStore", "Coca Cola", 1);
        assertTrue(r.isError_occured());
        r = service.addProductToCart(user1token.getResult(), "MyStore1", "Nonexistent Item", 1);
        Assertions.assertTrue(r.isError_occured() );
    }

    /***
     * use case: Inspecting Shopping cart req 2.4:
     */
    @Test
    public void InspectingShoppingCart(){
        Response <ShoppingCartDTO> r = service.getCartInfo(user1token.getResult());
        assertEquals(r.getResult().getBaskets().size(), 0);
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1);
        r = service.getCartInfo(user1token.getResult());
        assertEquals(r.getResult().getBaskets().size(), 1);
        assertEquals(r.getResult().getBaskets().get("MyStore1").getProductsQuantity().size(), 1);
    }

    /***
     * use case: Removing product from Shopping cart req 2.4:
     */
    @Test
    public void RemoveProductFromCart(){
        Response <Boolean> responseRemove = service.RemoveProductFromCart(user1token.getResult(), "MyStore1", "Coca Cola", 5);
        assertTrue(responseRemove.isError_occured());
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1);
        responseRemove = service.RemoveProductFromCart(user1token.getResult(), "MyStore1", "Coca Cola", (int) Math.floor(Math.random() * 10 + 1));
        assertFalse(responseRemove.isError_occured());
        Response<ShoppingCartDTO> responseCart = service.getCartInfo(user1token.getResult());
        assertEquals(responseCart.getResult().getBaskets().size(), 0);

    }

    /***
     * use case: purchasing a cart with validated payment and supplier req 2.5:
     */
    @Test
    public void PurchaseCartScenario(){
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), new PaymentInformation(true), new SupplyingInformation(true));
        assertTrue(r.isError_occured());            // can't purchase empty cart.
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1);
        r = service.purchaseCart(user1token.getResult(), new PaymentInformation(true), new SupplyingInformation(true));
        assertFalse(r.isError_occured());
        Response<ShoppingCartDTO> cartR = service.getCartInfo(user1token.getResult());
        assertEquals(0, cartR.getResult().getBaskets().size());
        assertEquals(service.getPurchaseHistory(user1token.getResult(), "user1").getResult().size(),1);
        assertEquals(1, service.receiveMessages(manager1token.getResult()).getResult().size());
    }
    /***
     * use case: purchasing a cart with failed payment or delivety req 2.5:
     */
    @Test
    public void PurchaseCartScenarioBadPaymentOrDelivery(){
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1);
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), new PaymentInformation(true), new SupplyingInformation(false));
        assertTrue(r.isError_occured());
        r = service.purchaseCart(user1token.getResult(), new PaymentInformation(false), new SupplyingInformation(true));
        assertTrue(r.isError_occured());
        r = service.purchaseCart(user1token.getResult(), new PaymentInformation(false), new SupplyingInformation(false));
        assertTrue(r.isError_occured());
        Response<ShoppingCartDTO> cartR = service.getCartInfo(user1token.getResult());
        assertEquals(1, cartR.getResult().getBaskets().size());
        assertEquals(service.getPurchaseHistory(user1token.getResult(), "user1").getResult().size(),0);
    }
    @After
    public void tearDown(){

    }

}
