package servlets;

import constants.Constants;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import engine.graph.WrapsTarget;
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

@WebServlet(name="LiveData", urlPatterns="/live-data")
public class LiveData extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String executionName= request.getParameter(Constants.EXECUTION_NAME);
        String targetName= request.getParameter(Constants.TARGET_NAME);

        GpupExeManager gpupExeManager = (GpupExeManager)getServletContext().getAttribute(GPUP_EXE_DIC);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(gpupExeManager.isGraphExExists(executionName)){
            GpupExecution gpupExecution=gpupExeManager.getGpupEx(executionName);
            if(gpupExecution!=null){
                response.setStatus(200);
                Target target = gpupExecution.getTarget(targetName);
                String TargetStatus="Target status: "+target.getTargetStatus();
                List <String>chosenTargetNames=gpupExecution.getTargetNamesList();

                String dataOnTarget="";
                switch(target.getTargetStatus()){
                    case FROZEN:
                    {
                        dataOnTarget="Frozen because "+target.FrozenBecauseThisTargets(chosenTargetNames);
                        break;
                    }
                    case SKIPPED:
                    {
                        dataOnTarget="Skipped because "+target.skippedBecauseThisTargets(chosenTargetNames);
                        break;
                    }
                    case WAITING:
                    {
                        dataOnTarget="Waiting for "+target.howMSWaiting()+" ms";
                        break;
                    }
                    case INPROCESS:
                    {
                        dataOnTarget="In Process for "+target.howMSinProcess()+" ms";
                        break;
                    }
                    case FINISHED:
                    {
                        dataOnTarget="Finished with the status: "+target.getFinishStatus();
                        break;
                    }
                }
                out.println(TargetStatus+"\n"+dataOnTarget);
            }else{
                response.setStatus(404);
                System.out.println("gpupExecution was null");
                out.println("gpupExecution was null");
            }
        }else{
            response.setStatus(404);
            System.out.println("gpupExecution wasnt Exists");
            out.println("gpupExecution was null");
        }


    }


}
