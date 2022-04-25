package test.AcceptanceTests;


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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class AT_Req2_3 {

    private Response<String> guest1Token, memberNoCartToken, memberWithCartToken, founder1token, founder2token, loggedOutMember, founder3token, memberBoughtCola, memberToChange1, memberToChange2, bloggerMember;
    private IService service = new Service();
    String longString;

    //===========================================Setup========================================


    @Before
    public void setUp()
    {
        longString = "";
        for(int i=0; i< 502; i++)
        {
            longString = longString + "a";
        }
        service = new Service();
        guest1Token = service.guestConnect();
        memberNoCartToken = service.guestConnect();
        memberWithCartToken = service.guestConnect();
        founder1token = service.guestConnect();
        founder2token = service.guestConnect();
        founder3token = service.guestConnect();
        loggedOutMember = service.guestConnect();
        memberBoughtCola = service.guestConnect();
        memberToChange1 = service.guestConnect();
        memberToChange2 = service.guestConnect();
        bloggerMember = service.guestConnect();


        //Register
        service.register("memberNoCart", "12345678");
        service.register("memberWithCart", "12345678");
        service.register("founder1", "12345678");
        service.register("founder2", "12345678");
        service.register("founder3", "12345678");
        service.register("notLoggedMember", "12345678");
        service.register("boughtCola", "12345678");
        service.register("toChange1", "12345678");
        service.register("toChange2", "12345678");
        service.register("ViralBlogger", "12345678");

        //Login
        service.login(memberNoCartToken.getResult(), "memberNoCart", "12345678");
        service.login(memberWithCartToken.getResult(), "memberWithCart", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(founder2token.getResult(), "founder2", "12345678");
        service.login(founder3token.getResult(), "founder3", "12345678");
        service.login(memberBoughtCola.getResult(), "boughtCola", "12345678");
        service.login(bloggerMember.getResult(), "ViralBlogger", "12345678");

        //Logout

        service.guestDisconnect(loggedOutMember.getResult());

        //Stores
        service.openStore(founder1token.getResult(), "MyStore1");
        service.openStore(founder2token.getResult(), "MyStore2");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6);

        //Others
        service.addProductToCart(memberWithCartToken.getResult(), "MyStore1", "Coca Cola", 2);
        service.addProductToCart(memberBoughtCola.getResult(), "MyStore1", "Coca Cola", 1);
        service.purchaseCart(memberBoughtCola.getResult(),new PaymentInformation(true), new SupplyingInformation(true));




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
        assertTrue(service.isMemberLoggedOut(memberNoCartToken.getResult()).getResult());

        //Member logout with cart - success
        assertFalse(service.logout(memberWithCartToken.getResult()).isError_occured());
        ShoppingCartDTO ShoppingCartMemberWithCart = service.getCartInfo(memberWithCartToken.getResult()).getResult();
        assertFalse(ShoppingCartMemberWithCart.getBaskets().isEmpty());
        assertTrue(service.isMemberLoggedOut(memberWithCartToken.getResult()).getResult());

    }

    /***
     * use case: openStore req 3.2 :
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
     * use case: productReview req 3.3, 3.4:
     */
    @Test
    public void testProductReview()
    {
        //Member write 500+ character review
        assertTrue(service.writeProductReview(memberBoughtCola.getResult(),"Coca Cola", "MyStore1",longString, 5).isError_occured());
        //Member Writes product review on product he didnt buy - fail
        assertTrue(service.writeProductReview(memberNoCartToken.getResult(),"Coca Cola", "MyStore1", "Taam Hahaim", 5).isError_occured());
        //Member writes product review - success
        assertFalse(service.writeProductReview(memberBoughtCola.getResult(), "Coca Cola", "MyStore1", "Taam Hahaim", 5).isError_occured());
    }

    /***
     * use case: storeReview req 3.4 :
     */
    @Test
    public void testStoreReview()
    {
        //guest can't write a review.
        assertTrue(service.writeStoreReview(guest1Token.getResult(),"MyStore1", "OMG pepsi is so much better!", 3).isError_occured());
        //only someone who bought from the store can write a review.
        assertTrue(service.writeStoreReview(bloggerMember.getResult(),"MyStore1", "OMG pepsi is so much better!", 3).isError_occured());
        service.addProductToCart(bloggerMember.getResult(), "MyStore1", "Coca Cola", 1);
        service.purchaseCart(bloggerMember.getResult(),new PaymentInformation(true), new SupplyingInformation(true));
        assertTrue(service.writeStoreReview(bloggerMember.getResult(),"MyStore1", "OMG pepsi is so much better!", -2).isError_occured());
        assertFalse(service.writeStoreReview(bloggerMember.getResult(),"MyStore1", "OMG pepsi is so much better!", 3).isError_occured());
        //same user can't write a review twice to the same store.
        assertTrue(service.writeStoreReview(bloggerMember.getResult(),"MyStore1", "OMG pepsi is so much better!", 3).isError_occured());

    }

    /***
     * use case: sendQuestionsToStore req 3.5 :
     */
    @Test
    public void testSendQuestionsToStore()
    {
        //Invalid message content - fail
        assertTrue(service.sendQuestionsToStore(memberWithCartToken.getResult(),"MyStore1", "").isError_occured());
        //Store doesnt exist - fail
        assertTrue(service.sendQuestionsToStore(memberWithCartToken.getResult(),"NoSuchStore", "Such a great and valid message").isError_occured());
        //Sends to store - success
        assertFalse(service.sendQuestionsToStore(memberWithCartToken.getResult(),"MyStore1", "Such a great and valid message").isError_occured());
    }

    /***
     * use case: sendComplaintToAdmin req 3.6 :
     */
    @Test
    public void testSendComplaint()
    {
        //Invalid message - fail
        assertTrue(service.sendComplaint(memberBoughtCola.getResult(),"").isError_occured());
        //No purchase history - fail
        assertTrue(service.sendComplaint(memberNoCartToken.getResult(), "This is wrong dammit!").isError_occured());
        //Sends complaint to admin - success
        assertFalse(service.sendComplaint(memberBoughtCola.getResult(), "This is wrong dammit!").isError_occured());
    }


    /***
     * use case: purchaseHistory req 3.7 :
     */
    @Test
    public void testPurchaseHistory()
    {
        //Member is not logged in - fail
        assertTrue(service.getPurchaseHistory(loggedOutMember.getResult(),"notLoggedMember").isError_occured());
        //Empty history - success
        Response<List<ShoppingCartDTO>> emptyHistory = service.getPurchaseHistory(memberNoCartToken.getResult(), "memberNoCart");
        assertFalse(emptyHistory.isError_occured());
        assertTrue(emptyHistory.getResult().isEmpty());
        //Non- empty history - success
        Response<List<ShoppingCartDTO>> nonEmptyHistory = service.getPurchaseHistory(memberBoughtCola.getResult(), "boughtCola");
        assertFalse(nonEmptyHistory.isError_occured());
        assertFalse(nonEmptyHistory.getResult().isEmpty());
    }


    /***
     * use case: changeUsername req 3.8 :
     */
    @Test
    public void testChangeUsername()
    {
        //Member not logged in - fail
        assertTrue(service.changeUsername(memberToChange1.getResult(), "validUsername").isError_occured());

        //Member logged in and invalid username - fail
        service.login(memberToChange1.getResult(), "toChange1", "12345678");
        assertTrue(service.changeUsername(memberToChange1.getResult(), "").isError_occured());

        //Member changes to valid userName - success
        Response<Boolean> res = service.changeUsername(memberToChange1.getResult(), "validUsername");
        assertFalse(res.isError_occured());
        service.logout(memberToChange1.getResult());
        assertFalse(service.login(memberToChange1.getResult(),"validUsername", "12345678").isError_occured());

    }

    /***
     * use case: changePassword req 3.8 :
     */
    @Test
    public void testChangePassword()
    {
        //Member not logged in - fail
        assertTrue(service.changePassword(memberToChange2.getResult(),"12345678", "123456789").isError_occured());

        //Member logged in and invalid username - fail
        service.login(memberToChange2.getResult(), "toChange2", "12345678");
        assertTrue(service.changePassword(memberToChange2.getResult(), "12345678","").isError_occured());

        //Member changes to valid userName - success
        Response<Boolean> res = service.changePassword(memberToChange2.getResult(), "12345678","12345678new");
        assertFalse(res.isError_occured());
        service.logout(memberToChange2.getResult());
        assertFalse(service.login(memberToChange2.getResult(),"toChange2", "12345678new" ).isError_occured());
    }

    /***
     * use case: addSecurityQuestion req 3.9 :
     */
    @Test
    public void testAddSecurityQuestion()
    {
        //Empty answer / question - fail
        assertTrue(service.addSecurityQuestion(memberNoCartToken.getResult(), "", "Answer").isError_occured());
        assertTrue(service.addSecurityQuestion(memberNoCartToken.getResult(), "Quesion", "").isError_occured());

        //Guest tries to add security question - fail
        assertTrue(service.addSecurityQuestion(guest1Token.getResult(),"Question", "Answer").isError_occured());

        //Sends non empty question and answer - success
        assertFalse(service.addSecurityQuestion(memberNoCartToken.getResult(), "Question", "Answer").isError_occured());
    }



    //===========================================Teardown========================================
    @After
    public void tearDown()
    {
        service = new Service();
    }
}
