package fakestore.model;

import fakestore.Result;

import java.util.*;

class Cart implements ICart {
    private final Model model;
    private final Set<Product> productsInCart = new HashSet<>();
    private final int[] cartQuantityMap = new int[100];
    private final int[] boughtQuantityMap = new int[100];
    private double total;

    Cart(Model model) {
        this.model = model;
    }

    Result<String, String> add(Product product) {
        return add(product, 1);
    }

    Result<String, String> add(Product product, int quantity) {
        if (quantity < 1) return Result.error("Quantity must be greater than 0");
        int newQuantity = cartQuantityMap[product.id() - 1] + boughtQuantityMap[product.id() - 1] + quantity;
        if (newQuantity > product.stock()) return Result.error("Not enough stock");
        total += product.price() * quantity;
        cartQuantityMap[product.id() - 1] = newQuantity;
        productsInCart.add(product);
        if (total > model.getBalance()) return Result.ok("Balance is overdrawn");
        return Result.ok("ok");
    }

    Result<Boolean, String> remove(Product product, int quantity) {
        if (quantity < 1) return Result.error("Quantity must be greater than 0");
        int quantityInCart = cartQuantityMap[product.id() - 1];
        if (quantityInCart == 0) return Result.error("No such product in cart");
        int newQuantity = Math.max(quantityInCart - quantity, 0);
        cartQuantityMap[product.id() - 1] = newQuantity;

        if (newQuantity > 0) total -= product.price() * quantity;
        else {
            // newQuantity is always 0
            total -= product.price() * quantityInCart;
            productsInCart.remove(product);
        }
        return Result.ok(true);
    }

    Result<String, String> buy(Product product) {
        int quantity = 1;
        int cartQuantity = cartQuantityMap[product.id() - 1];
        int boughtQuantity = boughtQuantityMap[product.id() - 1];
        // check if there is enough stock
        if (boughtQuantity > product.stock() || quantity + boughtQuantity > product.stock())
            return Result.error("Not enough stock");
        // check if there is enough balance
        if (model.getBalance() < product.price() * quantity) return Result.error("Not enough balance");
        // check if the quantity in the cart is blocking the purchase
        Result<String, String> buyResult;
        if (quantity + cartQuantity + boughtQuantity > product.stock()) {
            // is always negative
            int toRemoveFromCart = -(product.stock() - (boughtQuantity + cartQuantity + quantity));
            // remove from cart and then buy
            remove(product, toRemoveFromCart);
            buyResult = Result.ok("Removed " + toRemoveFromCart + (toRemoveFromCart > 1 ? " items " : " item ") + "from cart");
        } else buyResult = Result.ok("ok");
        boughtQuantityMap[product.id() - 1] += quantity;
        model.setBalance(model.getBalance() - product.price() * quantity);
        return buyResult;
    }

    Result<Boolean, String> buyAllInCart() {
        if (productsInCart.isEmpty()) return Result.error("Cart is empty");
        if (model.getBalance() < total) return Result.error("Not enough balance");
        for (Product product : productsInCart) {
            int quantityInCart = cartQuantityMap[product.id() - 1];
            boughtQuantityMap[product.id() - 1] += quantityInCart;
            total -= product.price() * quantityInCart;
            model.setBalance(model.getBalance() - product.price() * quantityInCart);
        }
        clear();
        return Result.ok(true);
    }

    private void clear() {
        productsInCart.clear();
        Arrays.fill(cartQuantityMap, 0);
    }

    @Override
    public Product[] getProductsInCart() {
        return this.productsInCart.toArray(Product[]::new);
    }

    @Override
    public int getQuantityInCart(Product product) {
        return cartQuantityMap[product.id() - 1];
    }

    @Override
    public int getBoughtQuantity(Product product) {
        return boughtQuantityMap[product.id() - 1];
    }

    @Override
    public double getTotal() {
        return total;
    }
}
