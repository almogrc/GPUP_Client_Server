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
import static constants.Constants.COMPILATION;

@WebServlet(name="UpdateTaskStatus", urlPatterns="/update-task-status")
public class UpdateTaskStatus extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String taskStatus = request.getParameter(Constants.TASK_STATUS);

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
                out.println("Can not find gpup execution update message"+gpupExeName);
            }else{
                response.setStatus(200);
                gpupExe.setExeStatus(taskStatus);
                if(taskStatus.equals(OVER) || taskStatus.equals(STOPPED)){
                    gpupExe.getWorkersList().clear();
                }
                out.println("update successfully to"+ taskStatus);
            }
        }

        else{
            response.setStatus(404);
            out.println("Can not find exeDic");
        }
    }
}
