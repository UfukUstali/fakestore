package fakestore.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    @Test
    void setWrongArgs() {
        Model model = Model.reset();
        model.setArgs(new String[]{"abc"});
    }

    @Test
    void setNoArgs() {
        Model model = Model.reset();
        model.setArgs(new String[]{});
    }

    @Test
    void getTest() throws InterruptedException {
        Model model = Model.getInstance();
        IResponse response = model.get("/products/categories");
        assertEquals(Status.PENDING, response.status());
        while (response.status() == Status.PENDING) {
            Thread.sleep(100);
        }
        assertEquals(Status.SUCCESS, response.status());
    }
}
