package task;

import component.workspace.WorkspaceController;
import engine.dto.DTOTarget;
import engine.dto.DTOTaskOnTarget;
import engine.graph.Target;
import engine.dto.Dto;
import javafx.beans.property.SimpleStringProperty;


public interface Task extends Runnable{

    public DTOTaskOnTarget getDTOTaskOnTarget();
    void setDtoTarget(DTOTarget dtoTarget);
    void setWorkerName(String userName);

    void setJobber(WorkspaceController workspaceController);


    //DTOTaskOnTarget run(Target currentTarget);
}
