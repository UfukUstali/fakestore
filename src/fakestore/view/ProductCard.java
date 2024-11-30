package fakestore.view;

import fakestore.Result;
import fakestore.model.Product;


class ProductCard implements CardComposer<ProductCard> {
    private final View view = View.getInstance();
    private final Card<ProductCard> card;
    private final Button addToCartButton = new Button()
            .setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0))
            .setLabel("Add to cart").setLabelSize(20).setFont(view.getFont("500"));
    private final Button buyNowButton = new Button()
            .setLabelColor(view.rgb(0, 0, 0))
            .setLabel("Buy now").setLabelSize(20).setFont(view.getFont("500"));

    ProductCard(Product product) {
        this.card = new Card<>(this, product);
        ready(product);
    }

    ProductCard(String id) {
        this.card = new Card<>(this, id);
        // disabled button colors
        buyNowButton.setBgColor(view.rgb(163, 163, 163)).setLabelColor(view.rgb(0, 0, 0, 0.5f));
        addToCartButton.setBgColor(view.rgb(163, 163, 163)).setLabelColor(view.rgb(0, 0, 0, 0.5f));
    }

    public void draw() {
        card.draw();
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

    @Override
    public Card<ProductCard> getCard() {
        return card;
    }

    @Override
    public int getHeight() {
        return card.getHeight() + 40;
    }

    @Override
    public void init() {
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

    @Override
    public void cleanUp() {
        card.cleanUp();
        buyNowButton.cleanUp();
        addToCartButton.cleanUp();
    }

    @Override
    public void ready(Product product) {
        buyNowButton
                .setBgColor(view.rgb(42, 89, 21)).setLabelColor(view.rgb(255, 255, 255))
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
                .setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0))
                .setCallback(() -> {
                    Result<String, String> res = view.getController().addToCart(product);
                    switch (res.status()) {
                        case OK -> {
                            if (!res.value().equals("ok")) view.toast(res.value());
                        }
                        case ERROR -> view.toast(res.error());
                    }
                });
    }
}
