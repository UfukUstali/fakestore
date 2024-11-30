package fakestore.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class FetchTest {
    @Test
    void fetchProducts_ShouldReturnProducts() throws InterruptedException {
        Fetch fetch = new Fetch("https://dummyjson.com");
        var response = fetch.fetch("/products");
        while (response.status().equals(Status.PENDING)) {
            Thread.sleep(1000);
        }
        assertEquals(Status.SUCCESS, response.status());
        assertNotNull(response);
        assertNull(response.error());
    }

    @Test
    void fetchProducts_ShouldReturnError() throws InterruptedException {
        Fetch fetch = new Fetch("https://dummyjson.com");
        var response = fetch.fetch("/productss");
        while (response.status().equals(Status.PENDING)) {
            Thread.sleep(1000);
        }
        assertEquals(Status.ERROR, response.status());
        assertNull(response.data(Object.class));
        assertNotNull(response.error());
    }

    @Test
    void fetchForce() throws InterruptedException {
        String path = "/products";
        Fetch fetch = new Fetch("https://dummyjson.com");
        var _response = fetch.fetch(path);
        while (_response.status().equals(Status.PENDING)) {
            Thread.sleep(1000);
        }
        assertEquals(Status.SUCCESS, _response.status());
        assertNotNull(_response.data(Products.class));
        assertNull(_response.error());
        var response = fetch.fetch(path, true);
        assertEquals(Status.PENDING, response.status());
    }

    @Test
    void malformedUrl() throws InterruptedException {
        Fetch fetch = new Fetch("htps://dummyjson.com");
        var response = fetch.fetch("/products");
        while (response.status().equals(Status.PENDING)) {
            Thread.sleep(1000);
        }
        assertEquals(Status.ERROR, response.status());
        assertNull(response.data(Object.class));
        assertNotNull(response.error());
    }
}
