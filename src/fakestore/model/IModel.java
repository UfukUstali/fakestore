package fakestore.model;

import fakestore.Result;

public interface IModel {
    /**
     * Get a response from the server
     *
     * @param path the path to make the request to
     * @return the response from the server
     * @see IResponse
     */
    IResponse get(String path);

    /**
     * Get the cart object
     *
     * @return the cart
     * @see ICart
     */
    ICart getCart();

    /**
     * Add one of a product to the cart
     * <p>
     * Result is error if the product is not in stock
     *
     * @param product the product to add
     * @return the result of the operation
     * @see Result
     */
    Result<String, String> addToCart(Product product);

    /**
     * Add n (with n being quantity) amount of a product to the cart
     * <p>
     * Result is error if the product is not in stock
     *
     * @param product  the product to add
     * @param quantity the quantity to add
     * @return the result of the operation
     * @see Result
     */
    Result<String, String> addToCart(Product product, int quantity);

    /**
     * Remove n (with n being quantity) of a product from the cart
     * <p>
     * Result is error if the product is not in the cart
     * <p>
     * If the quantity is greater than the quantity in the cart the product is removed from the cart
     *
     * @param product  the product to remove
     * @param quantity the quantity to remove
     * @return the result of the operation
     * @see Result
     */
    Result<Boolean, String> removeFromCart(Product product, int quantity);

    /**
     * Buy one of a product without using the cart
     * <p>
     * Result is error if the user does not have enough money to buy the product or the product is not in stock
     *
     * @param product the product to buy
     * @return the result of the operation
     * @see Result
     */
    Result<String, String> buy(Product product);

    /**
     * Buy all of the products in the cart
     * <p>
     * Result is error if the user does not have enough money to buy all of the products
     *
     * @return the result of the operation
     * @see Result
     */
    Result<Boolean, String> buyAllInCart();

    /**
     * Get the current balance of the user
     *
     * @return the current balance of the user
     */
    double getBalance();

    /**
     * Get the instance of the model
     *
     * @return the instance of the model
     */
    static IModel getInstance() {
        return Model.getInstance();
    }

    /**
     * Set the arguments for the model
     *
     * @param args the arguments to set
     */
    void setArgs(String[] args);
}
