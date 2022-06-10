package test.AcceptanceTests;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Service.IService;
import main.Service.Service;
import main.Stores.PurchasePolicy.Discounts.CompositeDiscounts.MaximumCompositeDiscount;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class AT_Req2_4_Discounts_Conditions {
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
    String storeName = "MyStore1";
    String productName1 = "Coke";
    String productName2 = "Milk";
    String category1 = "Drinks";
    String category2 = "Dairy";
    Double productprice1 = 50.0;
    Double productprice2 = 60.0;
    int productAmount = 100;
    String secretCode = "code";
    String wrongSecretCode = "wrongcode";

    @Before
    public void setUp() throws Exception {
        mockSupplyer = mock(SupplyingAdapter.class);
        mockPayment = mock(PaymentAdapter.class);
        mockPaymentInformation = mock(PaymentInformation.class);
        mockSupplyingInformation = mock(SupplyingInformation.class);
        when(mockSupplyer.supply(any(SupplyingInformation.class), any(HashMap.class))).thenReturn(true);
        when(mockPayment.makePayment(any(PaymentInformation.class), any(Double.class))).thenReturn(true);

        service = new Service(mockPayment, mockSupplyer);
        founder1token = service.guestConnect();
        user1token = service.guestConnect();

        service.register("founder1", "12345678");
        service.register("user1", "12345678");

        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(user1token.getResult(), "user1", "12345678");

        service.openStore(founder1token.getResult(), storeName);
        service.addProductToStore(founder1token.getResult(), productName1, category1, null, "tasty drink", storeName, productAmount, productprice1);
        service.addProductToStore(founder1token.getResult(), productName2, category2, null, "Full of calcium", storeName, productAmount, productprice2);
    }

    //---------------------------------CONDITIONS---------------------------------//

    @Test
    public void CreatingBasketValueCondition() {
        Assertions.assertFalse(service.CreateBasketValueCondition(founder1token.getResult(), storeName, 30.0).isError_occured());
    }

    @Test
    public void BasketValueConditionAllows() {
        Double minValue = 10.0;
        Integer basketvalueid = service.CreateBasketValueCondition(founder1token.getResult(), storeName, minValue).getResult();
        service.SetConditionToStore(founder1token.getResult(), storeName, basketvalueid);
        int toadd = (int) ((Math.floor(productprice1 / minValue)) + 1);
        service.addProductToCart(user1token.getResult(), storeName, productName1, toadd);
        Assertions.assertFalse(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void BasketValueConditionDisallows() {
        Double minValue = productprice1 + 1;
        Integer basketvalueid = service.CreateBasketValueCondition(founder1token.getResult(), storeName, minValue).getResult();
        service.SetConditionToStore(founder1token.getResult(), storeName, basketvalueid);
        service.addProductToCart(user1token.getResult(), storeName, productName1, 1);
        Assertions.assertTrue(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void CreatingCategoryAmountCondition() {
        Assertions.assertFalse(service.CreateCategoryAmountCondition(founder1token.getResult(), storeName, "Milk", 3).isError_occured());
    }

    @Test
    public void CategoryAmountConditionAllows() {
        int toadd = 3;
        Integer basketvalueid = service.CreateCategoryAmountCondition(founder1token.getResult(), storeName, category1, toadd).getResult();
        service.SetConditionToStore(founder1token.getResult(), storeName, basketvalueid);
        service.addProductToCart(user1token.getResult(), storeName, productName1, toadd);
        Assertions.assertFalse(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void CategoryAmountConditionDisallows() {
        int toadd = 3;
        Integer basketvalueid = service.CreateCategoryAmountCondition(founder1token.getResult(), storeName, category1, toadd).getResult();
        service.SetConditionToStore(founder1token.getResult(), storeName, basketvalueid);
        service.addProductToCart(user1token.getResult(), storeName, productName1, toadd - 1);
        Assertions.assertTrue(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void CreatingProductAmountCondition() {
        Assertions.assertFalse(service.CreateProductAmountCondition(founder1token.getResult(), storeName, productName1, 3).isError_occured());
    }

    @Test
    public void ProductAmountConditionAllows() {
        int toadd = 3;
        Integer basketvalueid = service.CreateProductAmountCondition(founder1token.getResult(), storeName, productName1, toadd).getResult();
        service.SetConditionToStore(founder1token.getResult(), storeName, basketvalueid);
        service.addProductToCart(user1token.getResult(), storeName, productName1, toadd);
        Assertions.assertFalse(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void ProductAmountConditionDisAllows() {
        int toadd = 3;
        Integer basketvalueid = service.CreateProductAmountCondition(founder1token.getResult(), storeName, productName1, toadd).getResult();
        service.SetConditionToStore(founder1token.getResult(), storeName, basketvalueid);
        service.addProductToCart(user1token.getResult(), storeName, productName1, toadd - 1);
        Assertions.assertTrue(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void CreatingLogicalAndCondition() {
        Response<Integer> cond1 = service.CreateBasketValueCondition(founder1token.getResult(), storeName, 30.0);
        Response<Integer> cond2 = service.CreateBasketValueCondition(founder1token.getResult(), storeName, 30.0);
        List<Integer> lst = new LinkedList<>();
        lst.add(cond1.getResult());
        lst.add(cond2.getResult());
        Assertions.assertFalse(service.CreateLogicalAndCondition(founder1token.getResult(), storeName, lst).isError_occured());
    }


    @Test
    public void CreatingLogicalAndBadConditions() {
        List<Integer> lst = new LinkedList<>();
        lst.add(2);
        Assertions.assertTrue(service.CreateLogicalAndCondition(founder1token.getResult(), storeName, lst).isError_occured());
    }

    @Test
    public void LogicalAndConditionAllows() {
        service.SetConditionToStore(founder1token.getResult(), storeName, generateLogicalAndCondition());
        service.addProductToCart(user1token.getResult(), storeName, productName2, 30);
        Assertions.assertFalse(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void LogicalAndConditionDisallows() {
        service.SetConditionToStore(founder1token.getResult(), storeName, generateLogicalAndCondition());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 30);
        Assertions.assertTrue(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void CreatingLogicalXor() {
        Response<Integer> cond1 = service.CreateBasketValueCondition(founder1token.getResult(), storeName, 30.0);
        Response<Integer> cond2 = service.CreateBasketValueCondition(founder1token.getResult(), storeName, 30.0);
        Assertions.assertFalse(service.CreateLogicalXorCondition(founder1token.getResult(), storeName, cond1.getResult(), cond2.getResult()).isError_occured());
    }

    @Test
    public void CreatingLogicalXorBadConditions() {
        Assertions.assertTrue(service.CreateLogicalXorCondition(founder1token.getResult(), storeName, 0, 0).isError_occured());
    }

    @Test
    public void LogicalXorConditionAllows() {
        service.SetConditionToStore(founder1token.getResult(), storeName, generateLogicalXorrCondition());
        service.addProductToCart(user1token.getResult(), storeName, productName2, 99);
        Assertions.assertFalse(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void LogicalXorConditionDisallows() {
        service.SetConditionToStore(founder1token.getResult(), storeName, generateLogicalXorrCondition());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 99);
        Assertions.assertTrue(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }


    @Test
    public void CreatingLogicalOr() {
        Response<Integer> cond1 = service.CreateBasketValueCondition(founder1token.getResult(), storeName, 30.0);
        Response<Integer> cond2 = service.CreateBasketValueCondition(founder1token.getResult(), storeName, 30.0);
        List<Integer> lst = new LinkedList<>();
        lst.add(cond1.getResult());
        lst.add(cond2.getResult());
        Assertions.assertFalse(service.CreateLogicalOrCondition(founder1token.getResult(), storeName, lst).isError_occured());
    }

    @Test
    public void CreatingLogicalOrBadConditions() {
        List<Integer> lst = new LinkedList<>();
        lst.add(2);
        Assertions.assertTrue(service.CreateLogicalOrCondition(founder1token.getResult(), storeName, lst).isError_occured());
    }

    @Test
    public void LogicalOrConditionAllows() {
        service.SetConditionToStore(founder1token.getResult(), storeName, generateLogicalOrCondition());
        service.addProductToCart(user1token.getResult(), storeName, productName2, 1);
        Assertions.assertFalse(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }

    @Test
    public void LogicalOrConditionDisallows() {
        service.SetConditionToStore(founder1token.getResult(), storeName, generateLogicalOrCondition());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 1);
        Assertions.assertTrue(service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation).isError_occured());
    }


    // basket value >=100 and categroy2 amount >=1
    private int generateLogicalAndCondition() {
        Response<Integer> cond1 = service.CreateBasketValueCondition(founder1token.getResult(), storeName, 100);
        Response<Integer> cond2 = service.CreateCategoryAmountCondition(founder1token.getResult(), storeName, category2, 1);
        List<Integer> lst = new LinkedList<>();
        lst.add(cond1.getResult());
        lst.add(cond2.getResult());
        Response<Integer> res = service.CreateLogicalAndCondition(founder1token.getResult(), storeName, lst);
        Assertions.assertFalse(res.isError_occured());
        return res.getResult();
    }

    // product2 amount > 1 or categroy1 amount >=2
    private int generateLogicalOrCondition() {
        Response<Integer> cond1 = service.CreateProductAmountCondition(founder1token.getResult(), storeName, productName2, 1);
        Response<Integer> cond2 = service.CreateCategoryAmountCondition(founder1token.getResult(), storeName, category1, 2);
        List<Integer> lst = new LinkedList<>();
        lst.add(cond1.getResult());
        lst.add(cond2.getResult());
        Response<Integer> res = service.CreateLogicalOrCondition(founder1token.getResult(), storeName, lst);
        Assertions.assertFalse(res.isError_occured());
        return res.getResult();
    }

    // Basketvalue >= 100 xor categroy1 amount >=2
    private int generateLogicalXorrCondition() {
        Response<Integer> cond1 = service.CreateBasketValueCondition(founder1token.getResult(), storeName, 100);
        Response<Integer> cond2 = service.CreateCategoryAmountCondition(founder1token.getResult(), storeName, category1, 2);
        Response<Integer> res = service.CreateLogicalXorCondition(founder1token.getResult(), storeName, cond1.getResult(), cond2.getResult());
        Assertions.assertFalse(res.isError_occured());
        return res.getResult();
    }

    //---------------------------------DISCOUNTS---------------------------------//

    @Test
    public void CreatingSimpleDiscount() {
        Assertions.assertFalse(service.CreateSimpleDiscount(founder1token.getResult(), storeName, LocalDate.MAX, 0.3).isError_occured());
    }

    @Test
    public void settingSimpleDiscountOnStore() {
        Response<Integer> discount = service.CreateSimpleDiscount(founder1token.getResult(), storeName, LocalDate.MAX, 0.3);
        Assertions.assertFalse(service.SetDiscountToStore(founder1token.getResult(), storeName, discount.getResult()).isError_occured());
    }

    @Test
    public void SimpleDiscountOnStoreWorks() {
        double percent = 0.1;
        Response<Integer> discount = service.CreateSimpleDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent);
        service.SetDiscountToStore(founder1token.getResult(), storeName, discount.getResult());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 10);
        service.addProductToCart(user1token.getResult(), storeName, productName2, 10);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (10 * productprice1 + 10 * productprice2) * (1 - percent));
    }

    @Test
    public void SimpleDiscountOnProductWorks() {
        double percent = 0.1;
        Response<Integer> discount = service.CreateSimpleDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent);
        service.SetDiscountToProduct(founder1token.getResult(), storeName, discount.getResult(), productName1);
        service.addProductToCart(user1token.getResult(), storeName, productName1, 10);
        service.addProductToCart(user1token.getResult(), storeName, productName2, 10);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (10 * productprice1 * (1 - percent) + 10 * productprice2));
    }

    @Test
    public void CreatingSecretDiscount() {
        Assertions.assertFalse(service.CreateSecretDiscount(founder1token.getResult(), storeName, LocalDate.MAX, 0.3, "bla").isError_occured());
    }

    @Test
    public void settingSecretDiscountOnStore() {
        Response<Integer> discount = service.CreateSecretDiscount(founder1token.getResult(), storeName, LocalDate.MAX, 0.3, secretCode);
        Assertions.assertFalse(service.SetDiscountToStore(founder1token.getResult(), storeName, discount.getResult()).isError_occured());
    }

    @Test
    public void SecretDiscountOnStoreWorks() {
        double percent = 0.1;
        Response<Integer> discount = service.CreateSecretDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent, secretCode);
        service.SetDiscountToStore(founder1token.getResult(), storeName, discount.getResult());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 10);
        service.addProductToCart(user1token.getResult(), storeName, productName2, 10);
        service.addDiscountPasswordToBasket(user1token.getResult(), storeName, secretCode);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (10 * productprice1 + 10 * productprice2) * (1 - percent));
    }

    @Test
    public void SecretDiscountOnStoreDisallows() {
        double percent = 0.1;
        Response<Integer> discount = service.CreateSecretDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent, secretCode);
        service.SetDiscountToStore(founder1token.getResult(), storeName, discount.getResult());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 10);
        service.addProductToCart(user1token.getResult(), storeName, productName2, 10);
        service.addDiscountPasswordToBasket(user1token.getResult(), storeName, wrongSecretCode);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (10 * productprice1 + 10 * productprice2));
    }

    @Test
    public void SecretDiscountOnProductWorks() {
        double percent = 0.1;
        Response<Integer> discount = service.CreateSecretDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent, secretCode);
        service.SetDiscountToProduct(founder1token.getResult(), storeName, discount.getResult(), productName1);
        service.addProductToCart(user1token.getResult(), storeName, productName1, 10);
        service.addProductToCart(user1token.getResult(), storeName, productName2, 10);
        service.addDiscountPasswordToBasket(user1token.getResult(), storeName, secretCode);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (10 * productprice1 * (1 - percent) + 10 * productprice2));
    }

    @Test
    public void CreatingConditionalDiscountNoCondition() {
        Assertions.assertTrue(service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, 0.3, 1).isError_occured());
    }

    @Test
    public void CreatingConditionalDiscount() {
        Assertions.assertFalse(service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, 0.3, generateLogicalAndCondition()).isError_occured());
    }

    @Test
    public void settingConditionalDiscountOnStore() {
        Response<Integer> discount = service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, 0.3, generateLogicalAndCondition());
        Assertions.assertFalse(service.SetDiscountToStore(founder1token.getResult(), storeName, discount.getResult()).isError_occured());
    }

    @Test
    public void ConditionalDiscountOnStoreWorks() {
        double percent = 0.1;
        Response<Integer> discount = service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent, generateLogicalAndCondition());
        service.SetDiscountToStore(founder1token.getResult(), storeName, discount.getResult());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 10);
        service.addProductToCart(user1token.getResult(), storeName, productName2, 10);
        service.addDiscountPasswordToBasket(user1token.getResult(), storeName, secretCode);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (10 * productprice1 + 10 * productprice2) * (1 - percent));
    }

    @Test
    public void ConditionalDiscountOnStoreDisallows() {
        double percent = 0.1;
        Response<Integer> discount = service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent, generateLogicalAndCondition());
        service.SetDiscountToStore(founder1token.getResult(), storeName, discount.getResult());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 1);
        service.addDiscountPasswordToBasket(user1token.getResult(), storeName, secretCode);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, productprice1);
    }

    @Test
    public void ConditionalDiscountOnProductWorks() {
        double percent = 0.1;
        Response<Integer> discount = service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent, generateLogicalAndCondition());
        service.SetDiscountToProduct(founder1token.getResult(), storeName, discount.getResult(), productName1);
        service.addProductToCart(user1token.getResult(), storeName, productName1, 10);
        service.addProductToCart(user1token.getResult(), storeName, productName2, 10);
        service.addDiscountPasswordToBasket(user1token.getResult(), storeName, secretCode);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (10 * productprice1 * (1 - percent) + 10 * productprice2));
    }

    @Test
    public void CreatingMaximumCompositeDiscount(){
        Double percent1 = 0.1;
        Double percent2 = 0.2;
        Response<Integer> discount1 = service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent1, generateLogicalAndCondition());
        Response<Integer> discount2 = service.CreateSimpleDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent2);
        LinkedList<Integer> discs = new LinkedList<>();
        discs.add(discount1.getResult());
        discs.add(discount2.getResult());
        Assertions.assertFalse(service.CreateMaximumCompositeDiscount(founder1token.getResult(), storeName, LocalDate.MAX, discs).isError_occured());
    }

    @Test
    public void CreatingMaximumCompositeBadDiscount(){
        LinkedList<Integer> discs = new LinkedList<>();
        discs.add(2);
        Assertions.assertTrue(service.CreateMaximumCompositeDiscount(founder1token.getResult(), storeName, LocalDate.MAX, discs).isError_occured());
    }

    @Test
    public void MaximumCompositeDiscountChoosesMax(){
        service.SetDiscountToStore(founder1token.getResult(), storeName, generateMaximumDiscount());
        service.addProductToCart(user1token.getResult(), storeName, productName2, 30);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (0.8 * 30 * productprice2));
    }

    @Test
    public void MaximumCompositeDiscountChoosesMaxCaseMaxDoesntApply(){
        service.SetDiscountToStore(founder1token.getResult(), storeName, generateMaximumDiscount());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 1);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (0.9 * productprice1));
    }

    @Test
    public void CreatingPlusDiscount(){
        generatePlusDiscount();
    }

    @Test
    public void CreatingPlusDiscountBadDiscountIDS(){
        LinkedList<Integer> discs = new LinkedList<>();
        discs.add(2);
        discs.add(3);
        Assertions.assertTrue( service.CreateMaximumCompositeDiscount(founder1token.getResult(), storeName, LocalDate.MAX, discs).isError_occured());
    }

    @Test
    public void PlusDiscountsWork(){
        service.SetDiscountToStore(founder1token.getResult(), storeName, generatePlusDiscount());
        service.addProductToCart(user1token.getResult(), storeName, productName2, 30);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (0.7 * 30 * productprice2));
    }

    @Test
    public void PlusDiscountsCaseWhereNotAllDiscountsAllow(){
        service.SetDiscountToStore(founder1token.getResult(), storeName, generatePlusDiscount());
        service.addProductToCart(user1token.getResult(), storeName, productName1, 1);
        service.purchaseCart(user1token.getResult(), mockPaymentInformation, mockSupplyingInformation);
        verify(mockPayment, times(1)).makePayment(mockPaymentInformation, (0.9 * productprice1));
    }

    // Max ( 20% for basket value >=100 and categroy2 amount >=1, 10% for everything)
    private int generateMaximumDiscount(){
        Double percent1 = 0.2;
        Double percent2 = 0.1;
        Response<Integer> discount1 = service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent1, generateLogicalAndCondition());
        Response<Integer> discount2 = service.CreateSimpleDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent2);
        LinkedList<Integer> discs = new LinkedList<>();
        discs.add(discount1.getResult());
        discs.add(discount2.getResult());
        Response<Integer> out = service.CreateMaximumCompositeDiscount(founder1token.getResult(), storeName, LocalDate.MAX, discs);
        Assertions.assertFalse(out.isError_occured());
        return out.getResult();
    }
    // Plus ( 20% for basket value >=100 and categroy2 amount >=1, 10% for everything) = 30%
    private int generatePlusDiscount(){
        Double percent1 = 0.2;
        Double percent2 = 0.1;
        Response<Integer> discount1 = service.CreateConditionalDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent1, generateLogicalAndCondition());
        Response<Integer> discount2 = service.CreateSimpleDiscount(founder1token.getResult(), storeName, LocalDate.MAX, percent2);
        LinkedList<Integer> discs = new LinkedList<>();
        discs.add(discount1.getResult());
        discs.add(discount2.getResult());
        Response<Integer> out = service.CreatePlusCompositeDiscount(founder1token.getResult(), storeName, LocalDate.MAX, discs);
        Assertions.assertFalse(out.isError_occured());
        return out.getResult();
    }


}
