package FX;/**
 * Created by Андрей on 18.05.2016.
 */

import filekeeper.FileKeeper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main extends Application {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    //private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Start JavaFX8 version." + FileKeeper.version);
        //this.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getClassLoader().getResource("mainFrm.fxml"));
            Node node = loader.load();
            MainFrmCtrl controller = loader.getController();
            controller.setMainClass(this);
            primaryStage.setScene(new Scene((Parent)node));
            primaryStage.setTitle("File Keeper v" + FileKeeper.version);
            primaryStage.getIcons().addAll(new Image("images/file-keeper-32-icon.png"));
            primaryStage.show();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

    }
}
