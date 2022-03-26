package appController.taskView.subcomponents.taskPreference;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class CompileTaskPrefController {

    @FXML private GridPane compilationPrefGridPane;
    @FXML private Button LoadDirectoryToCompileButton;
    @FXML private Button LoadDirectoryForCompiledCodeButton;

    private File directoryToCompile;
    private File directoryForCompiled;

    @FXML void handleLoadDirectoryForCompiledCode(ActionEvent event) {
        DirectoryChooser directoryChooser =new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        directoryChooser.setTitle("Select Directory For Compiled Code");
        directoryForCompiled=selectedDirectory;
    }

    @FXML void handleLoadDirectoryToCompile(ActionEvent event) {
        DirectoryChooser directoryChooser =new DirectoryChooser();
        directoryChooser.setTitle("Select Directory Of Code To Compile");
        File selectedDirectory = directoryChooser.showDialog(null);
        directoryToCompile=selectedDirectory;

    }

    public GridPane getRoot(){
        return compilationPrefGridPane;
    }
    public File getDirectoryToCompile(){
        return directoryToCompile;
    }
    public File getDirectoryForCompiled(){
        return directoryForCompiled;
    }


    public void resetMe() {//TODO
        directoryToCompile=null;
        directoryForCompiled=null;
    }
    public void deactivateMe(){
        LoadDirectoryToCompileButton.setDisable(true);
        LoadDirectoryForCompiledCodeButton.setDisable(true);
    }
    public void activateMe(){
        LoadDirectoryToCompileButton.setDisable(false);
        LoadDirectoryForCompiledCodeButton.setDisable(false);
    }
}
