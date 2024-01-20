package fakestore.view;

import fakestore.Result;
import fakestore.model.Product;


class ProductCard {
    private static final View view = View.getInstance();
    private final Card<ProductCard> card;
    private final Button addToCartButton = new Button()
            .setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0))
            .setLabel("Add to cart").setLabelSize(20).setFont(view.getFont("500"));
    private final Button buyNowButton = new Button()
            .setLabelColor(view.rgb(0, 0, 0))
            .setLabel("Buy now").setLabelSize(20).setFont(view.getFont("500"));

    protected ProductCard(Product product) {
        this.card = new Card<>(this, product);
        buyNowButton.setBgColor(view.rgb(42, 89, 21)).setLabelColor(view.rgb(255, 255, 255));
        addToCartButton.setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0));
        ready(product);
    }

    protected ProductCard(String id) {
        this.card = new Card<>(this, id);
        // disabled button colors
        buyNowButton.setBgColor(view.rgb(163, 163, 163)).setLabelColor(view.rgb(0, 0, 0, 0.5f));
        addToCartButton.setBgColor(view.rgb(163, 163, 163)).setLabelColor(view.rgb(0, 0, 0, 0.5f));
    }

    public void draw() {
        card.draw();
        var response = card.getResponse();
        var el = card.getEl();
        if (response != null) {
            switch (response.status()) {
                case PENDING -> {
                    buttons();
                    return;
                }
                case SUCCESS -> {
                    ready(response.data(Product.class));
                    buyNowButton.setBgColor(view.rgb(42, 89, 21)).setLabelColor(view.rgb(255, 255, 255));
                    addToCartButton.setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0));
                }
                case ERROR -> {
                    String error = response.error();
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
        // buttons
        buttons();
    }

    protected Card<ProductCard> getCard() {
        return card;
    }

    protected int getHeight() {
        return card.getHeight() + 40;
    }

    protected void init() {
        card.init();
        buyNowButton.getEl()
                .setParentX(card.getEl().getX()).setParentY(card.getEl().getY())
                .setX(4).setY(card.getEl().getHeight() + 8)
                .setWidth(card.getEl().getWidth() / 2 - 8).setHeight(32)
                .getOwner().setZ(card.getZ());
        addToCartButton.getEl()
                .setParentX(card.getEl().getX()).setParentY(card.getEl().getY())
                .setX(card.getEl().getWidth() / 2 + 4).setY(card.getEl().getHeight() + 8)
                .setWidth(card.getEl().getWidth() / 2 - 8).setHeight(32)
                .getOwner().setZ(card.getZ());
    }

    protected void cleanUp() {
        card.cleanUp();
        buyNowButton.cleanUp();
        addToCartButton.cleanUp();
    }

    private void ready(Product p) {
        buyNowButton
                .setCallback(() -> {
                    Result<String, String> res = view.getController().buy(p);
                    switch (res.status()) {
                        case OK -> {
                            if (res.value().equals("ok")) view.toast("Bought " + p.title());
                            else view.toast(res.value());
                        }
                        case ERROR -> view.toast(res.error());
                    }
                });
        addToCartButton
                .setCallback(() -> {
                    Result<String, String> res = view.getController().addToCart(p);
                    switch (res.status()) {
                        case OK -> {
                            if (!res.value().equals("ok")) view.toast(res.value());
                        }
                        case ERROR -> view.toast(res.error());
                    }
                });
    }

    private void buttons() {
        var el = card.getEl();
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
