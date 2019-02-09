package sample;

import com.google.common.primitives.Ints;
import com.martinaudio.d3.model.speakermodel.api.SpeakerAPI;
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
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import sample.fxviewer.EditViewer;
import sample.fxviewer.ViewPoint;
import sample.utils.FxUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    public void addObjectAction(ActionEvent actionEvent) throws IllegalAccessException, ClassNotFoundException {
        Random random = new Random();

        List<Color> colors = FxUtils.allColors();
        int colorIndex = random.nextInt(colors.size());

        int radius = random.nextInt(20);
        int x = random.nextInt(100) - 50;
        int y = random.nextInt(100) - 50;
        int z = random.nextInt(100) - 50;

//        Sphere sphere = new Sphere(radius);
        PhongMaterial material = new PhongMaterial(colors.get(colorIndex));
        material.setSpecularColor(Color.BLACK);

        final Mesh mesh = getDeviceMesh("test");
        MeshView sphere = new MeshView(mesh);
        sphere.setDrawMode(DrawMode.FILL);
        sphere.setCullFace(CullFace.NONE);
        PhongMaterial value = new PhongMaterial(Color.RED);
//        value.setSpecularColor(Color.RED);
//        value.setSpecularPower(Double.MAX_VALUE);
        sphere.setMaterial(value);
        
//        sphere.setMaterial(material);
        sphere.setTranslateX(0);
        sphere.setTranslateY(0);
        sphere.setTranslateZ(0);

        Sphere sphere1 = new Sphere(radius);
        sphere1.setMaterial(material);
        sphere1.setTranslateX(0);
        sphere1.setTranslateY(0);
        sphere1.setTranslateZ(0);

        editViewerObjects.getChildren().add(sphere);
    }

    
    public Mesh getDeviceMesh(String deviceId) {
        ArrayList<Short> triangles = new ArrayList<>();
        ArrayList<Double> vertices = new ArrayList<>();

        SpeakerAPI speakerApi = new SpeakerAPI();
        String path = System.getProperty("user.dir") + "/display3-lib";
        speakerApi.Initialize(path);

//        com.martinaudio.d3.model.speakermodel.Mesh mesh1 = speakerApi.getMesh(deviceId, meshId[1]);
//        System.out.println("Triangles = " + mesh1.triangles.size());
//        System.out.println("Vertexes = " + mesh1.vertices.size());
//        boolean equalsVertexes = vertices.size() == mesh1.vertices.size();
//        System.out.println("V Equals: " + equalsVertexes);
//        boolean trianglesEqual = triangles.size() == mesh1.triangles.size();
//        System.out.println("T Equals: " + trianglesEqual);

        String speakerId = speakerApi.getSpeakerIDs()[0];
        String meshId = speakerApi.getMesh_IDs(speakerId)[1];
        speakerApi.getMesh(speakerId, meshId, triangles, vertices);

        int vertexesCount = vertices.size() / 3;
        int trianglesCount = triangles.size() / 3;

        final Short minIndex = triangles.stream().min(Short::compareTo).get();
        final Short maxIndex = triangles.stream().max(Short::compareTo).get();

        System.out.println(
                "v: " + vertexesCount + " t: " + trianglesCount +
                " min index: " + minIndex +
                " max index:" + maxIndex);

//        vertices.addAll(Arrays.asList(new Double[] { 0d, 0d, 0d}));


        TriangleMesh mesh = new TriangleMesh();
//        float[] points = Floats.toArray(vertices);
        float[] points = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            Double vertex = vertices.get(i);

            points[i] = vertex.floatValue() / 1000;
        }
        mesh.getPoints().addAll(points);

//        int pointsCount = points.length / 3;
//        System.out.println("points = " + pointsCount);
//
//        int facesCount = triangles.size() / 3;
//        System.out.println("facesCount = " + facesCount);



        float[] texCoords = { 0, 0 };
        mesh.getTexCoords().addAll(texCoords);

        int[] elements = Ints.toArray(triangles);
        int[] faces = new int[elements.length * 2];
        for (int i = 0; i < elements.length; i++) {
            int element = elements[i] - 1; // convert into zero-based indexing

            int index = i * 2;
            faces[index] = element;
            if (element >= points.length || element < 0) {
//                System.out.println("index = " + i);
                System.out.println(i + " = " + element);
            }
            faces[index + 1] = 0;
        }
        mesh.getFaces().addAll(faces);
        int last = faces[faces.length - 1];
//        System.out.println("last = " + last);

//        System.out.println("min triangles = " + triangles.stream().min(Integer::compare).get());

        return mesh;
    }



    public void testAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Text");
        alert.showAndWait();
    }
}
