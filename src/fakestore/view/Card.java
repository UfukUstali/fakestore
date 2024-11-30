package fakestore.view;

import fakestore.Result;
import fakestore.controller.IClickable;
import fakestore.model.IResponse;
import fakestore.model.Product;
import processing.core.PImage;

import java.text.NumberFormat;
import java.util.Locale;

class Card<T extends CardComposer<T>> implements IClickable {
    private final View view = View.getInstance();
    private final T owner;
    private IResponse response;
    private Product product;
    private String title;
    private String[] description;
    private String price;
    private final Element<Card<T>> el = new Element<>(this);
    private int z;
    private Result<PImage, String> thumbnail;
    private final Rating rating = new Rating();
    private final Loading loading = new Loading()
            .getEl()
            .setX(0).setY(1)
            .getOwner();

    Card(T owner, Product product) {
        this.product = product;
        this.owner = owner;
    }

    Card(T owner, String id) {
        this.response = view.getController().get("/products/" + id);
        this.owner = owner;
    }

    void draw() {
        if (response != null) {
            switch (response.status()) {
                case PENDING -> {
                    bg();
                    loading.getEl().setParentX(el.getX() + el.getWidth() / 2 - el.getHeight() / 2).setParentY(el.getY());
                    loading.draw();
                    return;
                }
                case SUCCESS -> {
                    loading.getEl().setParentX(el.getX());
                    ready(response.data(Product.class));
                    owner.ready(product);
                    response = null;
                }
                case ERROR -> {
                    String error = response.error();
                    bg();
                    view.fill(view.rgb(255, 255, 255));
                    view.textFont(view.getFont("500"), 20);
                    view.textAlign(view.CENTER);
                    view.text(error, el.getX() + el.getWidth() / 2f, el.getY() + el.getHeight() / 2f);
                    view.textAlign(view.LEFT, view.TOP);
                    return;
                }
            }
        }
        bg();
        // thumbnail
        thumbnail = view.getThumbnail(product.thumbnail());
        switch (thumbnail.status()) {
            case OK ->
                    view.image(thumbnail.value(), el.getX() + 1, el.getY() + 1, el.getHeight() - 2, el.getHeight() - 2);
            case ERROR -> {
                if (thumbnail.error().equals("loading")) {
                    loading.getEl().setParentY(el.getY());
                    loading.draw();
                } else {
                    view.fill(view.rgb(255, 255, 255));
                    view.textFont(view.getFont("500"), 20);
                    view.text(thumbnail.error(), el.getX() + 16, el.getY() + 4);
                }
            }
        }
        // brand
        view.fill(view.rgb(255, 255, 255, 0.75f));
        view.textFont(view.getFont("400"), 14);
        view.text(product.brand(), el.getX() + el.getHeight() + 24, el.getY() + 8);
        // title
        view.fill(view.rgb(255, 255, 255));
        view.textFont(view.getFont("700"), 24);
        view.text(title, el.getX() + el.getHeight() + 24, el.getY() + 24);
        // rating
        rating.getEl().setParentY(el.getY());
        rating.draw();
        // description
        for (int i = 0; i < description.length; i++) {
            view.fill(view.rgb(255, 255, 255, 0.75f));
            view.textFont(view.getFont("400"), 14);
            view.text(description[i], el.getX() + el.getHeight() + 24, el.getY() + 96 + (i * 16));
        }
        // price
        view.fill(view.rgb(224, 40, 18));
        view.textFont(view.getFont("700"), 24);
        view.text(price, el.getX() + el.getWidth() - 24 - view.textWidth(price), el.getY() + el.getHeight() - 16 - 20);
    }

    @Override
    public void onClick() {
        view.getController().navigateTo("product", "product", String.valueOf(product.id()));
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return el.isInside(mouseX, mouseY);
    }

    @Override
    public void register() {
        view.getController().registerClickable(this);
    }

    @Override
    public void cleanUp() {
        view.getController().deregisterClickable(this);
    }

    Card<T> setZ(int z) {
        this.z = z;
        return this;
    }

    Element<Card<T>> getEl() {
        return el;
    }

    int getHeight() {
        return el.getHeight();
    }

    void init() {
        rating.getEl()
                .setParentX(el.getX()).setParentY(el.getY())
                .setX(el.getHeight() + 24).setY(56)
                .setWidth(120).setHeight(32);
        loading.getEl()
                .setParentX(el.getX()).setParentY(el.getY())
                .setWidth(el.getHeight()).setHeight(el.getHeight() - 2);
        if (product != null) {
            ready(product);
        }
    }

    private void ready(Product p) {
        product = p;
        title = view.truncate(product.title(), el.getWidth() - el.getHeight() - 48, 24, true);
        price = NumberFormat.getCurrencyInstance(Locale.GERMANY).format(product.price());
        view.textSize(24);
        description = view.lineWrap(product.description(), (int) (el.getWidth() - el.getHeight() - 48 - view.textWidth(price) - 16), 14, 3);
        view.requestThumbnail(product.thumbnail());
        thumbnail = view.getThumbnail(product.thumbnail());
        rating.setRating(product.rating());
        register();
    }

    private void bg() {
        view.fill(view.rgb(23, 23, 23));
        view.stroke(view.rgb(38, 38, 38));
        view.rect(el.getX(), el.getY(), el.getWidth(), el.getHeight(), 10);
        view.noStroke();
    }

    T getOwner() {
        return owner;
    }
}

interface CardComposer<T extends CardComposer<T>> {
    void draw();

    void init();

    void cleanUp();

    int getHeight();

    void ready(Product product);

    Card<T> getCard();
}
