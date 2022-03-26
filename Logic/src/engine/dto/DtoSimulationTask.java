package engine.dto;

public class DtoSimulationTask {
    private boolean isRandom;
    private int processTimeInteger;
    private double probSuccessDouble;
    private double probSuccessWWarningsDouble;

    public DtoSimulationTask(boolean isRandom, int processTimeInteger, double probSuccessDouble, double probSuccessWWarningsDouble) {
        this.isRandom=isRandom;
        this.processTimeInteger=processTimeInteger;
        this.probSuccessDouble=probSuccessDouble;
        this.probSuccessWWarningsDouble=probSuccessWWarningsDouble;
    }

    public DtoSimulationTask(){

    }

    public boolean getRandom() {//maybe isRandom
        return isRandom;
    }

    public Integer getProcessTimeInteger() {
        return processTimeInteger;
    }

    public Double getProbSuccessDouble() {
        return probSuccessDouble;
    }

    public Double getProbSuccessWWarningsDouble() {
        return probSuccessWWarningsDouble;
    }

    public void setRandom(boolean isRandom) {
        this.isRandom=isRandom;
    }

    public void setProcessTimeInteger(Integer processTimeInteger) {
        this.processTimeInteger=processTimeInteger;
    }

    public void setProbSuccessDouble(double probSuccessDouble) {
        this.probSuccessDouble=probSuccessDouble;
    }

    public void setProbSuccessWWarningsDouble(double probSuccessWWarningsDouble) {
        this.probSuccessWWarningsDouble=probSuccessWWarningsDouble;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setProbSuccessDouble(Double probSuccessDouble) {
        this.probSuccessDouble = probSuccessDouble;
    }

    public void setProbSuccessWWarningsDouble(Double probSuccessWWarningsDouble) {
        this.probSuccessWWarningsDouble = probSuccessWWarningsDouble;
    }
}
