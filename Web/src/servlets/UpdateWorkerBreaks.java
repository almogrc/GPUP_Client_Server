package servlets;

import constants.Constants;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;
import managerServices.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import static constants.Constants.GPUP_EXE_DIC;
import static constants.Constants.USER_MANAGER_ATTRIBUTE_NAME;

@WebServlet(name="UpdateWorkerBreaks", urlPatterns="/update-worker-breaks")
public class UpdateWorkerBreaks extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String workerName = request.getParameter(Constants.USERNAME);
        UserManager userManager = (UserManager) getServletContext().getAttribute(USER_MANAGER_ATTRIBUTE_NAME);

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
                List<String> activeWorkerList = gpupExe.getActiveWorkersList();
                if (activeWorkerList.contains(workerName)) {
                    activeWorkerList.remove(workerName);
                    gpupExe.setActiveWorkersList(activeWorkerList);
                } else {
                    activeWorkerList.add(workerName);
                    gpupExe.setActiveWorkersList(activeWorkerList);
                }
                response.setStatus(200);
            }


        }
    }
}



