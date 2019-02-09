package cz.ebrothers.fx3dtestingtool.fxviewer;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.transform.TransformChangedEvent;

/**
 * todo: transparent background
 *
 */
final class MiniAxisView extends Canvas {

    // todo: move all block into CSS
    public static final int AXIS_LABEL_OFFSET = 2;
    private double border = 24;
	private double textWidth = 1.0;
	private double axisLineWidth = 1.5;
	private double axisLineDashes = 5.0;
    private Font font = new Font("Arial", 14.0);

	private Point3D ptXn = null;
	private Point3D ptXp = null;

	private Point3D ptYn = null;
	private Point3D ptYp = null;

	private Point3D ptZn = null;
	private Point3D ptZp = null;

    private Point3D centerXY = null;

	private final Transform coordinateSystemTransform;
	private final Affine currViewingTransform = new Affine();

    private ViewerModel model;

	MiniAxisView(Transform coordinateSystemTransform, double parentWidth, ViewerModel model) {
        super(parentWidth, parentWidth);

        this.coordinateSystemTransform = coordinateSystemTransform;
        this.model = model;

        setSize(parentWidth);
        setMouseTransparent(true);

        // listen to axis color model
        ChangeListener<Color> axisColorListner = (observable, oldValue, newValue) -> {
            redrawAxes();
        };
        model.xAxisColorProperty().addListener(axisColorListner);
        model.yAxisColorProperty().addListener(axisColorListner);
        model.zAxisColorProperty().addListener(axisColorListner);

        EventHandler<TransformChangedEvent> rotationChangedListener = event -> {
            Transform rotation = model.getHorViewingRotate().createConcatenation(model.getVerViewingRotate());
            updateAxes(rotation);
        };
        model.getHorViewingRotate().setOnTransformChanged(rotationChangedListener);
        model.getVerViewingRotate().setOnTransformChanged(rotationChangedListener);

        getStyleClass().add("mini-axis-view");
        setStyle("-fx-background-color: pink");
    }

    /**
     * Resize mini axis view - shape is always square
     * @param edgeLength
     */
    void setSize(double edgeLength) {
	    setWidth(edgeLength);
		setHeight(edgeLength);

		double drawingAreaSize = (edgeLength - (2 * border)) / 2;
		centerXY = new Point3D(drawingAreaSize + border, drawingAreaSize + border, 0.0);

        ptXn = new Point3D(-drawingAreaSize, 0.0, 0.0);
        ptXp = new Point3D(drawingAreaSize, 0.0, 0.0);

        ptYn = new Point3D(0.0, -drawingAreaSize, 0.0);
        ptYp = new Point3D(0.0, drawingAreaSize, 0.0);

        ptZn = new Point3D(0.0, 0.0, -drawingAreaSize);
        ptZp = new Point3D(0.0, 0.0, drawingAreaSize);

        redrawAxes();
	}

	private void updateAxes(Transform transform) {
		Affine affine = new Affine(transform);
		try {
			affine = affine.createInverse();
		} catch (NonInvertibleTransformException nonInvertibleTransformException) {
			nonInvertibleTransformException.printStackTrace();
		}
		currViewingTransform.setToTransform(affine);

		redrawAxes();
	}

    private void redrawAxes() {
		currViewingTransform.setTx(centerXY.getX());
		currViewingTransform.setTy(centerXY.getY());
		currViewingTransform.setTz(0.0);

        Transform concatenation = currViewingTransform.createConcatenation(coordinateSystemTransform);
        Point3D newPtXn = concatenation.transform(ptXn);
        Point3D newPtXp = concatenation.transform(ptXp);

        Point3D newPtYn = concatenation.transform(ptYn);
        Point3D newPtYp = concatenation.transform(ptYp);

        Point3D newPtZn = concatenation.transform(ptZn);
        Point3D newPtZp = concatenation.transform(ptZp);

		GraphicsContext graphicsContext = getGraphicsContext2D();
		graphicsContext.clearRect(0d, 0d, getWidth(), getHeight());

		graphicsContext.setFont(font);
		graphicsContext.setTextBaseline(VPos.CENTER);

        redrawAxis(model.getXAxisColor(), newPtXn, newPtXp, "+x", "-x");
        redrawAxis(model.getYAxisColor(), newPtYn, newPtYp, "+y", "-y");
        redrawAxis(model.getZAxisColor(), newPtZn, newPtZp, "+z", "-z");
	}
    private void redrawAxis(Color axisColor, Point3D positiveLineEnd, Point3D negativeLineEnd,
                            String positiveLineLabel, String negativeLineLabel) {
        GraphicsContext graphicsContext = getGraphicsContext2D();
        graphicsContext.setStroke(axisColor);
        graphicsContext.setLineWidth(axisLineWidth);

        // positive part of the axis
        graphicsContext.setLineDashes(axisLineDashes);
        graphicsContext.strokeLine(centerXY.getX(), centerXY.getY(), positiveLineEnd.getX(), positiveLineEnd.getY());

        // negative part of the axis
        graphicsContext.setLineDashes(0);
        graphicsContext.strokeLine(centerXY.getX(), centerXY.getY(), negativeLineEnd.getX(), negativeLineEnd.getY());

        // axis labels 
        graphicsContext.setLineWidth(textWidth);
        graphicsContext.strokeText(positiveLineLabel,
                negativeLineEnd.getX() - AXIS_LABEL_OFFSET, negativeLineEnd.getY());
        graphicsContext.strokeText(negativeLineLabel,
                positiveLineEnd.getX() + AXIS_LABEL_OFFSET, positiveLineEnd.getY());
    }
}
