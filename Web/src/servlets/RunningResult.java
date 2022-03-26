package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.dto.DtoFinalRunResult;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static constants.Constants.GPUP_EXE_DIC;

@WebServlet(name="RunningResult", urlPatterns="/get-final-data")
public class RunningResult  extends HttpServlet {
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
                List<Target> choosenTargets=gpupExe.getChosenTargets();
                int skipped=0;
                int failure=0;
                int success=0;
                int successWithWarnings=0;
                for(Target curr :choosenTargets){
                    if(curr.getTargetStatus().equals(Target.TargetStatus.SKIPPED)){
                        skipped++;
                    }else if(curr.getFinishStatus().equals(Target.Finish.FAILURE)){
                        failure++;
                    }else if(curr.getFinishStatus().equals(Target.Finish.SUCCESS)){
                        success++;
                    }else if(curr.getFinishStatus().equals(Target.Finish.SUCCESS_WITH_WARNINGS)) {
                        successWithWarnings++;
                    }
                }
                DtoFinalRunResult resDto=new DtoFinalRunResult(failure,success,skipped,successWithWarnings);
                Gson gson =new Gson();
                String res=gson.toJson(resDto);
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