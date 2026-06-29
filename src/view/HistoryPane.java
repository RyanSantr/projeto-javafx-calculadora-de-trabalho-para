package view;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HistoryPane extends VBox {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final VBox entries = new VBox(8);

    public HistoryPane() {
        getStyleClass().add("history-card");
        setSpacing(12);

        Label title = new Label("Histórico de simulações");
        title.getStyleClass().add("panel-title");

        Button clearButton = new Button("Limpar histórico");
        clearButton.getStyleClass().addAll("secondary-button", "compact-button");
        clearButton.setOnAction(event -> clearHistory());

        HBox header = new HBox(12, title, clearButton);
        header.setAlignment(Pos.CENTER_LEFT);

        entries.getChildren().add(emptyState());
        getChildren().addAll(header, entries);
    }

    public void addEntry(String chargePc, String sideCm, String workJoule) {
        if (entries.getChildren().size() == 1 && entries.getChildren().get(0).getStyleClass().contains("empty-history")) {
            entries.getChildren().clear();
        }

        Label entry = new Label(TIME_FORMAT.format(LocalTime.now())
                + "  •  q = " + chargePc + " pC"
                + "  •  a = " + sideCm + " cm"
                + "  •  W = " + workJoule + " J");
        entry.getStyleClass().add("history-entry");
        entry.setWrapText(true);
        entries.getChildren().add(0, entry);

        if (entries.getChildren().size() > 6) {
            entries.getChildren().remove(6, entries.getChildren().size());
        }

        Animations.slideIn(entry, -18, 0);
    }

    public void clearHistory() {
        entries.getChildren().setAll(emptyState());
    }

    private Label emptyState() {
        Label label = new Label("Nenhum cálculo registrado ainda.");
        label.getStyleClass().addAll("history-entry", "empty-history");
        return label;
    }
}
