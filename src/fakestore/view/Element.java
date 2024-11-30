package fakestore.view;

class Element<T> {
    private int parentX;
    private int parentY;
    private int x;
    private int y;
    private int width;
    private int height;
    private final T owner;

    Element(T owner) {
        this.owner = owner;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    T getOwner() {
        return owner;
    }

    Element<T> setParentX(int parentX) {
        int oldParentX = this.parentX;
        this.parentX = parentX;
        this.x = x - oldParentX + parentX;
        return this;
    }

    Element<T> setParentY(int parentY) {
        int oldParentY = this.parentY;
        this.parentY = parentY;
        this.y = this.y - oldParentY + parentY;
        return this;
    }

    Element<T> setX(int x) {
        this.x = x + parentX;
        return this;
    }

    Element<T> setY(int y) {
        this.y = y + parentY;
        return this;
    }

    Element<T> setWidth(int width) {
        this.width = width;
        return this;
    }

    Element<T> setHeight(int height) {
        this.height = height;
        return this;
    }

    boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x &&
                mouseX <= x + width &&
                mouseY >= y &&
                mouseY <= y + height;
    }
}
