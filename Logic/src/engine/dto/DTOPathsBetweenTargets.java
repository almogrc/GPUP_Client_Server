package engine.dto;

import java.util.ArrayList;
import java.util.List;

public class DTOPathsBetweenTargets implements Dto{

    private String targetFrom;
    private String targetTo;
    private String isRequired;
    private List<ArrayList<String>> arrArrStringPathsNames;
    private boolean boolIsRequired;

    public DTOPathsBetweenTargets(String targetFrom, String targetTo, List<ArrayList<String>> arrArrStringPathsNames, Boolean isRequired){
        this.targetFrom=targetFrom;
        this.targetTo=targetTo;
        this.boolIsRequired=isRequired;
        if(boolIsRequired){
            this.isRequired="'required for' ratio";
        }else{
            this.isRequired="'depends on' ratio";
        }
        this.arrArrStringPathsNames =arrArrStringPathsNames;
    }

    public String toString(){
        String res="";
        String arrow;
        if(boolIsRequired){
            arrow="<-";
        }else{
            arrow="->";
        }

        int i=1;
        if(arrArrStringPathsNames.size()==0){
            res+="There is no "+isRequired+" path between "+targetFrom+" and "+targetTo+"\n";
        }else{
            res+="All the paths in "+isRequired+" between "+targetFrom+" and "+targetTo+":\n";
            for(ArrayList<String> arrString: arrArrStringPathsNames){
                res+=i+". ";
                i++;
                for(int j=0;j<arrString.size()-1;j++){
                    res+=arrString.get(j) +" "+ arrow +" ";
                }
                res+=arrString.get(arrString.size()-1);
                res+="\n";
            }
        }

        return res;
    }
}
