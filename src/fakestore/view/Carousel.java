package fakestore.view;

import fakestore.Result;
import processing.core.PImage;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Carousel {
    private static final View view = View.getInstance();
    private final Element<Carousel> el = new Element<>(this);
    private String[] urls;
    private Result<PImage, String>[] previewImages;
    private Result<PImage, String>[] images;
    private Result<PImage, String> mainImage;
    private Button[] buttons;
    private Button prev;
    private Button next;


    protected Carousel() {
        this.mainImage = Result.error("loading");
    }

    protected void draw() {
        switch (mainImage.status()) {
            case ERROR -> mainImage =  images[0];
            case OK -> {
                view.image(mainImage.value(), el.getX(), el.getY(), el.getWidth(), el.getHeight());
                prev.getEl().setParentY(el.getY());
                next.getEl().setParentY(el.getY());
            }
        }
        int gap = (el.getWidth() - 48 * urls.length) / (urls.length - 1);
        int y = el.getHeight() + 16;
        for (int i = 0; i < urls.length; i++) {
            int x = (48 + gap) * i;
            switch (previewImages[i].status()) {
                case ERROR -> previewImages[i] = view.getPreviewImage(urls[i]);
                case OK -> view.image(previewImages[i].value(), el.getX() + x, el.getY() + y, 48, 48);
            }
            switch (images[i].status()) {
                case ERROR -> images[i] = view.getImage(urls[i]);
                case OK -> {}
            }
            // headless button
            buttons[i].getEl().setParentY(el.getY());
        }
    }

    protected void setUrls(String[] urls) {
        this.urls = urls;
        this.previewImages = new Result[urls.length];
        this.images = new Result[urls.length];
        this.buttons = new Button[urls.length];
        this.prev = new Button().getEl()
                .setParentX(el.getX())
                .setX(0).setY(0).setWidth(el.getWidth() / 2).setHeight(el.getHeight())
                .getOwner()
                .setZ(1)
                .setCallback(() -> {
                    int index = Arrays.stream(images).toList().indexOf(mainImage);
                    if (index == - 1) return;
                    mainImage = images[(index + images.length - 1) % images.length];
                });
        this.next = new Button().getEl()
                .setParentX(el.getX())
                .setX(el.getWidth() / 2).setY(0).setWidth(el.getWidth() / 2).setHeight(el.getHeight())
                .getOwner()
                .setCallback(() -> {
                    int index = Arrays.stream(images).toList().indexOf(mainImage);
                    if (index == - 1) return;
                    mainImage = images[(index + 1) % images.length];
                });
        int gap = (el.getWidth() - 48 * urls.length) / (urls.length - 1);
        int y = el.getHeight() + 16;
        for (int i = 0; i < urls.length; i++) {
            int x = (48 + gap) * i;
            previewImages[i] = images[i] = Result.error("loading");
            int finalI = i;
            buttons[i] = new Button().getEl()
                    .setParentX(el.getX())
                    .setX(x).setY(y)
                    .setWidth(48).setHeight(48)
                    .getOwner()
                    .setZ(1)
                    .setCallback(() -> mainImage = images[finalI]);
        }
        Arrays.stream(urls).forEach(view::requestImageAndPreview);
    }

    protected boolean isReady() {
        return urls != null;
    }

    protected Element<Carousel> getEl() {
        return el;
    }

    protected int getHeight() {
        return el.getHeight() + 16 + 48;
    }

    protected void cleanUp() {
        Arrays.stream(buttons).forEach(Button::cleanUp);
    }
}
