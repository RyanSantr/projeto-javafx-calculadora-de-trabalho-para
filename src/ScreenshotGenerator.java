import java.io.File;
import javax.imageio.ImageIO;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;
import view.MainView;

public class ScreenshotGenerator extends Application {

    private MainView mainView;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        mainView = new MainView();
        mainView.calculateWithValues("2,30", "64");

        scene = new Scene(mainView, 1180, 720);
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Calculadora de Trabalho - Cargas Elétricas");
        stage.setScene(scene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.millis(700));
        delay.setOnFinished(event -> saveAllSnapshots());
        delay.play();
    }

    private void saveAllSnapshots() {
        try {
            mainView.selectVisualTab(0);
            saveSnapshot("screenshots/programa-calculadora-cargas.png");

            mainView.selectVisualTab(1);
            saveSnapshot("screenshots/programa-calculadora-cargas-3d.png");

            mainView.selectVisualTab(2);
            saveSnapshot("screenshots/programa-calculadora-cargas-grafico.png");
        } finally {
            System.exit(0);
        }
    }

    private void saveSnapshot(String path) {
        try {
            mainView.applyCss();
            mainView.layout();
            WritableImage image = scene.snapshot(null);
            File output = new File(path);
            output.getParentFile().mkdirs();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
