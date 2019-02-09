package sample.fxviewer;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.controlsfx.control.action.Action;
import sample.utils.FxUtils;

/**
 * User: Jan Frybl�k
 * Date: 17.09.2018
 * Time: 20:53
 */
public class EditViewer extends FXViewer {

    private enum MouseAction {
        ORBIT,
        MOVE
    }

    private final ActionButtonPanel buttonPanel;

    private MouseAction defaultMouseAction;

    private MouseButton pressedMouseButton;
    private double startX = 0.0;
    private double startY = 0.0;

    private Cursor rotCursor;
    private Cursor transCursor;
    private Cursor zoomCursor;

    public EditViewer(SceneAntialiasing sceneAntialiasing, Transform coordinateSystemTransform) {
        super(sceneAntialiasing, coordinateSystemTransform);

//        Image image = new Image(getClass().getResource("resources/rotCursor.png").toExternalForm());
//        rotCursor = new ImageCursor(image, image.getWidth() / 2.0, image.getHeight() / 2.0);
//        image = new Image(getClass().getResource("resources/translCursor.png").toExternalForm());
//        transCursor = new ImageCursor(image, image.getWidth() / 2.0, image.getHeight() / 2.0);
//        image = new Image(getClass().getResource("resources/zoomCursor.png").toExternalForm());
//        zoomCursor = new ImageCursor(image, image.getWidth() / 2.0, image.getHeight() / 2.0);

        defaultMouseAction = MouseAction.ORBIT;
        buttonPanel = createButtonPanel();

        getChildren().add(buttonPanel);

        getSubScene().setOnMousePressed(mouseEvent -> {
            startX = mouseEvent.getSceneX();
            startY = mouseEvent.getSceneY();

            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                pressedMouseButton = MouseButton.PRIMARY;
                // subScene.setCursor(rotCursor);
            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                pressedMouseButton = MouseButton.SECONDARY;
                // subScene.setCursor(transCursor);
            } else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
                pressedMouseButton = MouseButton.MIDDLE;
                // subScene.setCursor(zoomCursor);
            } else {
                pressedMouseButton = null;
            }
//                getMiniAxisView().redrawAxes();
        });

        getSubScene().setOnMouseDragged(mouseEvent -> {

            if (pressedMouseButton == MouseButton.PRIMARY) {
                switch (defaultMouseAction) {
                    case MOVE:
                        handleMouseMoveAction(mouseEvent);
                        break;
                    case ORBIT:
                    default:
                        handleMouseOrbitAction(mouseEvent);
                        break;
                }

            } else if (pressedMouseButton == MouseButton.SECONDARY) {
                handleMouseMoveAction(mouseEvent);

            } else if (pressedMouseButton == MouseButton.MIDDLE) {
                double oldZPosition = getModel().getViewingTranslate().getZ();
                double newZPosition = oldZPosition + (startY - mouseEvent.getSceneY()) * getZoomScale();

                getModel().getViewingTranslate().setZ(newZPosition);

                updateSceneClipping();
            }
            startX = mouseEvent.getSceneX();
            startY = mouseEvent.getSceneY();
        });

        getSubScene().setOnScroll(scrollEvent -> {
            double oldZPosition = getModel().getViewingTranslate().getZ();
            double newZPostion = oldZPosition + scrollEvent.getDeltaY() * getZoomScale() * 0.5;
            getModel().getViewingTranslate().setZ(newZPostion);

            updateSceneClipping();
        });

        getSubScene().setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                pressedMouseButton = null;
                getSubScene().setCursor(null);
            }
        });
    }

    @Override
    protected void setSize(double width, double height) {
        super.setSize(width, height);

        buttonPanel.relocate(0, 10);
    }

    private void handleMouseOrbitAction(MouseEvent mouseEvent) {
        double horAngleDelta = (mouseEvent.getSceneX() - startX) * getRotScale();
        getModel().getHorViewingRotate().appendRotation(horAngleDelta, Point3D.ZERO, Rotate.Y_AXIS);

        double verAngleDelta = (startY - mouseEvent.getSceneY()) * getRotScale();
        getModel().getVerViewingRotate().appendRotation(verAngleDelta, Point3D.ZERO, Rotate.X_AXIS);
    }

    private void handleMouseMoveAction(MouseEvent mouseEvent) {
        Point3D oldPosition = getModel().getViewingPosition();

        double newYPosition = oldPosition.getY() + (startY - mouseEvent.getSceneY()) * getTransScale();
        double newXPosition = oldPosition.getX() + (startX - mouseEvent.getSceneX()) * getTransScale();

        getModel().setViewingPosition(newXPosition, newYPosition, oldPosition.getZ());
    }

    private ActionButtonPanel createButtonPanel() {

        ActionButtonPanel actionButtonPanel = new ActionButtonPanel();

        // HOME button
        Action homeAction = new Action(ae -> setViewpoint(ViewPoint.ISO));
        Image homeImg = FxUtils.loadImage(FXViewer.class, "home.png");
        homeAction.setGraphic(new ImageView(homeImg));
        actionButtonPanel.addActionButton(homeAction);

        // ORBIT button
        Action orbitAction = new Action(ae -> {
            defaultMouseAction = MouseAction.ORBIT;
        });
        Image orbImg = FxUtils.loadImage(FXViewer.class, "rotate.png");
        orbitAction.setGraphic(new ImageView(orbImg));
        orbitAction.setSelected(defaultMouseAction == MouseAction.ORBIT);
        actionButtonPanel.addActionToggleButton(orbitAction);

        // MOVE button
        Action moveAction = new Action(ae -> {
            defaultMouseAction = MouseAction.MOVE;
        });
        Image movImg = FxUtils.loadImage(FXViewer.class, "move.png");
        moveAction.setGraphic(new ImageView(movImg));
        moveAction.setSelected(defaultMouseAction == MouseAction.MOVE);
        actionButtonPanel.addActionToggleButton(moveAction);

        // button toggling
        orbitAction.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            moveAction.setSelected(!isSelected);
        });
        moveAction.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            orbitAction.setSelected(!isSelected);
        });

        return actionButtonPanel;
    }
}
