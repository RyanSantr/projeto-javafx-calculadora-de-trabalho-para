package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ResultCard extends VBox {

    private final Label conversionLabel = new Label("Aguardando dados.");
    private final Label formulaLabel = new Label("U = (kq²/a)(√2 - 4)");
    private final Label substitutionLabel = new Label("Digite q e a para ver a substituição.");
    private final Label finalResultLabel = new Label("W = -- J");

    public ResultCard() {
        getStyleClass().add("result-stack");
        setSpacing(12);

        getChildren().addAll(
                section("Conversões", conversionLabel),
                section("Fórmula", formulaLabel),
                section("Substituição", substitutionLabel),
                finalResult()
        );
    }

    public void setResult(String conversions, String formula, String substitution, String finalResult) {
        conversionLabel.setText(conversions);
        formulaLabel.setText(formula);
        substitutionLabel.setText(substitution);
        finalResultLabel.setText(finalResult);
        Animations.slideIn(this, 22, 0);
    }

    public void clear() {
        conversionLabel.setText("Aguardando dados.");
        formulaLabel.setText("U = (kq²/a)(√2 - 4)");
        substitutionLabel.setText("Digite q e a para ver a substituição.");
        finalResultLabel.setText("W = -- J");
    }

    private VBox section(String title, Label content) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        content.getStyleClass().add("section-text");
        content.setWrapText(true);

        VBox box = new VBox(6, titleLabel, content);
        box.getStyleClass().add("result-mini-card");
        Animations.installHoverScale(box);
        return box;
    }

    private VBox finalResult() {
        Label titleLabel = new Label("Resultado final");
        titleLabel.getStyleClass().add("section-title");

        finalResultLabel.getStyleClass().add("final-result");
        finalResultLabel.setAlignment(Pos.CENTER);
        finalResultLabel.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(8, titleLabel, finalResultLabel);
        box.getStyleClass().add("final-result-card");
        Animations.installHoverScale(box);
        return box;
    }
}
