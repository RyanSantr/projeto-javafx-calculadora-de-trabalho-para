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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.PhysicsCalculator;
import model.PhysicsCalculator.WorkResult;

public class MainView extends StackPane {

    private static final String DEFAULT_MESSAGE = "Digite os valores para calcular automaticamente.";
    private static final String PARTIAL_INPUT_MESSAGE = "Preencha q e a para ver o cálculo completo.";
    private static final String EXAMPLE_CHARGE_PC = "2,30";
    private static final String EXAMPLE_SIDE_CM = "64";

    private final PhysicsCalculator calculator = new PhysicsCalculator();
    private final TextField chargeField = new TextField();
    private final TextField sideField = new TextField();
    private final Label statusLabel = new Label("● Sistema pronto");
    private final Label helperLabel = new Label(DEFAULT_MESSAGE);
    private final ResultCard resultCard = new ResultCard();
    private final HistoryPane historyPane = new HistoryPane();
    private final WorkGraphPane workGraphPane = new WorkGraphPane();
    private final TabPane visualTabs = new TabPane();
    private final PauseTransition autoCalculateDelay = new PauseTransition(Duration.millis(250));

    public MainView() {
        getStyleClass().add("root-pane");

        VBox shell = new VBox(22, createHeader(), createDashboard(), historyPane);
        shell.getStyleClass().add("app-shell");
        shell.setFillWidth(true);

        getChildren().addAll(createBackgroundGrid(), shell);
        setupAutoCalculation();
        playEntranceAnimation(shell);
    }

    private StackPane createBackgroundGrid() {
        StackPane background = new StackPane();
        background.getStyleClass().add("space-background");
        return background;
    }

    private HBox createHeader() {
        Label title = new Label("⚡ ELECTRIC FIELD SIMULATOR");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Physics Engine • JavaFX • Java 21");
        subtitle.getStyleClass().add("app-subtitle");

        VBox titleBox = new VBox(4, title, subtitle);

        statusLabel.getStyleClass().add("system-status");

        HBox header = new HBox(24, titleBox, statusLabel);
        header.getStyleClass().add("header-card");
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        return header;
    }

    private GridPane createDashboard() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("dashboard-grid");
        grid.setHgap(22);

        ColumnConstraints left = new ColumnConstraints();
        left.setMinWidth(285);
        left.setPrefWidth(310);

        ColumnConstraints center = new ColumnConstraints();
        center.setHgrow(Priority.ALWAYS);
        center.setMinWidth(420);

        ColumnConstraints right = new ColumnConstraints();
        right.setMinWidth(340);
        right.setPrefWidth(380);

        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(left, center, right);
        grid.getRowConstraints().add(row);
        grid.add(createInputPanel(), 0, 0);
        grid.add(createSimulationPanel(), 1, 0);
        grid.add(createResultPanel(), 2, 0);

        return grid;
    }

    private VBox createInputPanel() {
        Label title = new Label("Entradas");
        title.getStyleClass().add("panel-title");

        chargeField.setPromptText("⚡ Carga elétrica (pC)");
        chargeField.getStyleClass().add("input-field");

        sideField.setPromptText("📏 Distância (cm)");
        sideField.getStyleClass().add("input-field");

        Button calculateButton = button("Calcular", "primary-button");
        calculateButton.setOnAction(event -> calculate(true, true));

        Button clearButton = button("Limpar", "danger-button");
        clearButton.setOnAction(event -> clear());

        Button exampleButton = button("Exemplo", "secondary-button");
        exampleButton.setOnAction(event -> loadExample());

        HBox buttons = new HBox(10, calculateButton, clearButton, exampleButton);
        buttons.setAlignment(Pos.CENTER);
        HBox.setHgrow(calculateButton, Priority.ALWAYS);
        HBox.setHgrow(clearButton, Priority.ALWAYS);
        HBox.setHgrow(exampleButton, Priority.ALWAYS);

        helperLabel.getStyleClass().add("helper-label");
        helperLabel.setWrapText(true);

        VBox panel = new VBox(16, title, chargeField, sideField, buttons, helperLabel, createFormulaSummary());
        panel.getStyleClass().add("glass-card");
        panel.setMinHeight(520);
        Animations.installHoverScale(panel);
        return panel;
    }

    private VBox createFormulaSummary() {
        Label title = new Label("Modelo físico");
        title.getStyleClass().add("mini-title");

        Label formula = new Label("U = (kq²/a)(√2 − 4)");
        formula.getStyleClass().add("formula-chip");

        Label constant = new Label("k = 8,99 × 10⁹ N·m²/C²");
        constant.getStyleClass().add("muted-text");

        VBox box = new VBox(8, title, formula, constant);
        box.getStyleClass().add("info-box");
        return box;
    }

    private VBox createSimulationPanel() {
        Label title = new Label("Simulação 2D/3D");
        title.getStyleClass().add("panel-title");

        Label subtitle = new Label("Cargas alternadas nos vértices do quadrado");
        subtitle.getStyleClass().add("muted-text");

        visualTabs.getStyleClass().add("visual-tabs");
        visualTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        visualTabs.getTabs().setAll(
                new Tab("Cena 2D", new ChargeSquarePane()),
                new Tab("Núcleo 3D", new ChargeSquare3DPane()),
                new Tab("Curva W × q", workGraphPane)
        );

        VBox panel = new VBox(14, title, subtitle, visualTabs);
        panel.getStyleClass().add("glass-card");
        panel.setAlignment(Pos.CENTER);
        VBox.setVgrow(visualTabs, Priority.ALWAYS);
        Animations.installHoverScale(panel);
        return panel;
    }

    private VBox createResultPanel() {
        Label title = new Label("Resultado");
        title.getStyleClass().add("panel-title");

        VBox panel = new VBox(14, title, resultCard);
        panel.getStyleClass().add("glass-card");
        VBox.setVgrow(resultCard, Priority.ALWAYS);
        Animations.installHoverScale(panel);
        return panel;
    }

    private Button button(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("action-button", styleClass);
        button.setMaxWidth(Double.MAX_VALUE);
        Animations.installHoverScale(button);
        return button;
    }

    private void setupAutoCalculation() {
        autoCalculateDelay.setOnFinished(event -> calculate(false, false));
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
            helperLabel.setText(PARTIAL_INPUT_MESSAGE);
            resultCard.clear();
            workGraphPane.clearGraph();
            return;
        }

        autoCalculateDelay.playFromStart();
    }

    private void calculate(boolean showValidationErrors, boolean addHistory) {
        clearValidationState();

        try {
            double chargePc = parsePositiveNumber(chargeField, "Valor da carga q");
            double sideCm = parsePositiveNumber(sideField, "Distância a");
            WorkResult result = calculator.calculateFromUserUnits(chargePc, sideCm);

            updateResult(result);
            workGraphPane.updateGraph(result);
            statusLabel.setText("● Sistema calculando em tempo real");
            helperLabel.setText("Resultado atualizado. A curva e os cards já estão sincronizados.");

            if (addHistory) {
                historyPane.addEntry(formatDecimal(result.chargePc()), formatDecimal(result.sideCm()), formatScientific(result.workJoule()));
            }
        } catch (IllegalArgumentException exception) {
            if (showValidationErrors) {
                helperLabel.setText(exception.getMessage());
                statusLabel.setText("● Entrada inválida");
            }
        }
    }

    private void updateResult(WorkResult result) {
        String conversions = "q = " + formatDecimal(result.chargePc()) + " pC\n"
                + "q = " + formatScientific(result.chargeCoulomb()) + " C\n\n"
                + "a = " + formatDecimal(result.sideCm()) + " cm\n"
                + "a = " + formatDecimal(result.sideMeter()) + " m";

        String formula = "        kq²\nU = -------- × (√2 − 4)\n        a";

        String substitution = "U = ((8,99 × 10⁹) · (" + formatScientific(result.chargeCoulomb())
                + ")² / " + formatDecimal(result.sideMeter()) + ") · (√2 − 4)";

        resultCard.setResult(conversions, formula, substitution, "W = " + formatScientific(result.workJoule()) + " J");
    }

    public void calculateWithValues(String chargePc, String sideCm) {
        chargeField.setText(chargePc);
        sideField.setText(sideCm);
        calculate(true, true);
    }

    private double parsePositiveNumber(TextField field, String fieldName) {
        String value = field.getText();

        if (value == null || value.isBlank()) {
            markAsInvalid(field);
            throw new IllegalArgumentException(fieldName + " é obrigatório.");
        }

        try {
            double number = Double.parseDouble(value.trim().replace(",", "."));
            if (!Double.isFinite(number) || number <= 0) {
                markAsInvalid(field);
                throw new IllegalArgumentException(fieldName + " deve ser maior que zero.");
            }
            return number;
        } catch (NumberFormatException exception) {
            markAsInvalid(field);
            throw new IllegalArgumentException(fieldName + " deve ser um número válido.");
        }
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
        calculate(true, true);
    }

    private void resetOutput() {
        helperLabel.setText(DEFAULT_MESSAGE);
        statusLabel.setText("● Sistema pronto");
        resultCard.clear();
        workGraphPane.clearGraph();
    }

    private void clearValidationState() {
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

    private void playEntranceAnimation(VBox shell) {
        for (int i = 0; i < shell.getChildren().size(); i++) {
            Animations.fadeIn(shell.getChildren().get(i), i * 120);
        }
    }
}
