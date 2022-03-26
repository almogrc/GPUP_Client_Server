package engine.dto;

import engine.graph.Target;

public class DTOTarget {
    private String name;
    private String data;
    private String location;
    private String executionName;

    private int numOfDirectRequiredFor;
    private int numOfIndirectRequiredFor;
    private int numOfDirectDependsOn;
    private int numOfIndirectDependsOn;
    private String finishStatus;
    private String targetStatus;

    public DTOTarget(Target target, String taskName){
        this.name=target.getName();
        this.data=target.getData();
        this.location=target.getLocation().toString();
        this.numOfDirectRequiredFor=target.getNumOfDirectRequiredFor();
        this.numOfIndirectRequiredFor=target.getNumOfIndirectRequiredFor();
        this.numOfDirectDependsOn=target.getNumOfDirectDependsOn();
        this.numOfIndirectDependsOn=target.getNumOfIndirectDependsOn();
        this.targetStatus=target.getTargetStatus().toString();
        this.finishStatus=target.getFinishStatus().toString();
        this.executionName = taskName;
    }

    public DTOTarget(){}

    public String getExecutionName() {
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public DTOTarget(DTOTarget other) {
        this.setName(other.getName());
        this.setData(other.getData());
        this.setLocation(other.getLocation());
        this.setNumOfDirectRequiredFor(other.getNumOfDirectRequiredFor());
        this.setNumOfIndirectDependsOn(other.getNumOfIndirectDependsOn());
        this.setFinishStatus(other.getFinishStatus());
        this.setTargetStatus(other.getTargetStatus());
    }

    public String getName(){
        return this.name;
    }
    public String getData(){
        return this.data;
    }
    public String getLocation(){
        return this.location;
    }
    public int getNumOfDirectRequiredFor(){
        return this.numOfDirectRequiredFor;
    }
    public int getNumOfIndirectRequiredFor(){
        return this.numOfIndirectRequiredFor;
    }
    public int getNumOfDirectDependsOn(){
        return this.numOfDirectDependsOn;
    }
    public int getNumOfIndirectDependsOn(){
        return this.numOfIndirectDependsOn;
    }


    public void setName(String name){
        this.name=name;
    }
    public void setData(String data){
        this.data=data;
    }
    public void setLocation(String location){
        this.location=location;
    }
    public void setNumOfDirectRequiredFor(int numOfDirectRequiredFor){
        this.numOfDirectRequiredFor=numOfDirectRequiredFor;
    }
    public void setNumOfIndirectRequiredFor(int numOfIndirectRequiredFor){
        this.numOfIndirectRequiredFor=numOfIndirectRequiredFor;
    }
    public void setNumOfDirectDependsOn(int numOfDirectDependsOn){
        this.numOfDirectDependsOn=numOfDirectDependsOn;
    }
    public void setNumOfIndirectDependsOn(int numOfIndirectDependsOn){
        this.numOfIndirectDependsOn=numOfIndirectDependsOn;
    }


    public String getFinishStatus() {
        return finishStatus;
    }
    public void setFinishStatus(String finishStatusProperty) {
        this.finishStatus=finishStatusProperty;
    }


    public void setTargetStatus(String targetStatus) {
        this.targetStatus=targetStatus;
    }

    public String getTargetStatus(){
        return targetStatus;
    }
}
