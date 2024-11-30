package fakestore.controller;

public interface ITypeControl {
    /**
     * @param key     the key that was typed
     * @param keyCode the code for non ascii keys
     */
    void type(char key, int keyCode);

    /**
     * Will be called when the input is deregistered or clicked outside of by the controller
     */
    void deregister();
}
