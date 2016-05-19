package FX;/**
 * Created by Андрей on 18.05.2016.
 */

import filekeeper.FileKeeper;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Start JavaFX8 version." + FileKeeper.version);
    }
}
