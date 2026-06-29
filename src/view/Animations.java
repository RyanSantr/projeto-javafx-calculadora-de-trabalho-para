package view;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public final class Animations {

    private Animations() {
    }

    public static void fadeIn(Node node, double delayMillis) {
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(650), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMillis));
        fade.play();
    }

    public static void slideIn(Node node, double fromX, double delayMillis) {
        node.setTranslateX(fromX);
        node.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMillis));

        TranslateTransition slide = new TranslateTransition(Duration.millis(500), node);
        slide.setFromX(fromX);
        slide.setToX(0);
        slide.setDelay(Duration.millis(delayMillis));

        fade.play();
        slide.play();
    }

    public static void installHoverScale(Node node) {
        node.setOnMouseEntered(event -> scale(node, 1.015));
        node.setOnMouseExited(event -> scale(node, 1.0));
        node.setOnMousePressed(event -> scale(node, 0.985));
        node.setOnMouseReleased(event -> scale(node, 1.015));
    }

    private static void scale(Node node, double value) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(140), node);
        scale.setToX(value);
        scale.setToY(value);
        scale.play();
    }
}
