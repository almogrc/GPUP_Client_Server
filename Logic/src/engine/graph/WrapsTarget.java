package engine.graph;

import engine.dto.DTOTarget;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;
import java.util.List;

public class WrapsTarget {

    private CheckBox checkbox;
    private DTOTarget dtoTarget;
    private String workerNameOnTarget = "";


    public WrapsTarget(DTOTarget dtoTarget) {
        this.dtoTarget=dtoTarget;

    }

    public String getWorkerNameOnTarget() {
        return workerNameOnTarget;
    }

    public void setWorkerNameOnTarget(String workerNameOnTarget) {
        this.workerNameOnTarget = workerNameOnTarget;
    }

    public WrapsTarget(WrapsTarget other){
        this.dtoTarget=new DTOTarget(other.getDTOTarget());
    }
    public String getName(){return dtoTarget.getName();}

    public String getData(){return dtoTarget.getData();}

    public String getFinishStatus(){return dtoTarget.getFinishStatus();}

    public String getTargetStatus(){return dtoTarget.getTargetStatus();}

    public CheckBox getCheckbox(){//todo make checkBox

        if(checkbox==null){
            checkbox = new CheckBox();
            checkbox.setSelected(false);
        }
        return checkbox;
    }

//public List<String> getListOfNamesSets(){return target.getListOfNamesSets();}

    public int getNumOfDirectRequiredFor(){return dtoTarget.getNumOfDirectRequiredFor();}

    public int getNumOfIndirectRequiredFor(){return dtoTarget.getNumOfIndirectRequiredFor();}

    public int getNumOfDirectDependsOn(){return dtoTarget.getNumOfDirectDependsOn();}

    public int getNumOfIndirectDependsOn(){return dtoTarget.getNumOfIndirectDependsOn();}

    //public int getSerialSetsNumber(){return target.getSerialSetsNumber();}


    public String getLocation(){
        return dtoTarget.getLocation();
    }

    public void setCheckbox(Boolean flag){
        if(checkbox==null){
            checkbox = new CheckBox();
        }
        this.checkbox.setSelected(flag);
    }

    public DTOTarget getDTOTarget() {return this.dtoTarget; }

    public void setStatus(String status) {dtoTarget.setTargetStatus(status);}//TODO delete

    public void setFinishStatus(String finishStatus){dtoTarget.setFinishStatus(finishStatus);}

}
