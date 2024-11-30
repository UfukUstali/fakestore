package fakestore.controller;

public interface IClickable {
    /**
     * Called when the button is clicked
     */
    void onClick();

    /**
     * Comparator function
     * <p>
     * Used to determine the order in which of the clickable objects should take priority.
     */
    int getZ();

    /**
     * Filter function
     * <p>
     * Called when the mouse is clicked to check if the mouse is inside the clickable object
     *
     * @param mouseX x position of the mouse
     * @param mouseY y position of the mouse
     */
    boolean isInside(int mouseX, int mouseY);

    /**
     * Should be called when the clickable object is created to register it to the controller
     */
    void register();

    /**
     * Should be called when the clickable object is destroyed to deregister it from the controller
     */
    void cleanUp();
}
