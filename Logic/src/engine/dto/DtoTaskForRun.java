package engine.dto;

import engine.engineGpup.DetailsForTask;
import engine.graph.Target;

public class DtoTaskForRun {
    private DetailsForTask runDetails;
    private DTOTarget dtoTarget;


    public DtoTaskForRun(DetailsForTask runDetails, DTOTarget dtoTarget) {
        this.runDetails=runDetails;
        this.dtoTarget=dtoTarget;
    }

    public DtoTaskForRun(){
        this.runDetails=new DetailsForTask();
        this.dtoTarget=new DTOTarget();
    }


    public DetailsForTask getRunDetails() {
        return runDetails;
    }

    public void setRunDetails(DetailsForTask runDetails) {
        this.runDetails = runDetails;
    }

    public DTOTarget getDtoTarget() {
        return dtoTarget;
    }

    public void setDtoTarget(DTOTarget dtoTarget) {
        this.dtoTarget = dtoTarget;
    }
}
