package fakestore.view;

import java.util.ArrayList;
import java.util.List;

class Toasts {
    private final View view;
    private final ArrayList<Toast> toasts = new ArrayList<>();

    Toasts(View view) {
        this.view = view;
    }

    void add(String message) {
        toasts.add(new Toast(message, null));
    }

    void add(String message, String id) {
        if (id == null) {
            add(message);
            return;
        }
        for (Toast toast : toasts) {
            if (id.equals(toast.id)) {
                if (!toast.message.equals(message)) toast.message = message;
                toast.setTimeToLive(Toast.MAX_TIME_TO_LIVE);
                return;
            }
        }
        toasts.add(new Toast(message, id));
    }

    void draw() {
        ArrayList<Toast> toRemove = new ArrayList<>();
        List<Toast> reversed = toasts.reversed();
        for (int i = 0; i < reversed.size(); i++) {
            Toast toast = reversed.get(i);
            toast.draw(view.height - ((24 + 32) * (i + 1)));
            toast.setTimeToLive(toast.timeToLive - 100f / 6);
            if ((int) toast.timeToLive <= 0) toRemove.add(toast);
        }
        toRemove.forEach((toast) -> {
            toasts.remove(toast);
            toast.bgButton.cleanUp();
            toast.removeButton.cleanUp();
        });
    }

    private class Toast {
        // we don't want to initialize a new int every time a toast is created
        private static final int MAX_TIME_TO_LIVE = 3000;
        private String message;
        private final String id;
        private final int width = 256;
        private final int height = 32;
        private final int x = view.width - (width + 32);
        private float timeToLive = MAX_TIME_TO_LIVE;
        private float timeToLivePercent = ((timeToLive * 100) / MAX_TIME_TO_LIVE) / 100;

        private final Button bgButton = new Button().getEl()
                .setX(x)
                .setWidth(width).setHeight(height).getOwner()
                .setZ(998);

        private final Button removeButton = new Button()
                .getEl()
                .setParentX(x)
                .setX(width - 28).setY((height - 4) / 2 - 8)
                .setWidth(12).setHeight(16).getOwner()
                .setZ(999)
                .setImage(view.loadShape("close.svg"))
                .setCallback(() -> {
                    timeToLive = 0;
                    timeToLivePercent = 0;
                });

        private Toast(String message, String id) {
            this.message = view.truncate(message, width - 32, 16, true);
            this.id = id;
        }

        private void setTimeToLive(float timeToLive) {
            this.timeToLive = timeToLive;
            this.timeToLivePercent = ((timeToLive * 100) / MAX_TIME_TO_LIVE) / 100;
        }

        private void draw(int y) {
            // progress bar
            view.fill(view.rgb(251, 146, 60));
            view.rect(x, y + 4, width * timeToLivePercent, height - 4, 10);
            // bg
            view.fill(view.rgb(23, 23, 23));
            view.stroke(view.rgb(38, 38, 38));
            view.rect(x, y, width, height - 4, 10);
            view.noStroke();
            bgButton.getEl().setY(y);
            // bgButton is headless so we don't draw it
            // text
            view.fill(view.rgb(255, 255, 255));
            view.textFont(view.getFont("400"), 16);
            view.text(message, x + 16, y + 6);
            // close button
            removeButton.getEl().setParentY(y);
            removeButton.draw();
        }
    }

}

