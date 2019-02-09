package sample.utils;

import com.google.common.base.Preconditions;
import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.util.OptionalDouble;
import java.util.stream.IntStream;

/**
 * User: Jan Fryblík
 * Date: 10/2/18
 * Time: 10:13 PM
 */
public final class MeshWrapper {

    private MeshView meshView;

    public MeshWrapper(MeshView meshView) {
        Preconditions.checkNotNull(meshView);

        this.meshView = meshView;
    }

    public float minY() {
        OptionalDouble min = IntStream.range(0, getPoints().size())
                .filter(i -> i % 3 == 1) // filter out X and Z values
                .mapToDouble(i -> getPoints().get(i))
                .min();
        return (float) min.getAsDouble();
    }

    public TriangleMesh getTriangleMesh() {
        return (TriangleMesh) meshView.getMesh();
    }
    public ObservableFloatArray getPoints() {
        return getTriangleMesh().getPoints();
    }
}
