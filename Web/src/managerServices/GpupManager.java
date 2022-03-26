package managerServices;

import engine.engineGpup.GpupExecution;

import java.util.HashMap;
import java.util.Set;

public class GpupManager {

    private final HashMap<String, GpupExecution> gpupExecutionDic;//<graphName,GpupExecution>

    public GpupManager() {
        gpupExecutionDic = new HashMap<String, GpupExecution>();
    }

    public synchronized Boolean addGpup(String graphName, GpupExecution newGpupEx) {
        if(isGraphExists(graphName)){
            return false; //No additions made
        }else {
            gpupExecutionDic.put(graphName,newGpupEx);
            return true;
        }
    }

    /*
    public synchronized HashMap<String, GpupExecution> getGpup() {
        return gpupExecutionDic;
    }

     */

    public boolean isGraphExists(String graphName) {
        return gpupExecutionDic.containsKey(graphName);
    }

    public GpupExecution getGpup(String graphName) {
        return gpupExecutionDic.get(graphName);
    }

    public Set<String> getAllGraphsNames(){
        return gpupExecutionDic.keySet();
    }
}
