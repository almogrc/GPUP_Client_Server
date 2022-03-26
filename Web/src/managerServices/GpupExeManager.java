package managerServices;

import engine.engineGpup.GpupExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

public class GpupExeManager {
    private final HashMap<String, GpupExecution> gpupExecutionDic;

    public GpupExeManager() {
        gpupExecutionDic = new HashMap<String, GpupExecution>();
    }

    public synchronized Boolean addGpupsEx(String graphName, GpupExecution newGpupEx) {
        if(isGraphExExists(graphName)){
            return false; //No additions made
        }else {
            gpupExecutionDic.put(graphName,newGpupEx);
            return true;
        }
    }

/*
    public synchronized HashMap<String, GpupExecution> getGpupsExe() {
        return gpupExecutionDic;
    }

 */
public synchronized List<GpupExecution> getAllExe() {
    return new ArrayList<GpupExecution>(gpupExecutionDic.values().stream().collect(toCollection(ArrayList::new)));
}

    public boolean isGraphExExists(String graphName) {
        return gpupExecutionDic.containsKey(graphName);
    }

    public GpupExecution getGpupEx(String graphName) {
        return gpupExecutionDic.get(graphName);
    }

}
