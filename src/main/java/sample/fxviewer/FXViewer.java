package sample.fxviewer;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import sample.utils.FxUtils;

/**
 * JavaFX 3D Viewer.
 *
 * @author Marian
 */
public class FXViewer extends Pane {

    /**
     * Percentage ratio of {@code MiniAxisView} related to {@code FXViewer} height.
     * NOTE: {@code MiniAxisView} is always square shape.
     */
    public static final double MINI_AXIS_VIEW_SIZE_RATIO = 0.2d;

    private SubScene subScene;

    private MiniAxisView miniAxisView;

	// drawing axis
	private Group axesGroup;
	private Cylinder xAxis;
	private Cylinder yAxis;
	private Cylinder zAxis;

    private double sceneDiameter = 0.0;
	private double axisRadiusInit = 0.0;
	private double camDistInit = 0.0;
	private boolean isFirstClip = true;

    private double rotScale = 0.1;
    private double transScale = 0.001;
    private double zoomScale = 0.00125;

    private Group viewingGroup;
	private PerspectiveCamera perspectiveCamera;
	private AmbientLight ambSceneLight;
	private PointLight headLight;

	private Transform coordinateSystemTransform;
	// viewing center position
	private Translate viewingCenterTranslate;
	// viewing center reversed position
	private Translate viewingCenterRevTranslate;

	private Point3D sceneCenter;

    private boolean isLookAtCenter = true;

    /**
     * Contains everything for scene. (camera, lights, axis, user objects etc.)
     */
    private Group subSceneRootGroup;
    /**
     * Contains all user objects for display.
     */
    private Group sceneTopGroup;

    private ViewerModel model;

    public FXViewer(SceneAntialiasing sceneAntialiasing) {
		this(sceneAntialiasing, new Affine());
	}

	public FXViewer(SceneAntialiasing sceneAntialiasing, Transform coordinateSystemTransform) {
		this.coordinateSystemTransform = coordinateSystemTransform;

        this.sceneCenter = new Point3D(0.0, 0.0, 0.0);
        this.sceneTopGroup = new Group();
        sceneTopGroup.getTransforms().add(coordinateSystemTransform);

        this.model = new ViewerModel();
        Bindings.bindContent(sceneTopGroup.getChildren(), model.getSceneGroupList());
//        model.getSceneGroupList().addListener((ListChangeListener<Group>) c -> {
//            while (c.next()) {
//                for (Group group : c.getAddedSubList()) {
//                    sceneTopGroup.getChildren().add(group);
//                }
//                for (Group group : c.getRemoved()) {
//                    sceneTopGroup.getChildren().remove(group);
//                }
//            }
//        });

        this.subSceneRootGroup = new Group();

        initUserPerspective();
        initSubScene(sceneAntialiasing);

        initAxis();
        initMiniAxisView();

        subSceneRootGroup.getChildren().addAll(viewingGroup, ambSceneLight, axesGroup, sceneTopGroup);
		getChildren().addAll(subScene, miniAxisView);

		// maximize subscene and miniAxisView within its parent (this)
		ChangeListener<Number> changeListener = (observableValue, oldValue, newValue) -> setSize(getWidth(), getHeight());
		widthProperty().addListener(changeListener);
		heightProperty().addListener(changeListener);

		// setup CSS
        getStyleClass().add("fx-viewer");
        FxUtils.loadCssFile(FXViewer.class, this, "FXViewer.css");

		// Uncomment for background gradient
//		 Stop[] arrstop = new Stop[] { new Stop(0.0, Color.rgb(0, 73, 255)), new
//		 Stop(0.7, Color.rgb(127, 164, 255)),
//		 new Stop(1.0, Color.rgb(0, 73, 255)) };
//		 LinearGradient linearGradient = new LinearGradient(0.0, 0.0, 0.0, 1.0, true,
//		 CycleMethod.NO_CYCLE, arrstop);
//		 Background background = new Background(new BackgroundFill[]{new
//		 BackgroundFill(linearGradient, null, null)});
//		 setBackground(background);

        // for debug purposes
//		viewingCenterTranslate.setOnTransformChanged((event) -> {
//			System.out.println("Viewing Center Translate: " + viewingCenterTranslate);
//		});
    }

    public double getRotScale() {
        return rotScale;
    }
    public double getTransScale() {
        return transScale;
    }
    public double getZoomScale() {
        return zoomScale;
    }

    public ViewerModel getModel() {
        return model;
    }

    protected SubScene getSubScene() {
        return subScene;
    }
    protected MiniAxisView getMiniAxisView() {
        return miniAxisView;
    }

    protected Group getViewingGroup() {
        return viewingGroup;
    }

    private Point3D getRotationCenter() {
        return new Point3D(viewingCenterTranslate.getX(), viewingCenterTranslate.getY(), viewingCenterTranslate.getZ());
    }
    private void setRotationCenter(Point3D point3D) {
        if (isLookAtCenter) {
            Point3D point3D2 = getCamPos();
            double d = point3D.distance(point3D2);
            updateCenterTranslations(point3D);
            model.getViewingTranslate().setX(point3D.getX());
            model.getViewingTranslate().setY(point3D.getY());
            model.getViewingTranslate().setZ(point3D.getZ() - d);

        } else {
            Transform transform = concatVievingTransforms();
            updateCenterTranslations(point3D);
            Transform transform2 = viewingCenterTranslate
                    .createConcatenation(model.getHorViewingRotate())
                    .createConcatenation(model.getVerViewingRotate())
                    .createConcatenation(viewingCenterRevTranslate);
            try {
                Transform transform3 = transform2.createInverse();
                Transform transform4 = transform3.createConcatenation(transform);
                model.getViewingTranslate().setX(transform4.getTx());
                model.getViewingTranslate().setY(transform4.getTy());
                model.getViewingTranslate().setZ(transform4.getTz());
            } catch (NonInvertibleTransformException nonInvertibleTransformException) {
                nonInvertibleTransformException.printStackTrace();
            }
        }
        updateSceneClipping();
    }

    protected void setSize(double width, double height) {
	    // subScene keeps full size
		subScene.setWidth(width);
		subScene.setHeight(height);

        double miniAxisEdgeLength = height * MINI_AXIS_VIEW_SIZE_RATIO;
        miniAxisView.setSize(miniAxisEdgeLength);
        miniAxisView.relocate(0.0, height - miniAxisEdgeLength);
	}

	protected void updateSceneClipping() {
		Point3D point3D = getInvPoint(getRotationCenter());
		double d = point3D.getZ() + sceneDiameter * 1.0 * 1.1;
		double d2 = point3D.getZ() - sceneDiameter * 1.0 * 1.1;
		double d3 = Math.max(d2, d / 3000.0);
		Point3D point3D2 = getCamPos();
		double d4 = point3D2.magnitude();
		if (isFirstClip) {
			isFirstClip = false;
			camDistInit = d4;
		} else {
			double d5 = d4 / camDistInit;
			double d6 = axisRadiusInit * d5;
			xAxis.setRadius(d6);
			yAxis.setRadius(d6);
			zAxis.setRadius(d6);
		}
		perspectiveCamera.setFarClip(d);
		perspectiveCamera.setNearClip(d3);
	}

//	public void importNode(Node node) {
//		if (currentlyImportedNode != null) {
//			model.getSceneTopGroup().getChildren().remove(currentlyImportedNode);
//		}
//		// setRotPicker(node);
//		currentlyImportedNode = node;
//		model.getSceneTopGroup().getChildren().add(currentlyImportedNode);
//		setViewpoint(ViewPoint.ISO);
//	}

	public void setViewpoint(ViewPoint viewPoint) {
		// NOTE: SceneTopGroup uses the coordinateSystemTransform
		BoundingBox scene3DBinL = (BoundingBox) sceneTopGroup.getBoundsInParent(); // getBoundsInLocal()?
		double d = Math.max(scene3DBinL.getWidth(), Math.max(scene3DBinL.getHeight(), scene3DBinL.getDepth()));
		sceneDiameter = Math.sqrt(3.0) * d;
		sceneCenter = new Point3D((scene3DBinL.getMaxX() + scene3DBinL.getMinX()) / 2.0,
				(scene3DBinL.getMaxY() + scene3DBinL.getMinY()) / 2.0,
				(scene3DBinL.getMaxZ() + scene3DBinL.getMinZ()) / 2.0);
		setRotationCenter(sceneCenter);
		transScale = sceneDiameter * 0.001;
		zoomScale = sceneDiameter * 0.00125;

		double d2 = (sceneCenter.magnitude() + d / 2.0) * 2.0 * 2.0;
		axisRadiusInit = d / 700.0;
		xAxis.setHeight(d2);
		xAxis.setRadius(axisRadiusInit);
		yAxis.setHeight(d2);
		yAxis.setRadius(axisRadiusInit);
		zAxis.setHeight(d2);
		zAxis.setRadius(axisRadiusInit);
		isFirstClip = true;

		double distToSceneCenter = distToSceneCenter(sceneDiameter / 2.0);

		switch (viewPoint) {
			case FRONT: {
				model.getHorViewingRotate().setToTransform(new Affine());
				model.getVerViewingRotate().setToTransform(new Affine());
				break;
			}
			case BACK: {
                model.getHorViewingRotate().setToTransform(new Rotate(180.0, Rotate.Y_AXIS));
                model.getVerViewingRotate().setToTransform(new Affine());
				break;
			}
			case LEFT: {
                model.getHorViewingRotate().setToTransform(new Rotate(90.0, Rotate.Y_AXIS));
                model.getVerViewingRotate().setToTransform(new Affine());
				break;
			}
			case RIGHT: {
                model.getHorViewingRotate().setToTransform(new Rotate(-90.0, Rotate.Y_AXIS));
                model.getVerViewingRotate().setToTransform(new Affine());
				break;
			}
			case TOP: {
                model.getHorViewingRotate().setToTransform(new Affine());
                model.getVerViewingRotate().setToTransform(new Rotate(-90.0, Rotate.X_AXIS));
				break;
			}
			case BOTTOM: {
                model.getHorViewingRotate().setToTransform(new Affine());
                model.getVerViewingRotate().setToTransform(new Rotate(90.0, Rotate.X_AXIS));
				break;
			}
			case ISO: {
                model.getHorViewingRotate().setToTransform(new Rotate(45.0, Rotate.Y_AXIS));
                model.getVerViewingRotate().setToTransform(new Rotate(-35.0, Rotate.X_AXIS));
				break;
			}
			case CENTER: {
				// nothing to do
				break;
			}
		}

		updateCenterTranslations(sceneCenter);

		model.setViewingPosition(
		        sceneCenter.getX(),
                sceneCenter.getY(),
                sceneCenter.getZ() - distToSceneCenter);

		updateSceneClipping();
	}

	private double distToSceneCenter(double sceneRadius) {
		double fieldOfView = perspectiveCamera.getFieldOfView();
		double sceneWidth = subScene.getWidth();
		double sceneHeight = subScene.getHeight();
		double d5 = 1.0;
		if (sceneWidth <= sceneHeight) {
			return 1.0 * d5 * sceneRadius / Math.tan(Math.toRadians(fieldOfView / 2.0));
		}
		return 1.0 * d5 * sceneRadius / Math.tan(Math.toRadians(fieldOfView / 2.0));
	}

    private void updateCenterTranslations(Point3D point3D) {
        viewingCenterTranslate.setX(point3D.getX());
        viewingCenterTranslate.setY(point3D.getY());
        viewingCenterTranslate.setZ(point3D.getZ());
        viewingCenterRevTranslate.setX(-point3D.getX());
        viewingCenterRevTranslate.setY(-point3D.getY());
        viewingCenterRevTranslate.setZ(-point3D.getZ());
    }

    private Point3D getCamPos() {
        Transform transform = concatVievingTransforms();
        return transform.transform(0.0, 0.0, 0.0);
    }
    private Point3D getInvPoint(Point3D point3D) {
        try {
            Transform transform = concatVievingTransforms();
            return transform.inverseTransform(point3D);
        } catch (NonInvertibleTransformException nonInvertibleTransformException) {
            nonInvertibleTransformException.printStackTrace();
            return null;
        }
    }

    private Transform concatVievingTransforms() {
        Transform result = viewingCenterTranslate
                .createConcatenation(model.getHorViewingRotate())
                .createConcatenation(model.getVerViewingRotate())
                .createConcatenation(viewingCenterRevTranslate)
                .createConcatenation(model.getViewingTranslate());
        return result;
    }

    protected void initAxis() {
        this.xAxis = new Cylinder(0.5, 1.0, 36);
        xAxis.setRotationAxis(Rotate.Z_AXIS);
        xAxis.setRotate(90.0);
        PhongMaterial xAxisMaterial = new PhongMaterial();
        xAxisMaterial.diffuseColorProperty().bind(model.xAxisColorProperty());
        xAxisMaterial.specularColorProperty().bind(model.xAxisColorProperty());
        xAxisMaterial.setSpecularPower(4.0);
        xAxis.setMaterial(xAxisMaterial);

        this.yAxis = new Cylinder(0.5, 1.0, 36);
        PhongMaterial yAxisMaterial = new PhongMaterial();
        yAxisMaterial.diffuseColorProperty().bind(model.yAxisColorProperty());
        yAxisMaterial.specularColorProperty().bind(model.yAxisColorProperty());
        yAxisMaterial.setSpecularPower(100.0);
        yAxis.setMaterial(yAxisMaterial);

        this.zAxis = new Cylinder(0.5, 1.0, 36);
        zAxis.setRotationAxis(Rotate.X_AXIS);
        zAxis.setRotate(90.0);
        PhongMaterial zAxisMaterial = new PhongMaterial();
        zAxisMaterial.diffuseColorProperty().bind(model.zAxisColorProperty());
        zAxisMaterial.specularColorProperty().bind(model.zAxisColorProperty());
        zAxisMaterial.setSpecularPower(4.0);
        zAxis.setMaterial(zAxisMaterial);

        this.axesGroup = new Group();
        axesGroup.getStyleClass().add("viewer-axis");
        axesGroup.getChildren().addAll(xAxis, yAxis, zAxis);

        axesGroup.getTransforms().add(coordinateSystemTransform);
    }

    protected void initMiniAxisView() {
        this.miniAxisView = new MiniAxisView(coordinateSystemTransform, getLayoutBounds().getWidth(), model);
    }

    protected void initSubScene(SceneAntialiasing sceneAntialiasing) {
        this.ambSceneLight = new AmbientLight(Color.color(0.7, 0.7, 0.7));
        ambSceneLight.setLightOn(true);

        this.subScene = new SubScene(subSceneRootGroup, getLayoutBounds().getWidth(), getLayoutBounds().getHeight(),
                true, sceneAntialiasing);
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(perspectiveCamera);
    }

    /**
     * Initialize user perspective objects.
     */
    protected void initUserPerspective() {
        this.viewingGroup = new Group();
        this.viewingCenterTranslate = new Translate();
        this.viewingCenterRevTranslate = new Translate();

        perspectiveCamera = new PerspectiveCamera(true);
        perspectiveCamera.setVerticalFieldOfView(false);
        perspectiveCamera.setFieldOfView(44.0);

        this.headLight = new PointLight(Color.WHITE);
        headLight.setTranslateZ(-20000.0);

        viewingGroup.getChildren().setAll(perspectiveCamera, headLight);
        viewingGroup.getTransforms().setAll(viewingCenterTranslate, model.getHorViewingRotate(), model.getVerViewingRotate(),
                viewingCenterRevTranslate, model.getViewingTranslate());
    }
}
