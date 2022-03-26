package engine.dto;

public class DtoFinalRunResult {
    private int numOfFailed;
    private int numOfSuccess;
    private int numOfSkipped;
    private int numOfSuccessWithWarning;

    public DtoFinalRunResult(int numOfFailed,int numOfSuccess,int numOfSkipped,int numOfSuccessWithWarning){
        this.numOfFailed=numOfFailed;
        this.numOfSuccess=numOfSuccess;
        this.numOfSkipped=numOfSkipped;
        this.numOfSuccessWithWarning=numOfSuccessWithWarning;
    }
    public DtoFinalRunResult(){

    }
    public int getNumOfFailed() {
        return numOfFailed;
    }

    public void setNumOfFailed(int numOfFailed) {
        this.numOfFailed = numOfFailed;
    }

    public int getNumOfSuccess() {
        return numOfSuccess;
    }

    public void setNumOfSuccess(int numOfSuccess) {
        this.numOfSuccess = numOfSuccess;
    }

    public int getNumOfSkipped() {
        return numOfSkipped;
    }

    public void setNumOfSkipped(int numOfSkipped) {
        this.numOfSkipped = numOfSkipped;
    }

    public int getNumOfSuccessWithWarning() {
        return numOfSuccessWithWarning;
    }

    public void setNumOfSuccessWithWarning(int numOfSuccessWithWarning) {
        this.numOfSuccessWithWarning = numOfSuccessWithWarning;
    }
}
