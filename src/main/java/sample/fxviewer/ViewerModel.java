package sample.fxviewer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;

import java.util.ArrayList;

/**
 * User: Jan Fryblík
 * Date: 9/18/18
 * Time: 12:27 AM
 */
public class ViewerModel {

    private ObservableList<Group> sceneGroupList;

    private ObjectProperty<Color> xAxisColor;
    private ObjectProperty<Color> yAxisColor;
    private ObjectProperty<Color> zAxisColor;

    // camera position and angles
    private Affine horViewingRotate;
    private Affine verViewingRotate;
    private Translate viewingTranslate;

    private BooleanProperty axisVisible;

    public ViewerModel() {
        this.sceneGroupList = FXCollections.observableArrayList(new ArrayList<>());

        this.xAxisColor = new SimpleObjectProperty<>(Color.RED);
        this.yAxisColor = new SimpleObjectProperty<>(Color.GREEN);
        this.zAxisColor = new SimpleObjectProperty<>(Color.DARKBLUE);

        this.horViewingRotate = new Affine();
        this.verViewingRotate = new Affine();
        this.viewingTranslate = new Translate();

        this.axisVisible = new SimpleBooleanProperty(false);

        // for debug
        viewingTranslate.setOnTransformChanged((event) -> {
            System.out.println("Viewing Translate: " + viewingTranslate);
        });
    }

    public ObservableList<Group> getSceneGroupList() {
        return sceneGroupList;
    }

    public Color getXAxisColor() {
        return xAxisColor.get();
    }
    public ObjectProperty<Color> xAxisColorProperty() {
        return xAxisColor;
    }
    public void setXAxisColor(Color xAxisColor) {
        this.xAxisColor.set(xAxisColor);
    }

    public Color getYAxisColor() {
        return yAxisColor.get();
    }
    public ObjectProperty<Color> yAxisColorProperty() {
        return yAxisColor;
    }
    public void setYAxisColor(Color yAxisColor) {
        this.yAxisColor.set(yAxisColor);
    }

    public Color getZAxisColor() {
        return zAxisColor.get();
    }
    public ObjectProperty<Color> zAxisColorProperty() {
        return zAxisColor;
    }
    public void setZAxisColor(Color zAxisColor) {
        this.zAxisColor.set(zAxisColor);
    }

    public Affine getHorViewingRotate() {
        return horViewingRotate;
    }
    public Affine getVerViewingRotate() {
        return verViewingRotate;
    }
    public Translate getViewingTranslate() {
        return viewingTranslate;
    }

    public boolean isAxisVisible() {
        return axisVisible.get();
    }
    public BooleanProperty axisVisibleProperty() {
        return axisVisible;
    }
    public void setAxisVisible(boolean axisVisible) {
        this.axisVisible.set(axisVisible);
    }

    /**
     * Returns point of current camera position completed from viewing translate components.
     *
     */
    public Point3D getViewingPosition() {
        return new Point3D(
                viewingTranslate.getX(),
                viewingTranslate.getY(),
                viewingTranslate.getZ());
    }
    /**
     * Sets camera translate transformation to certain point {@code x, y, z}.
     *
     * @see #viewingTranslate
     */
    public void setViewingPosition(double x, double y, double z) {
        viewingTranslate.setX(x);
        viewingTranslate.setY(y);
        viewingTranslate.setZ(z);
    }
}
