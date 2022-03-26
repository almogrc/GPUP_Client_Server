package engine.graph;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.*;

public class Target implements Serializable {


    public enum Location{LEAF,MIDDLE,ROOT,INDEPENDENT}
    public enum Finish{SUCCESS,FAILURE, SUCCESS_WITH_WARNINGS,NOTFINISHED}
    public enum TargetStatus{FROZEN ,SKIPPED ,WAITING ,INPROCESS ,FINISHED}

    private String name;
    private String data;
    private Date startTimeOfWiting;
    private Date startTimeOfInProcess;

    private TargetStatus targetStatus;
    private Finish finishStatus;


    private Location location;
    private List<Target>  dependsOn=new ArrayList<Target>();
    private List<Target> requiredFor=new ArrayList<Target>();
    private List<SerialSet> listOfSets;

    //private SimpleStringProperty finishStatusProperty;
    //private SimpleStringProperty targetStatusProperty;
    public Target(String name, String data, Location type){
        this.name=name;
        this.data=data;
        location = type;
        listOfSets=null;
        setFinishStatus(Finish.NOTFINISHED);
        setTargetStatus(TargetStatus.FROZEN);
    }

    public Target(Target another) {
        this.setName(another.getName());
        this.setData(another.getData());
        this.setTargetStatus(another.getTargetStatus());
        this.setFinishStatus(another.getFinishStatus());
        this.setLocation(another.getLocation());
        this.dependsOn=new ArrayList<Target>();
        this.requiredFor=new ArrayList<Target>();
        listOfSets=new ArrayList<SerialSet>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getStartTimeOfWiting() {
        return startTimeOfWiting;
    }

    public void setStartTimeOfWiting(Date startTimeOfWiting) {
        this.startTimeOfWiting = startTimeOfWiting;
    }

    public Date getStartTimeOfInProcess() {
        return startTimeOfInProcess;
    }

    public void setStartTimeOfInProcess(Date startTimeOfInProcess) {
        this.startTimeOfInProcess = startTimeOfInProcess;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Target> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<Target> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public List<Target> getRequiredFor() {
        return requiredFor;
    }

    public void setRequiredFor(List<Target> requiredFor) {
        this.requiredFor = requiredFor;
    }

    public void setListOfSets(List<SerialSet> listOfSets) {
        this.listOfSets = listOfSets;
    }

    public void addSerialSets(List<SerialSet> listOfSets) {
        this.listOfSets=listOfSets;
    }

    public String getName(){ return name;  }

    public List<Target>getDependingOn(){
        return dependsOn;
    }

    public List<Target>getRequiredTo(){
        return requiredFor;
    }

    public TargetStatus getTargetStatus(){
        return targetStatus;
    }

    public Finish getFinishStatus(){
        return finishStatus;
    }


    public boolean addDependingOn(Target newTarget){
        if(newTarget!=null){
            dependsOn.add(newTarget);
            return true;
        }else {
            ///trow try add null target
            return false;
        }
    }

    public boolean addRequiredFor(Target newTarget){
        if(newTarget!=null){
            requiredFor.add(newTarget);
            return true;
        }else {
            ///trow try add null target
            return false;
        }
    }

    public Location getLocation(){return location;}

    public String getData() {return data;}

    public List<String> getRequiredToTargetsName() {
        List<String>res=new ArrayList<String>();
        for(Target target : requiredFor){
            res.add(target.getName());
        }
        return res;
    }

    public List<String> getDependingOnTargetsName() {
        List<String>res=new ArrayList<String>();
        for(Target target : dependsOn){
            res.add(target.getName());
        }
        return res;
    }



    @Override
    public String toString(){
        return name;
    }

    public void setFinishStatus(Finish outcome) {
        this.finishStatus=outcome;
    }

    public void setTargetStatus(TargetStatus status) {
        this.targetStatus=status;
        switch(status) {
            case WAITING:{
                if(startTimeOfWiting==null){
                    startTimeOfWiting=new Date();
                }
            }
                break;
            case INPROCESS:
                if(startTimeOfInProcess==null){
                    startTimeOfInProcess=new Date();
                }
                break;
            default:
                // code block
        }
    }

    public int getNumOfDirectDependsOn(){return dependsOn.size();}

    public int getNumOfDirectRequiredFor(){return requiredFor.size();}

    public int getNumOfIndirectDependsOn(){
        return getAllOfIndirectDependsOn().size();
    }

    public Set<Target> getAllOfIndirectDependsOn(){
        Set<Target> canGetThem=new HashSet<Target>();
        Set<Target> temp=new HashSet<Target>();

        canGetThem.add(this);
        int size;

        do{
            size=canGetThem.size();
            for(Target curr: canGetThem){
                temp.addAll(curr.dependsOn);
            }
            canGetThem.addAll(temp);
            temp.clear();
        }while (size!=canGetThem.size());
        canGetThem.remove(this);
        return canGetThem;
    }

    public int getNumOfIndirectRequiredFor(){
        return getAllOfIndirectRequiredFor().size();
    }

    public Set<Target> getAllOfIndirectRequiredFor(){
        Set<Target> canGetThem=new HashSet<Target>();
        Set<Target> temp=new HashSet<Target>();

        canGetThem.add(this);
        int size;

        do{
            size=canGetThem.size();
            for(Target curr: canGetThem){
                temp.addAll(curr.requiredFor);
            }
            canGetThem.addAll(temp);
            temp.clear();
        }while (size!=canGetThem.size());
        canGetThem.remove(this);
        return canGetThem;
    }

    public int getSerialSetsNumber(){return listOfSets.size();}

    public List<String> getListOfNamesSets(){
        List<String> res = new ArrayList<String>();
        for(SerialSet serialSet:listOfSets) {
            res.add(serialSet.getName());
        }
        return res;
    }

    public List<SerialSet> getListOfSets(){
        return listOfSets;
    }

    public String howMSinProcess(){
        Date d=new Date();
        return String.valueOf((d.getTime()-startTimeOfInProcess.getTime()));
    }

    public String howMSWaiting(){
        Date d=new Date();
        return String.valueOf((d.getTime()-startTimeOfWiting.getTime()));
    }

    public String FrozenBecauseThisTargets(List <String> chosenTargets){
        List<String>res=new ArrayList<String>();
        for(Target iNeedHim:dependsOn){
            if(chosenTargets.contains( iNeedHim.getName())) {
                if (iNeedHim.getTargetStatus().equals(TargetStatus.WAITING) || iNeedHim.getTargetStatus().equals(TargetStatus.FROZEN) || iNeedHim.getTargetStatus().equals(TargetStatus.INPROCESS)) {
                    res.add(iNeedHim.getName());
                }
            }
        }
        return res.toString();
    }

    public String skippedBecauseThisTargets(List <String> chosenTargets){
        List<String>res=new ArrayList<String>();
        for(Target iNeedHim:dependsOn){
            if(chosenTargets.contains( iNeedHim.getName())) {
                if (iNeedHim.getTargetStatus().equals(TargetStatus.SKIPPED) || iNeedHim.getFinishStatus().equals(Finish.FAILURE)) {
                    res.add(iNeedHim.getName());
                }
            }
        }
        return res.toString();
    }
}
