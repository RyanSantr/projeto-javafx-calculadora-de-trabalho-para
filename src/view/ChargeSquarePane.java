package view;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ChargeSquarePane extends StackPane {

    private static final double SIZE = 360;
    private static final double START = 70;
    private static final double END = 290;
    private static final double RADIUS = 24;

    public ChargeSquarePane() {
        getStyleClass().add("diagram-pane");
        setMinSize(SIZE, SIZE);
        setPrefSize(SIZE, SIZE);
        setMaxSize(SIZE, SIZE);
        setAlignment(Pos.CENTER);
        draw();
    }

    private void draw() {
        Group group = new Group();

        Line top = sideLine(START, START, END, START);
        Line right = sideLine(END, START, END, END);
        Line bottom = sideLine(START, END, END, END);
        Line left = sideLine(START, START, START, END);

        Text sideLabel = label("a", (START + END) / 2, END + 34, "side-label");

        group.getChildren().addAll(
                top, right, bottom, left,
                sideLabel,
                charge(START, START, "+q", Color.web("#e53935")),
                charge(END, START, "-q", Color.web("#1e63d6")),
                charge(START, END, "-q", Color.web("#1e63d6")),
                charge(END, END, "+q", Color.web("#e53935"))
        );

        getChildren().add(group);
    }

    private Line sideLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.getStyleClass().add("square-line");
        return line;
    }

    private Group charge(double x, double y, String symbol, Color color) {
        Circle circle = new Circle(x, y, RADIUS);
        circle.setFill(color);
        circle.getStyleClass().add("charge-circle");

        Text text = label(symbol, x, y + 6, "charge-text");
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        return new Group(circle, text);
    }

    private Text label(String value, double x, double y, String styleClass) {
        Text text = new Text(value);
        text.getStyleClass().add(styleClass);
        text.setX(x);
        text.setY(y);

        text.layoutBoundsProperty().addListener((observable, oldValue, bounds) ->
                text.setX(x - bounds.getWidth() / 2)
        );

        return text;
    }
}
