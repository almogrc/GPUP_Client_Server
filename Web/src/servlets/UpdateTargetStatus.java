package servlets;

import constants.Constants;
import engine.dto.DtoTargetForWorker;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;
import managerServices.UserManager;
import users.Worker;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.*;

@WebServlet(name="UpdateTargetStatus", urlPatterns="/update-target-status")
public class UpdateTargetStatus extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String targetName = request.getParameter(Constants.TARGET_NAME);
        String targetStatus = request.getParameter(Constants.TARGET_STATUS);
        String userName = request.getParameter(Constants.USERNAME);

        UserManager userManager = (UserManager) getServletContext().getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
        GpupExeManager gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Worker worker = userManager.getWorkersMap().get(userName);

        if (gpupExeDic != null) {
            response.setStatus(200);
            GpupExecution gpupExe = gpupExeDic.getGpupEx(gpupExeName);
            if(gpupExe == null){
                response.setStatus(404);
                out.println("Can not find gpup execution update message"+gpupExeName);
            }else{
                Target target=gpupExe.getTarget(targetName);
                if(target==null){
                    response.setStatus(404);
                    out.println("Can not find target "+targetName+" in "+ gpupExeName);
                }else{
                    response.setStatus(200);
                    switch(targetStatus) {
                        case "FROZEN":{
                            gpupExe.getTarget(targetName).setTargetStatus(Target.TargetStatus.FROZEN);
                            for(DtoTargetForWorker dtoTargetForWorker :worker.getDtoTargetForWorkerList()){
                                if(dtoTargetForWorker.getTargetName().equals(targetName)&& gpupExe.getTaskName().equals(dtoTargetForWorker.getExeName())){
                                    dtoTargetForWorker.setTargetStatus(targetStatus);
                                }
                            }
                            System.out.println("update successfully FROZEN");
                            out.println(targetName+ " FROZEN");
                            break;
                        }
                        case "WAITING":{
                            gpupExe.getTarget(targetName).setTargetStatus(Target.TargetStatus.WAITING);
                            for(DtoTargetForWorker dtoTargetForWorker :worker.getDtoTargetForWorkerList()){
                                if(dtoTargetForWorker.getTargetName().equals(targetName)&& gpupExe.getTaskName().equals(dtoTargetForWorker.getExeName())){
                                    dtoTargetForWorker.setTargetStatus(targetStatus);
                                }
                            }
                            System.out.println("update successfully WAITING");
                            out.println(targetName+ " WAITING");
                            break;
                        }
                        case "INPROCESS":{
                            gpupExe.getTarget(targetName).setTargetStatus(Target.TargetStatus.INPROCESS);
                            for(DtoTargetForWorker dtoTargetForWorker :worker.getDtoTargetForWorkerList()){
                                if(dtoTargetForWorker.getTargetName().equals(targetName)&& gpupExe.getTaskName().equals(dtoTargetForWorker.getExeName())){
                                    dtoTargetForWorker.setTargetStatus(targetStatus);
                                }
                            }
                            System.out.println("update successfully INPROCESS");
                            out.println(targetName+" IN PROCESS");
                            break;
                        }
                        case "SKIPPED":{
                            gpupExe.getTarget(targetName).setTargetStatus(Target.TargetStatus.SKIPPED);
                            for(DtoTargetForWorker dtoTargetForWorker :worker.getDtoTargetForWorkerList()){
                                if(dtoTargetForWorker.getTargetName().equals(targetName)&& gpupExe.getTaskName().equals(dtoTargetForWorker.getExeName())){
                                    dtoTargetForWorker.setTargetStatus(targetStatus);
                                }
                            }
                            System.out.println("update successfully SKIPPED");
                            out.println(targetName+" SKIPPED");
                            break;
                        }
                        case "FINISHED":{
                            gpupExe.getTarget(targetName).setTargetStatus(Target.TargetStatus.FINISHED);
                            for(DtoTargetForWorker dtoTargetForWorker :worker.getDtoTargetForWorkerList()){
                                if(dtoTargetForWorker.getTargetName().equals(targetName) && gpupExe.getTaskName().equals(dtoTargetForWorker.getExeName())){//maybe add taskname equals
                                    dtoTargetForWorker.setTargetStatus(targetStatus);
                                    if(dtoTargetForWorker.getKindOfTask().equals(SIMULATION)) {
                                        dtoTargetForWorker.setCreditsReceived(dtoTargetForWorker.getCreditsReceived()+gpupExe.getSimulationPrice());
                                        //dtoTargetForWorker.getCreditsReceived()+
                                        worker.addMoney(gpupExe.getSimulationPrice());
                                    }else if(dtoTargetForWorker.getKindOfTask().equals(COMPILATION)){
                                        dtoTargetForWorker.setCreditsReceived(dtoTargetForWorker.getCreditsReceived()+gpupExe.getCompilationPrice());
                                        worker.addMoney(gpupExe.getCompilationPrice());

                                    }
                                }
                            }
                            System.out.println("update successfully FINISHED");
                            out.println(targetName+" FINISHED");
                            break;
                        }
                        default:{
                            System.out.println("something get wrong with status update servlet");
                            out.println("something get wrong with status update servlet");
                            response.setStatus(500);
                        }
                    }
                }
            }
        }

        else{
            response.setStatus(404);
            out.println("Can not find exeDic");
        }

    }
}

