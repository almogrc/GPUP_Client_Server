package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import engine.dto.DTOTaskOnTarget;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;

import java.io.*;
import java.util.List;

import static constants.Constants.GPUP_EXE_DIC;


@WebServlet(name="WriteTaskToFile", urlPatterns="/write-task-to-file")
public class WriteTaskToFile extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String targetName = request.getParameter(Constants.TARGET_NAME);
        String jsonNewTask =  request.getParameter(Constants.TASK_RUN_SUMMARY);
        Gson gson = new Gson();
        DTOTaskOnTarget dtoTaskOnTarget = gson.fromJson(jsonNewTask, new TypeToken<DTOTaskOnTarget>(){}.getType());

        GpupExeManager gpupExeManager = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);
        GpupExecution gpupExe = gpupExeManager.getGpupEx(gpupExeName);

        try (
                Writer out1 = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(gpupExe.getFolder() + "//" + targetName + ".log"), "UTF-8"))) {
            out1.write(dtoTaskOnTarget.toString());
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.println("file written good");
                response.setStatus(200);
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(404);
            }
        } catch (Exception e) {
            try {
                throw e;
            } catch (Exception ex) {
                response.setStatus(404);
            }
        }
    }
}
