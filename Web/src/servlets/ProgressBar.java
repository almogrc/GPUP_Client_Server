package servlets;


import com.google.gson.Gson;
import constants.Constants;
import engine.dto.DtoExeListsByStatus;
import engine.dto.DtoTargetExeTableData;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.GPUP_EXE_DIC;


@WebServlet(name="ProgressBar", urlPatterns="/get-progressbar")
public class ProgressBar extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);

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
            if (gpupExe != null) {
                int numOfProcessedTargets=0;
                List<Target> choosenTargets=gpupExe.getChosenTargets();
                for(Target curr :choosenTargets){
                    if(curr.getTargetStatus().equals(Target.TargetStatus.FINISHED)||curr.getTargetStatus().equals(Target.TargetStatus.SKIPPED)){
                        numOfProcessedTargets++;
                    }
                }
                double res=(double)numOfProcessedTargets/choosenTargets.size();
                out.println(res);
            } else {
                System.out.println("ERROR! gpupExe was NULL");
                out.println("ERROR! gpupExe wasn't found");
                response.setStatus(404);
            }
        } else {
            System.out.println("ERROR! gpupExeDic was NULL");
            out.println("ERROR! no exe data base was found");
            response.setStatus(404);
        }
    }
}