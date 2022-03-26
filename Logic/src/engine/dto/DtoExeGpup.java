package engine.dto;

import engine.engineGpup.DetailsForTask;
import engine.engineGpup.GpupExecution;

import java.util.List;

public class DtoExeGpup {
    private String taskName;



    private String graphName;
    private String createBy;
    private String taskType;
    private String isSignedFor;
    private String taskStatus;



    private String isPaused;
    private int targetPrice;
    private int progressByPercentage;
    private int amountOfTargetsDoneByWorker;
    private int creditsMadeByWorker;
    private int numOfWorkers;
    private int priceForExe;
    private int numOfTargets;
    private int numOfRoots;
    private int numOfIndependent;
    private int numOfLeafs;
    private int numOfMiddle;
    public DtoExeGpup(GpupExecution gpupExecution){
        DetailsForTask runDetails=gpupExecution.getRunDetails();
        taskName=gpupExecution.getTaskName();
        createBy=gpupExecution.getUserNameExeCreator();
        if(runDetails.isSimulation()){
            taskType="SIMULATION";
            targetPrice = gpupExecution.getSimulationPrice();
        }else{
            taskType="COMPILATION";
            targetPrice = gpupExecution.getCompilationPrice();
        }
        taskStatus=gpupExecution.getExeStatus();
        numOfWorkers=gpupExecution.getNumOfWorkers();
        priceForExe=gpupExecution.getPriceForExe();

        numOfTargets=gpupExecution.getNumOfTargets();
        numOfRoots=gpupExecution.getNumOfRoot();
        numOfIndependent=gpupExecution.getNumOfIndependent();
        numOfLeafs=gpupExecution.getNumOfLeaf();
        numOfMiddle=gpupExecution.getNumOfMiddle();
        graphName=gpupExecution.getGraphName();
        progressByPercentage=0;
        amountOfTargetsDoneByWorker=0;
        creditsMadeByWorker=0;
    }

    public int getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(int targetPrice) {
        this.targetPrice = targetPrice;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
    public String getIsPaused() {
        return isPaused;
    }

    public void setIsPaused(String isPaused) {
        this.isPaused = isPaused;
    }

    public void setIsSignedFor(String isSignedFor) {
        this.isSignedFor = isSignedFor;
    }

    public String getIsSignedFor() {
        return isSignedFor;
    }

    public DtoExeGpup() {
    }

    public void setAmountOfTargetsDoneByWorker(int amountOfTargetsDoneByWorker) {
        this.amountOfTargetsDoneByWorker = amountOfTargetsDoneByWorker;
    }

    public void setCreditsMadeByWorker(int creditsMadeByWorker) {
        this.creditsMadeByWorker = creditsMadeByWorker;
    }

    public void setProgressByPercentage(int progressByPercentage) {
        this.progressByPercentage = progressByPercentage;
    }

    public int getAmountOfTargetsDoneByWorker() {
        return amountOfTargetsDoneByWorker;
    }

    public int getCreditsMadeByWorker() {
        return creditsMadeByWorker;
    }

    public int getProgressByPercentage() {
        return progressByPercentage;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getNumOfWorkers() {
        return numOfWorkers;
    }

    public void setNumOfWorkers(int numOfWorkers) {
        this.numOfWorkers = numOfWorkers;
    }

    public int getPriceForExe() {
        return priceForExe;
    }

    public void setPriceForExe(int priceForExe) {
        this.priceForExe = priceForExe;
    }

    public int getNumOfTargets() {
        return numOfTargets;
    }

    public void setNumOfTargets(int numOfTargets) {
        this.numOfTargets = numOfTargets;
    }

    public int getNumOfRoots() {
        return numOfRoots;
    }

    public void setNumOfRoots(int numOfRoots) {
        this.numOfRoots = numOfRoots;
    }

    public int getNumOfIndependent() {
        return numOfIndependent;
    }

    public void setNumOfIndependent(int numOfIndependent) {
        this.numOfIndependent = numOfIndependent;
    }

    public int getNumOfLeafs() {
        return numOfLeafs;
    }

    public void setNumOfLeafs(int numOfLeafs) {
        this.numOfLeafs = numOfLeafs;
    }

    public int getNumOfMiddle() {
        return numOfMiddle;
    }

    public void setNumOfMiddle(int numOfMiddle) {
        this.numOfMiddle = numOfMiddle;
    }
}


