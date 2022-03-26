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


@WebServlet(name="UpdateFinishStatus", urlPatterns="/update-finish-status")
public class UpdateFinishStatus extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String targetName = request.getParameter(Constants.TARGET_NAME);
        String targetStatus = request.getParameter(Constants.TARGET_FINISH_STATUS);


        GpupExeManager gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }



        if (gpupExeDic != null) {
            response.setStatus(200);
            GpupExecution gpupExe = gpupExeDic.getGpupEx(gpupExeName);
            if(gpupExe == null){
                response.setStatus(404);
                out.println("Can not find gpup execution update finish Status"+gpupExeName);
            }else{
                Target target=gpupExe.getTarget(targetName);
                if(target==null){
                    response.setStatus(404);
                    out.println("Can not find target "+targetName+" in "+ gpupExeName);
                }else{
                    response.setStatus(200);
                    gpupExe.addLog(targetName+" "+ targetStatus);
                    switch(targetStatus) {
                        case "SUCCESS_WITH_WARNINGS":{
                            target.setFinishStatus(Target.Finish.SUCCESS_WITH_WARNINGS);
                            out.println(targetName+ " SUCCESS WITH WARNINGS");
                            break;
                        }
                        case "SUCCESS":{
                            target.setFinishStatus(Target.Finish.SUCCESS);
                            out.println(targetName+ " SUCCESS");
                            break;
                        }
                        case "FAILURE":{
                            target.setFinishStatus(Target.Finish.FAILURE);
                            out.println(targetName+" FAILURE");
                            break;
                        } case "NOT_FINISHED":{//todo not supposed to be here
                            target.setFinishStatus(Target.Finish.NOTFINISHED);
                            out.println(targetName+ " NOT FINISHED");
                            break;
                        }

                        default:{
                            System.out.println("something get wrong with finish status update servlet");
                            out.println("something get wrong with finish status update servlet");
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

