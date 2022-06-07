package test.AcceptanceTests;
import main.DTO.BidDTO;
import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Service.IService;
import main.Service.Service;
import main.Stores.Product;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import test.testUtils.testsFactory;

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ATuser2Requirements {

    @Mock
    ISupplying mockSupplyer;
    @Mock
    IPayment mockPayment;
    @Mock
    PaymentInformation mockPaymentInformation;
    @Mock
    SupplyingInformation mockSupplyingInformation;
    Response<String> founder1token, user1token, user2token, owner1token;
    IService service;
    PaymentInformation pi = testsFactory.getSomePI();
    SupplyingInformation si = testsFactory.getSomeSI();
    double CokePrice = 5;
    String bargainedItem = "Pear";
    double bargainedItemPrice = 50;
    String bargainedItem2 = "Airpods";
    double bargainedItemPrice2 = 100;

    String AuctionedItem = "Apple juice";
    double auctionedItemPrice = 50;

    @Before
    public void setUp() {
        mockSupplyer = mock(SupplyingAdapter.class);
        mockPayment = mock(PaymentAdapter.class);
        mockPaymentInformation = mock(PaymentInformation.class);
        mockSupplyingInformation = mock(SupplyingInformation.class);
        when(mockSupplyer.bookDelivery(any(SupplyingInformation.class))).thenReturn(true);
        when(mockPayment.validateCard(any(PaymentInformation.class))).thenReturn(true);
        when(mockSupplyer.supply(any(SupplyingInformation.class), any(HashMap.class))).thenReturn(true);
        when(mockPayment.makePayment(any(PaymentInformation.class), any(Double.class))).thenReturn(true);

        service = new Service(mockPayment, mockSupplyer);
        founder1token = service.guestConnect();
        user1token = service.guestConnect();
        user2token = service.guestConnect();
        owner1token = service.guestConnect();

        service.register("manager1", "12345678");
        service.register("founder1", "12345678");
        service.register("user1", "12345678");
        service.register("user2", "12345678");
        service.register("owner1", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(user1token.getResult(), "user1", "12345678");
        service.login(user2token.getResult(), "user2", "12345678");
        service.login(owner1token.getResult(), "owner1", "12345678");

        service.openStore(founder1token.getResult(), "MyStore1");
        service.appointStoreOwner(founder1token.getResult(), "owner1", "MyStore1");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, CokePrice);
        service.addProductToStore(founder1token.getResult(), "Sprite", "Drinks", null, "tasty drink", "MyStore1", 100, CokePrice);
        service.addProductToStore(founder1token.getResult(), bargainedItem, "Drinks", null, "tasty drink", "MyStore1", 100, bargainedItemPrice);
        service.addProductToStore(founder1token.getResult(), bargainedItem2, "Apple", null, "good", "MyStore1", 100, bargainedItemPrice2);
        service.addProductToStore(founder1token.getResult(), AuctionedItem, "Drinks", null, "tasty drink", "MyStore1", 100, auctionedItemPrice);
        service.addBargainPolicy(founder1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice);
        service.addBargainPolicy(founder1token.getResult(), "MyStore1", bargainedItem2, bargainedItemPrice2);
        service.addAuctionPolicy(founder1token.getResult(), "MyStore1", AuctionedItem, auctionedItemPrice, LocalDate.now().plusDays(5));
    }

    /***
     * use case: Searching for a store req 2.1:
     */
    @Test
    public void SearchingForANoneExistentStore() {
        Response<StoreDTO> r = service.getStoreInfo("NoneExistent store");
        assertTrue(r.isError_occured());
        Assertions.assertNull(r.getResult());
    }

    @Test
    public void SearchingForAStore() {
        Response<StoreDTO> r = service.getStoreInfo("MyStore1");
        assertFalse(r.isError_occured());
        Assertions.assertNotNull(r.getResult());
    }

    /***
     * use case:Searching for a product req 2.2:
     */
    @Test
    public void SearchingForANoneExistingProduct() {
        Response<List<ProductDTO>> r = service.getProductsByInfo("NoneExistent Product", null, null, null, null, null, null);
        assertFalse(r.isError_occured());
        Assertions.assertEquals(r.getResult().size(), 0);
    }

    @Test
    public void SearchingForAProduct() {
        Response<List<ProductDTO>> r = service.getProductsByInfo("Coca Cola", "Drinks", null, null, null, null, null);
        assertFalse(r.isError_occured());
        Assertions.assertEquals(r.getResult().size(), 1);
    }

    /***
     * use case: Adding a Product to the Shopping Cart req 2.3:
     */
    @Test
    public void AddingProductToCart() {
        Response<Boolean> r = service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1);
        Assertions.assertTrue(!r.isError_occured() && r.getResult());
    }

    @Test
    public void AddingProductToCartLargeQuantity() {
        Response<Boolean> r = service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 150);
        assertTrue(r.isError_occured());
    }

    @Test
    public void AddingProductToCartNoneExistingStore() {
        Response<Boolean> r = service.addProductToCart(user1token.getResult(), "NonExistentStore", "Coca Cola", 1);
        assertTrue(r.isError_occured());
    }

    @Test
    public void AddingNonExistingProductToStore() {
        Response<Boolean> r = service.addProductToCart(user1token.getResult(), "MyStore1", "Nonexistent Item", 1);
        Assertions.assertTrue(r.isError_occured());
    }

    /***
     * use case: Inspecting Shopping cart req 2.4:
     */
    @Test
    public void InspectingEmptyCart() {
        Response<ShoppingCartDTO> r = service.getCartInfo(user1token.getResult());
        assertEquals(r.getResult().getBaskets().size(), 0);
    }

    @Test
    public void InspectingShoppingCart() {
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1);
        Response<ShoppingCartDTO> r = service.getCartInfo(user1token.getResult());
        assertEquals(r.getResult().getBaskets().size(), 1);
        assertEquals(r.getResult().getBaskets().get("MyStore1").getProductsQuantity().size(), 1);
    }

    /***
     * use case: Removing product from Shopping cart req 2.4:
     */
    @Test
    public void RemovingProductFromEmptyCart() {
        Response<Boolean> responseRemove = service.RemoveProductFromCart(user1token.getResult(), "MyStore1", "Coca Cola", 5);
        assertTrue(responseRemove.isError_occured());
    }

    @Test
    public void RemoveProductFromCart() {
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 1);
        Response<Boolean> responseRemove = service.RemoveProductFromCart(user1token.getResult(), "MyStore1", "Coca Cola", (int) Math.floor(Math.random() * 10 + 1));
        assertFalse(responseRemove.isError_occured());
        Response<ShoppingCartDTO> responseCart = service.getCartInfo(user1token.getResult());
        assertEquals(responseCart.getResult().getBaskets().size(), 0);

    }

    /***
     * use case: purchasing a cart with validated payment and supplier req 2.5:
     */
    @Test
    public void PurchaseEmptyCart() {
        verify(mockPayment, times(0)).makePayment(any(PaymentInformation.class), any(Double.class));
        verify(mockSupplyer, times(0)).supply(any(SupplyingInformation.class), any(HashMap.class));
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), pi, si);
        assertTrue(r.isError_occured());
    }

    @Test
    public void PurchaseCartMakesPaymentAndSupply() {
        int amount = 5;
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 5);
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), pi, si);
        assertFalse(r.isError_occured());
        verify(mockPayment, times(1)).makePayment(pi, amount * CokePrice);
        verify(mockSupplyer, times(1)).supply(any(SupplyingInformation.class), anyMapOf(Product.class, Integer.class));
    }

    @Test
    public void PurchaseCartClearsBasket() {
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 5);
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), pi, si);
        assertFalse(r.isError_occured());
        Response<ShoppingCartDTO> cartR = service.getCartInfo(user1token.getResult());
        assertEquals(0, cartR.getResult().getBaskets().size());
    }

    @Test
    public void PurchaseCartAddsHistory() {
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 5);
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), pi, si);
        assertFalse(r.isError_occured());
        assertEquals(service.getPurchaseHistory(user1token.getResult(), "user1").getResult().size(), 1);
    }

    @Test
    public void PurchaseCartNotifies() {
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 5);
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), pi, si);
        assertFalse(r.isError_occured());
        assertEquals(1, service.receiveMessages(owner1token.getResult()).getResult().size());
    }

    @Test
    public void PurchaseCartFailedSupply() {
        when(mockSupplyer.supply(any(SupplyingInformation.class), any(HashMap.class))).thenReturn(false);
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 5);
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), pi, si);
        assertTrue(r.isError_occured());
        verify(mockPayment, times(1)).abort(pi);
    }

    @Test
    public void PurchaseCartFailedPayment() {
        when(mockPayment.makePayment(any(PaymentInformation.class), any(Double.class))).thenReturn(false);
        service.addProductToCart(user1token.getResult(), "MyStore1", "Coca Cola", 5);
        Response<Boolean> r = service.purchaseCart(user1token.getResult(), pi, si);
        assertTrue(r.isError_occured());
        verify(mockSupplyer, times(1)).abort(si);
    }

    @Test
    public void SetProductForRaffle() {
        Assertions.assertFalse(service.addRafflePolicy(owner1token.getResult(), "MyStore1", "Sprite", 50.0).isError_occured());
    }

    @Test
    public void SetProductForRaffleNoPermissions() {
        Assertions.assertTrue(service.addRafflePolicy(user1token.getResult(), "MyStore1", "Sprite", 50.0).isError_occured());
    }

    @Test
    public void BuyRafflePartly() {
        double pay = 25;
        Assertions.assertFalse(service.addRafflePolicy(owner1token.getResult(), "MyStore1", "Sprite", 50.0).isError_occured());
        Assertions.assertFalse(service.addProductToCart(user2token.getResult(), "MyStore1", "Sprite", 1).isError_occured());
        Assertions.assertFalse(service.setCostumPriceForProductInCart(user2token.getResult(), "MyStore1", "Sprite", pay).isError_occured());
        Assertions.assertFalse(service.purchaseCart(user2token.getResult(), pi, si).isError_occured());
        verify(mockPayment, times(1)).makePayment(pi, pay);
        verify(mockSupplyer, times(0)).supply(any(SupplyingInformation.class), anyMapOf(Product.class, Integer.class));
    }

    @Test
    public void cantPayOverRaffleAmount() {
        double fullPrice = 50.0;
        double pay = fullPrice + 1;
        service.addRafflePolicy(owner1token.getResult(), "MyStore1", "Sprite", fullPrice);
        service.addProductToCart(user2token.getResult(), "MyStore1", "Sprite", 1);
        Assertions.assertTrue(service.setCostumPriceForProductInCart(user2token.getResult(), "MyStore1", "Sprite", pay).isError_occured());
    }

    @Test
    public void BuyRaffleFully() {
        double fullPrice = 50.0;
        double pay1 = 15;
        double pay2 = fullPrice - pay1;
        Assertions.assertFalse(service.addRafflePolicy(owner1token.getResult(), "MyStore1", "Sprite", fullPrice).isError_occured());
        service.addProductToCart(user2token.getResult(), "MyStore1", "Sprite", 1);
        service.addProductToCart(user1token.getResult(), "MyStore1", "Sprite", 1);
        service.setCostumPriceForProductInCart(user2token.getResult(), "MyStore1", "Sprite", pay2);
        service.setCostumPriceForProductInCart(user1token.getResult(), "MyStore1", "Sprite", pay1);
        service.purchaseCart(user2token.getResult(), pi, si);
        service.purchaseCart(user1token.getResult(), pi, si);
        verify(mockPayment, times(1)).makePayment(pi, pay1);
        verify(mockPayment, times(1)).makePayment(pi, pay2);
        verify(mockSupplyer, times(1)).supply(any(SupplyingInformation.class), anyMapOf(Product.class, Integer.class));
    }

    @Test
    public void biddingOnBargain(){
        Assertions.assertFalse(service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation).isError_occured());
        Assertions.assertEquals(1, service.getUserBids(founder1token.getResult(), "MyStore1", bargainedItem).getResult().size());
    }

//    @Test
//    public void biddingOnBargainNotifiesStaff(){
//        service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation);
//        Assertions.assertEquals(1, service.receiveMessages(founder1token.getResult()).getResult().size());
//        Assertions.assertEquals(1, service.receiveMessages(owner1token.getResult()).getResult().size());
//    }

    @Test
    public void founderApprovesBargainOffer(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation).isError_occured();
        Assertions.assertFalse(service.ApproveBid(founder1token.getResult(), "MyStore1", bargainedItem, "user1").isError_occured());
    }

    @Test
    public void OwnerCanApproveOffer(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation).isError_occured();
        Assertions.assertFalse(service.ApproveBid(owner1token.getResult(), "MyStore1", bargainedItem, "user1").isError_occured());
    }

    @Test
    public void allStaffApprovesBargain(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation).isError_occured();
        assertFalse(service.ApproveBid(owner1token.getResult(), "MyStore1", bargainedItem, "user1").isError_occured());
        assertFalse(service.ApproveBid(founder1token.getResult(), "MyStore1", bargainedItem, "user1").isError_occured());
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, bargainedItemPrice + 1);
        verify(mockSupplyer, times(1)).supply(any(SupplyingInformation.class), any(HashMap.class));
        Response<List<String>> history=service.getPurchaseHistory(user1token.getResult(),"user1");
        Response<List<String>> store_history=service.getStorePurchaseHistory(founder1token.getResult(),"MyStore1");
        assertFalse(history.isError_occured());
        assertEquals(1, history.getResult().size());
        assertFalse(store_history.isError_occured());
        assertEquals(1, store_history.getResult().size());
    }

    @Test
    public void NotallStaffApprovesBargain(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation).isError_occured();
        service.ApproveBid(owner1token.getResult(), "MyStore1", bargainedItem, "user1");
        verify(mockPayment, times(0)).makePayment(mockPaymentInformation, bargainedItemPrice + 1);
        verify(mockSupplyer, times(0)).supply(any(SupplyingInformation.class), any(HashMap.class));
    }

    @Test
    public void BargainDeclinedRemovesBid(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation).isError_occured();
        service.ApproveBid(owner1token.getResult(), "MyStore1", bargainedItem, "user1");
        service.DeclineBid(founder1token.getResult(), "MyStore1", bargainedItem, "user1");
        Assertions.assertEquals(0, service.getUserBids(founder1token.getResult(), "MyStore1", bargainedItem).getResult().size());
    }

    @Test
    public void bargainDeclinedNotifiesUser(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, bargainedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation).isError_occured();
        service.ApproveBid(owner1token.getResult(), "MyStore1", bargainedItem, "user1");
        service.DeclineBid(founder1token.getResult(), "MyStore1", bargainedItem, "user1");
        Assertions.assertEquals(1, service.receiveMessages(user1token.getResult()).getResult().size());
    }

    @Test
    public void biddingOnBargainTwiceUpdatesLastBargain(){
        Double costumePrice1 = bargainedItemPrice + 1;
        Double costumePrice2 = bargainedItemPrice + 3;
        Assertions.assertFalse(service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, costumePrice1 , mockPaymentInformation, mockSupplyingInformation).isError_occured());
        Assertions.assertFalse(service.bidOnProduct(user1token.getResult(), "MyStore1", bargainedItem, costumePrice2 , mockPaymentInformation, mockSupplyingInformation).isError_occured());
        List<BidDTO> bids = service.getUserBids(founder1token.getResult(), "MyStore1", bargainedItem).getResult();
        Assertions.assertEquals(bids.get(0).getCostumePrice(), costumePrice2);
        Assertions.assertEquals(bids.size(), 1);
    }

    @Test
    public void biddingOnAuction(){
        Assertions.assertFalse(service.bidOnProduct(user1token.getResult(), "MyStore1", AuctionedItem, auctionedItemPrice , mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void CantBidOnAuctionSamePrice(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", AuctionedItem, auctionedItemPrice , mockPaymentInformation, mockSupplyingInformation);
        Assertions.assertTrue(service.bidOnProduct(user1token.getResult(), "MyStore1", AuctionedItem, auctionedItemPrice , mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void CantBidOnAuctionLowerPrice(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", AuctionedItem, auctionedItemPrice , mockPaymentInformation, mockSupplyingInformation);
        Assertions.assertTrue(service.bidOnProduct(user1token.getResult(), "MyStore1", AuctionedItem, auctionedItemPrice - 1 , mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void HighestBidOnAuctionCounts(){
        service.bidOnProduct(user1token.getResult(), "MyStore1", AuctionedItem, auctionedItemPrice , mockPaymentInformation, mockSupplyingInformation);
        service.bidOnProduct(user1token.getResult(), "MyStore1", AuctionedItem, auctionedItemPrice + 1, mockPaymentInformation, mockSupplyingInformation);
        Assertions.assertEquals(auctionedItemPrice + 1, service.getUserBids(founder1token.getResult(),"MyStore1", AuctionedItem).getResult().get(0).getCostumePrice());
    }

    @Test
    public void userBidsOnTwoDifferentProducts(){
        assertFalse(service.bidOnProduct(user1token.getResult(),"MyStore1",bargainedItem2,bargainedItemPrice2+1,mockPaymentInformation,mockSupplyingInformation).isError_occured());
        assertFalse(service.bidOnProduct(user1token.getResult(),"MyStore1",bargainedItem,bargainedItemPrice+1,mockPaymentInformation,mockSupplyingInformation).isError_occured());
        assertEquals(1,service.getUserBids(founder1token.getResult(),"MyStore1",bargainedItem).getResult().size());
        assertEquals(1,service.getUserBids(founder1token.getResult(),"MyStore1",bargainedItem2).getResult().size());
    }

    @Test
    public void userBidsOnTwoDifferentProductsAndApprovedOneOfThem(){
        assertFalse(service.bidOnProduct(user1token.getResult(),"MyStore1",bargainedItem2,bargainedItemPrice2+1,mockPaymentInformation,mockSupplyingInformation).isError_occured());
        assertFalse(service.bidOnProduct(user1token.getResult(),"MyStore1",bargainedItem,bargainedItemPrice+1,mockPaymentInformation,mockSupplyingInformation).isError_occured());
        assertFalse(service.ApproveBid(owner1token.getResult(), "MyStore1", bargainedItem, "user1").isError_occured());
        assertFalse(service.ApproveBid(founder1token.getResult(), "MyStore1", bargainedItem, "user1").isError_occured());
        assertEquals(1,service.getUserBids(founder1token.getResult(),"MyStore1",bargainedItem2).getResult().size());


    }

    @After
    public void tearDown() {

    }
}
