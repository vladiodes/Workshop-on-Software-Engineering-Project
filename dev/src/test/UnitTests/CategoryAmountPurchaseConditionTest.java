package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.CategoryAmountPurchaseCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryAmountPurchaseConditionTest {

    @Mock
    ShoppingBasket basketMock;
    CategoryAmountPurchaseCondition subject;
    String category = "category";
    String wrong_category = "_wrong_category";
    int requiredAmount = (int) (Math.random() * 100);
    HashMap<Product, Integer> items;
    @Mock
    Product productMock1;
    @Mock
    Product productMock2;
    @BeforeEach
    void setUp() {
        basketMock = mock(ShoppingBasket.class);
        productMock1 = mock(Product.class);
        productMock2 = mock(Product.class);
        subject = new CategoryAmountPurchaseCondition(category, requiredAmount);
        when(productMock1.getCategory()).thenReturn(category);
        when(productMock2.getCategory()).thenReturn(category);
        items = new HashMap<>();
        items.put(productMock1, requiredAmount / 2);
        items.put(productMock2, requiredAmount / 2 + 1);
        when(basketMock.getProductsAndQuantities()).thenReturn(items);
    }

    @Test
    void passAllows() {
        assertTrue(subject.pass(basketMock));
    }

    @Test
    void passDisallows() {
        when(productMock1.getCategory()).thenReturn(wrong_category);
        assertFalse(subject.pass(basketMock));
    }

    @Test
    void passDisallowsEmptybasket() {
        when(basketMock.getProductsAndQuantities()).thenReturn(new HashMap<>());
        assertFalse(subject.pass(basketMock));
    }
}