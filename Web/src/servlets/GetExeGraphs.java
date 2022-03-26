package servlets;

import com.google.gson.Gson;
import engine.dto.DtoExeGpup;
import engine.dto.DtoTargetForWorker;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import engine.graph.WrapsTarget;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;
import managerServices.UserManager;
import users.Worker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static constants.Constants.*;

@WebServlet(name="GetExeGraphs", urlPatterns="/get-execution-graphs")
public class GetExeGraphs extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        GpupExeManager gpupExe = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);
        String typeUser = request.getParameter(USER_TYPE);
        String userName = request.getParameter(USERNAME);
        boolean flag=false;
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gpupExe != null) {
        response.setStatus(200);
            List<DtoExeGpup> res=new ArrayList<DtoExeGpup>();
            List<GpupExecution> gpupExecutionList= gpupExe.getAllExe();
            for(GpupExecution temp:gpupExecutionList){
                DtoExeGpup dtoExeGpup= new DtoExeGpup(temp);
                dtoExeGpup.setProgressByPercentage(temp.getPresentOfFinish());
                if(typeUser.equals(ADMIN)){
                    dtoExeGpup.setIsSignedFor(NO);
                }else if(typeUser.equals(WORKER)){
                    List<String> currListWorkers= temp.getWorkersList();
                    for(String workerName:currListWorkers){
                        if(workerName.equals(userName)){
                            dtoExeGpup.setIsSignedFor(YES);
                            flag=true;
                        }
                    }
                    if(!flag){
                      dtoExeGpup.setIsSignedFor(NO);
                    }
                    flag=false;
                    List<WrapsTarget> wrapsTargetList = temp.getWrapsTargetList();
                    int creditsMadeByWorker=0;
                    int targetsDidByWorker=0;
                    UserManager userManager = (UserManager) getServletContext().getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
                    Worker worker = userManager.getWorkersMap().get(userName);
                    for( DtoTargetForWorker dtoTargetForWorker: worker.getDtoTargetForWorkerList()){
                        if(dtoTargetForWorker.getExeName().equals(temp.getTaskName()) && dtoTargetForWorker.getTargetStatus().equals("FINISHED")){
                            if(!temp.getRunDetails().isSimulation()){
                                creditsMadeByWorker += temp.getCompilationPrice();
                            }
                            else if(temp.getRunDetails().isSimulation()){
                                creditsMadeByWorker += temp.getSimulationPrice();
                            }
                            targetsDidByWorker++;
                        }
                    }
                    dtoExeGpup.setAmountOfTargetsDoneByWorker(targetsDidByWorker);
                    dtoExeGpup.setCreditsMadeByWorker(creditsMadeByWorker);


                    if(temp.getActiveWorkersList().contains(userName)){
                        dtoExeGpup.setIsPaused(ACTIVATED_BY_WORKER);
                    }
                    else{
                        dtoExeGpup.setIsPaused(PAUSED_BY_WORKER);
                    }
                }

                res.add(dtoExeGpup);
            }
            Gson gson=new Gson();
            String json = gson.toJson(res);
            out.println(json);
        }
        else {
            out.println("");
        }

    }
}

