package test.AcceptanceTests;


import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.testUtils.testsFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


public class AT_Req2_3 {

    private Response<String> guest1Token, memberNoCartToken, memberWithCartToken, founder1token, founder2token, loggedOutMember, founder3token, memberBoughtCola, memberToChange1, memberToChange2, bloggerMember;
    private IService service;
    String longString;
    int threadCount;

    //===========================================Setup========================================


    @Before
    public void setUp() throws Exception {
        longString = "";
        for(int i=0; i< 502; i++)
        {
            longString = longString + "a";
        }
        threadCount = 10000;
        service = new Service(testsFactory.alwaysSuccessPayment(), testsFactory.alwaysSuccessSupplyer());
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
        service.purchaseCart(memberBoughtCola.getResult(), testsFactory.getSomePI(), testsFactory.getSomeSI());




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
        assertTrue(ShoppingCartMemberWithCart.getBaskets().isEmpty());
        assertTrue(service.isMemberLoggedOut(memberWithCartToken.getResult()).getResult());

    }

    /***
     * use case: openStore req 3.2 :
     */
    @Test
    public void testOpenStoreGuestFail() {
        //Guest open store - fail
        assertTrue(service.openStore(guest1Token.getResult(), "GuestStore").isError_occured());
    }

    /***
     * use case: openStore req 3.2 :
     */
    @Test
    public void testOpenStoreMemberNotLoggedInFail() {
        //Member not logged in opens store - fail
        assertTrue(service.openStore(loggedOutMember.getResult(), "Abcde").isError_occured());
    }

    /***
     * use case: openStore req 3.2 :
     */
    @Test
    public void testOpenStoreInvalidArgsFail() {
        //Member opens store invalid attributes - fail
        assertTrue(service.openStore(guest1Token.getResult(), "").isError_occured()); //Empty store name
        assertTrue(service.openStore(guest1Token.getResult(), "MyStore1").isError_occured()); //Store name already exists
    }

    /***
     * use case: openStore req 3.2 :
     */

    @Test
    public void testOpenStoreConcurrentSameName () throws InterruptedException{
        AtomicInteger successCounter = new AtomicInteger(0);
        Runnable openStoreExecution = () -> {
            Response<Boolean> response = service.openStore(founder1token.getResult(),"TestStore1");
            if(!response.isError_occured()) {
                successCounter.getAndIncrement();
            }
        };
        Thread[] openStoreThreads = new Thread[threadCount];
        for(int i=0;i<threadCount;i++) {
            openStoreThreads[i] = new Thread(openStoreExecution);
        }
        for(int i=0;i<threadCount;i++) {
            openStoreThreads[i].start();
        }
        for(int i=0;i<threadCount;i++) {
            openStoreThreads[i].join();
        }
        assertEquals(1,successCounter.get());
    }

    /***
     * use case: openStore req 3.2 :
     */
    @Test
    public void testOpenStoreSuccess() {
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
    public void testProductReviewLongReviewFail() {
        //Member write 500+ character review
        assertTrue(service.writeProductReview(memberBoughtCola.getResult(),"Coca Cola", "MyStore1",longString, 5).isError_occured());
    }

    /***
     * use case: productReview req 3.3, 3.4:
     */
    @Test
    public void testProductReviewDidntBuyFail() {
        //Member Writes product review on product he didnt buy - fail
        assertTrue(service.writeProductReview(memberNoCartToken.getResult(),"Coca Cola", "MyStore1", "Taam Hahaim", 5).isError_occured());
    }

    /***
     * use case: productReview req 3.3, 3.4:
     */
    @Test
    public void testProductReviewSuccess()
    {
        //Member writes product review - success
        assertFalse(service.writeProductReview(memberBoughtCola.getResult(), "Coca Cola", "MyStore1", "Taam Hahaim", 5).isError_occured());
    }

    /***
     * use case: storeReview req 3.4 :
     */
    @Test
    public void testStoreReviewGuestFail()
    {
        //guest can't write a review.
        assertTrue(service.writeStoreReview(guest1Token.getResult(),"MyStore1", "OMG pepsi is so much better!", 3).isError_occured());
    }

    /***
     * use case: storeReview req 3.4 :
     */
    @Test
    public void testStoreReview()
    {
        //only someone who bought from the store can write a review.
        assertTrue(service.writeStoreReview(bloggerMember.getResult(),"MyStore1", "OMG pepsi is so much better!", 3).isError_occured());
        service.addProductToCart(bloggerMember.getResult(), "MyStore1", "Coca Cola", 1);
        service.purchaseCart(bloggerMember.getResult(),testsFactory.getSomePI(), testsFactory.getSomeSI());
        assertTrue(service.writeStoreReview(bloggerMember.getResult(),"MyStore1", "OMG pepsi is so much better!", -2).isError_occured());
        assertFalse(service.writeStoreReview(bloggerMember.getResult(),"MyStore1", "OMG pepsi is so much better!", 3).isError_occured());
        //same user can't write a review twice to the same store.
        assertTrue(service.writeStoreReview(bloggerMember.getResult(),"MyStore1", "OMG pepsi is so much better!", 3).isError_occured());

    }

    /***
     * use case: sendQuestionsToStore req 3.5 :
     */
    @Test
    public void testSendQuestionsToStoreInvalidMessageFail()
    {
        //Invalid message content - fail
        assertTrue(service.sendQuestionsToStore(memberWithCartToken.getResult(),"MyStore1", "").isError_occured());
    }

    /***
     * use case: sendQuestionsToStore req 3.5 :
     */
    @Test
    public void testSendQuestionsToStoreStoreDoesntExistFail()
    {
        //Store doesnt exist - fail
        assertTrue(service.sendQuestionsToStore(memberWithCartToken.getResult(),"NoSuchStore", "Such a great and valid message").isError_occured());
    }

    /***
     * use case: sendQuestionsToStore req 3.5 :
     */
    @Test
    public void testSendQuestionsToStoreSuccess()
    {
        //Sends to store - success
        assertFalse(service.sendQuestionsToStore(memberWithCartToken.getResult(),"MyStore1", "Such a great and valid message").isError_occured());
    }

    /***
     * use case: sendComplaintToAdmin req 3.6 :
     */
    @Test
    public void testSendComplaintInvalidMessageFail()
    {
        //Invalid message - fail
        assertTrue(service.sendComplaint(memberBoughtCola.getResult(),"").isError_occured());
    }

    /***
     * use case: sendComplaintToAdmin req 3.6 :
     */
    @Test
    public void testSendComplaintNoPurchaseHistoryFail()
    {
        //No purchase history - fail
        assertTrue(service.sendComplaint(memberNoCartToken.getResult(), "This is wrong dammit!").isError_occured());
    }

    /***
     * use case: sendComplaintToAdmin req 3.6 :
     */
    @Test
    public void testSendComplaintSuccess()
    {
        //Sends complaint to admin - success
        assertFalse(service.sendComplaint(memberBoughtCola.getResult(), "This is wrong dammit!").isError_occured());
    }

    /***
     * use case: purchaseHistory req 3.7 :
     */
    @Test
    public void testPurchaseHistoryNotLoggedInFail()
    {
        //Member is not logged in - fail
        assertTrue(service.getPurchaseHistory(loggedOutMember.getResult(),"notLoggedMember").isError_occured());
    }

    /***
     * use case: purchaseHistory req 3.7 :
     */
    @Test
    public void testPurchaseHistoryEmptyStoresSuccess() {
        //Empty history - success
        Response<List<String>> emptyHistory = service.getPurchaseHistory(memberNoCartToken.getResult(), "memberNoCart");
        assertFalse(emptyHistory.isError_occured());
        assertTrue(emptyHistory.getResult().isEmpty());
    }

    /***
     * use case: purchaseHistory req 3.7 :
     */
    @Test
    public void testPurchaseHistoryNonEmptyStoresSuccess()
    {
        //Non- empty history - success
        Response<List<String>> nonEmptyHistory = service.getPurchaseHistory(memberBoughtCola.getResult(), "boughtCola");
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
    public void testAddSecurityQuestionEmptyQuestionFail()
    {
        //Empty question - fail
        assertTrue(service.addSecurityQuestion(memberNoCartToken.getResult(), "", "Answer").isError_occured());
    }

    /***
     * use case: addSecurityQuestion req 3.9 :
     */
    @Test
    public void testAddSecurityQuestionEmptyAnswerFail()
    {
        //Empty answer - fail
        assertTrue(service.addSecurityQuestion(memberNoCartToken.getResult(), "Quesion", "").isError_occured());
    }

    /***
     * use case: addSecurityQuestion req 3.9 :
     */
    @Test
    public void testAddSecurityQuestionGuestFail()
    {
        //Guest tries to add security question - fail
        assertTrue(service.addSecurityQuestion(guest1Token.getResult(),"Question", "Answer").isError_occured());
    }

    /***
     * use case: addSecurityQuestion req 3.9 :
     */
    @Test
    public void testAddSecurityQuestionSuccess()
    {
        //Sends non empty question and answer - success
        assertFalse(service.addSecurityQuestion(memberNoCartToken.getResult(), "Question", "Answer").isError_occured());
    }

    /**
     * Concurrency Add store with the same name by 2 users
     */
    @Test
    public void concurrentAddStoreWithSameName() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Runnable founder1AddStore = () -> {
            Response<Boolean> resp = service.openStore(founder1token.getResult(), "SpecialStore");
            if (!resp.isError_occured())
                counter.incrementAndGet();
        };

        Runnable founder2AddStore = () -> {
            Response<Boolean> resp = service.openStore(founder2token.getResult(), "SpecialStore");
            if (!resp.isError_occured())
                counter.incrementAndGet();
        };

        Thread founder1AddStoreThread = new Thread(founder1AddStore);
        Thread founder2AddStoreThread = new Thread(founder2AddStore);

        founder1AddStoreThread.start();
        founder2AddStoreThread.start();

        founder1AddStoreThread.join();
        founder2AddStoreThread.join();
        assertEquals(1, counter.get());
    }



    //===========================================Teardown========================================
    @After
    public void tearDown() throws Exception {
        service= new Service(testsFactory.alwaysSuccessPayment(), testsFactory.alwaysSuccessSupplyer());
    }
}
