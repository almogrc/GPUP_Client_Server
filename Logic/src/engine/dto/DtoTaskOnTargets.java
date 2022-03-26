package engine.dto;

import engine.graph.Target;

import java.time.LocalTime;
import java.util.List;

public class DtoTaskOnTargets implements Dto {

        private int numOfSucceeded;
        private int numOfFailed;
        private int numOfSucceededWithWarnings;
        private int numOfSkipped;
        private int numOfFrozen;

        private LocalTime processTimeInFormat;
        private List<DTOTaskOnTarget> listOfDtosOnEachTarget;



    public DtoTaskOnTargets(LocalTime processTimeInFormat, List<DTOTaskOnTarget> dtoArr ){
        this.listOfDtosOnEachTarget = dtoArr;
        this.processTimeInFormat = processTimeInFormat;
        String currStatus;
        String currFinish;
        for( DTOTaskOnTarget dtoTaskOnTarget: listOfDtosOnEachTarget){
            currFinish = dtoTaskOnTarget.getFinish();

            switch (currFinish){

                case "FAILURE":
                {
                    numOfFailed++;
                    break;
                }
                case "SUCCESS":
                {
                    numOfSucceeded++;
                    break;
                }
                case "SUCCESS_WITH_WARNINGS":
                {
                    numOfSucceededWithWarnings++;
                    break;
                }
                case "NOTFINISHED":
                {
                    currStatus = dtoTaskOnTarget.getStatus();
                    if(currStatus.equals("SKIPPED"))
                    {
                        numOfSkipped++;
                    }else{
                        numOfFrozen++;
                    }
                    break;
                }

            }
        }
    }

    @Override
    public String toString(){
        String dataToFile;
        String res = "";
        for(DTOTaskOnTarget dtoRunTarget:listOfDtosOnEachTarget){
            res+=dtoRunTarget.toString();
        }
        res+="\n\n";

        res+="###########################################################\n"+"Run Summary:\n"+
        "Total number of targets: "+listOfDtosOnEachTarget.size()+"\n"+
        "Number of successful targets: "+numOfSucceeded+"\n"+
        "Number of targets that succeeded with warnings: "+numOfSucceededWithWarnings+"\n"+
        "Number of Targets Failed: "+numOfFailed+
        "\nNumber of targets that couldn't run (skipped or frozen targets): "+(numOfSkipped+numOfFrozen)+ ifNoMoreWorkToBeDone()+"\n";

        for(DTOTaskOnTarget dtoTaskOnTarget: listOfDtosOnEachTarget){
            res+= "***********************************************************\n";
            dataToFile ="Target "+dtoTaskOnTarget.getName()+
                    "\nData of target: "+dtoTaskOnTarget.getData()+"\n"+
                    extraDetailIfFinished(dtoTaskOnTarget)+"\n";
            res += dataToFile;
        }


        return res;

    }
        public String ifNoMoreWorkToBeDone(){
            String res ="\n";
            if (listOfDtosOnEachTarget.size() == 0)
                res+= "Please take note : there are no more targets to process in 'incremental', all succeeded.";
            return res;
        }
        public String extraDetailIfFinished(DTOTaskOnTarget dtoTaskOnTarget){
        String res="";
            if(dtoTaskOnTarget.getFinish().equals(Target.Finish.NOTFINISHED)){
                res +=  "Target didn't run, ended with "+ dtoTaskOnTarget.getStatus().toString()+" status\n";
            }else{
                res +=  "Target finished with " + dtoTaskOnTarget.getFinish().toString()+" status\n"+
                        "Target ran in "+ LocalTime.ofSecondOfDay(dtoTaskOnTarget.getProcessingTime()/1000).toString()+" (HH:MM:SS)\n"+
                        dtoTaskOnTarget.getNewSkippedTargets() + dtoTaskOnTarget.getNewOpenedTargets();

            }
            return res;
        }
}
