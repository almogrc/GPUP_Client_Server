package engine.dto;

public class DtoTargetExeTableData {
    private String name;
    private String status;
    private String finishedStatus;

    public DtoTargetExeTableData(){}

    public DtoTargetExeTableData(String name, String status, String finishedStatus) {
        this.name=name;
        this.status=status;
        this.finishedStatus=finishedStatus;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getFinishStatus() {
        return finishedStatus;
    }
}
