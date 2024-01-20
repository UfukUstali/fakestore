package fakestore.view;

import com.google.gson.JsonObject;
import fakestore.controller.Controller;
import processing.core.PFont;

class Pagination {
    private static final View view = View.getInstance();
    private final Element<Pagination> el = new Element<>(this).setHeight(0);
    private boolean canPaginate;
    private final int limit;
    private int pageCount;
    private int currentPage;
    private Button[] buttons;
    private Button prev;
    private Button next;

    protected Pagination(int total, int skip) {
        this.limit = 30;
        if (total <= limit) return;
        // array length
        this.canPaginate = true;
        // array index
        this.pageCount = (int) Math.ceil(total / (double) limit);
        this.currentPage = (int) Math.ceil(skip / (double) limit);
//        System.out.println(el.getX());
        this.buttons = new Button[pageCount];
    }

    protected void draw() {
        if (!canPaginate) return;
        for (Button button : buttons) button.getEl().setParentY(el.getY()).getOwner().draw();
        prev.getEl().setParentY(el.getY()).getOwner().draw();
        next.getEl().setParentY(el.getY()).getOwner().draw();
    }

    protected Element<Pagination> getEl() {
        return el;
    }

    protected void init() {
        if (!canPaginate) {
            el.setHeight(0);
            return;
        }
        el.setWidth(48 + (pageCount * 40) + 32).setX(320 - (el.getWidth() / 2));
        Controller controller = view.getController();
        int bgColor = view.rgb(251, 146, 60);
        int labelColor = view.rgb(0, 0, 0);
        int currentBgColor = view.rgb(38, 38, 38);
        int currentLabelColor = view.rgb(255, 255, 255);
        int labelSize = 12;
        PFont labelFont = view.getFont("500");
        for (int i = 0; i < pageCount; i++) {
            int finalI = i;
            buttons[i] = new Button().getEl()
                    .setParentX(el.getX())
                    .setX((32 + 16) + (i * (24 + 16))).setY(0)
                    .setWidth(24).setHeight(el.getHeight())
                    .getOwner()
                    .setZ(1)
                    .setBgColor(currentPage == i ? currentBgColor : bgColor)
                    .setLabelColor(currentPage == i ? currentLabelColor : labelColor)
                    .setLabel(String.valueOf(i + 1)).setLabelSize(labelSize).setFont(labelFont)
                    .setCallback(() -> {
                        JsonObject args = controller.getCurrentArgs().deepCopy();
                        args.addProperty("skip", String.valueOf(finalI * limit));
                        controller.navigateTo(controller.getCurrentPath(), args);
                    });
        }
        boolean isFirst = currentPage == 0;
        this.prev = new Button().getEl()
                .setParentX(el.getX())
                .setX(0).setY(0)
                .setWidth(32).setHeight(el.getHeight())
                .getOwner()
                .setZ(1)
                .setBgColor(isFirst ? currentBgColor : bgColor)
                .setLabelColor(isFirst ? currentLabelColor : labelColor)
                .setLabel("<").setLabelSize(labelSize).setFont(view.getFont("800"))
                .setCallback(isFirst ? () -> {
                } : () -> {
                    JsonObject args = controller.getCurrentArgs().deepCopy();
                    args.addProperty("skip", String.valueOf((currentPage - 1) * limit));
                    controller.navigateTo(controller.getCurrentPath(), args);
                });
        boolean isLast = currentPage + 1 == pageCount;
//        System.out.println("isLast: " + isLast + ", currentPage: " + currentPage + ", pageCount: " + pageCount);
        this.next = new Button().getEl()
                .setParentX(el.getX())
                .setX((32 + 16) + (pageCount * (24 + 16))).setY(0)
                .setWidth(32).setHeight(el.getHeight())
                .getOwner()
                .setZ(1)
                .setBgColor(isLast ? currentBgColor : bgColor)
                .setLabelColor(isLast ? currentLabelColor : labelColor)
                .setLabel(">").setLabelSize(labelSize).setFont(view.getFont("800"))
                .setCallback(isLast ? () -> {
                } : () -> {
                    JsonObject args = controller.getCurrentArgs().deepCopy();
                    args.addProperty("skip", String.valueOf((currentPage + 1) * limit));
                    controller.navigateTo(controller.getCurrentPath(), args);
                });
    }

    protected boolean canPaginate() {
        return canPaginate;
    }

    protected void cleanUp() {
        for (Button button : buttons) button.cleanUp();
        prev.cleanUp();
        next.cleanUp();
    }
}
