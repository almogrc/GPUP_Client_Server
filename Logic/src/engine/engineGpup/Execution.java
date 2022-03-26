package engine.engineGpup;

import engine.dto.Dto;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

public interface Execution {
    //String peath = null;

    //public void initGpupExecution(File file) throws JAXBException, FileNotFoundException;

    public Dto getTargetsData();

    public Dto getTargetData(String target);

    public Dto getAllPathsBetweenTargets(String targetFrom, String targetTo,boolean isRequired);

    //public void runTask(DetailsForTask details,
    //                    Consumer<String> successSummary,
    //                    Consumer<String> successWWSummary,
    //                    Consumer<String> failureSummary,
    //                    Consumer<String> skippedSummary,
    //                    Consumer<String> summary,
    //                    Consumer<String> logData,
    //                    Runnable onFinish);

    public Dto getCircleOfTarget(String targetName);

    //public boolean getWasRunBefore();

    /*
    public void readObjectFromFile(String filePath);

    public void writeObjectToFile(String path);

     */

    void stopTask(boolean isStopTask);

    //void setNumOfThreads(int numOfThreads);
}
