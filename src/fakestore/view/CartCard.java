package fakestore.view;

import fakestore.model.Product;

class CartCard implements CardComposer<CartCard> {
    private final View view = View.getInstance();
    private final Card<CartCard> card;
    private final CartButtons cartButtons = new CartButtons();

    CartCard(Product product) {
        this.card = new Card<>(this, product);
        ready(product);
    }

    public void draw() {
        card.draw();
        var el = card.getEl();
        view.strokeWeight(1);
        cartButtons.getEl().setParentY(el.getY())
                .getOwner().draw();
    }

    @Override
    public Card<CartCard> getCard() {
        return card;
    }

    @Override
    public int getHeight() {
        return card.getHeight() + 8 + cartButtons.getEl().getHeight();
    }

    @Override
    public void init() {
        card.init();
        cartButtons.getEl()
                .setParentX(card.getEl().getX()).setParentY(card.getEl().getY())
                .setX(4).setY(card.getEl().getHeight() + 8)
                .setWidth(card.getEl().getWidth() - 8).setHeight(32)
                .getOwner().setZ(card.getZ());
        cartButtons.init();
    }

    @Override
    public void cleanUp() {
        card.cleanUp();
        cartButtons.cleanUp();
    }

    @Override
    public void ready(Product product) {
        cartButtons.setProduct(product);
    }
}
