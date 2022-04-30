package UnitTests;

import main.NotificationBus;
import main.Stores.IStore;
import main.Stores.Product;
import main.Users.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    private Product p1, p2, p3;

    @BeforeEach
    void setUp() {
        List<String> keyWordsCola = new LinkedList<>();
        keyWordsCola.add("Cola");
        keyWordsCola.add("Coca");
        keyWordsCola.add("Coke");

        List<String> keyWordsPizza = new LinkedList<>();
        keyWordsPizza.add("Pizza");
        keyWordsPizza.add("Cheesy");
        keyWordsPizza.add("Tasty");

        p1 = new Product(null,"Coca Cola", "Drink", keyWordsCola, "So good", 50, 6);
        p2 = new Product(null,"Pizza", "Food", keyWordsPizza, "Cheesy", 100, 25);
    }

    @Test
    void setProperties()
    {
        assertTrue(p1.getName().equals("Coca Cola"));
        assertTrue(p1.getCategory().equals("Drink"));
        assertTrue(p1.getDescription().equals("So good"));
        assertTrue(p1.getQuantity() == 50);
        assertTrue(p1.getPrice()==6);

        List<String> keyWordsFanta = new LinkedList<>();
        keyWordsFanta.add("Fanta");
        keyWordsFanta.add("Kinley");
        keyWordsFanta.add("Orange");
        p1.setProperties("Fanta", "Drink", keyWordsFanta, "Yummy", 60, 5);

        assertTrue(p1.getName().equals("Fanta"));
        assertTrue(p1.getCategory().equals("Drink"));
        assertTrue(p1.getDescription().equals("Yummy"));
        assertEquals(p1.getQuantity(), 60);
        assertEquals(p1.getPrice(),5);
    }

    @Test
    void subtractQuantity()
    {
        assertEquals(p2.getQuantity(), 100);
        assertThrows(IllegalArgumentException.class, ()->p2.subtractQuantity(101));
        assertDoesNotThrow(()->p2.subtractQuantity(50));
        assertEquals(p2.getQuantity(), 50);
        assertThrows(IllegalArgumentException.class, ()->p2.subtractQuantity(51));
        assertDoesNotThrow(()->p2.subtractQuantity(50));
        assertEquals(p2.getQuantity(), 0);
    }
}
