package fakestore.controller;

import com.google.gson.JsonObject;

public interface IRoute {
    /**
     * @param args the arguments passed to the route
     * @return a Runnable that is the cleanup function for the route, which will be called by the {@link IController} when the route is exited
     */
    Runnable navigateTo(JsonObject args);
}