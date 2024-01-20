package fakestore.view;

import processing.core.PShape;

class Rating {
    private static final View view = View.getInstance();
    private final Element<Rating> el = new Element<>(this);
    private static PShape emptyStar;
    private static PShape halfStar;
    private static PShape fullStar;
    // max is 5
    private float rating;
    // max of 5 stars = 10 half stars
    private int halfStarCount;

    protected void draw() {
        int x = el.getX();
        int y = el.getY();
        int w = el.getWidth();
        int h = el.getHeight();
        int starW = 24;
        int starH = 21;
        int starX = x;
        int starY = y;
        int before = halfStarCount;
        // stars
        for (int i = 0; i < 5; i++) {
            if (before > 1) {
                view.shape(fullStar, starX, starY, starW, starH);
                before -= 2;
            } else if (before == 1) {
                view.shape(halfStar, starX, starY, starW, starH);
                before--;
            } else {
                view.shape(emptyStar, starX, starY, starW, starH);
            }
            starX += starW;
        }
    }

    protected Element<Rating> getEl() {
        return el;
    }

    protected Rating setRating(float rating) {
        this.rating = rating;
        this.halfStarCount = Math.round(rating * 2);
//        System.out.println(halfStarCount);
        return this;
    }


    protected static void load() {
        emptyStar = view.loadShape("stars/empty.svg");
        halfStar = view.loadShape("stars/half.svg");
        fullStar = view.loadShape("stars/full.svg");
    }
}
