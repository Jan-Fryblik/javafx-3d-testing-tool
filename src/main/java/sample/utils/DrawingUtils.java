package sample.utils;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * User: Jan Fryblík
 * Date: 20.2.2017
 * Time: 16:11
 */
public final class DrawingUtils {

    private DrawingUtils() {

    }

    /**
     * Returns center of the {@code node}.
     *
     * x = x + (width / 2)
     * y = y + (height / 2)
     */
    public static Point2D getCenterPoint(Node node) {
        double x = centerPointX(node);
        double y = centerPointY(node);

        return new Point2D(x, y);
    }
    /**
     * Returns center of the line from {@code startPoint} to {@code endPoint}.
     */
    public static Point2D getCenterPoint(Point2D startPoint, Point2D endPoint) {
        double x = startPoint.getX() + (endPoint.getX() - startPoint.getX()) / 2;
        double y = startPoint.getY() + (endPoint.getY() - startPoint.getY()) / 2;

        return new Point2D(x, y);
    }
    /**
     * Returns center of the line from center of the {@code startNode} to the center of the {@code endNode}.
     */
    public static Point2D getCenterPoint(Node startNode, Node endNode) {
        Point2D startPoint = getCenterPoint(startNode);
        Point2D endPoint = getCenterPoint(endNode);

        Point2D result = getCenterPoint(startPoint, endPoint);
        return result;
    }
    /**
     * Returns center of the line from center of the {@code startNode} to {@code endPoint}.
     */
    public static void setCenterPoint(Node node, Point2D centerPoint) {
        double layoutX = centerPoint.getX() - (node.getLayoutBounds().getWidth() / 2);
        double layoutY = centerPoint.getY() - (node.getLayoutBounds().getHeight() / 2);

        node.relocate(layoutX, layoutY);
    }

    /**
     * Returns center of the top side for given {@code node}.
     *
     * <pre>
     * ----X----
     * |       |
     * |       |
     * |       |
     * ---------
     * </pre>
     * where X is returned position.
     */
    public static Point2D getTopSideCenter(Node node) {
        double x = centerPointX(node);
        double y = node.getLayoutY();

        return new Point2D(x, y);
    }
    /**
     * Returns center of the right side for given {@code node}.
     *
     * <pre>
     * ---------
     * |       |
     * |       X
     * |       |
     * ---------
     * </pre>
     * where X is returned position.
     */
    public static Point2D getRightSideCenter(Node node) {
        double x = node.getLayoutX() + node.getLayoutBounds().getWidth();
        double y = centerPointY(node);

        return new Point2D(x, y);
    }
    /**
     * Returns center of the bottom side for given {@code node}.
     *
     * <pre>
     * ---------
     * |       |
     * |       |
     * |       |
     * ----X----
     * </pre>
     * where X is returned position.
     */
    public static Point2D getBottomSideCenter(Node node) {
        double x = centerPointX(node);
        double y = node.getLayoutY() + node.getLayoutBounds().getHeight();

        return new Point2D(x, y);
    }
    /**
     * Returns center of the left side for given {@code node}.
     *
     * <pre>
     * ---------
     * |       |
     * X       |
     * |       |
     * ---------
     * </pre>
     * where X is returned position.
     */
    public static Point2D getLeftSideCenter(Node node) {
        double x = node.getLayoutX();
        double y = centerPointY(node);

        return new Point2D(x, y);
    }
    /**
     * Returns top right point for the given {@code node}.
     *
     * <pre>
     * --------X
     * |       |
     * |       |
     * |       |
     * ---------
     * </pre>
     * where X is returned position.
     */
    public static Point2D getTopRight(Node node) {
        double x = node.getLayoutX() + node.getLayoutBounds().getWidth();
        double y = node.getLayoutY();

        return new Point2D(x, y);
    }
    /**
     * Returns right bottom point for the given {@code node}.
     *
     * <pre>
     * ---------
     * |       |
     * |       |
     * |       |
     * --------X
     * </pre>
     * where X is returned position.
     */
    public static Point2D getRightBottom(Node node) {
        double x = node.getLayoutX() + node.getLayoutBounds().getWidth();
        double y = node.getLayoutY() + node.getLayoutBounds().getHeight();

        return new Point2D(x, y);
    }
    /**
     * Returns left bottom point for the given {@code node}.
     *
     * <pre>
     * ---------
     * |       |
     * |       |
     * |       |
     * X--------
     * </pre>
     * where X is returned position.
     */
    public static Point2D getLeftBottom(Node node) {
        double x = node.getLayoutX();
        double y = node.getLayoutY() + node.getLayoutBounds().getHeight();

        return new Point2D(x, y);
    }

    public static double centerPointX(Node node) {
        double x = node.getLayoutX() + (node.getLayoutBounds().getWidth() / 2);
        return x;
    }
    public static double centerPointY(Node node) {
        double y = node.getLayoutY() + (node.getLayoutBounds().getHeight() / 2);
        return y;
    }

    /**
     * Translates coordinates of the {@code node} through parents so that result is position of the {@code node} in {@code
     * drawingPane}.
     *
     * @param node
     * @param drawingPane
     *
     * @return
     */
    public static Point2D translateToParent(Node node, Node drawingPane) {
        Node parent = node.getParent();
        Point2D result = new Point2D(node.getLayoutX(), node.getLayoutY());

        while (!parent.equals(drawingPane)) {
            result = parent.localToParent(result);
            parent = parent.getParent();
        }

        return result;
    }
    /**
     * Translates coordinates of the {@code node} through parents so that result is position of the {@code node} in {@code
     * drawingPane}.
     *
     * @param node
     * @param drawingPane
     *
     * @return
     */
    public static Point2D translateToParent(Node node, Node drawingPane, Point2D pointToTranslate) {
        Node parent = node.getParent();
        Point2D result = pointToTranslate;

        while (!parent.equals(drawingPane)) {
            result = parent.localToParent(result);
            parent = parent.getParent();
        }

        return result;
    }
}

