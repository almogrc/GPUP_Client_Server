package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import constants.Constants;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

import static constants.Constants.GPUP_MANAGER_DIC;

@WebServlet(name="WhatIf", urlPatterns="/what-if")
public class WhatIf extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String graphName= request.getParameter(Constants.GRAPH_NAME);
        String targetName= request.getParameter(Constants.TARGET_NAME);
        String ratio= request.getParameter(Constants.RATIO);
        GpupManager gpupExManager = (GpupManager)getServletContext().getAttribute(GPUP_MANAGER_DIC);
        boolean isRequired=false;

        if(ratio.equals("true")){
            isRequired=true;
        }

        PrintWriter out = null;


        if(gpupExManager.isGraphExists(graphName)){
            GpupExecution gpupExecution = gpupExManager.getGpup(graphName);
            if(gpupExecution!=null){
                try {
                    out = response.getWriter();
                    List<String> AllOfIndirect= gpupExecution.getAllOfIndirect(targetName,isRequired);
                    Gson gson=new GsonBuilder().create();
                    String jsonArray=gson.toJson(AllOfIndirect);
                    out.println(jsonArray);
                   // response200(gpupExecution,targetName,isRequired,out);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {
                response.setStatus(404);
                out.println("gpup exe is null- what If");
                System.out.println("gpup exe is null- what If");
            }
        }else{
            response.setStatus(404);
            out.println("gpup exe isnt exists - what If");
            System.out.println("gpup exe isnt exists - what If");
        }
    }

    private void response200(GpupExecution gpupExecution, String targetName, boolean isRequired, PrintWriter out) {

    }
}
