package fakestore.view;

import fakestore.controller.IController;
import fakestore.model.ICart;
import fakestore.model.Product;

class CartButtons {
    private final View view = View.getInstance();
    private final Element<CartButtons> el = new Element<>(this);
    private final ICart cart = view.getController().getCart();
    private Product product;
    private final Button[] minusButtons = new Button[4];
    private final Input input;
    private final Button[] plusButtons = new Button[4];

    CartButtons() {
        for (int i = 0; i < 4; i++) {
            minusButtons[3 - i] = new Button().setLabel("-" + (int) Math.pow(2, i))
                    .setLabelSize(20).setFont(view.getFont("500"))
                    .setBgColor(view.rgb(239, 70, 59)).setLabelColor(view.rgb(255, 255, 255));
            plusButtons[i] = new Button().setLabel("+" + (int) Math.pow(2, i))
                    .setLabelSize(20).setFont(view.getFont("500"))
                    .setBgColor(view.rgb(42, 89, 21)).setLabelColor(view.rgb(255, 255, 255));
        }
        input = new Input().setType(InputType.NUMBER).setZ(0)
                .setInputSize(20).setAlignment(InputAlignment.CENTER);
    }

    void draw() {
        if (product != null) update();
        for (int i = 0; i < 4; i++) {
            minusButtons[i].getEl().setParentY(el.getY())
                    .getOwner().draw();
            plusButtons[i].getEl().setParentY(el.getY())
                    .getOwner().draw();
        }
        input.getEl().setParentY(el.getY())
                .getOwner().draw();
    }


    Element<CartButtons> getEl() {
        return el;
    }

    void setZ(int z) {
        for (int i = 0; i < 4; i++) {
            minusButtons[i].setZ(z);
            plusButtons[i].setZ(z);
        }
    }

    CartButtons setProduct(Product product) {
        this.product = product;
        input.setInput(String.valueOf(cart.getQuantityInCart(product)))
                .setAction((input) -> {
                    int quantity = Integer.parseInt(input);
                    int inCart = cart.getQuantityInCart(product);
                    IController controller = view.getController();
                    controller.removeFromCart(product, inCart);
                    if (quantity <= 0) return;
                    var result = controller.addToCart(product, quantity);
                    switch (result.status()) {
                        case OK -> {
                            if (!"ok".equals(result.value())) view.toast(result.value());
                        }
                        case ERROR -> {
                            controller.addToCart(product, inCart);
                            view.toast(result.error());
                        }
                    }
                    update();
                });
        update();
        return this;
    }

    private void update() {
        int inCart = cart.getQuantityInCart(product);
        int available = product.stock() - (cart.getBoughtQuantity(product) + inCart);
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            if (inCart < (int) Math.pow(2, 3 - i)) {
                minusButtons[i]
                        .setBgColor(view.rgb(163, 163, 163)).setLabelColor(view.rgb(0, 0, 0, 0.5f))
                        .setCallback(() -> {
                        });
            } else {
                minusButtons[i]
                        .setBgColor(view.rgb(239, 70, 59)).setLabelColor(view.rgb(255, 255, 255))
                        .setCallback(() -> {
                            int quantity = inCart - (int) Math.pow(2, 3 - finalI);
                            if (quantity < 0) quantity = 0;
                            input.getAction().call(String.valueOf(quantity));
                            input.setInput(String.valueOf(quantity));
                        });
            }
            if (available < (int) Math.pow(2, i)) {
                plusButtons[i]
                        .setBgColor(view.rgb(163, 163, 163)).setLabelColor(view.rgb(0, 0, 0, 0.5f))
                        .setCallback(() -> {
                        });
            } else {
                plusButtons[i]
                        .setBgColor(view.rgb(42, 89, 21)).setLabelColor(view.rgb(255, 255, 255))
                        .setCallback(() -> {
                            int quantity = (int) Math.pow(2, finalI);
                            if (quantity > available) quantity = available + inCart;
                            else quantity += inCart;
                            input.getAction().call(String.valueOf(quantity));
                            input.setInput(String.valueOf(quantity));
                        });

            }
        }
        String stringOf = String.valueOf(inCart);
        if (!input.isFocused()) input.setInput(stringOf);
    }

    void init() {
        int tenth = el.getWidth() / 10;
        for (int i = 0; i < 4; i++) {
            minusButtons[i].getEl()
                    .setParentX(el.getX()).setParentY(el.getY())
                    .setWidth(tenth - 8).setHeight(el.getHeight())
                    .setX(i * tenth);
            plusButtons[i].getEl()
                    .setParentX(el.getX()).setParentY(el.getY())
                    .setWidth(tenth - 8).setHeight(el.getHeight())
                    .setX(4 * tenth + (el.getWidth() / 5 + 8) + i * tenth);
        }
        input.getEl()
                .setParentX(el.getX()).setParentY(el.getY())
                .setWidth(el.getWidth() / 5).setHeight(el.getHeight())
                .setX(el.getWidth() / 2 - el.getWidth() / 10);
    }

    void cleanUp() {
        for (int i = 0; i < 4; i++) {
            minusButtons[i].cleanUp();
            plusButtons[i].cleanUp();
        }
        input.cleanUp();
    }
}
