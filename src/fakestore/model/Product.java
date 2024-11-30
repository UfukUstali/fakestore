package fakestore.model;

import java.util.Arrays;

public record Product(int id, String title, String description, double price, double discountPercentage, float rating,
                      int stock, String brand, String category, String thumbnail, String[] images) {
    public Product(int id, String title, String description, double price, double discountPercentage, float rating, int stock, String brand, String category, String thumbnail, String[] images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.rating = rating;
        this.stock = stock;
        this.brand = brand;
        this.category = category;
        this.thumbnail = thumbnail;
        if (images.length == 1) {
            this.images = images;
            return;
        }
        this.images = Arrays.stream(images).filter(image -> !image.equals(thumbnail)).toArray(String[]::new);
    }

    /**
     * @return a dummy product
     */
    public static Product getDummyProduct() {
        return new Product(1, "dummy", "it is a dummy", 10, 0, 4.7f, 15, "dummy brand", "dummy category", "dummy thumbnail", new String[]{"dummy", "dummy1", "dummy2"});
    }
}
