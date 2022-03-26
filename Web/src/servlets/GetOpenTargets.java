package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.dto.DtoTargetForWorker;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;
import managerServices.GpupManager;
import managerServices.UserManager;
import users.Worker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import static constants.Constants.*;

@WebServlet(name="GetOpenTargets", urlPatterns="/get-open-targets")
public class GetOpenTargets extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String targetName = request.getParameter(Constants.TARGET_NAME);

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
                out.println("Can not find gpup execution get opened Targets" + gpupExeName);
            } else {
                Target target = gpupExe.getTarget(targetName);
                if (target == null) {
                    response.setStatus(404);
                    out.println("Can not find target " + targetName + " in " + gpupExeName);
                } else {
                    response.setStatus(200);
                    List<String> res=gpupExe.allOpenedTargets(target);
                    Gson gson=new Gson();
                    out.println(gson.toJson(res));
                }
            }
        }
    }
}
