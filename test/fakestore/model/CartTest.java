package fakestore.model;

import fakestore.EResult;
import fakestore.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {
    private Model model;

    @BeforeEach
    void setUp() {
        model = Model.reset();
        model.setArgs(new String[]{"1000"});
    }

    @Test
    void addProductToCart() {
        Product product = new Product(1, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        Result<String, String> result = model.addToCart(product);
        assertEquals(EResult.OK, result.status());
        assertEquals(1, model.getCart().getQuantityInCart(product));
    }

    @Test
    void addProductToCartWithQuantity() {
        Product product = new Product(1, "Test", "Test", 10, 1, .23f, 6, "Test", "Test", "Test", new String[]{"Test"});
        Result<String, String> result = model.addToCart(product, 5);
        assertEquals(EResult.OK, result.status());
        assertEquals(5, model.getCart().getQuantityInCart(product));
    }

    @Test
    void addProductToCartExceedingStock() {
        Product product = new Product(1, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        Result<String, String> result = model.addToCart(product, 15);
        assertEquals(EResult.ERROR, result.status());
    }

    @Test
    void removeProductFromCartWithQuantity() {
        Product product = new Product(1, "Test", "Test", 10, 1, .23f, 6, "Test", "Test", "Test", new String[]{"Test"});
        model.addToCart(product, 5);
        Result<Boolean, String> result = model.removeFromCart(product, 3);
        assertEquals(EResult.OK, result.status());
        assertEquals(2, model.getCart().getQuantityInCart(product));
    }

    @Test
    void removeProductFromCartExceedingQuantity() {
        Product product = new Product(1, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        model.addToCart(product, 5);
        Result<Boolean, String> result = model.removeFromCart(product, 6);
        assertEquals(EResult.ERROR, result.status());
    }

    @Test
    void buyProductExceedingCartQuantity() {
        Product product = new Product(1, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        model.addToCart(product, 4);
        Result<String, String> bought = model.buy(product);
        assertEquals(EResult.OK, bought.status());
    }

    @Test
    void buyAllProductsInCart() {
        Product product1 = new Product(1, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        Product product2 = new Product(2, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        model.addToCart(product1, 5);
        model.addToCart(product2, 3);
        Result<Boolean, String> result = model.buyAllInCart();
        assertEquals(EResult.OK, result.status());
        assertEquals(0, model.getCart().getProductsInCart().length);
    }

    @Test
    void boughtQuantityTest() {
        Product product = new Product(1, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        model.addToCart(product, 4);
        model.buyAllInCart();
        assertEquals(4, model.getCart().getBoughtQuantity(product));
    }

    @Test
    void totalTest() {
        Product product1 = new Product(1, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        Product product2 = new Product(2, "Test", "Test", 20, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        model.addToCart(product1, 4);
        model.addToCart(product2, 3);
        assertEquals(100, model.getCart().getTotal());
    }
}
