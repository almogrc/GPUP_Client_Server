package engine.dto;

import engine.graph.Target;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DTOTaskOnTarget implements Dto{
   // שם טרגט
   // זמן ריצה
   // DATA
   // FINISH STATUS
   // List string
   // List string skipped


    private String targetName="";
    private String data="";
    private int processingTime=0;
    private String finish="NOTFINISHED";
    private String status="FROZEN";
    private List<String> newOpenedTargets=null;
    private String newSkippedTargets="";
    private String startingTime;
    private String endingTime;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public void setNewOpenedTargets(List<String> newOpenedTargets) {
        this.newOpenedTargets = newOpenedTargets;
    }

    public void setNewSkippedTargets(String newSkippedTargets) {
        this.newSkippedTargets = newSkippedTargets;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public DTOTaskOnTarget(String name, String data, String status) {
        targetName=name;
        this.data=data;
        newOpenedTargets=new ArrayList<String>();
        newSkippedTargets="";
        //this.status= status;
    }
    public DTOTaskOnTarget(){

    }
    public String getName(){return this.targetName;}

    public String getData(){return this.data;}

    public void setProcessingTime(int processingTime) {this.processingTime=processingTime; }

    public int getProcessingTime(){return this.processingTime;}

    public void setStatus(String status){this.status = status; }

    public String getStatus(){return status; }

    public void setFinishStatus(String finishStatus) {this.finish=finishStatus; }

    public String getFinish(){return finish;}

    public void setTargetsSkipped(List<String> skippedList) {
        if(skippedList!=null) {
            newSkippedTargets = skippedList.toString();
        }
    }

    public void addOpenedTarget(List<String> allOpen) {
        newOpenedTargets=allOpen;
    }

    public void setStartingTime(String startingTime) { this.startingTime=startingTime;}

    public void setEndingTime(String endingTime) {this.endingTime=endingTime;}


    @Override
    public String toString(){
        String s = "***********************************************************\n" +
                didFinishOrNot() +
                "Target's data : " + data + "\n" +
                "Task outcome on target : " + printFinish() + "\n" +
                getNewOpenedTargets() +
                getNewSkippedTargets();
        return s;

    }

    public String getNewSkippedTargets() {
        if(!finish.equals(Target.Finish.FAILURE)){
            return "";
        }
        if(newSkippedTargets.isEmpty()){
            return "There are no skipped targets duo to "+targetName+"\n";
        }else{
            return "The targets that were skipped are: "+newSkippedTargets+"\n";
        }
    }

    private String printFinish() {
        if(finish.equals(Target.Finish.NOTFINISHED)){
            return status.toString() + isFrozen();
        }else{
            return finish.toString();
        }
    }

    public String getNewOpenedTargets() {
        if(this.newOpenedTargets==null){
            return "";
        }
        if(!(finish.equals(Target.Finish.SUCCESS)||finish.equals(Target.Finish.SUCCESS_WITH_WARNINGS))){
            return "";
        }
        if(newOpenedTargets.size()==0) {
            return targetName + " didn't open any other target."+"\n";
        }else{
            return "The targets that are open now: "+ newOpenedTargets.toString()+"\n";
        }
    }

    private String isFrozen(){
        if(status.equals(Target.TargetStatus.FROZEN)){
            return " - Target detected on circle!";
        }
        return "";
    }

    private String didFinishOrNot(){
        if(!finish.equals(Target.Finish.NOTFINISHED)){
            return "Processing "+targetName+" :"+"\n" +"Start processing time : "+startingTime+"\n"+
                    "- target "+targetName+" in sleeping mode -\n"+
                    "- target "+targetName+" woke up -\n"+
                    "End of processing time :"+endingTime+"\n"+
                    "Total processed for : "+LocalTime.ofSecondOfDay(processingTime/1000).toString()+"\n";
        }
        return "Failed to process "+targetName+"\n";
    }
}
