package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.dto.DTOLayoutTask;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static constants.Constants.GPUP_EXE_DIC;

@WebServlet(name="LayoutTask", urlPatterns="/layout-task")
public class LayoutTask extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);

        GpupExeManager gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);


        PrintWriter out = null;
        try {
            out = response.getWriter();
            if(gpupExeDic!=null) {
                GpupExecution gpupExe = gpupExeDic.getGpupEx(gpupExeName);
                if(gpupExe!=null) {
                    response.setStatus(200);
                    List<String> targetNames = gpupExe.getTargetNamesList();
                    String executionStatus = gpupExe.getExeStatus();
                    DTOLayoutTask dtoLayoutTask = new DTOLayoutTask(targetNames, executionStatus);

                    Gson gson = new Gson();
                    String json = gson.toJson(dtoLayoutTask);
                    out.println(json);
                    out.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
