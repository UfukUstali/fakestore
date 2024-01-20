import fakestore.controller.Controller;
import fakestore.model.Model;
import fakestore.view.IView;
import fakestore.view.View;
import processing.core.PApplet;

public class Main {
    public static void main(String[] args) {
        String[] viewArgs = new String[1];
        String[] modelArgs = new String[1];

        for (String arg : args) {
            int splitIndex = arg.indexOf("=");
            if (splitIndex != -1) {
                String param = arg.substring(0, splitIndex);
                String value = arg.substring(splitIndex + 1);
                System.out.println(param + " = " + value);
                switch (param) {
                    case "trackpad" -> viewArgs[0] = value;
                    case "balance" -> modelArgs[0] = value;
                    default -> System.err.println("Invalid argument: " + arg);
                }
                continue;
            }
            System.err.println("Invalid argument: " + arg);
        }

        // MVC
        Model model = Model.getInstance();
        IView view = View.getPublicInstance();
        Controller controller = Controller.getInstance();
        // mvc binding
        controller.setView(view);
        controller.setModel(model);
        view.setController(controller);
        // args
        view.setArgs(viewArgs);
        model.setArgs(modelArgs);

        PApplet.runSketch(new String[]{"FakeStore"}, View.getPApplet());
    }
}
