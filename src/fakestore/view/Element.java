package fakestore.view;

class Element <T> {
    private int parentX;
    private int parentY;
    private int x;
    private int y;
    private int width;
    private int height;
    private final T owner;

    protected Element(T owner) {
        this.owner = owner;
    }

    protected int getParentX() {
        return parentX;
    }

    protected int getParentY() {
        return parentY;
    }

    protected int getX() {
        return x;
    }

    protected int getY() {
        return y;
    }

    protected int getWidth() {
        return width;
    }

    protected int getHeight() {
        return height;
    }

    protected T getOwner() {
        return owner;
    }

    protected Element<T> setParentX(int parentX) {
        int oldParentX = this.parentX;
        this.parentX = parentX;
        this.x = x - oldParentX + parentX;
        return this;
    }

    protected Element<T> setParentY(int parentY) {
        int oldParentY = this.parentY;
        this.parentY = parentY;
        this.y = this.y - oldParentY + parentY;
        return this;
    }

    protected Element<T> setX(int x) {
        this.x = x + parentX;
        return this;
    }

    protected Element<T> setY(int y) {
        this.y = y + parentY;
        return this;
    }

    protected Element<T> setWidth(int width) {
        this.width = width;
        return this;
    }

    protected Element<T> setHeight(int height) {
        this.height = height;
        return this;
    }

    protected boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x &&
                mouseX <= x + width &&
                mouseY >= y &&
                mouseY <= y + height;
    }
}
