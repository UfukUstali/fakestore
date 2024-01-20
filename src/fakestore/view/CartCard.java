package fakestore.view;

import fakestore.Result;
import fakestore.model.Cart;
import fakestore.model.Product;


class CartCard {
    private static final View view = View.getInstance();
    private final Card<CartCard> card;

    protected CartCard(Product product) {
        this.card = new Card<>(this, product);
        ready(product);
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

    protected Card<CartCard> getCard() {
        return card;
    }

    protected int getHeight() {
        return card.getHeight() + 40;
    }

    protected void init() {
        card.init();
    }

    protected void cleanUp() {
        card.cleanUp();
    }

    private void ready(Product p) {

    }

    private void buttons() {
        var el = card.getEl();
        view.strokeWeight(1);
        view.stroke(view.rgb(38, 38, 38));

        view.noStroke();
        view.strokeWeight(2);
    }
}
