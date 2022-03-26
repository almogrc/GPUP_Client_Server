package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import engine.dto.DTOTarget;
import engine.dto.DtoExeGpup;
import engine.dto.DtoTargetForWorker;
import engine.dto.DtoTaskForRun;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;
import managerServices.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static constants.Constants.*;

@WebServlet(name="GetJob", urlPatterns="/get-job")
public class GetJob extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String jsonWorkerJobList=request.getParameter(Constants.EXECUTIONS_TO_WORK);
        String workerName = request.getParameter(Constants.USERNAME);
        String numOfThreadsString = request.getParameter(Constants.NUM_OF_THREADS);
        Gson gson =new Gson();

        List<DtoExeGpup> workerJobList = gson.fromJson(jsonWorkerJobList, new TypeToken<List<DtoExeGpup>>(){}.getType());

        Integer numOfThreads = Integer.valueOf(numOfThreadsString);
        GpupExeManager gpupExeManager = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);
        UserManager userManager = (UserManager) getServletContext().getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gpupExeManager != null) {
            Queue<GpupExecution> gpupExecutionQueue=new ArrayDeque<>();


            for(DtoExeGpup currDtoExeGpup:workerJobList){
                GpupExecution gpupExe = gpupExeManager.getGpupEx(currDtoExeGpup.getTaskName());
                if(gpupExe!=null) {
                    gpupExecutionQueue.add(gpupExe);
                }
            }
            int currNumOfThreads=0;
            List<DtoTaskForRun> dtoTaskForRunList = new ArrayList<>();
            while(!gpupExecutionQueue.isEmpty() && currNumOfThreads<numOfThreads){
                GpupExecution currGpupExe=gpupExeManager.getGpupEx(gpupExecutionQueue.remove().getTaskName());
                if(currGpupExe.getExeStatus().equals(ACTIVATED) && currGpupExe.getWorkersList().contains(workerName)){
                    //if() todo check if paused maybe from dtoExeGpup
                    String isWorkerPaused = "";
                    for(DtoExeGpup currDtoExeGpup:workerJobList){
                        if(currDtoExeGpup.getTaskName().equals(currGpupExe.getTaskName())){
                            isWorkerPaused = currDtoExeGpup.getIsPaused();
                        }
                    }
                    if(isWorkerPaused.equals(ACTIVATED_BY_WORKER)){
                        Target target = currGpupExe.getTargetToProcess();
                        if(target!=null) {
                            DtoTargetForWorker dtoTargetForWorker;
                            if (currGpupExe.getRunDetails().isSimulation()) {
                                dtoTargetForWorker = new DtoTargetForWorker(target.getName(), currGpupExe.getTaskName(), SIMULATION, target.getTargetStatus().toString(), 0);
                            } else {
                                dtoTargetForWorker = new DtoTargetForWorker(target.getName(), currGpupExe.getTaskName(), COMPILATION, target.getTargetStatus().toString(), 0);
                            }
                            userManager.getWorkersMap().get(workerName).addDtoTargetForWorkerList(dtoTargetForWorker);

                            DtoTaskForRun dtoTaskForRun = new DtoTaskForRun(currGpupExe.getRunDetails(),new DTOTarget(target,currGpupExe.getTaskName())); //todo
                            dtoTaskForRunList.add(dtoTaskForRun);


                            gpupExecutionQueue.add(currGpupExe);
                            currNumOfThreads++;
                        }
                    }

                }

            }
            String json = gson.toJson(dtoTaskForRunList);
            out.println(json);




        }

    }
}
