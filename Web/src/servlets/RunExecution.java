package servlets;

import constants.Constants;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.*;

@WebServlet(name="RunExecution", urlPatterns="/run-execution")
public class RunExecution extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String typeOfAction = request.getParameter(Constants.TYPE_OF_ACTION);
        GpupExeManager gpupExeManager = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gpupExeManager != null) {
            GpupExecution gpupExe = gpupExeManager.getGpupEx(gpupExeName);
            if (gpupExe != null) {
                switch (typeOfAction){
                    case RUN:{
                        gpupExe.runTask();
                        gpupExe.setExeStatus(ACTIVATED);
                        break;
                    }
                    case STOP:{
                        gpupExe.setExeStatus(STOPPED);
                        break;
                    }case RESUME:{
                        gpupExe.setExeStatus(ACTIVATED);
                        break;
                    }case PAUSE:{
                        gpupExe.setExeStatus(PAUSED);
                        break;
                    }
                    default:{
                        out.println("Something got wrong!");
                    }
                }

            }
        }
    }
}
