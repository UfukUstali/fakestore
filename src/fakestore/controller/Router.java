package fakestore.controller;

import com.google.gson.JsonObject;
import fakestore.view.IView;

import java.util.Map;

class Router {
    // singleton
    private static final Router instance = new Router();
    private String currentPath;
    private JsonObject currentArgs;
    private Runnable cleanUp = () -> {
    };
    private Map<String, IRoute> routes;

    private Router() {
    }

    void setView(IView view) {
        routes = view.getRoutes();
    }

    static Router getInstance() {
        return instance;
    }

    void navigateTo(String path) {
        cleanUp.run();
        currentPath = path;
        currentArgs = null;
        cleanUp = routes.get(path).navigateTo(new JsonObject());
    }

    void navigateTo(String path, JsonObject args) {
        cleanUp.run();
        currentPath = path;
        currentArgs = args;
        cleanUp = routes.get(path).navigateTo(args);
    }

    String getCurrentPath() {
        return currentPath;
    }

    JsonObject getCurrentArgs() {
        return currentArgs;
    }
}
