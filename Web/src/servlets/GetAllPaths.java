package servlets;

import constants.Constants;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupManager;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.GPUP_MANAGER_DIC;

@WebServlet(name="GetAllPaths", urlPatterns="/get-all-paths")
public class GetAllPaths extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String graphName = request.getParameter(Constants.GRAPH_NAME);
        String firstTargetName = request.getParameter(Constants.FIRST_TARGET);
        String secondTargetName = request.getParameter(Constants.SECOND_TARGET);
        String ratio = request.getParameter(Constants.RATIO);

        GpupManager gpupExManager = (GpupManager)getServletContext().getAttribute(GPUP_MANAGER_DIC);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(gpupExManager.isGraphExists(graphName)){
            GpupExecution gpupExecution=gpupExManager.getGpup(graphName);
            if(gpupExecution!=null){
                response.setStatus(200);
                response200(gpupExecution,out,firstTargetName,secondTargetName,ratio);
            }else{
                response.setStatus(404);
                System.out.println("gpupExecution was null get all paths");
                out.println("gpupExecution was null");
            }
        }else{
            response.setStatus(404);
            System.out.println("gpupExecution wasnt Exists get all paths");
            out.println("gpupExecution isnt exist GET ALL PATHS");
        }
    }

    private void response200(GpupExecution gpupExecution, PrintWriter out, String firstTargetName, String secondTargetName, String ratio) {
        boolean required=false;
        if(ratio.equals("true")){
            required=true;
        }
        out.println(gpupExecution.getAllPathsBetweenTargets(firstTargetName,secondTargetName,required).toString());

    }

}





