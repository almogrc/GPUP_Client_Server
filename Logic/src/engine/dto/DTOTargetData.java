package engine.dto;

import engine.graph.Target;

import java.util.List;

public class DTOTargetData implements Dto{
    private String targetName;
    private Target.Location location;
    private List<String>requiredFor;
    private List<String>dependsOn;
    private String data;


    public DTOTargetData(String targetName, Target.Location location, List<String>requiredFor, List<String>dependsOn,String data){
        this.targetName=targetName;
        this.location=location;
        this.requiredFor=requiredFor;
        this.dependsOn=dependsOn;
        this.data=data;
    }

    public String toString(){
        return  "***********************************************************************************\n" +
                "1. Target's name: "+targetName+
                "\n2. Target's location: "+location+
                "\n3. List of all the targets he depends on directly: "+getListValue(requiredFor)+
                "\n4. List of all the targets he required for directly: "+getListValue(dependsOn)+
                "\n5. Target's Data: "+ getValIfExist(data)+
                "\n***********************************************************************************";


    }

    private String getValIfExist(String val) {
        if(val==null){
            return "There is no value";
        }else{
            return val;
        }
    }

    private String getListValue(List<String> list) {
        if(list==null){
            return "There are no targets";
        }else{
            return list.toString();
        }
    }
}
