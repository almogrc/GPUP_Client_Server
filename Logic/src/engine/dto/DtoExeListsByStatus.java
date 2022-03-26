package engine.dto;

import java.util.ArrayList;
import java.util.List;

public class DtoExeListsByStatus {
    private List<DtoTargetExeTableData> frozenList;
    private List<DtoTargetExeTableData> waitingList;
    private List<DtoTargetExeTableData> inProcessList;
    private List<DtoTargetExeTableData> finishedList;
    private List<DtoTargetExeTableData> skippedList;

    public DtoExeListsByStatus(){
        frozenList=new ArrayList<DtoTargetExeTableData>();
        waitingList=new ArrayList<DtoTargetExeTableData>();
        inProcessList=new ArrayList<DtoTargetExeTableData>();
        finishedList=new ArrayList<DtoTargetExeTableData>();
        skippedList=new ArrayList<DtoTargetExeTableData>();
    }

    public DtoExeListsByStatus(List<DtoTargetExeTableData> frozenList, List<DtoTargetExeTableData> waitingList, List<DtoTargetExeTableData> inProcessList, List<DtoTargetExeTableData> finishedList, List<DtoTargetExeTableData> skippedList)
    {
        this.frozenList=frozenList;
        this.waitingList=waitingList;
        this.inProcessList=inProcessList;
        this.finishedList=finishedList;
        this.skippedList=skippedList;
    }


    public List<DtoTargetExeTableData> getFrozenList() {
        return frozenList;
    }

    public List<DtoTargetExeTableData> getWaitingList() {
        return waitingList;
    }

    public List<DtoTargetExeTableData> getInProcessList() {
        return inProcessList;
    }

    public List<DtoTargetExeTableData> getFinishedList() {
        return finishedList;
    }


    public List<DtoTargetExeTableData> getSkippedList() {
        return skippedList;
    }
}
