package engine.dto;

import engine.graph.WrapsTarget;

import java.util.ArrayList;
import java.util.List;

public class DtoNewTask {


    private DtoCompilationTask dtoCompilationTask;
    private DtoSimulationTask dtoSimulationTask;

    private String userNameCreator;
    private String taskName;
    private List<String> TargetListName;
    private String graphName;
    private boolean isSimulation;

    public DtoNewTask(){
        dtoCompilationTask=new DtoCompilationTask();
        dtoSimulationTask=new DtoSimulationTask();
        TargetListName=new ArrayList<String>();
    }

    public DtoNewTask(String taskName, List<String> TargetListName, String userNameCreator,DtoSimulationTask dtoSimulationTask) {
        this.taskName=taskName;
        this.TargetListName=TargetListName;
        this.dtoSimulationTask=dtoSimulationTask;
        this.isSimulation=true;
        this.userNameCreator=userNameCreator;
        this.dtoCompilationTask=new DtoCompilationTask();
    }

    public DtoNewTask(String taskName, List<String> TargetListName, String userName, DtoCompilationTask dtoCompilationTask) {
        this.taskName=taskName;
        this.TargetListName=TargetListName;
        this.userNameCreator=userName;
        this.dtoCompilationTask=dtoCompilationTask;
        this.isSimulation=false;
        this.dtoSimulationTask=new DtoSimulationTask();
    }

    public DtoCompilationTask getDtoCompilationTask() {
        return dtoCompilationTask;
    }

    public void setDtoCompilationTask(DtoCompilationTask dtoCompilationTask) {
        this.dtoCompilationTask = dtoCompilationTask;
    }

    public String getUserNameCreator() {
        return userNameCreator;
    }

    public void setUserNameCreator(String userNameCreator) {
        this.userNameCreator = userNameCreator;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getTargetListName() {
        return TargetListName;
    }

    public void setTargetListName(List<String> targetListName) {
        TargetListName = targetListName;
    }

    public DtoSimulationTask getDtoSimulationTask() {
        return dtoSimulationTask;
    }

    public void setDtoSimulationTask(DtoSimulationTask dtoSimulationTask) {
        this.dtoSimulationTask = dtoSimulationTask;
    }

    public boolean isSimulation() {
        return isSimulation;
    }

    public void setSimulation(boolean simulation) {
        isSimulation = simulation;
    }

    public void setGraphName(String graphName) {
        this.graphName=graphName;
    }
    public String getGraphName() {
        return graphName;
    }
}
