package fakestore.view;

import processing.core.PShape;

class Rating {
    private final View view = View.getInstance();
    private final Element<Rating> el = new Element<>(this);
    private final PShape emptyStar = view.loadShape("stars/empty.svg");
    private final PShape halfStar = view.loadShape("stars/half.svg");
    private final PShape fullStar = view.loadShape("stars/full.svg");
    // max of 5 stars = 10 half stars
    private int halfStarCount;

    void draw() {
        int x = el.getX();
        int y = el.getY();
        int starW = 24;
        int starH = 21;
        int starX = x;
        int before = halfStarCount;
        // stars
        for (int i = 0; i < 5; i++) {
            if (before > 1) {
                view.shape(fullStar, starX, y, starW, starH);
                before -= 2;
            } else if (before == 1) {
                view.shape(halfStar, starX, y, starW, starH);
                before--;
            } else {
                view.shape(emptyStar, starX, y, starW, starH);
            }
            starX += starW;
        }
    }

    Element<Rating> getEl() {
        return el;
    }

    Rating setRating(float rating) {
        // max is 5
        this.halfStarCount = Math.round(rating * 2);
//        System.out.println(halfStarCount);
        return this;
    }
}
