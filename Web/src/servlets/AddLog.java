package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.GPUP_EXE_DIC;
@WebServlet(name="AddLog", urlPatterns="/add-log")

public class AddLog extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String log = request.getParameter(Constants.LOG);

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
            if (gpupExe == null) {
                response.setStatus(404);
                out.println("Can not find gpup execution Add Log" + gpupExeName);
            } else {
                response.setStatus(200);
                gpupExe.addLog(log);
                out.println(log);
            }
        } else {
            response.setStatus(404);
            out.println("Can not find exeDic");
        }
    }
}
