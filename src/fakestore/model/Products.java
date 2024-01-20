package fakestore.model;

public record Products(Product[] products, int total, int skip, int limit) {
    public String next() {
        return String.valueOf(skip + limit);
    }
}
