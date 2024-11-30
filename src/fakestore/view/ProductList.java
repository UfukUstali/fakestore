package fakestore.view;

import fakestore.model.Products;

import java.util.Arrays;

class ProductList {
    private final Element<ProductList> el = new Element<>(this);
    private Products products;
    private ProductCard[] cards;
    private Pagination pagination;

    ProductList() {
    }

    void draw() {
        Arrays.stream(cards).forEach(card -> {
            card.getCard().getEl().setParentY(el.getY());
            card.draw();
        });
        if (pagination.canPaginate()) pagination.getEl().setParentY(el.getY())
                .getOwner().draw();
    }

    void setList(Products products) {
        this.products = products;
        this.pagination = new Pagination(products.total(), products.skip()).getEl()
                .setParentX(el.getX())
                .setY(16 + products.products().length * (200 + 32) + 16)
                .setHeight(32)
                .getOwner();
        this.pagination.init();
        this.cards = new ProductCard[products.products().length];
        for (int j = 0; j < products.products().length; j++) {
            ProductCard card = new ProductCard(products.products()[j]).getCard()
                    .getEl()
                    .setParentX(el.getX())
                    .setY(16 + j * (200 + 32))
                    .setWidth(640).setHeight(160)
                    .getOwner().getOwner();
            card.init();
            cards[j] = card;
        }
    }

    Element<ProductList> getEl() {
        return el;
    }

    void cleanUp() {
        Arrays.stream(cards).forEach(ProductCard::cleanUp);
        pagination.cleanUp();
    }

    boolean isReady() {
        return products != null;
    }

    int getHeight() {
        return 16 + products.products().length * (200 + 32) + pagination.getEl().getHeight();
    }
}
