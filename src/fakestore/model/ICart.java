package fakestore.model;

import fakestore.Result;

public interface ICart {
    Product[] getProductsInCart();

    int getQuantityInCart(Product product);

    double getTotal();

    Product[] getBoughtProducts();
}
