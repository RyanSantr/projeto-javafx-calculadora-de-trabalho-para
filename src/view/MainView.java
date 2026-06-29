package view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.PhysicsCalculator;
import model.PhysicsCalculator.WorkResult;

public class MainView extends BorderPane {

    private static final String DEFAULT_MESSAGE = "Digite os valores para calcular automaticamente.";
    private static final String PARTIAL_INPUT_MESSAGE = "Preencha q e a para ver o cálculo completo.";
    private static final String EXAMPLE_CHARGE_PC = "2,30";
    private static final String EXAMPLE_SIDE_CM = "64";

    private final PhysicsCalculator calculator = new PhysicsCalculator();
    private final TextField chargeField = new TextField();
    private final TextField sideField = new TextField();
    private final Label resultLabel = new Label(DEFAULT_MESSAGE);
    private final Label errorLabel = new Label();
    private final Label statusLabel = new Label("Cálculo automático ativo.");
    private final WorkGraphPane workGraphPane = new WorkGraphPane();
    private final TabPane visualTabs = new TabPane();
    private final PauseTransition autoCalculateDelay = new PauseTransition(Duration.millis(250));

    public MainView() {
        getStyleClass().add("root-pane");
        setPadding(new Insets(28));

        setLeft(createInputPanel());
        setCenter(createCenterPanel());
        setRight(createResultPanel());
        setupAutoCalculation();
    }

    private VBox createInputPanel() {
        Label title = new Label("Entradas");
        title.getStyleClass().add("panel-title");

        Label chargeLabel = new Label("Valor da carga q (pC)");
        chargeLabel.getStyleClass().add("field-label");
        chargeField.setPromptText("Ex.: 2,30");
        chargeField.getStyleClass().add("input-field");

        Label sideLabel = new Label("Distância a (cm)");
        sideLabel.getStyleClass().add("field-label");
        sideField.setPromptText("Ex.: 64");
        sideField.getStyleClass().add("input-field");

        Button calculateButton = new Button("Calcular");
        calculateButton.getStyleClass().addAll("primary-button", "action-button");
        calculateButton.setMaxWidth(Double.MAX_VALUE);
        calculateButton.setOnAction(event -> calculate(true));

        Button clearButton = new Button("Limpar");
        clearButton.getStyleClass().addAll("secondary-button", "action-button");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setOnAction(event -> clear());

        Button exampleButton = new Button("Exemplo");
        exampleButton.getStyleClass().addAll("secondary-button", "action-button");
        exampleButton.setMaxWidth(Double.MAX_VALUE);
        exampleButton.setOnAction(event -> loadExample());

        HBox buttons = new HBox(10, calculateButton, clearButton, exampleButton);
        buttons.setAlignment(Pos.CENTER);
        HBox.setHgrow(calculateButton, Priority.ALWAYS);
        HBox.setHgrow(clearButton, Priority.ALWAYS);
        HBox.setHgrow(exampleButton, Priority.ALWAYS);

        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox panel = new VBox(14,
                title,
                chargeLabel,
                chargeField,
                sideLabel,
                sideField,
                buttons,
                errorLabel,
                statusLabel,
                spacer,
                createFormulaSummary()
        );
        panel.getStyleClass().add("side-panel");
        panel.setPrefWidth(300);
        panel.setMaxWidth(320);

        return panel;
    }

    private VBox createFormulaSummary() {
        Label title = new Label("Modelo físico");
        title.getStyleClass().add("mini-title");

        Label formula = new Label("W = U = (kq²/a)(√2 - 4)");
        formula.getStyleClass().add("formula-chip");

        Label constant = new Label("k = 8,99 × 10^9 N·m²/C²");
        constant.getStyleClass().add("muted-text");

        VBox box = new VBox(8, title, formula, constant);
        box.getStyleClass().add("info-box");
        return box;
    }

    private VBox createCenterPanel() {
        Label title = new Label("Arranjo das cargas");
        title.getStyleClass().add("panel-title");

        Label subtitle = new Label("+q e -q posicionadas nos vértices de um quadrado");
        subtitle.getStyleClass().add("muted-text");

        visualTabs.getStyleClass().add("visual-tabs");
        visualTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab diagram2dTab = new Tab("2D", new ChargeSquarePane());
        Tab diagram3dTab = new Tab("3D animado", new ChargeSquare3DPane());
        Tab graphTab = new Tab("Grafico W x q", workGraphPane);

        visualTabs.getTabs().addAll(diagram2dTab, diagram3dTab, graphTab);

        VBox panel = new VBox(18, title, subtitle, visualTabs);
        panel.getStyleClass().add("center-panel");
        panel.setAlignment(Pos.CENTER);
        BorderPane.setMargin(panel, new Insets(0, 24, 0, 24));
        VBox.setVgrow(visualTabs, Priority.ALWAYS);

        return panel;
    }

    private VBox createResultPanel() {
        Label title = new Label("Cálculo");
        title.getStyleClass().add("panel-title");

        resultLabel.getStyleClass().add("result-text");
        resultLabel.setWrapText(true);
        resultLabel.setMinHeight(460);
        resultLabel.setMaxHeight(Double.MAX_VALUE);

        VBox panel = new VBox(18, title, resultLabel);
        panel.getStyleClass().add("side-panel");
        panel.setPrefWidth(360);
        panel.setMaxWidth(390);
        VBox.setVgrow(resultLabel, Priority.ALWAYS);

        return panel;
    }

    private void setupAutoCalculation() {
        autoCalculateDelay.setOnFinished(event -> calculate(false));
        chargeField.textProperty().addListener((observable, oldValue, newValue) -> scheduleAutoCalculation());
        sideField.textProperty().addListener((observable, oldValue, newValue) -> scheduleAutoCalculation());
    }

    private void scheduleAutoCalculation() {
        clearValidationState();

        if (allInputsBlank()) {
            resetOutput();
            return;
        }

        if (hasPartialInput()) {
            resultLabel.setText(PARTIAL_INPUT_MESSAGE);
            workGraphPane.clearGraph();
            return;
        }

        autoCalculateDelay.playFromStart();
    }

    private void calculate(boolean showValidationErrors) {
        clearValidationState();

        try {
            double qPc = parsePositiveNumber(chargeField, "Valor da carga q");
            double aCm = parsePositiveNumber(sideField, "Distância a");
            WorkResult result = calculator.calculateFromUserUnits(qPc, aCm);

            resultLabel.setText(buildResultText(result));
            statusLabel.setText("Resultado atualizado automaticamente.");
            workGraphPane.updateGraph(result);
        } catch (IllegalArgumentException exception) {
            if (showValidationErrors) {
                errorLabel.setText(exception.getMessage());
                resultLabel.setText("Corrija os campos destacados para calcular o trabalho.");
            }
        }
    }

    public void calculateWithValues(String chargePc, String sideCm) {
        chargeField.setText(chargePc);
        sideField.setText(sideCm);
        calculate(true);
    }

    private double parsePositiveNumber(TextField field, String fieldName) {
        String value = field.getText();

        if (value == null || value.isBlank()) {
            markAsInvalid(field);
            throw new IllegalArgumentException(fieldName + " é obrigatório.");
        }

        String normalized = value.trim().replace(",", ".");

        try {
            double number = Double.parseDouble(normalized);
            if (!Double.isFinite(number) || number <= 0) {
                markAsInvalid(field);
                throw new IllegalArgumentException(fieldName + " deve ser maior que zero.");
            }
            return number;
        } catch (NumberFormatException exception) {
            markAsInvalid(field);
            throw new IllegalArgumentException(fieldName + " deve ser um número válido. Use vírgula ou ponto para decimais.");
        }
    }

    private String buildResultText(WorkResult result) {
        return """
                Fórmula utilizada
                W = (kq²/a)(√2 - 4)

                Conversões realizadas
                q = %s pC
                q = %s C

                a = %s cm
                a = %s m

                Substituição
                W = ((8,99 × 10^9) · (%s)² / %s) · (√2 - 4)

                Resultado final
                W = %s J
                """.formatted(
                formatDecimal(result.chargePc()),
                formatScientific(result.chargeCoulomb()),
                formatDecimal(result.sideCm()),
                formatDecimal(result.sideMeter()),
                formatScientific(result.chargeCoulomb()),
                formatDecimal(result.sideMeter()),
                formatScientific(result.workJoule())
        );
    }

    private String formatDecimal(double value) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("pt-BR"));
        DecimalFormat decimalFormat = new DecimalFormat("0.########", symbols);
        return decimalFormat.format(value);
    }

    private String formatScientific(double value) {
        if (value == 0) {
            return "0";
        }

        int exponent = (int) Math.floor(Math.log10(Math.abs(value)));
        double mantissa = value / Math.pow(10, exponent);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("pt-BR"));
        DecimalFormat mantissaFormat = new DecimalFormat("0.00", symbols);

        return mantissaFormat.format(mantissa) + " × 10^" + exponent;
    }

    private void clear() {
        chargeField.clear();
        sideField.clear();
        clearValidationState();
        resetOutput();
    }

    private void loadExample() {
        chargeField.setText(EXAMPLE_CHARGE_PC);
        sideField.setText(EXAMPLE_SIDE_CM);
        calculate(true);
    }

    private void resetOutput() {
        resultLabel.setText(DEFAULT_MESSAGE);
        statusLabel.setText("Cálculo automático ativo.");
        workGraphPane.clearGraph();
    }

    private void clearValidationState() {
        errorLabel.setText("");
        chargeField.getStyleClass().remove("input-error");
        sideField.getStyleClass().remove("input-error");
    }

    private void markAsInvalid(TextField field) {
        if (!field.getStyleClass().contains("input-error")) {
            field.getStyleClass().add("input-error");
        }
    }

    private boolean allInputsBlank() {
        return chargeField.getText().isBlank() && sideField.getText().isBlank();
    }

    private boolean hasPartialInput() {
        return chargeField.getText().isBlank() || sideField.getText().isBlank();
    }
}
