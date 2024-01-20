package fakestore.model;
import fakestore.EResult;
import fakestore.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {
    private Model model;

    @BeforeEach
    void setUp() {
        model = Model.reset();
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
    void removeProductFromCart() {
        Product product = new Product(1, "Test", "Test", 10, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
        model.addToCart(product);
        Result<Boolean, String> result = model.removeFromCart(product);
        assertEquals(EResult.OK, result.status());
        assertEquals(0, model.getCart().getQuantityInCart(product));
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
        var res = model.addToCart(product, 4);
        assertEquals(EResult.OK, res.status());
        Result<String, String> result = model.buy(product, 4);
        assertEquals(EResult.OK, result.status());
        assertEquals(0, model.getCart().getQuantityInCart(product));
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
}

//class CartTest {
//    private final Model model = Model.getInstance();
//    @Test
//    void getProducts() {
//        ICart cart = model.getCart();
//        assertNotNull(cart.getProductsInCart());
//    }
//
//    @Test
//    void addAndRemove() {
//        Product product = new Product(1, "Test", "Test", 1, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
//        Result<Boolean, String> result = model.addToCart(product);
//        assertEquals(EResult.OK, result.status());
//        result = model.removeFromCart(product);
//        assertEquals(EResult.OK, result.status());
//    }
//
//    @Test
//    void addAndRemoveWithQuantity() {
//        Product product = new Product(1, "Test", "Test", 1, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
//        Result<Boolean, String> result = model.addToCart(product, 2);
//        int quantity = model.getCart().getQuantityInCart(product);
//        assertEquals(EResult.OK, result.status());
//        result = model.removeFromCart(product, quantity);
//        assertEquals(EResult.OK, result.status());
//    }
//
//    @Test
//    void clearCart() {
//        Product product = new Product(1, "Test", "Test", 1, 1, .23f, 4, "Test", "Test", "Test", new String[]{"Test"});
//        Result<Boolean, String> result = model.addToCart(product);
//        assertEquals(EResult.OK, result.status());
//        model.clearCart();
//        assertEquals(0, model.getCart().getProductsInCart().length);
//    }
//}
