package fakestore.model;

import fakestore.Result;

public class Model {
    private static final Model instance = new Model();
    private final Fetch fetch = new Fetch("https://dummyjson.com");
    private Cart cart = new Cart(this);
    private double balance;

    private Model() {
    }

    public static Model reset() {
        return new Model().setCart();
    }

    public IResponse get(String path) {
        return fetch.fetch(path);
    }

    public ICart getCart() {
        return cart;
    }

    public Result<String, String> addToCart(Product product) {
        return cart.add(product);
    }

    public Result<String, String> addToCart(Product product, int quantity) {
        return cart.add(product, quantity);
    }

    public Result<Boolean, String> removeFromCart(Product product) {
        return cart.remove(product);
    }

    public Result<Boolean, String> removeFromCart(Product product, int quantity) {
        return cart.remove(product, quantity);
    }

    public Result<String, String> buy(Product product) {
        return cart.buy(product);
    }

    public Result<String, String> buy(Product product, int quantity) {
        return cart.buy(product, quantity);
    }

    public Result<Boolean, String> buyAllInCart() {
        return cart.buyAllInCart();
    }

    public void clearCart() {
        cart.clear();
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public static Model getInstance() {
        return instance;
    }

    public void setArgs(String[] args) {
        if (args.length == 0) return;
        balance = Double.parseDouble(args[0]);
    }

    private Model setCart() {
        cart = new Cart(this);
        return this;
    }
}
