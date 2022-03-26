package engine.dto;

import java.util.ArrayList;
import java.util.List;

public class DTOTargetsGraph {
    List<DTOTarget> listDTOTarget;
    String XMLUploadedUserName;
    int simulationPrice;
    int compilationPrice;
    String graphName;
    public DTOTargetsGraph(List<DTOTarget> dtoTargetList, String graphName, String userNameLoaded, int simulationPrice, int compilationPrice){
        listDTOTarget=new ArrayList<DTOTarget>();
        listDTOTarget= dtoTargetList;
        this.graphName=graphName;
        this.simulationPrice=simulationPrice;
        this.compilationPrice=compilationPrice;
        this.XMLUploadedUserName=userNameLoaded;

    }

    public DTOTargetsGraph(List<DTOTarget> listDTOTarget){this.listDTOTarget=listDTOTarget;}

    public String getXMLUploadedUserName(){return XMLUploadedUserName;}
    public int getSimulationPrice(){return simulationPrice;}
    public int getCompilationPrice(){return compilationPrice;}
    public String getGraphName(){return graphName;}


    public void setXMLUploadedUserName(String XMLUploadedUserName){this.XMLUploadedUserName=XMLUploadedUserName;}
    public void setSimulationPrice(int simulationPrice){this.simulationPrice=simulationPrice;}
    public void setCompilationPrice(int compilationPrice){this.compilationPrice=compilationPrice;}
    public void setGraphName(String graphName){this.graphName=graphName;}

    public List<DTOTarget> getListDTOTarget(){return listDTOTarget;}

    public void setListDTOTarget(List<DTOTarget> listDTOTarget){this.listDTOTarget=listDTOTarget;}


    public int getNumOfIndependent() {
        int res=0;
        for(DTOTarget dtoTarget : listDTOTarget){
            if(dtoTarget.getLocation().equals("INDEPENDENT")){
                res++;
            }
        }
        return res;
    }

    public int getNumOfMiddle() {
        int res=0;
        for(DTOTarget dtoTarget : listDTOTarget){
            if(dtoTarget.getLocation().equals("MIDDLE")){
                res++;
            }
        }
        return res;
    }

    public int getNumOfRoot() {
        int res=0;
        for(DTOTarget dtoTarget : listDTOTarget){
            if(dtoTarget.getLocation().equals("ROOT")){
                res++;
            }
        }
        return res;
    }
    public List<String> getTargetNamesList() {
        List<String>res=new ArrayList<String>();
        for(DTOTarget dtoTarget : listDTOTarget){
            res.add(dtoTarget.getName());
        }
        return res;
    }

    public int getNumOfLeaf() {
        int res=0;
        for(DTOTarget dtoTarget : listDTOTarget){
            if(dtoTarget.getLocation().equals("LEAF")){
                res++;
            }
        }
        return res;
    }


}
