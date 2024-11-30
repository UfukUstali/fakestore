package fakestore.model;

import fakestore.Result;

class Model implements IModel {
    // singleton
    private static final Model instance = new Model();
    private final Fetch fetch = new Fetch("https://dummyjson.com");
    private Cart cart = new Cart(this);
    private double balance;

    private Model() {
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

    public Result<Boolean, String> removeFromCart(Product product, int quantity) {
        return cart.remove(product, quantity);
    }

    public Result<String, String> buy(Product product) {
        return cart.buy(product);
    }

    public Result<Boolean, String> buyAllInCart() {
        return cart.buyAllInCart();
    }

    public double getBalance() {
        return balance;
    }

    void setBalance(double balance) {
        this.balance = balance;
    }

    static Model getInstance() {
        return instance;
    }

    public void setArgs(String[] args) {
        try {
            if (args.length == 0) throw new Exception();
            balance = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            balance = Math.random() * 5000 + 5000;
            System.err.println("Invalid balance argument: " + args[0] + ", using random balance: " + balance);
        } catch (Exception e) {
            balance = Math.random() * 5000 + 5000;
            System.out.println("No balance argument, using random balance: " + balance);
        }
    }

    // reset the model to its initial state when testing
    static Model reset() {
        return new Model().setCart();
    }

    private Model setCart() {
        cart = new Cart(this);
        return this;
    }
}
