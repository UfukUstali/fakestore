package fakestore.view;

class CategoryList {
    private final View view = View.getInstance();
    private final Element<CategoryList> el = new Element<>(this);
    private Button[] links;
    private final String currentCategory;

    CategoryList() {
        if (view.getController().getCurrentPath().equals("category")) {
            currentCategory = view.getController().getCurrentArgs().get("category").getAsString();
            return;
        }
        this.currentCategory = null;
    }

    void draw() {
        for (int i = 0; i < links.length; i++) {
            view.stroke(view.rgb(38, 38, 38));
            view.strokeWeight(1);
            links[i].getEl().setY(i * (24 + 8)).getOwner().draw();
            view.noStroke();
            view.strokeWeight(2);
        }
    }

    CategoryList setList(String[] categories) {
        this.links = new Button[categories.length];
        for (int i = 0; i < categories.length; i++) {
            String formatted = view.getFormatted(categories[i]);
            boolean isCurrent = categories[i].equals(currentCategory);
            int finalI = i;
            links[i] = new Button().getEl()
                    .setParentX(el.getX()).setParentY(el.getY())
                    .setWidth(el.getWidth()).setHeight(24).getOwner()
                    .setBgColor(isCurrent ? view.rgb(251, 146, 60) : view.rgb(23, 23, 23))
                    .setLabel(formatted).setLabelColor(isCurrent ? view.rgb(0, 0, 0) : view.rgb(255, 255, 255))
                    .setLabelSize(16).setFont(view.getFont("600")).setLabelAlign()
                    .setCallback(() -> view.getController().navigateTo("category", "category", categories[finalI]));
        }
        return this;
    }

    Element<CategoryList> getEl() {
        return el;
    }

    void cleanUp() {
        if (links == null) return;
        for (Button link : links) {
            link.cleanUp();
        }
    }

    boolean isReady() {
        return links != null;
    }
}
