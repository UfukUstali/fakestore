package fakestore.controller;

import com.google.gson.JsonObject;
import fakestore.view.IView;

import java.util.Map;

class Router {
    private static final Router instance = new Router();
    private IView view;
    private String currentPath;
    private JsonObject currentArgs;
    private Runnable cleanUp = () -> {};
    private Map<String, IRoute> routes;

    private Router() {
    }

    protected void setView(IView view) {
        this.view = view;
        routes = view.getRoutes();
    }

    protected static Router getInstance() {
        return instance;
    }

    protected void navigateTo(String path) {
        cleanUp.run();
        currentPath = path;
        currentArgs = null;
        cleanUp = routes.get(path).navigateTo(new JsonObject());
    }

    protected void navigateTo(String path, JsonObject args) {
        cleanUp.run();
        currentPath = path;
        currentArgs = args;
        cleanUp = routes.get(path).navigateTo(args);
    }

    protected String getCurrentPath() {
        return currentPath;
    }

    protected JsonObject getCurrentArgs() {
        return currentArgs;
    }
}
