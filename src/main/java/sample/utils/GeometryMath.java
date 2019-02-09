package sample.utils;

import javafx.geometry.Point2D;

/**
 * User: Jan Fryblík
 * Date: 10/22/18
 * Time: 11:22 PM
 */
public final class GeometryMath {

    private GeometryMath() {

    }

    public static Point2D findClosestPoint(Point2D lineStartPoint, Point2D lineEndPoint, Point2D pivot) {
        double px = pivot.getX();
        double py = pivot.getY();
        double sx1 = lineStartPoint.getX();
        double sy1 = lineStartPoint.getY();
        double sx2 = lineEndPoint.getX();
        double sy2 = lineEndPoint.getY();

        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;

        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        Point2D result;
        if (u < 0)
            result = new Point2D(sx1, sy1);
        else if (u > 1)
            result = new Point2D(sx2, sy2);
        else
            result = new Point2D((int) Math.round(sx1 + u * xDelta), (int) Math.round(sy1 + u * yDelta));

        return result;
    }
}
