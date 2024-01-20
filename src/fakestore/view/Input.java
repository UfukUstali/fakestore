package fakestore.view;

import fakestore.controller.IClickable;
import fakestore.controller.ITypeControl;
import processing.core.PFont;

import static processing.core.PConstants.*;

class Input implements IClickable {
    private static final View view = View.getInstance();
    private final Element<Input> el = new Element<>(this);
    private int z;
    private final StringBuilder input = new StringBuilder();
    private boolean limit = false;
    private boolean registered = false;
    private String placeholder;
    private int inputSize = 12;
    private PFont inputFont;
    private int bgColor;
    private IAction action;

    public Input() {
        this.bgColor = view.rgb(23, 23, 23);
        this.inputFont = view.getFont("600");
        register();
    }

    public void draw() {
        // background
        view.fill(bgColor);
        view.stroke(registered ? view.rgb(199, 116, 47) : view.rgb(38, 38, 38));
        view.rect(el.getX(), el.getY(), el.getWidth(), el.getHeight(), 10);
        view.noStroke();
        // text
        view.textFont(inputFont, inputSize);
        if (input.isEmpty()) {
            // placeholder
            view.fill(view.rgb(255, 255, 255, 0.5f));
            view.text(placeholder, el.getX() + 16, el.getY() + el.getHeight() / 2f - 7);
        } else {
            // input
            String text = input.toString();
            view.fill(view.rgb(255, 255, 255, 0.75f));
            view.text(text, el.getX() + 16, el.getY() + el.getHeight() / 2f - 7);
            if (view.textWidth(text) > el.getWidth() - 45) {
                if (!limit) {
                    limit = true;
                    view.toast("Input is too long", "search");
                }
            } else limit = false;
        }
    }

    @Override
    public void onClick() {
        registered = true;
        view.getController().registerInput(new ITypeControl() {
            @Override
            public void type(char key, int keyCode) {
//                System.out.println(((int) key) + " " + keyCode);
                switch (key) {
                    case BACKSPACE -> {
                        if (!(input.length() > 0)) return;
                        input.deleteCharAt(input.length() - 1);
                    }
                    // wierd edge case 127 is ctrl + backspace
                    case 127 -> {
                        if (!(input.length() > 0)) return;
                        int start = input.lastIndexOf(" ");
                        if (start == -1) start = 0;
                        input.delete(start, input.length());
                    }
                    // didn't test RETURN on Mac but it should work
                    case ENTER, RETURN -> {
                        if (action != null) action.call(input.toString());
                        view.getController().deregisterInput();
                        input.delete(0, input.length());
                        registered = false;
                    }
                    default -> {
                        if (limit) return;
                        input.append(key);
                    }
                }
            }
            @Override
            public void deregister() {
                registered = false;
            }
        });
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return el.isInside(mouseX, mouseY);
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void register() {
        view.getController().registerClickable(this);
    }

    @Override
    public void cleanUp() {
        view.getController().deregisterClickable(this);
    }

    protected String getInput() {
        return input.toString();
    }

    protected Element<Input> getEl() {
        return el;
    }

    protected Input setZ(int z) {
        this.z = z;
        return this;
    }

    protected Input setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    protected Input setInputSize(int size) {
        this.inputSize = size;
        return this;
    }

    protected Input setFont(PFont font) {
        this.inputFont = font;
        return this;
    }

    protected Input setBgColor(int bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    protected Input setAction(IAction action) {
        this.action = action;
        return this;
    }
}

interface IAction {
    void call(String input);
}
