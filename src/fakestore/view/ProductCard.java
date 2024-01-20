package fakestore.view;

import fakestore.Result;
import fakestore.controller.IClickable;
import fakestore.model.IResponse;
import fakestore.model.Product;
import processing.core.PImage;

import java.text.NumberFormat;
import java.util.Locale;

class ProductCard implements IClickable {
    private static final View view = View.getInstance();
    private IResponse response;
    private Product product;
    private String title;
    private String[] description;
    private String price;
    private final Element<ProductCard> el = new Element<>(this);
    private int z;
    private Result<PImage, String> thumbnail;
    private final Rating rating = new Rating();
    private final Button addToCartButton = new Button()
            .setZ(z + 1)
            .setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0))
            .setLabel("Add to cart").setLabelSize(20).setFont(view.getFont("500"));
    private final Button buyNowButton = new Button()
            .setZ(z + 1)
            .setLabelColor(view.rgb(0, 0, 0))
            .setLabel("Buy now").setLabelSize(20).setFont(view.getFont("500"));
    private final Loading loading = new Loading()
            .getEl()
            .setX(0).setY(1)
            .getOwner();

    protected ProductCard(Product product) {
        this.product = product;
        buyNowButton.setBgColor(view.rgb(42, 89, 21)).setLabelColor(view.rgb(255, 255, 255));
        addToCartButton.setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0));
    }

    protected ProductCard(String id) {
        this.response = view.getController().get("/products/" + id);
        // disabled button colors
        buyNowButton.setBgColor(view.rgb(163, 163, 163)).setLabelColor(view.rgb(0, 0, 0, 0.5f));
        addToCartButton.setBgColor(view.rgb(163, 163, 163)).setLabelColor(view.rgb(0, 0, 0, 0.5f));
    }

    // horizontal product card
    // thumbnail to the left, middle title and description, price and cart button to the right
    public void draw() {
        if (response != null) {
            switch (response.status()) {
                case PENDING -> {
                    bg();
                    buttons();
                    loading.getEl().setParentX(el.getX() + el.getWidth() / 2 - el.getHeight() / 2).setParentY(el.getY());
                    loading.draw();
                    return;
                }
                case SUCCESS -> {
                    loading.getEl().setParentX(el.getX());
                    ready(response.data(Product.class));
                    buyNowButton.setBgColor(view.rgb(42, 89, 21)).setLabelColor(view.rgb(255, 255, 255));
                    addToCartButton.setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0));
                    response = null;
                }
                case ERROR -> {
                    String error = response.error();
                    bg();
                    buttons();
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
        // buttons
        buttons();
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
        view.getController().deregisterClickable(this, buyNowButton, addToCartButton);
    }

    protected ProductCard setZ(int z) {
        this.z = z;
        return this;
    }

    protected Element<ProductCard> getEl() {
        return el;
    }

    protected int getHeight() {
        return el.getHeight() + 40;
    }

    protected void init() {
        rating.getEl()
                .setParentX(el.getX()).setParentY(el.getY())
                .setX(el.getHeight() + 24).setY(56)
                .setWidth(120).setHeight(32);
        buyNowButton.getEl()
                .setParentX(el.getX()).setParentY(el.getY())
                .setX(4).setY(el.getHeight() + 8)
                .setWidth(el.getWidth() / 2 - 8).setHeight(32);
        addToCartButton.getEl()
                .setParentX(el.getX()).setParentY(el.getY())
                .setX(el.getWidth() / 2 + 4).setY(el.getHeight() + 8)
                .setWidth(el.getWidth() / 2 - 8).setHeight(32);
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
        buyNowButton
                .setCallback(() -> {
                    Result<String, String> res = view.getController().buy(product);
                    switch (res.status()) {
                        case OK -> {
                            if (res.value().equals("ok")) view.toast("Bought " + product.title());
                            else view.toast(res.value());
                        }
                        case ERROR -> view.toast(res.error());
                    }
                });
        addToCartButton
                .setCallback(() -> {
                    Result<String, String> res = view.getController().addToCart(product);
                    switch (res.status()) {
                        case OK -> {
                            if (!res.value().equals("ok")) view.toast(res.value());
                        }
                        case ERROR -> view.toast(res.error());
                    }
                });
        register();
    }

    private void bg() {
        view.fill(view.rgb(23, 23, 23));
        view.stroke(view.rgb(38, 38, 38));
        view.rect(el.getX(), el.getY(), el.getWidth(), el.getHeight(), 10);
        view.noStroke();
    }

    private void buttons() {
        view.strokeWeight(1);
        view.stroke(view.rgb(38, 38, 38));
        buyNowButton.getEl().setParentY(el.getY());
        buyNowButton.draw();
        addToCartButton.getEl().setParentY(el.getY());
        addToCartButton.draw();
        view.noStroke();
        view.strokeWeight(2);
    }
}
