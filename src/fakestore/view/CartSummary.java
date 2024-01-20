package fakestore.view;

import fakestore.model.ICart;
import fakestore.model.Product;
import fakestore.model.Products;

import java.text.NumberFormat;
import java.util.Locale;

class CartSummary {
    private static final View view = View.getInstance();
    private final Element<CartSummary> el = new Element<>(this);
    private final ICart cart = view.getController().getCart();
    private final Button checkoutButton = new Button()
            .setZ(1)
            .setBgColor(view.rgb(251, 146, 60)).setLabelColor(view.rgb(0, 0, 0))
            .setLabel("Checkout").setLabelSize(20).setFont(view.getFont("500"))
            .setCallback(() -> view.getController().navigateTo("cart"));

    protected CartSummary() {
        el.setX(view.width - 288 - 16).setY(16 + 65)
                .setWidth(288);
        checkoutButton.getEl()
                .setParentX(el.getX()).setParentY(el.getY())
                .setX(16)
                .setWidth(256).setHeight(32);
    }

    protected void draw() {
        Product[] products = cart.getProductsInCart();
        boolean canList = products.length > 0;
        el.setHeight(64 + 32 + (canList ? Math.min(products.length, 13) * (32) : 64) + 32 + 16);

        // bg
        view.fill(view.rgb(23, 23, 23));
        view.stroke(view.rgb(38, 38, 38));
        view.rect(el.getX(), el.getY(), el.getWidth(), el.getHeight(), 10);
        view.noStroke();
        // title (Summary)
        view.fill(view.rgb(255, 255, 255));
        view.textFont(view.getFont("700"), 24);
        view.textAlign(view.CENTER, view.TOP);
        view.text("Summary", el.getX() + el.getWidth() / 2f, el.getY() + 24);
        view.textAlign(view.LEFT, view.TOP);
        // product names list
        if (canList) {
            view.textFont(view.getFont("500"), 16);
            view.textAlign(view.LEFT, view.TOP);
            for (int i = 0; i < Math.min(products.length, 12); i++) {
                Product product = products[i];
                view.text(view.truncate(product.title(), el.getWidth() / 2, 16, true), el.getX() + 10, el.getY() + 64 + i * (32));
                view.textAlign(view.RIGHT, view.TOP);
                String price = NumberFormat.getCurrencyInstance(Locale.GERMANY).format(product.price());
                view.fill(view.rgb(163, 163, 163));
                view.text(cart.getQuantityInCart(product) + "X", el.getX() + el.getWidth() - view.textWidth(price) - 12, el.getY() + 64 + i * (32));
                view.fill(view.rgb(255, 255, 255));
                view.text(price, el.getX() + el.getWidth() - 10, el.getY() + 64 + i * (32));
                view.textAlign(view.LEFT, view.TOP);
            }
            if (products.length > 12) {
                view.textFont(view.getFont("500"), 16);
                view.fill(view.rgb(163, 163, 163));
                view.text("...and " + (products.length - 12) + " more", el.getX() + 10, el.getY() + 64 + 12 * (32));
            }
        } else {
            view.textFont(view.getFont("500"), 24);
            view.textAlign(view.CENTER, view.TOP);
            view.text("No products in cart", el.getX() + el.getWidth() / 2f, el.getY() + 80);
            view.textAlign(view.LEFT, view.TOP);
        }
        // separator
        view.stroke(view.rgb(38, 38, 38));
        view.line(el.getX() + 10, el.getY() + el.getHeight() - 32 - 32 - 16, el.getX() + el.getWidth() - 10, el.getY() + el.getHeight() - 32 - 32 - 16);
        view.noStroke();
        // total
        view.fill(view.getController().getBalance() >= cart.getTotal() ? view.rgb(255, 255, 255) : view.rgb(255, 148, 148));
        view.textFont(view.getFont("500"), 16);
        view.text("Total: ", el.getX() + 10, el.getY() + el.getHeight() - 24 - 32 - 16);
        view.textAlign(view.RIGHT, view.TOP);
        view.text(NumberFormat.getCurrencyInstance(Locale.GERMANY).format(cart.getTotal()), el.getX() + el.getWidth() - 10, el.getY() + el.getHeight() - 24 - 32 - 16);
        view.textAlign(view.LEFT, view.TOP);
        // checkout button
        checkoutButton.getEl().setY(el.getHeight() - 32 - 16).getOwner().draw();
    }

    protected void cleanUp() {
        checkoutButton.cleanUp();
    }
}
