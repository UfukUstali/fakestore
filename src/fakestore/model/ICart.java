package fakestore.model;

public interface ICart {
    /**
     * Get a copy of all the products in the cart.
     */
    Product[] getProductsInCart();

    /**
     * Get the quantity of a product in the cart.
     */
    int getQuantityInCart(Product product);

    /**
     * Get how many of a product has been bought.
     */
    int getBoughtQuantity(Product product);

    /**
     * Get how much all the products in the cart cost.
     */
    double getTotal();
}
