package view;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.scene.Node;
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
    private static final double HALF_SIDE = 102;
    private static final Color POSITIVE = Color.web("#ff365f");
    private static final Color NEGATIVE = Color.web("#2dd4ff");

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
        Point3D topLeft = new Point3D(-HALF_SIDE, 0, -HALF_SIDE);
        Point3D topRight = new Point3D(HALF_SIDE, 0, -HALF_SIDE);
        Point3D bottomLeft = new Point3D(-HALF_SIDE, 0, HALF_SIDE);
        Point3D bottomRight = new Point3D(HALF_SIDE, 0, HALF_SIDE);

        Group chargeSystem = new Group();
        Group flowLayer = new Group();
        List<FlowParticle> flowParticles = new ArrayList<>();
        List<Node> pulseNodes = new ArrayList<>();

        Group positiveA = charge(topLeft, true, pulseNodes);
        Group negativeA = charge(topRight, false, pulseNodes);
        Group negativeB = charge(bottomLeft, false, pulseNodes);
        Group positiveB = charge(bottomRight, true, pulseNodes);

        chargeSystem.getChildren().addAll(
                starField(),
                grid(),
                energyCore(pulseNodes),
                edge(topLeft, topRight, Color.web("#2dd4ff", 0.88), 4.8),
                edge(topRight, bottomRight, Color.web("#67e8f9", 0.70), 3.4),
                edge(bottomLeft, bottomRight, Color.web("#2dd4ff", 0.88), 4.8),
                edge(topLeft, bottomLeft, Color.web("#67e8f9", 0.70), 3.4),
                edge(topLeft, bottomRight, Color.web("#ff4d6d", 0.42), 2.0),
                edge(topRight, bottomLeft, Color.web("#2dd4ff", 0.34), 2.0),
                fieldArc(topLeft, topRight, -1, Color.web("#ffd166", 0.72)),
                fieldArc(topLeft, bottomLeft, 1, Color.web("#ffd166", 0.72)),
                fieldArc(bottomRight, topRight, 1, Color.web("#f97316", 0.60)),
                fieldArc(bottomRight, bottomLeft, -1, Color.web("#f97316", 0.60)),
                fieldArc(topLeft, bottomRight, 1, Color.web("#ff4d6d", 0.44)),
                fieldArc(topRight, bottomLeft, -1, Color.web("#2dd4ff", 0.44)),
                orbitRing(142, Color.web("#22c55e", 0.34), -18),
                orbitRing(176, Color.web("#a855f7", 0.30), 24),
                positiveA,
                negativeA,
                negativeB,
                positiveB,
                flowLayer
        );

        addFlow(flowLayer, flowParticles, topLeft, topRight, 0.00);
        addFlow(flowLayer, flowParticles, topLeft, bottomLeft, 0.25);
        addFlow(flowLayer, flowParticles, bottomRight, topRight, 0.50);
        addFlow(flowLayer, flowParticles, bottomRight, bottomLeft, 0.75);

        Group world = new Group(chargeSystem);
        Rotate xRotate = new Rotate(-48, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(24, Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(4, Rotate.Z_AXIS);
        world.getTransforms().addAll(xRotate, yRotate, zRotate);

        AmbientLight ambientLight = new AmbientLight(Color.color(0.50, 0.54, 0.63));
        PointLight redLight = pointLight(Color.web("#ff4d6d"), -210, -210, -260);
        PointLight blueLight = pointLight(Color.web("#2dd4ff"), 220, -160, 180);
        PointLight greenLight = pointLight(Color.web("#22c55e"), 0, -120, 120);
        PointLight whiteLight = pointLight(Color.WHITE, 0, -320, -420);

        Group root = new Group(world, ambientLight, redLight, blueLight, greenLight, whiteLight);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(2500);
        camera.setTranslateZ(-780);

        SubScene subScene = new SubScene(root, SIZE, SIZE, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#050816"));
        subScene.setCamera(camera);
        enableMouseControl(subScene, xRotate, yRotate, camera);

        RotateTransition rotation = new RotateTransition(Duration.seconds(18), chargeSystem);
        rotation.setAxis(Rotate.Y_AXIS);
        rotation.setFromAngle(0);
        rotation.setToAngle(360);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.play();

        RotateTransition orbitRotation = new RotateTransition(Duration.seconds(1), flowLayer);
        orbitRotation.setAxis(Rotate.Y_AXIS);
        orbitRotation.setFromAngle(0);
        orbitRotation.setToAngle(360);
        orbitRotation.setCycleCount(Animation.INDEFINITE);
        orbitRotation.play();

        startFlowAnimation(flowParticles);
        startPulseAnimation(pulseNodes);

        return subScene;
    }

    private Group charge(Point3D position, boolean positive, List<Node> pulseNodes) {
        Color coreColor = positive ? POSITIVE : NEGATIVE;
        Color haloColor = positive ? Color.web("#ff4d6d", 0.22) : Color.web("#2dd4ff", 0.22);

        Sphere halo = new Sphere(34);
        halo.setMaterial(material(haloColor, Color.WHITE));

        Sphere core = new Sphere(22);
        core.setMaterial(material(coreColor, Color.WHITE));

        Sphere highlight = new Sphere(6);
        highlight.setMaterial(material(Color.rgb(255, 255, 255, 0.70), Color.WHITE));
        highlight.getTransforms().add(new Translate(-8, -12, -13));

        Group group = new Group(halo, core, highlight);
        group.getTransforms().add(new Translate(position.getX(), position.getY(), position.getZ()));
        pulseNodes.add(halo);
        return group;
    }

    private Group grid() {
        Group group = new Group();
        Color gridColor = Color.web("#67e8f9", 0.28);

        for (int offset = -160; offset <= 160; offset += 32) {
            group.getChildren().add(edge(new Point3D(offset, 24, -150), new Point3D(offset, 24, 150), gridColor, 0.8));
            group.getChildren().add(edge(new Point3D(-150, 24, offset), new Point3D(150, 24, offset), gridColor, 0.8));
        }

        return group;
    }

    private Group starField() {
        Group group = new Group();
        for (int i = 0; i < 80; i++) {
            double x = ((i * 73) % 360) - 180;
            double y = -190 - ((i * 31) % 160);
            double z = ((i * 47) % 420) - 210;
            double radius = i % 9 == 0 ? 1.8 : 1.0;

            Sphere star = new Sphere(radius);
            Color color = i % 4 == 0 ? Color.web("#67e8f9", 0.95) : Color.web("#ffffff", 0.78);
            star.setMaterial(material(color, Color.WHITE));
            star.getTransforms().add(new Translate(x, y, z));
            group.getChildren().add(star);
        }
        return group;
    }

    private Group energyCore(List<Node> pulseNodes) {
        Group core = new Group();

        Cylinder beam = new Cylinder(3.0, 132);
        beam.setMaterial(material(Color.web("#2dd4ff", 0.46), Color.WHITE));
        beam.getTransforms().add(new Translate(0, -18, 0));

        Sphere nucleus = new Sphere(13);
        nucleus.setMaterial(material(Color.web("#22c55e", 0.72), Color.WHITE));
        nucleus.getTransforms().add(new Translate(0, -18, 0));

        Sphere corona = new Sphere(26);
        corona.setMaterial(material(Color.web("#2dd4ff", 0.14), Color.WHITE));
        corona.getTransforms().add(new Translate(0, -18, 0));

        core.getChildren().addAll(beam, corona, nucleus);
        pulseNodes.add(corona);
        pulseNodes.add(nucleus);
        return core;
    }

    private Group fieldArc(Point3D start, Point3D end, int direction, Color color) {
        Group arc = new Group();
        int segments = 18;
        Point3D previous = null;

        Point3D chord = end.subtract(start).normalize();
        Point3D side = new Point3D(-chord.getZ(), 0, chord.getX()).multiply(direction);

        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            Point3D base = start.interpolate(end, t);
            double wave = Math.sin(t * Math.PI);
            Point3D current = base
                    .add(side.multiply(wave * 34))
                    .add(0, -36 * wave, 0);

            if (previous != null) {
                arc.getChildren().add(edge(previous, current, color, 0.82));
            }

            previous = current;
        }

        return arc;
    }

    private Group orbitRing(double radius, Color color, double y) {
        Group ring = new Group();
        Point3D previous = null;
        Point3D first = null;
        int segments = 56;

        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            Point3D current = new Point3D(Math.cos(angle) * radius, y, Math.sin(angle) * radius);

            if (i == 0) {
                first = current;
            }

            if (previous != null) {
                ring.getChildren().add(edge(previous, current, color, 1.0));
            }

            previous = current;
        }

        if (previous != null && first != null) {
            ring.getChildren().add(edge(previous, first, color, 1.0));
        }

        return ring;
    }

    private void addFlow(Group layer, List<FlowParticle> particles, Point3D start, Point3D end, double phaseOffset) {
        for (int i = 0; i < 5; i++) {
            Sphere particle = new Sphere(4.2);
            particle.setMaterial(material(Color.web("#facc15"), Color.WHITE));
            layer.getChildren().add(particle);
            particles.add(new FlowParticle(particle, start, end, phaseOffset + i * 0.16, 0.55 + i * 0.035));
        }
    }

    private void startFlowAnimation(List<FlowParticle> particles) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double seconds = now / 1_000_000_000.0;
                for (FlowParticle particle : particles) {
                    particle.update(seconds);
                }
            }
        };
        timer.start();
    }

    private void startPulseAnimation(List<Node> nodes) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double seconds = now / 1_000_000_000.0;
                for (int i = 0; i < nodes.size(); i++) {
                    Node node = nodes.get(i);
                    double scale = 1.0 + Math.sin(seconds * 2.7 + i * 0.65) * 0.10;
                    node.setScaleX(scale);
                    node.setScaleY(scale);
                    node.setScaleZ(scale);
                }
            }
        };
        timer.start();
    }

    private Cylinder edge(Point3D start, Point3D end, Color color, double radius) {
        Point3D direction = end.subtract(start);
        Point3D midpoint = start.midpoint(end);

        Cylinder cylinder = new Cylinder(radius, direction.magnitude());
        cylinder.setMaterial(material(color, Color.WHITE));

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D rotationAxis = yAxis.crossProduct(direction);
        double angle = Math.toDegrees(Math.acos(yAxis.normalize().dotProduct(direction.normalize())));

        cylinder.getTransforms().add(new Translate(midpoint.getX(), midpoint.getY(), midpoint.getZ()));
        if (rotationAxis.magnitude() > 0.0001) {
            cylinder.getTransforms().add(new Rotate(angle, rotationAxis));
        }

        return cylinder;
    }

    private PhongMaterial material(Color diffuse, Color specular) {
        PhongMaterial material = new PhongMaterial(diffuse);
        material.setSpecularColor(specular);
        material.setSpecularPower(26);
        return material;
    }

    private PointLight pointLight(Color color, double x, double y, double z) {
        PointLight light = new PointLight(color);
        light.getTransforms().add(new Translate(x, y, z));
        return light;
    }

    private void enableMouseControl(SubScene scene, Rotate xRotate, Rotate yRotate, PerspectiveCamera camera) {
        double[] anchor = new double[4];

        scene.setOnMousePressed(event -> {
            anchor[0] = event.getSceneX();
            anchor[1] = event.getSceneY();
            anchor[2] = xRotate.getAngle();
            anchor[3] = yRotate.getAngle();
        });

        scene.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - anchor[0];
            double deltaY = event.getSceneY() - anchor[1];
            xRotate.setAngle(clamp(anchor[2] - deltaY * 0.35, -68, 35));
            yRotate.setAngle(anchor[3] + deltaX * 0.35);
        });

        scene.setOnScroll(event -> camera.setTranslateZ(clamp(camera.getTranslateZ() + event.getDeltaY() * 0.45, -980, -460)));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private Label createLegend() {
        Label legend = new Label("Arraste para girar | scroll para zoom | arcos = interacoes");
        legend.getStyleClass().add("visual-caption");
        StackPane.setAlignment(legend, Pos.BOTTOM_CENTER);
        return legend;
    }

    private static class FlowParticle {
        private final Sphere node;
        private final Point3D start;
        private final Point3D end;
        private final double phase;
        private final double speed;

        FlowParticle(Sphere node, Point3D start, Point3D end, double phase, double speed) {
            this.node = node;
            this.start = start;
            this.end = end;
            this.phase = phase;
            this.speed = speed;
        }

        void update(double seconds) {
            double progress = (seconds * speed + phase) % 1.0;
            double lift = Math.sin(progress * Math.PI) * -18;
            Point3D position = start.interpolate(end, progress);

            node.setTranslateX(position.getX());
            node.setTranslateY(position.getY() + lift);
            node.setTranslateZ(position.getZ());
            node.setScaleX(0.75 + Math.sin(progress * Math.PI) * 0.65);
            node.setScaleY(node.getScaleX());
            node.setScaleZ(node.getScaleX());
        }
    }
}
