package fakestore.model;

import java.util.Arrays;

public record Product(int id, String title, String description, double price, double discountPercentage, float rating, int stock, String brand, String category, String thumbnail, String[] images) {
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
        this.images = Arrays.stream(images).filter(image -> !image.equals(thumbnail)).toArray(String[]::new);
    }
}
