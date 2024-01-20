package fakestore.controller;

import com.google.gson.JsonObject;
import fakestore.Result;
import fakestore.model.ICart;
import fakestore.model.IResponse;
import fakestore.model.Model;
import fakestore.model.Product;
import fakestore.view.IView;
import fakestore.view.View;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Controller {
    private static final Controller instance = new Controller();
    private Model model;
    private IView view;
    private final Router router = Router.getInstance();
    private final Set<IClickable> clickable = new HashSet<>();
    private Optional<ITypeControl> typeControl = Optional.empty();

    private Controller() {
    }

    public void navigateTo(String path) {
        router.navigateTo(path);
    }

    public void navigateTo(String path, JsonObject args) {
        router.navigateTo(path, args);
    }

    public void navigateTo(String path, String key, String value) {
        JsonObject args = new JsonObject();
        args.addProperty(key, value);
        router.navigateTo(path, args);
    }

    public String getCurrentPath() {
        return router.getCurrentPath();
    }

    public JsonObject getCurrentArgs() {
        return router.getCurrentArgs();
    }

    public IResponse get(String path) {
        return model.get(path);
    }

    public void mousePressed(int mouseX, int mouseY) {
        typeControl.ifPresent(ITypeControl::deregister);
        typeControl = Optional.empty();
        clickable.stream()
                .filter(clickable -> clickable.isInside(mouseX, mouseY))
                .max(Comparator.comparingInt(IClickable::getZ))
                .ifPresent(IClickable::onClick);
    }

    public void keyTyped(char key, int keyCode) {
        typeControl.ifPresent(typeControl -> typeControl.type(key, keyCode));
    }

    public void registerClickable(IClickable clickable) {
        this.clickable.add(clickable);
    }

    public void deregisterClickable(IClickable... clickable) {
        deregisterInput();
        for (IClickable c : clickable) {
            this.clickable.remove(c);
        }
    }

    public void registerInput(ITypeControl typeControl) {
        this.typeControl.ifPresent(ITypeControl::deregister);
        this.typeControl = Optional.of(typeControl);
    }

    public void deregisterInput() {
        typeControl.ifPresent(ITypeControl::deregister);
        typeControl = Optional.empty();
    }





    public static Controller getInstance() {
        return instance;
    }

    public void setView(IView view) {
        this.view = view;
        router.setView(view);
    }

    public ICart getCart() {
        return model.getCart();
    }

    public Result<String, String> addToCart(Product product) {
        return model.addToCart(product);
    }

    public Result<String, String> addToCart(Product product, int quantity) {
        return model.addToCart(product, quantity);
    }

    public Result<Boolean, String> removeFromCart(Product product) {
        return model.removeFromCart(product);
    }

    public Result<Boolean, String> removeFromCart(Product product, int quantity) {
        return model.removeFromCart(product, quantity);
    }

    public Result<String, String> buy(Product product) {
        return model.buy(product);
    }

    public Result<String, String> buy(Product product, int quantity) {
        return model.buy(product, quantity);
    }

    public Result<Boolean, String> buyAllInCart() {
        return model.buyAllInCart();
    }

    public void clearCart() {
        model.clearCart();
    }

    public double getBalance() {
        return model.getBalance();
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
