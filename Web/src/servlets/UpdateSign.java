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
import java.util.List;

import static constants.Constants.*;

@WebServlet(name="UpdateSign", urlPatterns="/update-sign")
public class UpdateSign extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String workerName = request.getParameter(Constants.USERNAME);

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
                String answer="";
                if(!gpupExe.getExeStatus().equals(STOPPED) && !gpupExe.getExeStatus().equals(OVER)) {
                    List<String> workerListNames = gpupExe.getWorkersList();
                    List<String> activeWorkerList = gpupExe.getActiveWorkersList();
                    if (workerListNames.contains(workerName)) {
                        workerListNames.remove(workerName);
                        if (activeWorkerList.contains(workerName)) {
                            activeWorkerList.remove(workerName);
                        }
                        gpupExe.setActiveWorkersList(activeWorkerList);
                        gpupExe.setWorkersList(workerListNames);
                    } else {
                        workerListNames.add(workerName);
                        if (!activeWorkerList.contains(workerName)) {
                            activeWorkerList.add(workerName);
                        }
                        gpupExe.setWorkersList(workerListNames);
                        gpupExe.setActiveWorkersList(activeWorkerList);
                    }
                   answer = "blabla";
                }
                else{
                    answer="show error";
                }
                out.println(answer);
                    response.setStatus(200);
            }


        }
    }
}
