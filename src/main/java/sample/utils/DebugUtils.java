package sample.utils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Jan Fryblík
 * Date: 8/23/18
 * Time: 1:49 AM
 */
public final class DebugUtils {

    private DebugUtils() {

    }

    /**
     * Every child in node's tree is colored by different color. Then you can clearly
     * see boundaries of UI tree.
     *
     * @param node Starting point of the node's tree.
     */
    public static void colorizeNodeTree(Node node) {
        try {
            List<Color> colors = FxUtils.allColors();
            LinkedList<Color> colorList = new LinkedList<>(colors);

            node.setStyle("-fx-background-color: " + FxUtils.toRGBCode(colorList.get(0)));

            FxUtils.visitChildrenTree(node, visitedNode -> {
                if (colorList.isEmpty()) {
                    colorList.addAll(colors);
                }

                Color color = colorList.poll();
                visitedNode.setStyle("-fx-background-color: " + FxUtils.toRGBCode(color));
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void printStyleClasses(Node node) {
        ObservableList<String> styleClass = node.getStyleClass();

        String styles = "";
        for (int i = 0; i < styleClass.size(); i++) {
            String style = styleClass.get(i);
            styles += style;

            // if not last item
            if (styleClass.size() - 1 != i) {
                styles += ", ";
            }
        }
        System.out.println(node.toString() + " = " + styles);
    }

    public static void printStackTrace() {
        Exception exception = new Exception();
        exception.printStackTrace();
    }

    /**
     * Prints information into console about node which is clicked by mouse.
     *
     * @param node Parent of the node's tree which handles mouse events.
     */
    public static void printClickedNode(Node node) {
        if (Parent.class.isAssignableFrom(node.getClass())) {
            Parent parent = (Parent) node;
            for (Node child : parent.getChildrenUnmodifiable()) {
                child.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> printNodeInfo(node));

                printClickedNode(child);
            }
        } else {
            node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> printNodeInfo(node));
        }
    }

    public static void printCollection(List<?> list) {
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            System.out.println("Object at " + i + " position = " + o);
        }
    }

    public static void printNodeInfo(Node node) {
        System.out.println("JavaFX Node Info = " + node.toString());
    }

    public static void dump(Node n) {
        dump(n, 0);
    }

    public static void dump(Node n, int depth) {
        for (int i = 0; i < depth; i++) System.out.print("  ");
        System.out.println(n + " " + n.impl_getStyleMap());
        if (n instanceof Parent)
            for (Node c : ((Parent) n).getChildrenUnmodifiable())
                dump(c, depth + 1);
    }

}
