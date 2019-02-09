package sample.utils;

import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * User: Jan Fryblík
 * Date: 8/15/18
 * Time: 11:32 PM
 */
public final class FxUtils {

    private FxUtils() {

    }

    public static void addStyleSheetIfNotExists(Parent parent, String stylesheetPath) {
        if (!parent.getStylesheets().contains(stylesheetPath)) {
            parent.getStylesheets().add(stylesheetPath);
        }
    }
    public static void addStyleIfNotExists(Styleable styleable, String cssStyle) {
        if (!styleable.getStyleClass().contains(cssStyle)) {
            styleable.getStyleClass().add(cssStyle);
        }
    }
    public static void removeStyleIfExists(List<? extends Styleable> styleables, String cssClass) {
        for (Styleable styleable : styleables) {
            styleable.getStyleClass().remove(cssClass);
        }
    }

    public static <T extends Parent> T findParentByClass(Node child, Class<T> parentClass) {
        Parent parent = child.getParent();
        while (parent != null && parentClass.isAssignableFrom(parent.getClass()) == false) {
            parent = parent.getParent();
        }
        return (T) parent;
    }

    public static void relayoutAll(Node node) {
        node.autosize();
        node.applyCss();

        if (Parent.class.isAssignableFrom(node.getClass())) {
            Parent parent = (Parent) node;

            ObservableList<Node> childrenUnmodifiable = parent.getChildrenUnmodifiable();
            for (Node child : childrenUnmodifiable) {
                relayoutAll(child);
            }

            parent.layout();
            parent.requestLayout();
        }
    }
    public static void relayout(Node propertyPanel) {
        propertyPanel.applyCss();
        propertyPanel.autosize();

        if (propertyPanel instanceof Parent) {
            Parent parent = (Parent) propertyPanel;
            parent.layout();
            parent.requestLayout();
        }
    }

    /**
     * Switches CSS class from one to another. Selection is based on boolean property.
     *
     * @param node
     * @param activated
     * @param activeClassName
     * @param inactiveClassName
     */
    public static void switchClasses(Node node, boolean activated, String activeClassName, String inactiveClassName) {
        if (activated) {
            node.getStyleClass().remove(inactiveClassName);
            node.getStyleClass().add(activeClassName);
        } else {
            node.getStyleClass().remove(activeClassName);
            node.getStyleClass().add(inactiveClassName);
        }
    }
    /**
     * Switches CSS classes so that one is added, others are removed.
     *
     * @param node
     * @param activeIndex
     * @param classes
     */
    public static void switchCssClasses(Node node, int activeIndex, String[] classes) {
        ObservableList<String> nodeCssClasses = node.getStyleClass();
        nodeCssClasses.removeAll(classes);
        nodeCssClasses.add(classes[activeIndex]);
    }

    /**
     * Loads CSS file for component.
     *
     * @param parent
     * @param filePath
     */
    public static void loadCssFile(Parent parent, String filePath) {
        // todo: this doesnt work with inheritance, replace getClass
        URL cssFileUrl = parent.getClass().getResource(filePath);
        loadCssFile(parent, cssFileUrl);
    }
    /**
     * Loads CSS file for Parent component.
     *
     * @param parent
     * @param cssFileUrl
     */
    public static void loadCssFile(Parent parent, URL cssFileUrl) {
        String externalFilePath = cssFileUrl.toExternalForm();

        ObservableList<String> stylesheets = parent.getStylesheets();
        if (!stylesheets.contains(externalFilePath)) {
            stylesheets.add(externalFilePath);
        }
    }
    /**
     * Loads CSS file for component.
     *
     * @param parent
     * @param filePath
     */
    public static void loadCssFile(Class<?> cssPathBaseClass, Parent parent, String filePath) {
        URL cssFileUrl = cssPathBaseClass.getResource(filePath);
        String externalFilePath = cssFileUrl.toExternalForm();

        ObservableList<String> stylesheets = parent.getStylesheets();
        if (!stylesheets.contains(externalFilePath)) {
            stylesheets.add(externalFilePath);
        }
    }

    /**
     * Loads CSS file for Scene component.
     *
     * @param parent
     * @param cssFileUrl
     */
    public static void loadCssFile(Scene parent, URL cssFileUrl) {
        String externalFilePath = cssFileUrl.toExternalForm();

        ObservableList<String> stylesheets = parent.getStylesheets();
        if (!stylesheets.contains(externalFilePath)) {
            stylesheets.add(externalFilePath);
        }
    }

    public static Image loadImage(Class<?> imgPathBaseClass, String filePath) {
        InputStream is = imgPathBaseClass.getResourceAsStream(filePath);
        Image img = new Image(is);
        return img;
    }

    public static String toRGBCode(Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

    public static List<Color> allColors() throws ClassNotFoundException, IllegalAccessException {
        List<Color> colors = new ArrayList<>();
        Class clazz = Class.forName("javafx.scene.paint.Color");
        if (clazz != null) {
            Field[] field = clazz.getFields();
            for (int i = 0; i < field.length; i++) {
                Field f = field[i];
                Object obj = f.get(null);
                if(obj instanceof Color){
                    colors.add((Color) obj);
                }

            }
        }
        return colors;
    }

    public static void visitChildrenTree(Node node, Consumer<Node> consumer) {
        if (node instanceof Parent) {
            Parent parent = (Parent) node;

            for (Node child : parent.getChildrenUnmodifiable()) {
                consumer.accept(child);

                visitChildrenTree(child, consumer);
            }
        }
    }

    public static void visitAllParents(Node node, Consumer<Parent> consumer) {
        Parent parent = node.getParent();
        while (parent != null) {
            consumer.accept(parent);

            parent = parent.getParent();
        }
    }
}

