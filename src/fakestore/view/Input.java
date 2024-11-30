package fakestore.view;

import fakestore.controller.IClickable;
import fakestore.controller.ITypeControl;
import processing.core.PFont;

import static processing.core.PConstants.*;

class Input implements IClickable {
    private final View view = View.getInstance();
    private final Element<Input> el = new Element<>(this);
    private int z;
    private InputType type = InputType.TEXT;
    private InputAlignment alignment = InputAlignment.LEFT;
    private final StringBuilder input = new StringBuilder();
    private boolean limit = false;
    private boolean registered = false;
    private String placeholder = "";
    private int inputSize = 12;
    private PFont inputFont;
    private int bgColor;
    private IAction action;

    public Input() {
        this.bgColor = view.rgb(23, 23, 23);
        this.inputFont = view.getFont("600");
        register();
    }

    void draw() {
        // background
        view.fill(bgColor);
        view.stroke(registered ? view.rgb(199, 116, 47) : view.rgb(38, 38, 38));
        view.rect(el.getX(), el.getY(), el.getWidth(), el.getHeight(), 10);
        view.noStroke();
        // text
        view.textFont(inputFont, inputSize);
        int x = switch (alignment) {
            case LEFT -> el.getX() + 16;
            case CENTER -> (int) (el.getX() + el.getWidth() / 2f - view.textWidth(input.toString()) / 2f);
        };
        if (input.isEmpty()) {
            // placeholder
            view.fill(view.rgb(255, 255, 255, 0.5f));
            view.text(placeholder, x, el.getY() + el.getHeight() / 2f - 7);
        } else {
            // input
            String text = input.toString();
            view.fill(view.rgb(255, 255, 255, 0.75f));
            view.text(text, x, el.getY() + el.getHeight() / 2f - 7);
            if (view.textWidth(text) > el.getWidth() - 45) {
                if (!limit) {
                    limit = true;
                    view.toast("Input is too long");
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
                if (input.length() == 1) {
                    char first = input.charAt(0);
                    if (first == '0' || first == ' ') input.deleteCharAt(0);
                }
                switch (key) {
                    case BACKSPACE -> {
                        if (input.length() < 1) return;
                        input.deleteCharAt(input.length() - 1);
                    }
                    // wierd edge case 127 is ctrl + backspace
                    case 127 -> {
                        if (input.length() < 1) return;
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
                        switch (type) {
                            case NUMBER -> {
                                if (!Character.isDigit(key) || (input.length() == 0 && key == '0')) return;
                            }
                            case TEXT -> {
                                if (!Character.isLetterOrDigit(key) && key != ' ') return;
                            }
                        }
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

    boolean isFocused() {
        return registered;
    }

    Element<Input> getEl() {
        return el;
    }

    Input setType(InputType type) {
        this.type = type;
        return this;
    }

    Input setAlignment(InputAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    Input setZ(int z) {
        this.z = z;
        return this;
    }

    Input setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    Input setInput(String input) {
        this.input.delete(0, this.input.length());
        this.input.append(input);
        return this;
    }

    Input setInputSize(int size) {
        this.inputSize = size;
        return this;
    }

    Input setFont(PFont font) {
        this.inputFont = font;
        return this;
    }

    Input setAction(IAction action) {
        this.action = action;
        return this;
    }

    IAction getAction() {
        return action;
    }
}

interface IAction {
    void call(String input);
}

enum InputType {
    NUMBER, TEXT
}

enum InputAlignment {
    LEFT, CENTER
}
