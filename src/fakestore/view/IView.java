package fakestore.view;

import fakestore.controller.Controller;
import fakestore.controller.IRoute;

import java.util.Map;

public interface IView {
    void setController(Controller controller);
    void setArgs(String[] args);
    Map<String, IRoute> getRoutes();
}
