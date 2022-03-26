package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import engine.dto.DtoExeListsByStatus;
import engine.dto.DtoNewTask;
import engine.engineGpup.GpupExecution;
import engine.graph.Target;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;
import managerServices.GpupManager;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static constants.Constants.GPUP_EXE_DIC;
import static constants.Constants.GPUP_MANAGER_DIC;

@WebServlet(name="UploadNewTask", urlPatterns="/upload-new-task")
public class UploadNewTask  extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {


        PrintWriter out = null;
        try {
            out = response.getWriter();
            //System.out.println(out);///////////////////////
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());///////////////////////
        }

        String newTaskJson = request.getParameter(Constants.NEW_TASK);
        Gson gson=new Gson();
        DtoNewTask newTask = gson.fromJson(newTaskJson,new TypeToken<DtoNewTask>(){}.getType());
        GpupExeManager gpupExeDic;
        synchronized(this){//if gpupExeDic is null its create him. save from double object
            gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);
            if(gpupExeDic==null){
                getServletContext().setAttribute(GPUP_EXE_DIC,new GpupExeManager());
                gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);
            }
        }

        if(gpupExeDic.isGraphExExists(newTask.getTaskName())){//if task already exist send property message
            response.setStatus(404);
            out.println(newTask.getTaskName()+" already exist. Task not loaded.");
            System.out.println(newTask.getTaskName()+" already exist. Task not loaded 52");
        }

        else{//task not exist-

            GpupManager gpupManager = (GpupManager) getServletContext().getAttribute(GPUP_MANAGER_DIC);
            GpupExecution originalGraph = gpupManager.getGpup(newTask.getGraphName());


            if(originalGraph==null){//its never should happen because task get from some exist gpupExe from GpupManager
                response.setStatus(193);/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                out.println(newTask.getGraphName()+" not found 58");
                System.out.println(newTask.getGraphName()+" not found 59");////////////////
            }

            else{
                //create new deep copy of gpupExe
                GpupExecution newExeGraph=new GpupExecution(originalGraph);
                newExeGraph.setTask(newTask);
                newExeGraph.setTaskName(newTask.getTaskName());
                newExeGraph.setChosenTargetList(newTask);
                newExeGraph.setUserNameExeCreator(newTask.getUserNameCreator());
                gpupExeDic.addGpupsEx(newExeGraph.getTaskName(),newExeGraph);

                out.println(newTask.getTaskName()+" loaded successfully 64");
                out.flush();
                System.out.println(newTask.getTaskName()+" loaded successfully 65");////////////////
                response.setStatus(200);/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        }
    }
}

