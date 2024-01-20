package fakestore.view;

import com.google.gson.*;
import fakestore.Result;
import fakestore.controller.Controller;
import fakestore.controller.IRoute;
import fakestore.model.IResponse;
import fakestore.model.Products;
import fakestore.model.Product;
import processing.core.*;
import processing.event.MouseEvent;

import java.util.*;
import java.util.function.Function;

public class View extends PApplet implements IView {
    private static final View instance = new View();
    private boolean isTrackpad = false;
    private Controller controller;
    private IPage header;
    private Optional<IPage> layout = Optional.empty();
    private IPage currentRoute;
    private int pageHeight = 0;
    private int scroll = 65;
    private final Map<String, PFont> fonts = new HashMap<>();
    private final Map<String, Result<PImage, String>> images = new HashMap<>();
    private final Map<String, Result<PImage, String>> previewImages = new HashMap<>();
    private final Map<String, PShape> svgs = new HashMap<>();
    private final Map<String, Result<PImage, String>> thumbnails = new HashMap<>();
    private PGraphics thumbnailMask;
    private PGraphics mainMask;
    private PGraphics previewMask;
    private final Toasts toasts = Toasts.getInstance(this);
    private Button goToTop;

    private View() {
    }


//------------------------------------------------------     PROCESSING     ------------------------------------------------------

    @Override
    public void settings() {
        size(1280, 720);
    }

    @Override
    public void setup() {
        for (int i = 300; i <= 800; i += 100) fonts.put(String.valueOf(i), createFont(i + ".ttf", 32));
        noStroke();
        rectMode(CORNER);
        textAlign(LEFT, TOP);
        ellipseMode(CORNER);
        colorMode(RGB, 255, 255, 255, 1.0f);
        strokeWeight(2);
        Rating.load();
        background(rgb(23, 23, 23));
        header();
//        controller.navigateTo("home");
//        controller.navigateTo("error", "error", "A special error occurred");
//        controller.navigateTo("product", "product", "1");
//        controller.navigateTo("search", "term", "a");
        controller.navigateTo("category", "category", "smartphones");
        goToTop = new Button()
                .getEl()
                .setX(width - 64).setY(height - 64)
                .setWidth(32).setHeight(32).getOwner()
                .setZ(99)
                .setBgColor(rgb(251, 146, 60))
                .setLabel("^").setLabelColor(rgb(38, 38, 38)).setLabelSize(20).setFont(fonts.get("800"))
                .setCallback(() -> scroll = 65);
        // thumbnail mask to round corners
        thumbnailMask = createGraphics(160, 160);
        thumbnailMask.beginDraw();
        thumbnailMask.background(0);
        thumbnailMask.fill(255);
        thumbnailMask.rect(0, 0, 160, 160, 10);
        thumbnailMask.endDraw();
        // main mask to round corners
        mainMask = createGraphics(320, 320);
        mainMask.beginDraw();
        mainMask.background(0);
        mainMask.fill(255);
        mainMask.rect(0, 0, 320, 320, 10);
        mainMask.endDraw();
        // preview mask to round corners
        previewMask = createGraphics(48, 48);
        previewMask.beginDraw();
        previewMask.background(0);
        previewMask.fill(255);
        previewMask.rect(0, 0, 48, 48, 10);
        previewMask.endDraw();
    }

    @Override
    public void mousePressed() {
        if (mouseButton == LEFT) controller.mousePressed(mouseX, mouseY);
    }

    @Override
    public void keyTyped() {
        controller.keyTyped(key, keyCode);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        if ((pageHeight + 65) <= height) return;
        scroll = constrain(scroll + event.getCount() * (isTrackpad ? -10 : 10), 65 - (pageHeight + 65 - height), 65);
    }

    @Override
    public void draw() {
        background(rgb(23, 23, 23));
        layout.ifPresent(IPage::render);
        currentRoute.render();
        header.render();
        stroke(rgb(38, 38, 38));
        strokeWeight(1);
        goToTop.draw();
        noStroke();
        strokeWeight(2);
        toasts.draw();
    }

//------------------------------------------------------     UTILITIES     ------------------------------------------------------

    @Override
    public PShape loadShape(String filename) {
        return svgs.computeIfAbsent(filename, super::loadShape);
    }

    protected void requestImageAndPreview(String url) {
        images.computeIfAbsent(url, (s) -> {
            Thread thread = new Thread(() -> {
                PImage src = loadImage(url, "jpg");
                if (null == src) {
                    thumbnails.put(url, Result.error("Image could not be loaded"));
                    return;
                }
                PImage image = createImage(320, 320, ARGB);
                PImage preview = createImage(48, 48, ARGB);
                int a = Math.min(src.width, src.height);

                image.copy(src, 0, 0, a, a, 0, 0, image.width, image.height);
                preview.copy(src, 0, 0, a, a, 0, 0, preview.width, preview.height);

                image.mask(mainMask);
                preview.mask(previewMask);
                images.put(url, Result.ok(image));
                previewImages.put(url, Result.ok(preview));
            });
            thread.start();
            return Result.error("loading");
        });
    }

    protected Result<PImage, String> getImage(String url) {
        return images.getOrDefault(url, Result.error("request was not made"));
    }

    protected Result<PImage, String> getPreviewImage(String url) {
        return previewImages.getOrDefault(url, Result.error("request was not made"));
    }

    protected void requestThumbnail(String url) {
        thumbnails.computeIfAbsent(url, (s) -> {
            Thread thread = new Thread(() -> {
                PImage src = loadImage(url, "jpg");
                if (null == src) {
                    thumbnails.put(url, Result.error("Image could not be loaded"));
                    return;
                }
                PImage image = createImage(160, 160, ARGB);
                int a = Math.min(src.width, src.height);

                image.copy(src, 0, 0, a, a, 0, 0, image.width, image.height);

                image.mask(thumbnailMask);
                thumbnails.put(url, Result.ok(image));
            });
            thread.start();
            return Result.error("loading");
        });
    }

    protected Result<PImage, String> getThumbnail(String url) {
        return thumbnails.getOrDefault(url, Result.error("request was not made"));
    }

    protected PFont getFont(String s) {
        return fonts.get(s);
    }

    protected int rgb(int r, int g, int b, float a) {
        return color(r, g, b, a);
    }

    protected int rgb(int r, int g, int b) {
        return color(r, g, b);
    }

    protected void toast(String message) {
        toasts.add(message);
    }

    protected void toast(String message, String id) {
        toasts.add(message, id);
    }

    protected String[] lineWrap(String text, int width, int textSize, int lineCount) {
        // text size effects text width
        textSize(textSize);
        // early return if text fits in one line
        if (textWidth(text) <= width) {
            return new String[]{text};
        }
        ArrayList<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            // if line is empty, add word
            if (line.isEmpty()) {
                float textWidth = textWidth(word);
                // if word doesn't fit in one line
                if (textWidth > width) break;
                line.append(word);
                continue;
            }
            float textWidth = textWidth(line + " " + word);
            // if word fits in line, add word
            if (textWidth <= width) {
                line.append(" ").append(word);
                // if word is last word, add line to lines and break
                if (word.equals(words[words.length - 1])) {
                    lines.add(line.toString());
                    break;
                }
                continue;
            }
            // if word doesn't fit in line, add line to lines and start new line
            lines.add(line.toString());
            // if lineCount is reached, truncate last line and break
            if (lines.size() == lineCount) {
                // if word is last word, no need to truncate
                if (word.equals(words[words.length - 1])) break;
                lines.set(lines.size() - 1, truncate(line.toString(), width, textSize, false));
                break;
            }
            // if word doesn't fit in one line
            if (textWidth(word) > width) break;
            line = new StringBuilder(word);
        }
        return lines.toArray(String[]::new);
    }

    protected String truncate(String text, int width, int textSize, boolean optional) {
        // text size effects text width
        textSize(textSize);
        String suffix = "...";
        Function<String, String> goBackwards = (String t) -> {
            int i = t.length() - 1;
            while (textWidth(t + suffix) > width) {
                t = t.substring(0, i);
                i--;
            }
            return t + "...";
        };
        if (textWidth(text) <= width) {
            if (optional) return text;
            return goBackwards.apply(text);
        }
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (!line.isEmpty()) line.append(" ");
            line.append(word);
            if (textWidth(line + suffix) > width) {
                return goBackwards.apply(line.toString());
            }
        }
        // this should never happen
        return line.toString();
    }

    protected String getFormatted(String categories) {
        StringBuilder formatted = new StringBuilder();
        for (var s : categories.split("-")) {
            formatted.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
        }
        return formatted.toString();
    }

    protected Result<String, Boolean> safeGet(JsonObject json, String key) {
        if (json == null || !json.has(key)) return Result.error(false);
        JsonElement element = json.get(key);
        if (element.isJsonNull() || !element.isJsonPrimitive()) return Result.error(false);
        return Result.ok(element.getAsString());
    }

//------------------------------------------------------     LAYOUTS     ------------------------------------------------------

    private void header() {
        new Button()
                .getEl()
                .setX(0).setY(0)
                .setWidth(width).setHeight(64).getOwner()
                .setZ(98);

        Button logo = new Button()
                .getEl()
                .setX(32).setY(16)
                .setWidth(100).setHeight(32).getOwner()
                .setZ(99)
                .setLabel("FakeStore").setLabelSize(20).setFont(fonts.get("800"))
                .setLabelColor(rgb(255, 255, 255))
                .setCallback(() -> controller.navigateTo("home"));

        Input search = new Input()
                .getEl()
                .setWidth(400).setHeight(32)
                .setX(164).setY(16).getOwner()
                .setZ(999)
                .setPlaceholder("Search")
                .setInputSize(20).setFont(fonts.get("600"))
                .setAction((input) -> controller.navigateTo("search", "term", input));

        Button cart = new Button()
                .getEl()
                .setX(width - 64).setY(20)
                .setWidth(24).setHeight(24).getOwner()
                .setZ(99)
                .setImage(loadShape("cart.svg"))
                .setCallback(() -> controller.navigateTo("cart"));

        header = () -> {
            // bg
            fill(rgb(23, 23, 23, 0.9f));
            rect(0, 0, width, 64);
            // border
            stroke(rgb(38, 38, 38));
            line(0, 64, width, 64);
            noStroke();

            cart.draw();
            logo.draw();
            search.draw();
        };
    }

    private Runnable defaultLayout() {
        Loading loading = new Loading()
                .getEl()
                .setX(16).setY(16 + 65)
                .setWidth(288).setHeight(450).getOwner();
        IResponse categories = controller.get("/products/categories");
        CategoryList categoryList = new CategoryList()
                .getEl()
                .setX(16).setY(16 + 65)
                .setWidth(288)
                .getOwner();

        CartSummary cartSummary = new CartSummary();

        layout = Optional.of(() -> {
            // left (categories)
            switch (categories.status()) {
                case PENDING -> loading.draw();
                case ERROR -> toast(categories.error());
                case SUCCESS -> {
                    if (!categoryList.isReady()) categoryList.setList(categories.data(String[].class));
                    categoryList.draw();
                }
            }
            // right (cart summary)
            cartSummary.draw();
        });

        return () -> {
            categoryList.cleanUp();
            cartSummary.cleanUp();
        };
    }

//------------------------------------------------------     PAGES/ROUTES     ------------------------------------------------------

    private Runnable home(JsonObject args) {
        ProductCard[] products = new ProductCard[15];

        for (int i = 0; i < products.length; i++) {
            products[i] = new ProductCard(String.valueOf((int) Math.floor(random(1, 100))))
                    .getEl()
                    .setX(width / 2 - 320).setY(16 + i * (200 + 32))
                    .setWidth(640).setHeight(160)
                    .getOwner()
                    .setZ(0);
            products[i].init();
        }

        scroll = 65;

        pageHeight = 16 + products.length * (products[0].getHeight() + 32);

        Runnable layoutCleanUp = defaultLayout();

        currentRoute = () -> {
            int y = scroll;
            Arrays.stream(products).forEach(product -> {
                product.getEl().setParentY(y);
                product.draw();
            });
        };

        return () -> {
            Arrays.stream(products).forEach(ProductCard::cleanUp);
            layoutCleanUp.run();
        };
    }

    private Runnable search(JsonObject args) {
        var term = safeGet(args, "term");
        var skip = safeGet(args, "skip");
        skip = switch (skip.status()) {
            case ERROR -> Result.ok("0");
            case OK -> {
                try {
                    Integer.parseInt(skip.value());
                    yield skip;
                } catch (NumberFormatException e) {
                    yield Result.ok("0");
                }
            }
        };

        IResponse response = switch (term.status()) {
            case ERROR -> IResponse.error("Search term not found");
            case OK -> controller.get("/products/search?q=" + term.value() + "&skip=" + skip.value());
        };

        Loading loading = new Loading()
                .getEl()
                .setX(320).setY(65 + 16 + 32)
                .setWidth(640).setHeight(450).getOwner();

        ProductList productList = new ProductList().getEl()
                .setParentY(65)
                .setX(width / 2 - 320).setY(32)
                .getOwner();

        Runnable layoutCleanUp = defaultLayout();

        scroll = 65;

        pageHeight = height - 65;

        currentRoute = () -> {
            int y = scroll;
            fill(rgb(255, 255, 255));
            textFont(fonts.get("800"), 24);
            text(truncate("Results for: " + term.value(), 640, 24, true), 320, 16 + y);
            switch (response.status()) {
                case PENDING -> loading.draw();
                case ERROR -> controller.navigateTo("error", "error", response.error());
                case SUCCESS -> {
                    if (!productList.isReady()) {
                        productList.setList(response.data(Products.class));
                        pageHeight = 16 + 32 + 16 + productList.getHeight();
                        return;
                    }
                    productList.getEl().setParentY(y)
                            .getOwner().draw();

                }
            }
        };

        return () -> {
            productList.cleanUp();
            layoutCleanUp.run();
        };
    }

    private Runnable category(JsonObject args) {
        var category = safeGet(args, "category");
        var skip = safeGet(args, "skip");
        skip = switch (skip.status()) {
            case ERROR -> Result.ok("0");
            case OK -> {
                try {
                    Integer.parseInt(skip.value());
                    yield skip;
                } catch (NumberFormatException e) {
                    yield Result.ok("0");
                }
            }
        };

        IResponse response = switch (category.status()) {
            case ERROR -> IResponse.error("Category not found");
            case OK -> controller.get("/products/category/" + category.value() + "?skip=" + skip.value());
        };

        Loading loading = new Loading()
                .getEl()
                .setX(320).setY(65 + 16 + 32)
                .setWidth(640).setHeight(450).getOwner();

        ProductList productList = new ProductList().getEl()
                .setParentY(65)
                .setX(width / 2 - 320).setY(48)
                .getOwner();

        Runnable layoutCleanUp = defaultLayout();

        scroll = 65;

        pageHeight = height - 65;

        currentRoute = () -> {
            int y = scroll;
            fill(rgb(255, 255, 255));
            textFont(fonts.get("800"), 32);
            text(getFormatted(category.value()), 320, 16 + y);
            switch (response.status()) {
                case PENDING -> loading.draw();
                case ERROR -> controller.navigateTo("error", "error", response.error());
                case SUCCESS -> {
                    if (!productList.isReady()) {
                        productList.setList(response.data(Products.class));
                        pageHeight = 16 + 32 + 16 + productList.getHeight();
                        return;
                    }
                    productList.getEl().setParentY(y)
                            .getOwner().draw();

                }
            }
        };

        return () -> {
            productList.cleanUp();
            layoutCleanUp.run();
        };
    }

    private Runnable product(JsonObject args) {
        var id = safeGet(args, "product");
        id = switch (id.status()) {
            case ERROR -> Result.error(false);
            case OK -> {
                try {
                    Integer.parseInt(id.value());
                    yield id;
                } catch (NumberFormatException e) {
                    yield Result.error(false);
                }
            }
        };

        IResponse response = switch (id.status()) {
            case ERROR -> IResponse.error("Product not found");
            case OK -> controller.get("/products/" + id.value());
        };

        Carousel carousel = new Carousel()
                .getEl()
                .setX(320).setY(65 + 16 + 32)
                .setWidth(320).setHeight(320).getOwner();

        Loading loading = new Loading()
                .getEl()
                .setX(320).setY(65 + 16 + 32)
                .setWidth(640).setHeight(450).getOwner();

        Runnable layoutCleanUp = defaultLayout();

        scroll = 65;

        pageHeight = height - 65;

        currentRoute = () -> {
            int y = scroll;
            switch (response.status()) {
                case PENDING -> loading.draw();
                case ERROR -> controller.navigateTo("error", "error", response.error());
                case SUCCESS -> {
                    if (!carousel.isReady()) {
                        carousel.setUrls(response.data(Product.class).images());
                        pageHeight = 16 + 32 + 16 + carousel.getHeight();
                        return;
                    }
                    carousel.getEl().setParentY(y)
                            .getOwner().draw();
                }
            }
        };

        return () -> {
            layoutCleanUp.run();
            carousel.cleanUp();
        };
    }

    private Runnable cart(JsonObject args) {
        CartSummary cartSummary = new CartSummary();
//        CartList cartList = new CartList();

        Runnable layoutCleanUp = defaultLayout();

        scroll = 65;

        pageHeight = height - 65;

        currentRoute = () -> {
            cartSummary.draw();
//            cartList.draw();
        };

        return () -> {
            cartSummary.cleanUp();
//            cartList.cleanUp();
            layoutCleanUp.run();
        };
    }

    private Runnable error(JsonObject args) {
        Result<String, Boolean> message = safeGet(args, "error");

        Button goHome = new Button()
                .getEl()
                .setParentX(0).setParentY(65)
                .setX(width / 2 - 64).setY(height / 2 - 16)
                .setWidth(128).setHeight(32).getOwner()
                .setZ(0)
                .setLabel("Go Home").setLabelSize(20).setFont(fonts.get("800"))
                .setBgColor(rgb(251, 146, 60)).setLabelColor(rgb(0, 0, 0))
                .setCallback(() -> controller.navigateTo("home"));

        scroll = 65;

        pageHeight = height - 65;

        currentRoute = () -> {
            fill(rgb(255, 255, 255));
            textFont(fonts.get("800"), 32);
            text("Error", width / 2f - textWidth("Error") / 2, height / 2f - 48);
            textFont(fonts.get("500"), 20);
            String m = switch (message.status()) {
                case ERROR -> "An error occurred";
                case OK -> message.value();
            };
            text(m, width / 2f - textWidth(m) / 2, height / 2f);
            goHome.draw();
        };

        return goHome::cleanUp;
    }

//------------------------------------------------------     MISCELLANEOUS     ------------------------------------------------------

    protected static View getInstance() {
        return instance;
    }

    protected Controller getController() {
        return controller;
    }

    public static IView getPublicInstance() {
        return instance;
    }

    public static PApplet getPApplet() {
        return instance;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void setArgs(String[] args) {
        isTrackpad = "true".equals(args[0]);
    }

    @Override
    public Map<String, IRoute> getRoutes() {
        return Map.ofEntries(
                Map.entry("home", this::home),
                Map.entry("search", this::search),
                Map.entry("category", this::category),
                Map.entry("product", this::product),
                Map.entry("cart", this::cart),
                Map.entry("error", this::error)
        );
    }
}
