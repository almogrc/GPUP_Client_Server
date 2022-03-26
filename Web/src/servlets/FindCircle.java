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

@WebServlet(name="FindCircle", urlPatterns="/find-circle")
public class FindCircle extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        String graphName= request.getParameter(Constants.GRAPH_NAME);
        String targetName= request.getParameter(Constants.TARGET_NAME);
        GpupManager gpupExManager = (GpupManager)getServletContext().getAttribute(GPUP_MANAGER_DIC);

        PrintWriter out = null;


        if(gpupExManager.isGraphExists(graphName)){
            GpupExecution gpupExecution=gpupExManager.getGpup(graphName);
            if(gpupExecution!=null){
                response.setStatus(200);
                try {
                    out = response.getWriter();
                    out.println(gpupExecution.getCircleOfTarget(targetName).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                response.setStatus(404);
                System.out.println("gpupExecution was null Find Circle");
                out.println("gpupExecution was null");
            }
        }else{
            response.setStatus(404);
            System.out.println("gpupExecution wasnt Exists");
            out.println("gpupExecution was null");
        }
    }

    private void response200(GpupExecution gpupExecution, PrintWriter out, String targetName) {

    }
}

