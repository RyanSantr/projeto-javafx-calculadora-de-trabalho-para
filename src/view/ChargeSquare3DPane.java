package view;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class ChargeSquare3DPane extends StackPane {

    private static final double SIZE = 390;
    private static final double HALF_SIDE = 98;

    public ChargeSquare3DPane() {
        getStyleClass().add("visual-pane");
        setMinSize(SIZE, SIZE);
        setPrefSize(SIZE, SIZE);
        setMaxSize(SIZE, SIZE);
        setAlignment(Pos.CENTER);

        getChildren().add(createScene());
        getChildren().add(createLegend());
    }

    private SubScene createScene() {
        Group chargeSystem = new Group();

        Point3D topLeft = new Point3D(-HALF_SIDE, 0, -HALF_SIDE);
        Point3D topRight = new Point3D(HALF_SIDE, 0, -HALF_SIDE);
        Point3D bottomLeft = new Point3D(-HALF_SIDE, 0, HALF_SIDE);
        Point3D bottomRight = new Point3D(HALF_SIDE, 0, HALF_SIDE);

        chargeSystem.getChildren().addAll(
                edge(topLeft, topRight),
                edge(topRight, bottomRight),
                edge(bottomLeft, bottomRight),
                edge(topLeft, bottomLeft),
                charge(topLeft, true),
                charge(topRight, false),
                charge(bottomLeft, false),
                charge(bottomRight, true)
        );

        Group world = new Group(chargeSystem);
        world.getTransforms().addAll(new Rotate(-18, Rotate.X_AXIS), new Rotate(12, Rotate.Z_AXIS));

        AmbientLight ambientLight = new AmbientLight(Color.color(0.55, 0.58, 0.65));
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.getTransforms().add(new Translate(-180, -260, -340));

        Group root = new Group(world, ambientLight, pointLight);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(2000);
        camera.setTranslateZ(-760);

        SubScene subScene = new SubScene(root, SIZE, SIZE, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#fbfcff"));
        subScene.setCamera(camera);

        RotateTransition rotation = new RotateTransition(Duration.seconds(12), chargeSystem);
        rotation.setAxis(Rotate.Y_AXIS);
        rotation.setFromAngle(0);
        rotation.setToAngle(360);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.play();

        return subScene;
    }

    private Sphere charge(Point3D position, boolean positive) {
        Sphere sphere = new Sphere(22);
        sphere.setMaterial(new PhongMaterial(positive ? Color.web("#e53935") : Color.web("#1e63d6")));
        sphere.getTransforms().add(new Translate(position.getX(), position.getY(), position.getZ()));
        return sphere;
    }

    private Cylinder edge(Point3D start, Point3D end) {
        Point3D direction = end.subtract(start);
        Point3D midpoint = start.midpoint(end);

        Cylinder cylinder = new Cylinder(4, direction.magnitude());
        cylinder.setMaterial(new PhongMaterial(Color.web("#253247")));

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D rotationAxis = yAxis.crossProduct(direction);
        double angle = Math.toDegrees(Math.acos(yAxis.normalize().dotProduct(direction.normalize())));

        cylinder.getTransforms().add(new Translate(midpoint.getX(), midpoint.getY(), midpoint.getZ()));
        if (rotationAxis.magnitude() > 0.0001) {
            cylinder.getTransforms().add(new Rotate(angle, rotationAxis));
        }

        return cylinder;
    }

    private Label createLegend() {
        Label legend = new Label("3D animado: cargas girando nos vertices do quadrado");
        legend.getStyleClass().add("visual-caption");
        StackPane.setAlignment(legend, Pos.BOTTOM_CENTER);
        return legend;
    }
}
