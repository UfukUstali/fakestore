package fakestore.controller;

import com.google.gson.JsonObject;
import fakestore.Result;
import fakestore.model.ICart;
import fakestore.model.IResponse;
import fakestore.model.Product;
import fakestore.view.IView;
import fakestore.model.IModel;

public interface IController {

// ------------------------------ VIEW ------------------------------

    /**
     * Navigate to a path
     * <p>
     * This method will navigate to the path and pass an empty JsonObject as the args
     *
     * @param path the path to navigate to
     */
    void navigateTo(String path);

    /**
     * Navigate to a path
     * <p>
     * This method will navigate to the path and pass the args as a JsonObject
     *
     * @param path the path to navigate to
     * @param args the arguments to pass to the path
     */
    void navigateTo(String path, JsonObject args);

    /**
     * Navigate to a path
     * <p>
     * This method will navigate to the path and pass the key and value as a JsonObject
     *
     * @param path  the path to navigate to
     * @param key   the key of the argument
     * @param value the value of the argument
     */
    void navigateTo(String path, String key, String value);

    /**
     * @return the current path that can be used to navigate back to the current page
     */
    String getCurrentPath();

    /**
     * @return the current arguments that can be used to read the arguments passed to the current page
     */
    JsonObject getCurrentArgs();

    /**
     * Should be called when the mouse is clicked
     *
     * @param mouseX x position of the mouse
     * @param mouseY y position of the mouse
     */
    void mousePressed(int mouseX, int mouseY);

    /**
     * Should be called when a key is typed
     *
     * @param key     the key that was typed
     * @param keyCode the code for non ascii keys
     */
    void keyTyped(char key, int keyCode);

    /**
     * Can be called to register a clickable object
     *
     * @param clickable the clickable object to register
     */
    void registerClickable(IClickable clickable);

    /**
     * Should be called to deregister multiple clickable objects
     *
     * @param clickable the clickable objects to deregister
     */
    void deregisterClickable(IClickable... clickable);

    /**
     * Can be called to register an input object
     *
     * @param typeControl the input object to register
     */
    void registerInput(ITypeControl typeControl);

    /**
     * Should be called to deregister the input object when it is destroyed
     */
    void deregisterInput();

// ------------------------------ MODEL ------------------------------

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

// ------------------------------ CONTROLLER ------------------------------

    /**
     * @return the instance of the controller
     */
    static IController getInstance() {
        return Controller.getInstance();
    }

    /**
     * Set the view
     *
     * @param view the view to set
     * @see IView
     */
    void setView(IView view);

    /**
     * Set the model
     *
     * @param model the model to set
     * @see IModel
     */
    void setModel(IModel model);
}
