package engine.dto;

public class DtoTargetForWorker {
    private String targetName;
    private String exeName;
    private String kindOfTask;
    private String targetStatus;
    private int creditsReceived;

    public DtoTargetForWorker(String targetName,String exeName,String kindOfTask,String targetStatus,int creditsReceived){
        this.targetName = targetName;
        this.exeName =exeName;
        this.kindOfTask = kindOfTask;
        this.targetStatus = targetStatus;
        this.creditsReceived = creditsReceived;
    }
    public DtoTargetForWorker(){

    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getExeName() {
        return exeName;
    }

    public void setExeName(String exeName) {
        this.exeName = exeName;
    }

    public String getKindOfTask() {
        return kindOfTask;
    }

    public void setKindOfTask(String kindOfTask) {
        this.kindOfTask = kindOfTask;
    }

    public String getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }

    public int getCreditsReceived() {
        return creditsReceived;
    }

    public void setCreditsReceived(int creditsReceived) {
        this.creditsReceived = creditsReceived;
    }
}
