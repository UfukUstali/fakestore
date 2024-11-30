package fakestore.view;

import processing.core.PApplet;

class Loading {
    private final View view = View.getInstance();
    private final Element<Loading> el = new Element<>(this);
    private int bgColor;
    private int circleColor;
    private float leading;
    private float trailing;
    private final float step;
    private boolean dir = false;

    Loading() {
        this.bgColor = view.rgb(23, 23, 23);
        this.circleColor = view.rgb(163, 163, 163);
        this.leading = view.random(0, 360);
        this.trailing = view.random(leading, leading + 120);
        this.step = view.random(1.8f, 2.4f);
    }

    void draw() {
        view.fill(bgColor);
        view.rect(el.getX(), el.getY(), el.getWidth(), el.getHeight(), 5);
        view.fill(circleColor);
        int x = el.getX() + el.getWidth() / 2;
        int y = el.getY() + el.getHeight() / 2;
        int r = Math.min(el.getWidth(), el.getHeight()) / 2;
        view.ellipseMode(view.CENTER);
        view.arc(x, y, r, r, PApplet.radians(leading), PApplet.radians(trailing), view.PIE);
        view.fill(bgColor);
        view.circle(x, y, r * (5 / 6f));
        view.ellipseMode(view.CORNER);
        next();
    }

    Element<Loading> getEl() {
        return el;
    }

    private void next() {
        leading += step;
        trailing += step;
        float diff = (trailing - leading + 360) % 360;
        if (diff <= 20) dir = true;
        else if (120 <= diff) dir = false;
        if (dir) trailing += step;
        else leading += step;
        if (leading >= 360 && trailing >= 360) {
            leading %= 360;
            trailing %= 360;
        }
    }
}
