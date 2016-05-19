package FX;

import filekeeper.FileKeeper;
import filekeeper.Lib;
import filekeeper.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TableView;

import java.util.ArrayList;


/**
 * Created by Андрей on 19.05.2016.
 */
public class MainFrmCtrl {

    private Main mainClass;
    private ArrayList<Task> taskList = new ArrayList<>();

    @FXML
    private ToolBar toolBar;
    @FXML
    private TableView tableView;

    @FXML
    private void initialize() {
        Button runButton = new Button("Run");
        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");
        Button editButton = new Button("Edit");
        toolBar.getItems().addAll(runButton, addButton, removeButton, editButton);
    }

    public void setMainClass(Main mainClass) {
        this.mainClass = mainClass;
        if (FileKeeper.fileXmlTasks.toFile().exists()) {
            taskList = Lib.readXml(FileKeeper.fileXmlTasks);
        }
    }
}
