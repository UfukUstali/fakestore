package fakestore.model;

public record Products(Product[] products, int total, int skip, int limit) {
}
