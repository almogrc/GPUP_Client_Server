package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import engine.dto.DTOTarget;
import engine.dto.DtoExeGpup;
import engine.dto.DtoTargetForWorker;
import engine.dto.DtoTaskForRun;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import engine.graph.WrapsTarget;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;
import managerServices.UserManager;
import users.Worker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static constants.Constants.*;
import static constants.Constants.COMPILATION;

@WebServlet(name="GetLog", urlPatterns="/get-log")
public class GetLog extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(EXECUTION_NAME);
        String indexString = request.getParameter(INDEX);
        GpupExeManager gpupExe = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);

        int index=Integer.parseInt(indexString);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (gpupExe != null) {
            response.setStatus(200);
            GpupExecution gpupExecution=gpupExe.getGpupEx(gpupExeName);
            if(gpupExecution==null){
                response.setStatus(200);
                out.println("error gpupExecution not found");
            }else{
                List<String>logList=gpupExecution.getLog();
                if(logList==null){
                    out.println("log list was null");
                }else{
                    List<String>res=logList.subList(index+1,logList.size());
                    Gson gson=new Gson();
                    String gsonRes=gson.toJson(res);
                    out.println(gsonRes);
                }
            }
        }
        else {
            out.println("");
        }


    }
}