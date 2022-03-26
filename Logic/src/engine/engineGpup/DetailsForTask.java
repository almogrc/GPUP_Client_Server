package engine.engineGpup;

import engine.dto.DtoCompilationTask;
import engine.dto.DtoSimulationTask;

import java.io.File;

public class DetailsForTask {
    private int processingTime;
    private double probabilityForSuccess;
    private double probabilityForSuccessWithWarnings;
    private boolean isRandom;
    private boolean isIncremental;
    private boolean isSimulation;

    private File directoryToCompile;
    private File directoryForCompiled;
    public DetailsForTask(int processingTime, double probabilityForSuccess, double probabilityForSuccessWithWarnings, boolean isRandom, boolean isIncremental, boolean isSimulation) {
        this.processingTime=processingTime;
        this.probabilityForSuccess=probabilityForSuccess;
        this.probabilityForSuccessWithWarnings=probabilityForSuccessWithWarnings;
        this.isRandom=isRandom;
        this.isIncremental=isIncremental;
        this.isSimulation=isSimulation;
    }

    public DetailsForTask(int processingTime, double probabilityForSuccess, double probabilityForSuccessWithWarnings, boolean isRandom,boolean isSimulation) {
        this.processingTime=processingTime;
        this.probabilityForSuccess=probabilityForSuccess;
        this.probabilityForSuccessWithWarnings=probabilityForSuccessWithWarnings;
        this.isRandom=isRandom;
        this.isSimulation=isSimulation;
    }

    public DetailsForTask(File directoryToCompile, File directoryForCompiled, boolean isIncremental, boolean isSimulation, int numOfThreads) {
        this.directoryForCompiled=directoryForCompiled;
        this.directoryToCompile=directoryToCompile;
        this.isIncremental=isIncremental;
        this.isSimulation=isSimulation;
    }

    public DetailsForTask(DtoCompilationTask dtoCompilation) {
        this.directoryToCompile= new File(dtoCompilation.getAbsolutePathToCompile());
        this.directoryForCompiled=new File(dtoCompilation.getAbsolutePathForCompiled());
        this.isSimulation=false;
    }

    public DetailsForTask(DetailsForTask other) {
        this.setProcessingTime(other.processingTime);
        this.setProbabilityForSuccess(other.probabilityForSuccess);
        this.setProbabilityForSuccessWithWarnings(other.getProbabilityForSuccessWithWarnings());
        this.setRandom(other.isRandom());
        this.setIncremental(other.isIncremental());
        this.setSimulation(other.isSimulation());
        this.directoryForCompiled=new File(other.getDirectoryForCompiled().getAbsolutePath());
        this.directoryToCompile=new File(other.getDirectoryToCompile().getAbsolutePath());
    }

    public DetailsForTask() {
    }

    public File getDirectoryToCompile() {
        return directoryToCompile;
    }

    public void setDirectoryToCompile(File directoryToCompile) {
        this.directoryToCompile = directoryToCompile;
    }

    public void setDirectoryForCompiled(File directoryForCompiled) {
        this.directoryForCompiled = directoryForCompiled;
    }

    public double getProbabilityForSuccess() {
        return probabilityForSuccess;
    }

    public double getProbabilityForSuccessWithWarnings() {
        return probabilityForSuccessWithWarnings;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public boolean isIncremental() {
        return isIncremental;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public boolean isSimulation() {
        return isSimulation;
    }

    public void setIncremental(boolean incremental) {
        isIncremental = incremental;
    }

    public void setProbabilityForSuccess(double probabilityForSuccess) {
        this.probabilityForSuccess = probabilityForSuccess;
    }

    public void setProbabilityForSuccessWithWarnings(double probabilityForSuccessWithWarnings) {
        this.probabilityForSuccessWithWarnings = probabilityForSuccessWithWarnings;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public void setSimulation(boolean simulation) {
        isSimulation = simulation;
    }

    public File getDirectoryForCompiled() {
        return directoryForCompiled;
    }

    public File geDirectoryToCompile() {
        return directoryToCompile;
    }
}
