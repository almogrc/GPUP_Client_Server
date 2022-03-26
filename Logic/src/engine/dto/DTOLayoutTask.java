package engine.dto;

import java.util.ArrayList;
import java.util.List;

public class DTOLayoutTask {
    private List<String> targetNames;
    private String executionStatus;


    public DTOLayoutTask() {
        this.targetNames=new ArrayList<String>();
    }

    public DTOLayoutTask(List<String> targetNames, String executionStatus) {
        this.targetNames=targetNames;
        this.executionStatus=executionStatus;
    }

    public List<String> getTargetNames() {
        return targetNames;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }

    public void setTargetNames(List<String> targetNames) {
        this.targetNames = targetNames;
    }
}
