package fakestore.view;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fakestore.Result;
import fakestore.controller.IController;
import fakestore.controller.IRoute;
import fakestore.model.ICart;
import fakestore.model.IResponse;
import fakestore.model.Product;
import fakestore.model.Products;
import processing.core.*;
import processing.event.MouseEvent;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Basic structure of the application:
 * <p>
 * Routes are defined as private methods that are passed to the controller by method reference.
 * When a route is called, it returns a Runnable that is called (by the controller) when the route is left and sets the currentRoute to a
 * {@link IPage} that is called every frame.
 * <p>
 * Layouts and their lifetime is managed by the specific route that called them.
 * Only exception to this is the header, which is always present.
 * <p>
 * Components are classes that compose {@link Element} and are used to render the UI.
 * They are configurable with a builder pattern and the creating party is responsible for their lifetime.
 * Usually, their draw method is called in the route. But they can also be used in headless mode.
 * This is useful when there are overlapping sections in the UI with the {@link Button} component.
 */
class View extends PApplet implements IView {
    // singleton
    private static final View instance = new View();
    private boolean isTrackpad = false;
    private IController controller;
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
    private final Toasts toasts = new Toasts(this);
    private Optional<Button> goToTop = Optional.empty();

    private View() {
    }


//------------------------------------------------------     PROCESSING     ------------------------------------------------------

    @Override
    public void settings() {
        size(1280, 720);
    }

    @Override
    public void setup() {
//        PSurfaceAWT awtSurface = (PSurfaceAWT) surface;
//        Frame frame = ((PSurfaceAWT.SmoothCanvas) awtSurface.getNative()).getFrame(); TODO change the title bar color if can figure out how
        windowResizable(false);
        surface.setTitle("FakeStore");
        surface.setIcon(loadImage("icon.png"));
        for (int i = 300; i <= 800; i += 100) fonts.put(String.valueOf(i), createFont(i + ".ttf", 32));
        noStroke();
        rectMode(CORNER);
        textAlign(LEFT, TOP);
        ellipseMode(CORNER);
        colorMode(RGB, 255, 255, 255, 1.0f);
        strokeWeight(2);
        loadShape("stars/empty.svg");
        loadShape("stars/half.svg");
        loadShape("stars/full.svg");
        // thumbnail mask to round corners
        thumbnailMask = createGraphics(160, 160);
        thumbnailMask.beginDraw();
        thumbnailMask.background(0);
        thumbnailMask.fill(255);
        thumbnailMask.rect(0, 0, 160, 160, 10);
        thumbnailMask.endDraw();
        // main mask to round corners
        mainMask = createGraphics(224, 224);
        mainMask.beginDraw();
        mainMask.background(0);
        mainMask.fill(255);
        mainMask.rect(0, 0, 224, 224, 10);
        mainMask.endDraw();
        // preview mask to round corners
        previewMask = createGraphics(36, 36);
        previewMask.beginDraw();
        previewMask.background(0);
        previewMask.fill(255);
        previewMask.rect(0, 0, 36, 36, 10);
        previewMask.endDraw();
        background(rgb(23, 23, 23));
        header();
        controller.navigateTo("home");
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
        goToTop.ifPresent(Button::draw);
        noStroke();
        strokeWeight(2);
        toasts.draw();
    }

//------------------------------------------------------     UTILITIES     ------------------------------------------------------

    @Override
    public PShape loadShape(String filename) {
        return svgs.computeIfAbsent(filename, super::loadShape);
    }

    void requestImageAndPreview(String url) {
        images.computeIfAbsent(url, (s) -> {
            Thread thread = new Thread(() -> {
                PImage src = loadImage(url, "jpg");
                if (null == src) {
                    thumbnails.put(url, Result.error("Image could not be loaded"));
                    return;
                }
                PImage image = createImage(224, 224, ARGB);
                PImage preview = createImage(36, 36, ARGB);
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

    Result<PImage, String> getImage(String url) {
        return images.getOrDefault(url, Result.error("request was not made"));
    }

    Result<PImage, String> getPreviewImage(String url) {
        return previewImages.getOrDefault(url, Result.error("request was not made"));
    }

    void requestThumbnail(String url) {
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

    Result<PImage, String> getThumbnail(String url) {
        return thumbnails.getOrDefault(url, Result.error("request was not made"));
    }

    PFont getFont(String s) {
        return fonts.get(s);
    }

    int rgb(int r, int g, int b, float a) {
        return color(r, g, b, a);
    }

    int rgb(int r, int g, int b) {
        return color(r, g, b);
    }

    void toast(String message) {
        toasts.add(message);
    }

    String[] lineWrap(String text, int width, int textSize, int lineCount) {
        if (text.isEmpty()) return new String[]{""};
        if (lineCount < 1) throw new IllegalArgumentException("lineCount must be greater than 0");
        if (lineCount == 1) return new String[]{truncate(text, width, textSize, true)};
        // text size effects text width
        pushStyle();
        textSize(textSize);
        // early return if text fits in one line
        if (textWidth(text) <= width) {
            popStyle();
            return new String[]{text};
        }
        ArrayList<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
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
                if (i == words.length - 1) {
                    lines.add(line.toString());
                    break;
                }
                continue;
            }
            // else, add line to lines and start new line
            lines.add(line.toString());
            // if lineCount is reached, truncate last line and break
            if (lines.size() == lineCount) {
                // if word is last word, no need to truncate
//                System.out.println(word + " " + words[words.length - 1]);
//                if (word.equals(words[words.length - 1])) break;
                lines.set(lines.size() - 1, truncate(line.toString(), width, textSize, false));
                break;
            }
            // if word doesn't fit in one line
            if (textWidth(word) > width) break;
            line = new StringBuilder(word);
        }
        popStyle();
        return lines.toArray(String[]::new);
    }

    String truncate(String text, int width, int textSize, boolean optional) {
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

    String getFormatted(String categories) {
        StringBuilder formatted = new StringBuilder();
        for (var s : categories.split("-")) {
            formatted.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
        }
        return formatted.toString();
    }

    Result<String, Boolean> safeGet(JsonObject json, String key) {
        if (json == null || !json.has(key)) return Result.error(false);
        JsonElement element = json.get(key);
        if (element.isJsonNull() || !element.isJsonPrimitive()) return Result.error(false);
        return Result.ok(element.getAsString());
    }

    private void setPageHeight(int pageHeight) {
        scroll = 65;
        if (pageHeight > height - 65) {
            goToTop = Optional.of(new Button()
                    .getEl()
                    .setX(width - 64).setY(height - 64)
                    .setWidth(32).setHeight(32).getOwner()
                    .setZ(99)
                    .setBgColor(rgb(251, 146, 60))
                    .setLabel("^").setLabelColor(rgb(38, 38, 38)).setLabelSize(20).setFont(fonts.get("800"))
                    .setCallback(() -> scroll = 65));
        } else {
            goToTop.ifPresent(Button::cleanUp);
            goToTop = Optional.empty();
        }
        this.pageHeight = pageHeight;
    }

    private void setPageHeight() {
        setPageHeight(height - 65);
    }

//------------------------------------------------------     LAYOUTS     ------------------------------------------------------

    private void header() {
        // headless mode: this button only needs to exist as a clickable element but doesn't need to be drawn
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

            logo.draw();
            search.draw();
            // balance
            double balance = controller.getBalance();
            fill(rgb(255, 255, 255));
            textFont(fonts.get("600"), 20);
            textAlign(RIGHT, CENTER);
            text(NumberFormat.getCurrencyInstance(Locale.GERMANY).format(balance), cart.getEl().getX() - 16, 32);
            textAlign(LEFT, TOP);
            cart.draw();
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

        IntStream.range(0, products.length).forEach(i -> {
            products[i] = new ProductCard(String.valueOf((int) Math.floor(random(1, 100))))
                    .getCard().getEl()
                    .setX(320).setY(16 + 24 + 16 + i * (200 + 32))
                    .setWidth(640).setHeight(160)
                    .getOwner()
                    .setZ(0).getOwner();
            products[i].init();
        });

        setPageHeight(16 + 24 + 16 + products.length * (products[0].getHeight() + 32));

        Runnable layoutCleanUp = defaultLayout();

        currentRoute = () -> {
            int y = scroll;
            fill(rgb(255, 255, 255));
            textFont(fonts.get("800"), 32);
            text("Home", 320, 16 + y);
            Arrays.stream(products).forEach(product -> {
                product.getCard().getEl().setParentY(y);
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

        setPageHeight();

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
                        setPageHeight(16 + 32 + 16 + productList.getHeight());
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

        setPageHeight();

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
                        setPageHeight(16 + 32 + 16 + productList.getHeight());
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

        ICart cart = controller.getCart();

        Carousel carousel = new Carousel().getEl()
                .setParentX(320)
                .setX(0).setY(16)
                .setWidth(224).setHeight(224)
                .getOwner();

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> description = new ArrayList<>();
        String[] price = new String[1];

        Rating rating = new Rating().getEl()
                .setParentX(320 + 224 + 8)
                .setY(16)
                .setWidth(120).setHeight(32)
                .getOwner();

        Loading loading = new Loading().getEl()
                .setX(320).setY(65 + 16 + 32)
                .setWidth(640).setHeight(450)
                .getOwner();

        CartButtons cartButtons = new CartButtons().getEl()
                .setParentX(320)
                .setY(carousel.getEl().getY() + carousel.getHeight() + 16)
                .setWidth(640).setHeight(32)
                .getOwner();
        cartButtons.init();

        Runnable layoutCleanUp = defaultLayout();

        setPageHeight();

        currentRoute = () -> {
            int y = scroll;
            switch (response.status()) {
                case PENDING -> loading.draw();
                case ERROR -> controller.navigateTo("error", "error", response.error());
                case SUCCESS -> {
                    Product product = response.data(Product.class);
                    if (!carousel.isReady()) {
                        carousel.setUrls(product.images());
                        title.addAll(List.of(lineWrap(product.title(), 640 - carousel.getEl().getWidth() - 32, 32, 2)));
                        rating.setRating(product.rating());
                        description.addAll(List.of(lineWrap(product.description(), 640 - carousel.getEl().getWidth() - 32, 20, 5)));
                        price[0] = NumberFormat.getCurrencyInstance(Locale.GERMANY).format(product.price());
                        cartButtons.setProduct(product);
                        setPageHeight(16 + 32 + 16 + carousel.getHeight());
                        return;
                    }
                    // images
                    carousel.getEl().setParentY(y)
                            .getOwner().draw();
                    int x = carousel.getEl().getX() + carousel.getEl().getWidth() + 8;
                    // brand
                    fill(rgb(255, 255, 255, 0.75f));
                    textFont(fonts.get("600"), 16);
                    text(response.data(Product.class).brand(), x, 16 + y);
                    int currentHeight = 16 + 16 + 8;
                    // title
                    fill(rgb(255, 255, 255));
                    textFont(fonts.get("800"), 32);
                    for (int i = 0; i < title.size(); i++) {
                        text(title.get(i), x, currentHeight + y + i * 32);
                    }
                    currentHeight += 8 + title.size() * 32;
                    // rating
                    rating.getEl().setParentY(y).setY(currentHeight)
                            .getOwner().draw();
                    // stock
                    fill(rgb(255, 255, 255, 0.75f));
                    textFont(fonts.get("400"), 20);
                    textAlign(RIGHT, TOP);
                    text("Stock: " + (product.stock() - cart.getBoughtQuantity(product)), 320 + 640, currentHeight + y);
                    textAlign(LEFT, TOP);
                    currentHeight += rating.getEl().getHeight() + 8;
                    // description
                    fill(rgb(255, 255, 255, 0.75f));
                    textFont(fonts.get("400"), 20);
                    for (int i = 0; i < description.size(); i++) {
                        text(description.get(i), x, currentHeight + y + i * 20);
                    }
                    // price
                    fill(rgb(224, 40, 18));
                    textFont(getFont("700"), 32);
                    textAlign(RIGHT, BOTTOM);
                    text(price[0], 320 + 640, carousel.getHeight() + y + 16);
                    textAlign(LEFT, TOP);
                    // cart buttons
                    cartButtons.getEl().setParentY(y)
                            .getOwner().draw();
                }
            }
        };

        return () -> {
            layoutCleanUp.run();
            carousel.cleanUp();
            cartButtons.cleanUp();
        };
    }

    private Runnable cart(JsonObject args) {
        ICart cart = controller.getCart();

        int[] i = {0};

        CartCard[] cards = Arrays.stream(cart.getProductsInCart()).map((product) -> {
            CartCard card = new CartCard(product)
                    .getCard().getEl()
                    .setX(320).setY(16 + i[0] * (200 + 32))
                    .setWidth(640).setHeight(160)
                    .getOwner()
                    .setZ(0).getOwner();
            card.init();
            i[0]++;
            return card;
        }).toArray(CartCard[]::new);

        Button goHome = new Button()
                .getEl()
                .setParentX(0).setParentY(65)
                .setX(width / 2 - 64).setY(height / 2 - 16)
                .setWidth(128).setHeight(32).getOwner()
                .setZ(0)
                .setLabel("Go Home").setLabelSize(20).setFont(fonts.get("800"))
                .setBgColor(rgb(251, 146, 60)).setLabelColor(rgb(0, 0, 0))
                .setCallback(() -> controller.navigateTo("home"));

        if (cards.length != 0) setPageHeight(16 + cards.length * (200 + 32));
        else setPageHeight();

        Runnable layoutCleanUp = defaultLayout();

        currentRoute = () -> {
            int y = scroll;
            if (cards.length == 0) {
                fill(rgb(255, 255, 255));
                textFont(fonts.get("800"), 32);
                text("Cart is empty", width / 2f - textWidth("Cart is empty") / 2, height / 2f - 48);
                goHome.draw();
                return;
            }
            Arrays.stream(cards).forEach(card -> {
                card.getCard().getEl().setParentY(y);
                card.draw();
            });
        };

        return () -> {
            Arrays.stream(cards).forEach(CartCard::cleanUp);
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

        setPageHeight();

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

    static View getInstance() {
        return instance;
    }

    IController getController() {
        return controller;
    }

    @Override
    public void setController(IController controller) {
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

interface IPage {
    void render();
}
