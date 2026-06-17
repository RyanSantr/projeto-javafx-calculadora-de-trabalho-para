package view;

import java.text.DecimalFormat;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.PhysicsCalculator;

public class WorkGraphPane extends VBox {

    private final PhysicsCalculator calculator = new PhysicsCalculator();
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    private final Label caption = new Label("Calcule para atualizar a curva W x q.");

    public WorkGraphPane() {
        getStyleClass().add("graph-pane");
        setPadding(new Insets(8, 0, 0, 0));

        xAxis.setLabel("Carga q (pC)");
        yAxis.setLabel("Trabalho W (J)");
        yAxis.setTickLabelFormatter(new ScientificAxisFormatter());
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);
        chart.setAnimated(false);
        chart.setMinHeight(315);
        chart.setPrefHeight(330);

        caption.getStyleClass().add("visual-caption");
        caption.setWrapText(true);
        caption.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(chart, caption);
        VBox.setVgrow(chart, Priority.ALWAYS);
    }

    public void updateGraph(double selectedChargePc, double sideCm) {
        chart.getData().clear();

        double maxChargePc = Math.max(5.0, selectedChargePc * 2.0);
        double sideMeters = calculator.cmToMeter(sideCm);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i <= 40; i++) {
            double chargePc = maxChargePc * i / 40.0;
            double chargeC = calculator.picoToCoulomb(chargePc);
            double work = calculator.calculateWork(chargeC, sideMeters);
            series.getData().add(new XYChart.Data<>(chargePc, work));
        }

        chart.getData().add(series);
        caption.setText("Curva para a = " + formatDecimal(sideCm) + " cm. O trabalho fica mais negativo conforme q aumenta.");
    }

    public void clearGraph() {
        chart.getData().clear();
        caption.setText("Calcule para atualizar a curva W x q.");
    }

    private String formatDecimal(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.2f", value);
    }

    private static class ScientificAxisFormatter extends StringConverter<Number> {

        private final DecimalFormat format = new DecimalFormat("0.0E0");

        @Override
        public String toString(Number value) {
            double number = value.doubleValue();
            if (Math.abs(number) < 1E-30) {
                return "0";
            }
            return format.format(number).replace("E", " x10^");
        }

        @Override
        public Number fromString(String value) {
            return 0;
        }
    }
}
