package sample.utils;

import javafx.geometry.Point2D;

import java.util.List;

/**
 * Absolute (fixed) layout utils.
 *
 * Set of function to easily layout children of a {@code Pane} where everything is positioned absolutely (fixed
 * position).
 *
 * User: Jan Fryblík
 * Date: 8/5/18
 * Time: 9:57 PM
 */
public final class AbsLayoutUtils {

    private AbsLayoutUtils() {

    }

    /**
     * Returns the most right (the highest X) and the most bottom (the highest Y) positions.
     *
     * @param points
     */
    public static Point2D getMaximumXY(List<Point2D> points) {
        double resultX = 0;
        double resultY = 0;

        for (Point2D point : points) {
            resultX = Math.max(point.getX(), resultX);
            resultY = Math.max(point.getY(), resultY);
        }

        Point2D result = new Point2D(resultX, resultY);
        return result;
    }
}