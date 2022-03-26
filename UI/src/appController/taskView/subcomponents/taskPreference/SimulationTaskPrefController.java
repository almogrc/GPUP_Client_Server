package appController.taskView.subcomponents.taskPreference;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class SimulationTaskPrefController {

    @FXML private GridPane SimulationPrefGridPane;
    @FXML private TextField ProcessTimeTextFiled;
    @FXML private CheckBox RandomCheckBox;
    @FXML private TextField ProbSuccessTextFiled;
    @FXML private TextField ProbSuccessWWarningsTextFiled;

    @FXML
    public void initialize(){

    }

    public GridPane getRoot(){
        return SimulationPrefGridPane;
    }
    public String getProcessTime() {
        return ProcessTimeTextFiled.getText();
    }
    public boolean getIsRandom(){
        return RandomCheckBox.isSelected();
    }
    public String getProbSuccessTextFiled(){
        return ProbSuccessTextFiled.getText();
    }
    public String getProbSuccessWWarningsTextFiled(){
        return ProbSuccessWWarningsTextFiled.getText();
    }

    public void resetMe(){
        ProcessTimeTextFiled.clear();
        ProbSuccessTextFiled.clear();
        ProbSuccessWWarningsTextFiled.clear();
        RandomCheckBox.setSelected(false);

    }


}
