package fakestore.view;

class CategoryList {
    private static final View view = View.getInstance();
    private final Element<CategoryList> el = new Element<>(this);
    private Button[] links;
    private final String currentCategory;

    protected CategoryList() {
        if (view.getController().getCurrentPath().equals("category")) {
            currentCategory = view.getController().getCurrentArgs().get("category").getAsString();
            return;
        }
        this.currentCategory = null;
    }

    protected CategoryList(String[] categories) {
        this();
        setList(categories);
    }

    protected void draw() {
        for (int i = 0; i < links.length; i++) {
            view.stroke(view.rgb(38, 38, 38));
            view.strokeWeight(1);
            links[i].getEl().setY(i * (24 + 8)).getOwner().draw();
            view.noStroke();
            view.strokeWeight(2);
        }
    }

    protected CategoryList setList(String[] categories) {
        this.links = new Button[categories.length];
        for (int i = 0; i < categories.length; i++) {
            String formatted = view.getFormatted(categories[i]);
            boolean isCurrent = categories[i].equals(currentCategory);
            int finalI = i;
            links[i] = new Button().getEl()
                    .setParentX(el.getX()).setParentY(el.getY())
                    .setWidth(el.getWidth()).setHeight(24).getOwner()
                    .setBgColor(isCurrent ? view.rgb(251, 146, 60) : view.rgb(23, 23, 23))
                    .setLabel(formatted.toString()).setLabelColor(isCurrent ? view.rgb(0, 0, 0) : view.rgb(255, 255, 255))
                    .setLabelSize(16).setFont(view.getFont("600")).setLabelAlign(view.LEFT)
                    .setCallback(() -> view.getController().navigateTo("category", "category", categories[finalI]));
        }
        return this;
    }

    protected Element<CategoryList> getEl() {
        return el;
    }

    protected void cleanUp() {
        for (Button link : links) {
            link.cleanUp();
        }
    }

    protected boolean isReady() {
        return links != null;
    }
}
