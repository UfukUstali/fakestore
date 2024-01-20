package fakestore.controller;

public interface IClickable {
    void onClick();

    int getZ();

    boolean isInside(int mouseX, int mouseY);

    void register();

    void cleanUp();
}
