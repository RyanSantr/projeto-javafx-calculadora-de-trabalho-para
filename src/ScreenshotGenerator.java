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

    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();
        mainView.calculateWithValues("2,30", "64");

        Scene scene = new Scene(mainView, 1180, 720);
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Calculadora de Trabalho - Cargas Elétricas");
        stage.setScene(scene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.millis(700));
        delay.setOnFinished(event -> saveSnapshot(scene));
        delay.play();
    }

    private void saveSnapshot(Scene scene) {
        try {
            WritableImage image = scene.snapshot(null);
            File output = new File("screenshots/programa-calculadora-cargas.png");
            output.getParentFile().mkdirs();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
