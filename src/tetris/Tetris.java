/**
 *
 * @author Mauricio Sosa Giri (free4GOD)
 */
package tetris;

import javafx.application.Application;
import javafx.stage.Stage;
import tetris.view.MainWindow;

public class Tetris extends Application {

    @Override
    public void start(Stage primaryStage) {
        new Tetris().run(primaryStage);
    }

    public void run(Stage primaryStage) {
        MainWindow window = new MainWindow(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
