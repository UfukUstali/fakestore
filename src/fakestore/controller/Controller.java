package fakestore.controller;

import com.google.gson.JsonObject;
import fakestore.Result;
import fakestore.model.ICart;
import fakestore.model.IResponse;
import fakestore.model.IModel;
import fakestore.model.Product;
import fakestore.view.IView;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class Controller implements IController {
    // singleton
    private static final Controller instance = new Controller();
    private IModel model;
    private final Router router = Router.getInstance();
    private final Set<IClickable> clickable = new HashSet<>();
    private Optional<ITypeControl> typeControl = Optional.empty();

    private Controller() {
    }

// ------------------------------ VIEW ------------------------------

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

    public void mousePressed(int mouseX, int mouseY) {
        deregisterInput();
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

// ------------------------------ MODEL ------------------------------

    public IResponse get(String path) {
        return model.get(path);
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


    public Result<Boolean, String> removeFromCart(Product product, int quantity) {
        return model.removeFromCart(product, quantity);
    }

    public Result<String, String> buy(Product product) {
        return model.buy(product);
    }

    public Result<Boolean, String> buyAllInCart() {
        return model.buyAllInCart();
    }

    public double getBalance() {
        return model.getBalance();
    }


// ------------------------------ CONTROLLER ------------------------------

    static Controller getInstance() {
        return instance;
    }

    public void setView(IView view) {
        router.setView(view);
    }

    public void setModel(IModel model) {
        this.model = model;
    }
}
