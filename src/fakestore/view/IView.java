package fakestore.view;

import fakestore.controller.IController;
import fakestore.controller.IRoute;
import processing.core.PApplet;

import java.util.Map;

public interface IView {
    /**
     * @return the routes that are available that the controller can navigate to
     */
    Map<String, IRoute> getRoutes();

    /**
     * @return the instance of the view
     */
    static IView getPublicInstance() {
        return View.getInstance();
    }

    /**
     * @return the instance of the view
     */
    static PApplet getPApplet() {
        return View.getInstance();
    }

    /**
     * Sets the controller for the view
     *
     * @param controller the controller to set
     */
    void setController(IController controller);

    /**
     * Sets the args for the view
     *
     * @param args the args to set
     */
    void setArgs(String[] args);
}
