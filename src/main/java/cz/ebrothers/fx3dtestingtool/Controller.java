package cz.ebrothers.fx3dtestingtool;

import cz.ebrothers.fx3dtestingtool.fxviewer.EditViewer;
import cz.ebrothers.fx3dtestingtool.fxviewer.ViewPoint;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final Transform D3_COORDS_TRANSFORMATION = new Rotate(90, Rotate.X_AXIS).createConcatenation(new Rotate(-90, Rotate.Z_AXIS));

    @FXML
    private StackPane rootLayout;

    @FXML
    private HBox layout;

    private EditViewer editViewer;

    private Group editViewerObjects;

    private Scale scale;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.editViewerObjects = new Group();

        this.editViewer = new EditViewer(SceneAntialiasing.BALANCED, D3_COORDS_TRANSFORMATION);
        editViewer.setMinWidth(Region.USE_PREF_SIZE);
        editViewer.setMinHeight(Region.USE_PREF_SIZE);
        editViewer.setMaxWidth(Double.MAX_VALUE);
        editViewer.setPrefWidth(800);
        editViewer.getModel().getSceneGroupList().add(editViewerObjects);
        editViewer.setViewpoint(ViewPoint.ISO);

        HBox.setHgrow(editViewer, Priority.ALWAYS);
        layout.getChildren().addAll(editViewer);

        scale = new Scale(1, 1, 1);
        editViewerObjects.getTransforms().add(scale);
    }

    private MeshView createSurface() {
        float[] points =
                {
                        10, 0, 1, // bottom right
                        10, 1, 1, // bottom left
                        50, 1, 1, // top left
                        50, 0, 1 // top right
                };

        float[] texCoords =
                {
                        0, 0
                };

        int[] faces =
                {
                        2, 0, 1, 0, 0, 0,
                        3, 0, 2, 0, 0, 0
                };

        // Create a TriangleMesh
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(texCoords);
        mesh.getFaces().addAll(faces);

        // Create a MeshView
        MeshView meshView = new MeshView();
        PhongMaterial value = new PhongMaterial(Color.RED);
        value.setSpecularColor(Color.RED);
        value.setSpecularPower(Double.MAX_VALUE);
        meshView.setMaterial(value);
        meshView.setMesh(mesh);
        meshView.setCullFace(CullFace.NONE);

        return meshView;
    }

    public void testAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Text");
        alert.showAndWait();
    }
}
