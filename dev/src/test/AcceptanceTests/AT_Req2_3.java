package test.AcceptanceTests;


import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;


public class AT_Req2_3 {

    private Response<String> guest1Token, memberNoCartToken, memeberWithCartToken, founder1token, founder2token, loggedOutMember, founder3token, memberBoughtCola;
    private IService service = new Service();


    //===========================================Setup========================================


    @Before
    public void setUp()
    {
        service = new Service();
        guest1Token = service.guestConnect();
        memberNoCartToken = service.guestConnect();
        memeberWithCartToken = service.guestConnect();
        founder1token = service.guestConnect();
        founder2token = service.guestConnect();
        founder3token = service.guestConnect();
        loggedOutMember = service.guestConnect();
        memberBoughtCola = service.guestConnect();


        //Register
        service.register("memberNoCart", "12345678");
        service.register("memberWithCart", "12345678");
        service.register("founder1", "12345678");
        service.register("founder2", "12345678");
        service.register("founder3", "12345678");
        service.register("notLoggedMember", "12345678");
        service.register("boughtCola", "12345678");

        //Login
        service.login(memberNoCartToken.getResult(), "memberNoCart", "12345678");
        service.login(memeberWithCartToken.getResult(), "memberWithCart", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(founder2token.getResult(), "founder2", "12345678");
        service.login(founder3token.getResult(), "founder3", "12345678");
        service.login(memberBoughtCola.getResult(), "boughtCola", "12345678");


        //Stores
        service.openStore(founder1token.getResult(), "MyStore1");
        service.openStore(founder2token.getResult(), "MyStore2");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6);

        //Others
        service.addProductToCart(memeberWithCartToken.getResult(), "MyStore1", "Coca Cola", 2);
        service.addProductToCart(memberBoughtCola.getResult(), "MyStore1", "Coca Cola", 1);
        service.purchaseCart(memberBoughtCola.getResult(),"4580000011112222", 23, 7, 5, 777);




    }


    //===========================================Test========================================


    /***
     * use case: Logout req 3.1 :
     */
    @Test
    public void testLogout()
    {
        //Guest logout - fail
        assertTrue(service.logout(guest1Token.getResult()).isError_occured());

        //Member logout no cart - success
        assertFalse(service.logout(memberNoCartToken.getResult()).isError_occured());
        ShoppingCartDTO shoppingCartMemberNoCart = service.getCartInfo(memberNoCartToken.getResult()).getResult();
        assertTrue(shoppingCartMemberNoCart.getBaskets().isEmpty());
        assertFalse(service.isLoggedOut(memberNoCartToken.getResult()).getResult());

        //Member logout with cart - success
        assertFalse(service.logout(memeberWithCartToken.getResult()).isError_occured());
        ShoppingCartDTO ShoppingCartMemberWithCart = service.getCartInfo(memeberWithCartToken.getResult()).getResult();
        assertFalse(ShoppingCartMemberWithCart.getBaskets().isEmpty());
        assertFalse(service.isLoggedOut(memeberWithCartToken.getResult()).getResult());

    }

    /***
     * use case: Logout req 3.2 :
     */
    @Test
    public void testOpenStore()
    {
        //Guest open store - fail
        assertTrue(service.openStore(guest1Token.getResult(), "GuestStore").isError_occured());

        //Member not logged in opens store - fail
        assertTrue(service.openStore(loggedOutMember.getResult(), "Abcde").isError_occured());

        //Member opens store invalid attributes - fail
        assertTrue(service.openStore(guest1Token.getResult(), "").isError_occured()); //Empty store name
        assertTrue(service.openStore(guest1Token.getResult(), "MyStore1").isError_occured()); //Store name already exists

        //Member open store - success
        assertFalse(service.openStore(founder3token.getResult(), "MyStore3").isError_occured());
        Response<StoreDTO> myStore3 = service.getStoreInfo("MyStore3");
        assertFalse(myStore3.isError_occured()); //Check that store actually opened and added
        assertTrue(myStore3.getResult().getIsActive());//Check store is active
    }

    /***
     * use case: Logout req 3.3 :
     */
    @Test
    public void testProductReview()
    {

        String longString  = "";
        for(int i=0; i< 502; i++)
        {
            longString = longString + "a";
        }
        //Member write 500+ character review
        assertTrue(service.writeProductReview(memberBoughtCola.getResult(),"Coca Cola", "MyStore1",longString, 5).isError_occured());
        //Member Writes product review on product he didnt buy - fail
        assertTrue(service.writeProductReview(memberNoCartToken.getResult(),"Coca Cola", "MyStore1", "Taam Hahaim", 5).isError_occured());
        //Member writes product review - success
        assertFalse(service.writeProductReview(memberBoughtCola.getResult(), "Coca Cola", "MyStore1", "Taam Hahaim", 5).isError_occured());
    }







    //===========================================Teardown========================================
    @After
    public void tearDown()
    {
        service = new Service();
    }
}
