package view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ChargeSquare3DPane extends StackPane {

    private static final double SIZE = 390;
    private static final double CENTER_X = 195;
    private static final double CENTER_Y = 218;
    private static final double HALF_SIDE = 108;

    private static final Color POSITIVE = Color.web("#ff365f");
    private static final Color NEGATIVE = Color.web("#2dd4ff");
    private static final Color WAVE = Color.web("#facc15");

    private final List<WavePulse> waves = new ArrayList<>();
    private final List<Circle> halos = new ArrayList<>();

    public ChargeSquare3DPane() {
        getStyleClass().add("visual-pane");
        setMinSize(SIZE, SIZE);
        setPrefSize(SIZE, SIZE);
        setMaxSize(SIZE, SIZE);
        setAlignment(Pos.CENTER);

        Pane scene = new Pane();
        scene.setMinSize(SIZE, SIZE);
        scene.setPrefSize(SIZE, SIZE);
        scene.setMaxSize(SIZE, SIZE);
        scene.getStyleClass().add("pseudo-3d-pane");

        buildScene(scene);
        getChildren().addAll(scene, createLegend());
        startAnimation();
    }

    private void buildScene(Pane scene) {
        IsoPoint topLeft = new IsoPoint(-HALF_SIDE, -HALF_SIDE);
        IsoPoint topRight = new IsoPoint(HALF_SIDE, -HALF_SIDE);
        IsoPoint bottomLeft = new IsoPoint(-HALF_SIDE, HALF_SIDE);
        IsoPoint bottomRight = new IsoPoint(HALF_SIDE, HALF_SIDE);

        scene.getChildren().addAll(platform(topLeft, topRight, bottomRight, bottomLeft));
        scene.getChildren().add(grid());

        Group waveLayer = new Group();
        scene.getChildren().addAll(
                connection(topLeft, topRight, Color.web("#2dd4ff", 0.85), 4.0),
                connection(topRight, bottomRight, Color.web("#67e8f9", 0.70), 3.0),
                connection(bottomLeft, bottomRight, Color.web("#2dd4ff", 0.85), 4.0),
                connection(topLeft, bottomLeft, Color.web("#67e8f9", 0.70), 3.0),
                connection(topLeft, bottomRight, Color.web("#ff4d6d", 0.30), 1.8),
                connection(topRight, bottomLeft, Color.web("#2dd4ff", 0.28), 1.8),
                waveLayer
        );

        addWaveLine(waveLayer, topLeft, topRight, 0.00);
        addWaveLine(waveLayer, topLeft, bottomLeft, 0.15);
        addWaveLine(waveLayer, bottomRight, topRight, 0.30);
        addWaveLine(waveLayer, bottomRight, bottomLeft, 0.45);
        addWaveLine(waveLayer, topLeft, bottomRight, 0.60);
        addWaveLine(waveLayer, topRight, bottomLeft, 0.78);

        List<Group> charges = List.of(
                charge(topLeft, true),
                charge(topRight, false),
                charge(bottomLeft, false),
                charge(bottomRight, true)
        );

        charges.stream()
                .sorted(Comparator.comparingDouble(group -> (double) group.getUserData()))
                .forEach(scene.getChildren()::add);
    }

    private Group platform(IsoPoint... points) {
        Polygon top = new Polygon();
        Polygon shadow = new Polygon();

        for (IsoPoint point : points) {
            Point2D projected = project(point);
            top.getPoints().addAll(projected.getX(), projected.getY());
            shadow.getPoints().addAll(projected.getX(), projected.getY() + 18);
        }

        shadow.setFill(Color.web("#020617", 0.70));
        shadow.setEffect(new DropShadow(28, Color.web("#2dd4ff", 0.14)));

        top.setFill(Color.web("#08111f", 0.72));
        top.setStroke(Color.web("#2dd4ff", 0.28));
        top.setStrokeWidth(1.4);

        return new Group(shadow, top);
    }

    private Group grid() {
        Group group = new Group();
        Color gridColor = Color.web("#67e8f9", 0.22);

        for (double offset = -HALF_SIDE; offset <= HALF_SIDE; offset += 36) {
            group.getChildren().add(connection(new IsoPoint(offset, -HALF_SIDE), new IsoPoint(offset, HALF_SIDE), gridColor, 0.75));
            group.getChildren().add(connection(new IsoPoint(-HALF_SIDE, offset), new IsoPoint(HALF_SIDE, offset), gridColor, 0.75));
        }

        return group;
    }

    private Line connection(IsoPoint start, IsoPoint end, Color color, double width) {
        Point2D a = project(start);
        Point2D b = project(end);

        Line line = new Line(a.getX(), a.getY(), b.getX(), b.getY());
        line.setStroke(color);
        line.setStrokeWidth(width);
        line.setEffect(new DropShadow(14, Color.web("#2dd4ff", 0.34)));
        return line;
    }

    private Group charge(IsoPoint base, boolean positive) {
        Point2D ground = project(base);
        double height = 34;
        double x = ground.getX();
        double y = ground.getY() - height;

        Ellipse shadow = new Ellipse(x, ground.getY() + 7, 29, 8);
        shadow.setFill(Color.web("#020617", 0.65));

        Line stem = new Line(x, ground.getY(), x, y + 18);
        stem.setStroke(Color.web("#e2e8f0", 0.32));
        stem.setStrokeWidth(1.5);

        Color color = positive ? POSITIVE : NEGATIVE;
        Circle halo = new Circle(x, y, 31);
        halo.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.18));
        halo.setEffect(new DropShadow(26, color));

        Circle body = new Circle(x, y, 23);
        body.setFill(radial(color));
        body.setStroke(Color.WHITE);
        body.setStrokeWidth(2);
        body.setEffect(new DropShadow(18, color));

        Circle shine = new Circle(x - 8, y - 9, 5);
        shine.setFill(Color.web("#ffffff", 0.82));

        Text label = new Text(positive ? "+q" : "-q");
        label.setFill(Color.WHITE);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        label.setX(x - 12);
        label.setY(y + 6);

        halos.add(halo);
        Group group = new Group(shadow, stem, halo, body, shine, label);
        group.setUserData(ground.getY());
        return group;
    }

    private RadialGradient radial(Color color) {
        return new RadialGradient(
                0,
                0,
                0.30,
                0.25,
                0.85,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(0.18, color.brighter()),
                new Stop(1, color.darker())
        );
    }

    private void addWaveLine(Group layer, IsoPoint start, IsoPoint end, double phaseOffset) {
        Point2D a = project(start);
        Point2D b = project(end);

        for (int i = 0; i < 4; i++) {
            WavePulse pulse = new WavePulse(a, b, phaseOffset + i * 0.22, 0.46 + i * 0.04);
            waves.add(pulse);
            layer.getChildren().add(pulse.node());
        }
    }

    private void startAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double seconds = now / 1_000_000_000.0;

                for (WavePulse wave : waves) {
                    wave.update(seconds);
                }

                for (int i = 0; i < halos.size(); i++) {
                    double scale = 1.0 + Math.sin(seconds * 3.1 + i * 0.8) * 0.09;
                    Circle halo = halos.get(i);
                    halo.setScaleX(scale);
                    halo.setScaleY(scale);
                    halo.setOpacity(0.58 + Math.sin(seconds * 2.4 + i) * 0.12);
                }
            }
        };
        timer.start();
    }

    private Point2D project(IsoPoint point) {
        double x = CENTER_X + (point.x() - point.z()) * 0.78;
        double y = CENTER_Y + (point.x() + point.z()) * 0.34;
        return new Point2D(x, y);
    }

    private Label createLegend() {
        Label legend = new Label("2.5D isometrico: desenhado em 2D com ondas eletricas lineares");
        legend.getStyleClass().add("visual-caption");
        StackPane.setAlignment(legend, Pos.BOTTOM_CENTER);
        return legend;
    }

    private record IsoPoint(double x, double z) {
    }

    private static class WavePulse {
        private final Group node;
        private final Point2D start;
        private final Point2D end;
        private final double phase;
        private final double speed;
        private final Line tail;
        private final Circle glow;
        private final Circle head;

        WavePulse(Point2D start, Point2D end, double phase, double speed) {
            this.start = start;
            this.end = end;
            this.phase = phase;
            this.speed = speed;

            tail = new Line();
            tail.setStroke(Color.web("#facc15", 0.55));
            tail.setStrokeWidth(2.2);
            tail.setEffect(new DropShadow(14, WAVE));

            glow = new Circle(8);
            glow.setFill(Color.web("#facc15", 0.16));

            head = new Circle(4.4);
            head.setFill(WAVE);
            head.setStroke(Color.WHITE);
            head.setStrokeWidth(0.9);
            head.setEffect(new DropShadow(16, WAVE));

            node = new Group(tail, glow, head);
        }

        Group node() {
            return node;
        }

        void update(double seconds) {
            double progress = (seconds * speed + phase) % 1.0;
            double previous = Math.max(0, progress - 0.10);

            Point2D current = start.interpolate(end, progress);
            Point2D trail = start.interpolate(end, previous);
            double pulse = Math.sin(progress * Math.PI);

            tail.setStartX(trail.getX());
            tail.setStartY(trail.getY());
            tail.setEndX(current.getX());
            tail.setEndY(current.getY());
            tail.setOpacity(0.22 + pulse * 0.70);

            glow.setCenterX(current.getX());
            glow.setCenterY(current.getY());
            glow.setScaleX(0.85 + pulse * 0.65);
            glow.setScaleY(glow.getScaleX());
            glow.setOpacity(0.22 + pulse * 0.50);

            head.setCenterX(current.getX());
            head.setCenterY(current.getY());
            head.setScaleX(0.82 + pulse * 0.36);
            head.setScaleY(head.getScaleX());
            head.setOpacity(0.36 + pulse * 0.64);
        }
    }
}
