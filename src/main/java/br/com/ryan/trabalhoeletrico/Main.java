package br.com.ryan.trabalhoeletrico;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Main extends Application {

    static {
        System.setProperty("prism.order", System.getProperty("prism.order", "sw"));
    }

    private static final double DESIGN_W = 1400;
    private static final double DESIGN_H = 1000;
    private static final double K = 8.99e9;
    private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final double WAVE_PERIOD = 700;

    private TextField qField;
    private TextField aField;
    private Label statusLabel;
    private Label qConversionLabel;
    private Label aConversionLabel;
    private Label substitutionLine1;
    private Label substitutionLine2;
    private Label resultLabel;
    private Label clockLabel;

    private Pane inputPanel;
    private Pane diagramWindow;
    private Pane calculationsPanel;
    private Pane calculationAnimationLayer;
    private Rectangle calculationScanLine;
    private Label calculationAnimationLabel;
    private Timeline calculationAnimation;
    private Node resultBox;
    private Pane characterLayer;
    private AudioClip calculateSound;

    private final DecimalFormat inputFormat = new DecimalFormat("0.00", commaSymbols());
    private final DecimalFormat factorFormat = new DecimalFormat("0.0000000", commaSymbols());
    private final DecimalFormat scientificFormat = new DecimalFormat("0.00E0", DecimalFormatSymbols.getInstance(Locale.US));

    private static DecimalFormatSymbols commaSymbols() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        return symbols;
    }

    @Override
    public void start(Stage stage) {
        Pane desktop = createDesktop();
        Pane root = new Pane(desktop);
        root.getStyleClass().add("root");
        root.setMinSize(0, 0);
        DoubleBinding scale = Bindings.createDoubleBinding(
                () -> Math.min(root.getWidth() / DESIGN_W, root.getHeight() / DESIGN_H),
                root.widthProperty(),
                root.heightProperty()
        );
        Scale desktopScale = new Scale(1, 1, 0, 0);
        desktop.getTransforms().add(desktopScale);

        ChangeListener<Number> resize = (obs, oldValue, newValue) -> {
            double currentScale = scale.get();
            desktopScale.setX(currentScale);
            desktopScale.setY(currentScale);
            desktop.setLayoutX(Math.max(0, (root.getWidth() - DESIGN_W * currentScale) / 2));
            desktop.setLayoutY(Math.max(0, (root.getHeight() - DESIGN_H * currentScale) / 2));
        };
        root.widthProperty().addListener(resize);
        root.heightProperty().addListener(resize);
        resize.changed(null, null, null);

        Scene scene = new Scene(root, 1366, 900);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        calculateSound = loadSound("/audio/calculate.wav");

        stage.setTitle("Calculadora de Trabalho Elétrico");
        stage.setMinWidth(760);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }

    private Pane createDesktop() {
        Pane desktop = new Pane();
        desktop.setPrefSize(DESIGN_W, DESIGN_H);
        desktop.setMinSize(DESIGN_W, DESIGN_H);
        desktop.setMaxSize(DESIGN_W, DESIGN_H);
        desktop.resize(DESIGN_W, DESIGN_H);

        desktop.getChildren().add(new AnimatedRetroBackground(DESIGN_W, DESIGN_H));
        desktop.getChildren().add(createTopBar());

        inputPanel = createInputPanel();
        inputPanel.setLayoutX(38);
        inputPanel.setLayoutY(105);
        desktop.getChildren().add(inputPanel);

        diagramWindow = createDiagramWindow();
        diagramWindow.setLayoutX(405);
        diagramWindow.setLayoutY(122);
        desktop.getChildren().add(diagramWindow);

        calculationsPanel = createCalculationsPanel();
        calculationsPanel.setLayoutX(365);
        calculationsPanel.setLayoutY(635);
        desktop.getChildren().add(calculationsPanel);

        addCharacter(desktop);
        addRightIcons(desktop);

        calculate();
        return desktop;
    }

    private Pane createTopBar() {
        Pane bar = new Pane();
        bar.getStyleClass().add("top-bar");
        bar.setLayoutX(14);
        bar.setLayoutY(18);
        bar.setPrefSize(1372, 62);

        Label appIcon = new Label("▣");
        appIcon.getStyleClass().add("retro-label");
        appIcon.setFont(Font.font("Consolas", FontWeight.BOLD, 28));
        appIcon.setLayoutX(24);
        appIcon.setLayoutY(12);

        Label title = new Label("Calculadora de Trabalho Elétrico");
        title.getStyleClass().add("retro-label");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        title.setLayoutX(74);
        title.setLayoutY(18);

        Label menu = new Label("Arquivo     Editar     Exibir     Ajuda");
        menu.getStyleClass().add("retro-label");
        menu.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        menu.setLayoutX(445);
        menu.setLayoutY(19);

        clockLabel = new Label();
        clockLabel.getStyleClass().add("retro-label");
        clockLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        clockLabel.setLayoutX(1088);
        clockLabel.setLayoutY(15);
        clockLabel.setPrefWidth(260);
        startClock();

        bar.getChildren().addAll(appIcon, title, menu, clockLabel);
        return bar;
    }

    private Pane createInputPanel() {
        Pane panel = new Pane();
        panel.getStyleClass().add("window");
        panel.setPrefSize(310, 485);

        HBox title = createWindowTitle("①  ENTRADAS", 310, 50);
        panel.getChildren().add(title);

        Label expand = new Label("⤢");
        expand.getStyleClass().add("retro-label");
        expand.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        expand.setLayoutX(260);
        expand.setLayoutY(12);
        panel.getChildren().add(expand);

        Label qLabel = label("Carga q (pC)  ⓘ", 30, 95, 16, true);
        panel.getChildren().add(qLabel);

        qField = new TextField("5,00");
        HBox qInput = inputWithUnit(qField, "pC", 230);
        qInput.setLayoutX(30);
        qInput.setLayoutY(128);
        panel.getChildren().add(qInput);

        panel.getChildren().add(label("1 pC = 10⁻¹² C", 30, 205, 16, false));
        panel.getChildren().add(dashedLine(28, 250, 260));

        panel.getChildren().add(label("Lado a (cm)  ⓘ", 30, 270, 16, true));
        aField = new TextField("10,00");
        HBox aInput = inputWithUnit(aField, "cm", 230);
        aInput.setLayoutX(30);
        aInput.setLayoutY(303);
        panel.getChildren().add(aInput);

        panel.getChildren().add(label("1 cm = 10⁻² m", 30, 380, 16, false));

        Button calc = new Button("⚡  Calcular");
        calc.getStyleClass().add("primary-button");
        calc.setLayoutX(30);
        calc.setLayoutY(425);
        calc.setPrefSize(250, 55);
        calc.setOnAction(e -> handleCalculateClick());

        Button clear = new Button("⟳  Limpar");
        clear.getStyleClass().add("secondary-button");
        clear.setLayoutX(30);
        clear.setLayoutY(492);
        clear.setPrefSize(250, 55);
        clear.setOnAction(e -> clearFields());

        Circle statusDot = new Circle(10, Color.web("#80d18c"));
        statusDot.setStroke(Color.web("#111111"));
        statusDot.setStrokeWidth(2);
        statusDot.setLayoutX(43);
        statusDot.setLayoutY(573);

        statusLabel = label("Pronto para calcular", 70, 555, 14, false);
        statusLabel.setPrefWidth(205);
        statusLabel.setWrapText(true);

        panel.setPrefSize(310, 610);
        panel.getChildren().addAll(calc, clear, statusDot, statusLabel);
        return panel;
    }

    private Pane createDiagramWindow() {
        Pane window = new Pane();
        window.getStyleClass().add("window");
        window.setPrefSize(625, 495);

        HBox title = createWindowTitle("DIAGRAMA — Quatro cargas em um quadrado", 625, 45);
        window.getChildren().add(title);

        DiagramPane diagram = new DiagramPane(555, 365);
        diagram.setLayoutX(35);
        diagram.setLayoutY(75);
        window.getChildren().add(diagram);

        HBox legend = new HBox(30);
        legend.setAlignment(Pos.CENTER);
        legend.getStyleClass().add("window-flat");
        legend.setLayoutX(70);
        legend.setLayoutY(430);
        legend.setPrefSize(485, 54);
        legend.getChildren().addAll(
                legendItem("+", "Positiva (+q)"),
                legendItem("−", "Negativa (−q)")
        );
        window.getChildren().add(legend);

        return window;
    }

    private Pane createCalculationsPanel() {
        Pane panel = new Pane();
        panel.getStyleClass().add("window");
        panel.setPrefSize(830, 340);

        HBox title = createWindowTitle("③  CÁLCULOS", 830, 46);
        panel.getChildren().add(title);

        Line divider = new Line(360, 46, 360, 340);
        divider.setStroke(Color.web("#111111"));
        divider.setStrokeWidth(2);
        panel.getChildren().add(divider);

        Label formulaTitle = label("Fórmula do trabalho elétrico", 40, 75, 15, false);
        panel.getChildren().add(formulaTitle);

        StackPane formulaBox = new StackPane();
        formulaBox.getStyleClass().add("formula-box");
        formulaBox.setLayoutX(28);
        formulaBox.setLayoutY(105);
        formulaBox.setPrefSize(285, 92);
        Label formula = new Label("W = (k q² / a)(√2 - 4)");
        formula.getStyleClass().add("retro-label");
        formula.setFont(Font.font("Georgia", FontWeight.BOLD, 27));
        formulaBox.getChildren().add(formula);
        panel.getChildren().add(formulaBox);

        Label pill = new Label("Conversões");
        pill.getStyleClass().add("info-pill");
        pill.setLayoutX(28);
        pill.setLayoutY(220);
        panel.getChildren().add(pill);

        qConversionLabel = label("q = 5,00 pC = 5,00 × 10⁻¹² C", 62, 265, 16, false);
        aConversionLabel = label("a = 10,00 cm = 1,00 × 10⁻¹ m", 62, 300, 16, false);
        panel.getChildren().addAll(qConversionLabel, aConversionLabel);

        Label substitutionTitle = label("Substituição dos valores", 388, 75, 15, false);
        panel.getChildren().add(substitutionTitle);

        StackPane substitutionBox = new StackPane();
        substitutionBox.getStyleClass().add("formula-box");
        substitutionBox.setLayoutX(382);
        substitutionBox.setLayoutY(103);
        substitutionBox.setPrefSize(410, 95);
        VBox subLines = new VBox(8);
        subLines.setPadding(new Insets(12, 14, 12, 14));
        substitutionLine1 = label("", 0, 0, 14, false);
        substitutionLine2 = label("", 0, 0, 14, false);
        substitutionLine1.setWrapText(true);
        substitutionLine2.setWrapText(true);
        subLines.getChildren().addAll(substitutionLine1, substitutionLine2);
        substitutionBox.getChildren().add(subLines);
        panel.getChildren().add(substitutionBox);

        panel.getChildren().add(dashedLine(385, 220, 380));
        panel.getChildren().add(label("Resultado final (exato)", 390, 245, 15, true));

        StackPane resultBoxPane = new StackPane();
        resultBoxPane.getStyleClass().add("result-box");
        resultBoxPane.setLayoutX(382);
        resultBoxPane.setLayoutY(270);
        resultBoxPane.setPrefSize(410, 58);
        resultBox = resultBoxPane;
        HBox resultLine = new HBox(20);
        resultLine.setAlignment(Pos.CENTER);
        Label bolt = new Label("⚡");
        bolt.setFont(Font.font("Consolas", FontWeight.BOLD, 28));
        bolt.setTextFill(Color.web("#159bc0"));
        resultLabel = new Label("-1.920e-13 J");
        resultLabel.getStyleClass().add("result-number");
        Label star = new Label("☆");
        star.setFont(Font.font("Consolas", FontWeight.BOLD, 28));
        resultLine.getChildren().addAll(bolt, resultLabel, star);
        resultBoxPane.getChildren().add(resultLine);
        panel.getChildren().add(resultBoxPane);

        addCatToCalculationsPanel(panel);
        panel.getChildren().add(createCalculationAnimationLayer());

        return panel;
    }

    private void addCharacter(Pane desktop) {
        characterLayer = new Pane();
        characterLayer.setPickOnBounds(false);

        ImageView girl = imageView("/assets/garota_perfil.png", 220, -1);
        girl.setLayoutX(184);
        girl.setLayoutY(538);
        makeSpriteClickable(girl, "Luna", () -> {
            statusLabel.setText("Luna iniciou os calculos.");
            pulse(girl);
            animateCharacterCalculation();
        });

        ImageView sparkles = imageView("/assets/brilhos.png", 92, -1);
        sparkles.setLayoutX(148);
        sparkles.setLayoutY(662);
        makeSpriteClickable(sparkles, "Brilhos", () -> {
            handleCalculateClick();
            statusLabel.setText("Energia recalculada.");
            pulse(sparkles);
            focusNode(resultBox, "Resultado atualizado.");
        });

        characterLayer.getChildren().addAll(sparkles, girl);
        desktop.getChildren().add(characterLayer);
    }

    private void addCatToCalculationsPanel(Pane panel) {
        ImageView cat = imageView("/assets/gato.png", 62, -1);
        cat.setLayoutX(742);
        cat.setLayoutY(4);
        makeSpriteClickable(cat, "Gato ajudante", () -> {
            statusLabel.setText("Gato: sistema simetrico detectado.");
            pulse(cat);
            focusNode(calculationsPanel, "Painel de calculos destacado.");
        });
        panel.getChildren().add(cat);
    }

    private Pane createCalculationAnimationLayer() {
        Pane layer = new Pane();
        layer.setLayoutX(382);
        layer.setLayoutY(103);
        layer.setPrefSize(410, 225);
        layer.setVisible(false);
        layer.setMouseTransparent(true);
        layer.getStyleClass().add("calculation-overlay");

        calculationScanLine = new Rectangle(62, 225);
        calculationScanLine.getStyleClass().add("calculation-scan");
        calculationScanLine.setTranslateX(-72);

        calculationAnimationLabel = new Label("Preparando calculo...");
        calculationAnimationLabel.getStyleClass().add("calculation-overlay-label");
        calculationAnimationLabel.setLayoutX(28);
        calculationAnimationLabel.setLayoutY(92);
        calculationAnimationLabel.setPrefSize(355, 40);
        calculationAnimationLabel.setAlignment(Pos.CENTER);

        layer.getChildren().addAll(calculationScanLine, calculationAnimationLabel);
        calculationAnimationLayer = layer;
        return layer;
    }

    private void addRightIcons(Pane desktop) {
        addDesktopIcon(desktop, "/assets/computador_retro.png", "Entradas", 1260, 145, 88,
                () -> {
                    focusNode(inputPanel, "Entradas selecionadas.");
                    qField.requestFocus();
                });
        addDesktopIcon(desktop, "/assets/caderno_diagrama.png", "Diagrama", 1256, 285, 86,
                () -> focusNode(diagramWindow, "Diagrama selecionado."));
        addDesktopIcon(desktop, "/assets/calculadora_fofa.png", "Cálculos", 1260, 430, 78,
                () -> focusNode(calculationsPanel, "Cálculos selecionados."));
        addDesktopIcon(desktop, "/assets/documento_raio.png", "Resultado", 1262, 560, 82,
                () -> {
                    calculate();
                    focusNode(resultBox, "Resultado recalculado.");
                });
    }

    private void addDesktopIcon(Pane desktop, String imagePath, String text, double x, double y, double width, Runnable action) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(x);
        box.setLayoutY(y);
        box.setPrefWidth(110);
        box.getStyleClass().add("desktop-icon");
        box.setCursor(Cursor.HAND);
        Tooltip.install(box, new Tooltip("Abrir " + text));
        box.setOnMouseClicked(e -> action.run());
        box.setOnMouseEntered(e -> {
            box.setScaleX(1.06);
            box.setScaleY(1.06);
        });
        box.setOnMouseExited(e -> {
            box.setScaleX(1.0);
            box.setScaleY(1.0);
        });

        ImageView icon = imageView(imagePath, width, -1);
        Label label = new Label(text);
        label.getStyleClass().add("icon-title");
        label.setTextAlignment(TextAlignment.CENTER);

        box.getChildren().addAll(icon, label);
        desktop.getChildren().add(box);
    }

    private void makeSpriteClickable(Node node, String tooltip, Runnable action) {
        node.setCursor(Cursor.HAND);
        node.setPickOnBounds(true);
        Tooltip.install(node, new Tooltip(tooltip));
        node.setOnMouseClicked(e -> action.run());
        node.setOnMouseEntered(e -> {
            node.setScaleX(1.035);
            node.setScaleY(1.035);
        });
        node.setOnMouseExited(e -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }

    private void animateCharacterCalculation() {
        if (calculationAnimationLayer == null || calculationScanLine == null || calculationAnimationLabel == null) {
            calculate();
            focusNode(calculationsPanel, "Calculo concluido.");
            return;
        }

        if (calculationAnimation != null) {
            calculationAnimation.stop();
        }
        calculationsPanel.toFront();
        keepForegroundVisible();
        calculationAnimationLayer.toFront();
        calculationAnimationLayer.setVisible(true);
        calculationAnimationLayer.setOpacity(1.0);
        calculationScanLine.setTranslateX(-72);
        calculationAnimationLabel.setText("Luna esta preparando o calculo...");
        statusLabel.setText("Luna esta calculando...");

        calculationAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(calculationScanLine.translateXProperty(), -72),
                        new KeyValue(calculationAnimationLayer.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(650), e ->
                        calculationAnimationLabel.setText("Convertendo pC para C e cm para m...")),
                new KeyFrame(Duration.millis(1350), e ->
                        calculationAnimationLabel.setText("Substituindo os valores na formula...")),
                new KeyFrame(Duration.millis(2150), e ->
                        calculationAnimationLabel.setText("Aplicando o fator geometrico...")),
                new KeyFrame(Duration.millis(2600),
                        new KeyValue(calculationAnimationLayer.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(3000),
                        new KeyValue(calculationScanLine.translateXProperty(), 430),
                        new KeyValue(calculationAnimationLayer.opacityProperty(), 0.0)
                )
        );
        calculationAnimation.setOnFinished(e -> {
            calculationAnimationLayer.setVisible(false);
            calculate();
            focusNode(resultBox, "Calculo da Luna concluido.");
        });
        calculationAnimation.playFromStart();
    }

    private void focusNode(Node node, String message) {
        if (node == null) return;
        node.toFront();
        keepForegroundVisible();
        node.setEffect(new DropShadow(30, Color.web("#12bfe8")));

        ScaleTransition grow = new ScaleTransition(Duration.millis(120), node);
        grow.setToX(1.025);
        grow.setToY(1.025);

        PauseTransition wait = new PauseTransition(Duration.millis(520));

        ScaleTransition back = new ScaleTransition(Duration.millis(160), node);
        back.setToX(1.0);
        back.setToY(1.0);

        SequentialTransition animation = new SequentialTransition(grow, wait, back);
        animation.setOnFinished(e -> node.setEffect(null));
        animation.play();

        if (statusLabel != null && message != null) {
            statusLabel.setText(message);
        }
    }

    private void handleCalculateClick() {
        playCalculateSound();
        calculate();
    }

    private AudioClip loadSound(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new AudioClip(resource.toExternalForm());
    }

    private void playCalculateSound() {
        if (calculateSound != null) {
            calculateSound.stop();
            calculateSound.play(0.35);
        }
    }

    private void startClock() {
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> clockLabel.setText("‹   ♪   " + LocalTime.now().format(CLOCK_FORMAT) + "   ▣")),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void keepForegroundVisible() {
        if (characterLayer != null) {
            characterLayer.toFront();
        }
    }

    private void pulse(Node node) {
        if (node == null) return;
        ScaleTransition grow = new ScaleTransition(Duration.millis(100), node);
        grow.setToX(1.08);
        grow.setToY(1.08);
        ScaleTransition back = new ScaleTransition(Duration.millis(140), node);
        back.setToX(1.0);
        back.setToY(1.0);
        new SequentialTransition(grow, back).play();
    }

    private HBox createWindowTitle(String text, double width, double height) {
        HBox bar = new HBox(10);
        bar.getStyleClass().add("window-title");
        bar.setPrefSize(width, height);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 16, 0, 16));

        Label dots = new Label("● ● ●");
        dots.getStyleClass().add("retro-label");
        dots.setTextFill(Color.web("#9edee4"));
        dots.setFont(Font.font("Consolas", FontWeight.BOLD, 16));

        Label title = new Label(text);
        title.getStyleClass().add("title-text");
        title.setMaxWidth(width - 120);
        title.setWrapText(false);

        bar.getChildren().addAll(dots, title);
        return bar;
    }

    private HBox inputWithUnit(TextField field, String unit, double width) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(width);

        field.setPrefSize(170, 60);

        Label arrows = new Label("⌃\n⌄");
        arrows.getStyleClass().add("retro-label");
        arrows.setFont(Font.font("Consolas", FontWeight.BOLD, 19));
        arrows.setTranslateX(-42);
        arrows.setMouseTransparent(true);

        Label unitLabel = new Label(unit);
        unitLabel.getStyleClass().add("retro-label");
        unitLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        unitLabel.setTranslateX(-30);

        Pane fieldWrapper = new Pane(field, arrows);
        fieldWrapper.setPrefSize(170, 60);
        arrows.setLayoutX(143);
        arrows.setLayoutY(8);

        box.getChildren().addAll(fieldWrapper, unitLabel);
        return box;
    }

    private HBox legendItem(String symbol, String text) {
        HBox box = new HBox(9);
        box.setAlignment(Pos.CENTER);

        StackPane circle = new StackPane();
        circle.setPrefSize(30, 30);
        Circle c = new Circle(15, Color.web("#9fddea"));
        c.setStroke(Color.web("#111111"));
        c.setStrokeWidth(2);
        Label s = new Label(symbol);
        s.getStyleClass().add("retro-label");
        s.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        circle.getChildren().addAll(c, s);

        Label l = new Label(text);
        l.getStyleClass().add("retro-label");
        l.setFont(Font.font("Consolas", FontWeight.NORMAL, 15));
        box.getChildren().addAll(circle, l);
        return box;
    }

    private Label label(String text, double x, double y, double size, boolean bold) {
        Label label = new Label(text);
        label.getStyleClass().add("retro-label");
        label.setFont(Font.font("Consolas", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        label.setLayoutX(x);
        label.setLayoutY(y);
        return label;
    }

    private Line dashedLine(double x, double y, double width) {
        Line line = new Line(x, y, x + width, y);
        line.setStroke(Color.web("#8d8a7d"));
        line.setStrokeWidth(1.5);
        line.getStrokeDashArray().addAll(8.0, 6.0);
        return line;
    }

    private ImageView imageView(String path, double fitWidth, double fitHeight) {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Asset não encontrado: " + path);
        }
        ImageView view = new ImageView(new Image(stream));
        view.setPreserveRatio(true);
        if (fitWidth > 0) view.setFitWidth(fitWidth);
        if (fitHeight > 0) view.setFitHeight(fitHeight);
        return view;
    }

    private void calculate() {
        try {
            double qPc = parsePositive(qField.getText(), "A carga q");
            double aCm = parsePositive(aField.getText(), "O lado a");

            // Trabalho para montar o sistema: quatro lados negativos e duas diagonais positivas.
            double k = 8.99e9;
            double qC = qPc * 1e-12;
            double aM = aCm / 100.0;
            double trabalho = (k * qC * qC / aM) * (Math.sqrt(2) - 4);
            double geometricFactor = Math.sqrt(2) - 4;

            String resultado = String.format(Locale.US, "%.3e J", trabalho);

            qConversionLabel.setText("q = " + comma(qPc) + " pC = " + sciMantissa(qC) + " × 10" + superscriptExponent(qC) + " C");
            aConversionLabel.setText("a = " + comma(aCm) + " cm = " + sciMantissa(aM) + " × 10" + superscriptExponent(aM) + " m");

            substitutionLine1.setText("W = (k q² / a)(√2 - 4)");
            substitutionLine2.setText("W = (8,99 × 10⁹ × (" + sciMantissa(qC) + " × 10" + superscriptExponent(qC) + ")² / (" + sciMantissa(aM) + " × 10" + superscriptExponent(aM) + ")) × " + factorFormat.format(geometricFactor));

            resultLabel.setText(resultado);
            statusLabel.setText("Tudo certo!");
        } catch (IllegalArgumentException ex) {
            statusLabel.setText("Verifique os valores");
            resultLabel.setText("Erro");
            qConversionLabel.setText("q em pC precisa ser maior que zero.");
            aConversionLabel.setText("a em cm precisa ser maior que zero.");
            substitutionLine1.setText(ex.getMessage());
            substitutionLine2.setText("");
        }
    }

    private void clearFields() {
        qField.clear();
        aField.clear();
        qConversionLabel.setText("q = -- pC = -- C");
        aConversionLabel.setText("a = -- cm = -- m");
        substitutionLine1.setText("");
        substitutionLine2.setText("");
        resultLabel.setText("");
        statusLabel.setText("Pronto para calcular");
    }

    private double parsePositive(String text, String fieldName) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(fieldName + " é obrigatório.");
        }

        String clean = text.trim().replace(" ", "");
        if (clean.contains(",") && clean.contains(".")) {
            clean = clean.replace(".", "").replace(',', '.');
        } else {
            clean = clean.replace(',', '.');
        }

        try {
            double value = Double.parseDouble(clean);
            if (!Double.isFinite(value) || value <= 0) {
                throw new IllegalArgumentException(fieldName + " deve ser maior que zero.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " deve ser um número válido.");
        }
    }

    private String comma(double value) {
        return inputFormat.format(value);
    }

    private String sciMantissa(double value) {
        if (value == 0) return "0,00";
        String raw = scientificFormat.format(value);
        String mantissa = raw.substring(0, raw.indexOf('E'));
        return mantissa.replace('.', ',');
    }

    private int exponent(double value) {
        if (value == 0) return 0;
        String raw = scientificFormat.format(value);
        return Integer.parseInt(raw.substring(raw.indexOf('E') + 1));
    }

    private String superscriptExponent(double value) {
        return toSuperscript(exponent(value));
    }

    private String formatWork(double value) {
        String raw = scientificFormat.format(value);
        String mantissa = raw.substring(0, raw.indexOf('E')).replace('.', ',');
        int exp = Integer.parseInt(raw.substring(raw.indexOf('E') + 1));
        return mantissa + " × 10" + toSuperscript(exp) + " J";
    }

    private String toSuperscript(int number) {
        String s = String.valueOf(number);
        StringBuilder out = new StringBuilder();
        for (char ch : s.toCharArray()) {
            out.append(switch (ch) {
                case '-' -> '⁻';
                case '0' -> '⁰';
                case '1' -> '¹';
                case '2' -> '²';
                case '3' -> '³';
                case '4' -> '⁴';
                case '5' -> '⁵';
                case '6' -> '⁶';
                case '7' -> '⁷';
                case '8' -> '⁸';
                case '9' -> '⁹';
                default -> ch;
            });
        }
        return out.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }

    static class AnimatedRetroBackground extends Pane {
        private final Canvas canvas;
        private final double width;
        private final double height;
        private double waveShiftTop;
        private double waveShiftBottom;

        AnimatedRetroBackground(double width, double height) {
            this.width = width;
            this.height = height;
            this.canvas = new Canvas(width, height);

            setPrefSize(width, height);
            getChildren().add(canvas);
            draw();
            startAnimation();
        }

        private void startAnimation() {
            AnimationTimer timer = new AnimationTimer() {
                private long lastTime = -1;

                @Override
                public void handle(long now) {
                    if (lastTime < 0) {
                        lastTime = now;
                        return;
                    }

                    double dt = (now - lastTime) / 1_000_000_000.0;
                    lastTime = now;

                    waveShiftTop = wrapShift(waveShiftTop + (55 * dt));
                    waveShiftBottom = wrapShift(waveShiftBottom - (32 * dt));
                    draw();
                }
            };
            timer.start();
        }

        private double wrapShift(double value) {
            double wrapped = value % WAVE_PERIOD;
            return wrapped < 0 ? wrapped + WAVE_PERIOD : wrapped;
        }

        private void draw() {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, width, height);

            gc.setFill(Color.web("#f7eedc"));
            gc.fillRect(0, 0, width, height);

            drawWaveBand(gc, waveShiftTop, 185, 58, 0.0, Color.web("#aee6e7"));
            drawWaveBand(gc, waveShiftBottom, 470, 82, Math.PI / 3, Color.web("#8fd6dc"));
            drawWaveBand(gc, waveShiftTop * 0.72, 690, 54, Math.PI / 2, Color.web("#d8f2ef"));

            gc.setFill(Color.rgb(255, 255, 255, 0.20));
            gc.fillRect(0, 0, width, height);

            gc.setStroke(Color.web("#111111"));
            gc.setLineWidth(4);
            gc.strokeRoundRect(10, 10, width - 20, height - 20, 18, 18);
        }

        private void drawWaveBand(GraphicsContext gc, double shift, double baseY, double amplitude, double phaseOffset, Color color) {
            gc.setFill(color);
            gc.beginPath();
            gc.moveTo(0, height);
            gc.lineTo(0, waveY(0, shift, baseY, amplitude, phaseOffset));
            for (double x = 0; x <= width; x += 8) {
                gc.lineTo(x, waveY(x, shift, baseY, amplitude, phaseOffset));
            }
            gc.lineTo(width, height);
            gc.closePath();
            gc.fill();
        }

        private double waveY(double x, double shift, double baseY, double amplitude, double phaseOffset) {
            double phase = ((x + shift) / WAVE_PERIOD) * Math.PI * 2 + phaseOffset;
            return baseY + Math.sin(phase) * amplitude + Math.sin(phase * 0.5) * amplitude * 0.35;
        }
    }

    static class DiagramPane extends Pane {
        private final double w;
        private final double h;

        DiagramPane(double w, double h) {
            this.w = w;
            this.h = h;
            setPrefSize(w, h);
            draw();
        }

        private void draw() {
            double left = 80;
            double right = w - 80;
            double top = 50;
            double bottom = h - 55;
            double centerX = w / 2;
            double centerY = (top + bottom) / 2;

            Line topLine = solidLine(left, top, right, top);
            Line leftLine = solidLine(left, top, left, bottom);
            Line rightLine = solidLine(right, top, right, bottom);
            Line bottomLine = solidLine(left, bottom, right, bottom);

            Line d1 = dashedLine(left, top, right, bottom);
            Line d2 = dashedLine(right, top, left, bottom);

            getChildren().addAll(topLine, leftLine, rightLine, bottomLine, d1, d2);

            addCharge(left, top, "+", "+q", -25, -45);
            addCharge(right, top, "−", "−q", -12, -45);
            addCharge(left, bottom, "−", "−q", -25, 20);
            addCharge(right, bottom, "+", "+q", -15, 20);

            Circle center = new Circle(centerX, centerY, 8, Color.web("#72cce0"));
            center.setStroke(Color.web("#111111"));
            center.setStrokeWidth(1.5);
            getChildren().add(center);

            Label o = text("O", centerX - 10, centerY + 12, 26, true);
            Label aTop = text("a", centerX - 8, top - 50, 26, true);
            Label aRight = text("a", right + 26, centerY - 8, 26, true);
            getChildren().addAll(o, aTop, aRight);
        }

        private Line solidLine(double x1, double y1, double x2, double y2) {
            Line line = new Line(x1, y1, x2, y2);
            line.setStroke(Color.web("#111111"));
            line.setStrokeWidth(2.2);
            return line;
        }

        private Line dashedLine(double x1, double y1, double x2, double y2) {
            Line line = solidLine(x1, y1, x2, y2);
            line.getStrokeDashArray().addAll(10.0, 8.0);
            return line;
        }

        private void addCharge(double x, double y, String symbol, String label, double labelDx, double labelDy) {
            Circle c = new Circle(x, y, 20, Color.web("#9edee4"));
            c.setStroke(Color.web("#111111"));
            c.setStrokeWidth(2.5);

            Label sign = text(symbol, x - 8, y - 13, 27, true);
            Label name = text(label, x + labelDx, y + labelDy, 25, true);
            getChildren().addAll(c, sign, name);
        }

        private Label text(String text, double x, double y, double size, boolean serif) {
            Label label = new Label(text);
            label.setFont(Font.font(serif ? "Georgia" : "Consolas", FontWeight.BOLD, size));
            label.setTextFill(Color.web("#111111"));
            label.setLayoutX(x);
            label.setLayoutY(y);
            return label;
        }
    }
}
