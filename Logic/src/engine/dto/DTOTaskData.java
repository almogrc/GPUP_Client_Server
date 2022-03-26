package engine.dto;

import engine.graph.Target;

import java.util.List;

public class DTOTaskData implements Dto{
   // שם טרגט
   // זמן ריצה
   // DATA
   // FINISH STATUS
   // List string
   // List string skkiped


    private String targetName;
    private double processingTime;
    private String data;
    private Target.Finish finish;
    private List<String> newOpenedTargets;
    private List<String> newSkippedTargets;


    public DTOTaskData(String name, String data) {
        targetName=name;
        this.data=data;
    }
}
