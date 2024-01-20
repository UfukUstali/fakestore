package fakestore.view;

import fakestore.controller.IClickable;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;

class Button implements IClickable {
    private static final View view = View.getInstance();
    private final Element<Button> el = new Element<>(this);
    private int z;
    private String label;
    private int labelSize = 12;
    private PFont labelFont;
    private int horizontalAlign;
    private PImage image;
    private PShape svg;
    private int bgColor;
    private int labelColor;
    private ICallback callback = () -> {};

    public Button() {
        view.colorMode(view.RGB, 255, 255, 255, 255);
        this.bgColor = view.color(255, 0);
        view.colorMode(view.RGB, 255, 255, 255, 1.0f);
        this.horizontalAlign = view.CENTER;
        this.labelFont = view.getFont("500");
        register();
    }

    public void draw() {
        if (image != null) {
            view.image(image, el.getX(), el.getY(), el.getWidth(), el.getHeight());
            return;
        }
        if (svg != null) {
            view.shape(svg, el.getX(), el.getY(), el.getWidth(), el.getHeight());
            return;
        }
        view.fill(bgColor);
        view.rect(el.getX(), el.getY(), el.getWidth(), el.getHeight(), 10);
        view.fill(labelColor);
        view.textAlign(horizontalAlign, view.CENTER);
        view.textFont(labelFont, labelSize);
        view.text(label, el.getX() + (horizontalAlign == view.CENTER ? el.getWidth() / 2f : 16), el.getY() + el.getHeight() / 2f);
        view.textAlign(view.LEFT, view.TOP);
    }

    public void onClick() {
        callback.call();
    }

    public boolean isInside(int mouseX, int mouseY) {
        return el.isInside(mouseX, mouseY);
    }

    public int getZ() {
        return z;
    }

    public void register() {
        view.getController().registerClickable(this);
    }

    public void cleanUp() {
        view.getController().deregisterClickable(this);
    }

    protected Element<Button> getEl() {
        return el;
    }

    public Button setZ(int z) {
        this.z = z;
        return this;
    }

    public Button setLabel(String label) {
        this.label = label;
        return this;
    }

    public Button setLabelSize(int size) {
        this.labelSize = size;
        return this;
    }

    public Button setFont(PFont font) {
        this.labelFont = font;
        return this;
    }

    protected Button setLabelAlign(int horizontal) {
        this.horizontalAlign = horizontal;
        return this;
    }

    public Button setBgColor(int bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public Button setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    public Button setCallback(ICallback callback) {
        this.callback = callback;
        return this;
    }

    public Button setImage(PImage image) {
        this.image = image;
        return this;
    }

    protected Button setImage(PShape image) {
        this.svg = image;
        return this;
    }
}
