package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import engine.dto.DtoExeGpup;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.FROM_SCRATCH;
import static constants.Constants.GPUP_EXE_DIC;

@WebServlet(name="UploadNewTaskFromOld", urlPatterns="/upload-new-task-from-old")
public class UploadNewTaskFromOld extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        try {
            out = response.getWriter();
            //System.out.println(out);///////////////////////
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());///////////////////////
        }

        String oldTaskJson = request.getParameter(Constants.OLD_TASK);
        String exeCreator = request.getParameter(Constants.USERNAME);
        String wayToRun = request.getParameter(Constants.RUN_WAY);
        Gson gson=new Gson();
        DtoExeGpup oldTask = gson.fromJson(oldTaskJson,new TypeToken<DtoExeGpup>(){}.getType());
        GpupExeManager gpupExeDic;
        synchronized(this) {//if gpupExeDic is null its create him. save from double object
            gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);
            if (gpupExeDic == null) {// todo error
                getServletContext().setAttribute(GPUP_EXE_DIC,new GpupExeManager());
                gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);
            }
        }
        if(gpupExeDic.isGraphExExists(oldTask.getTaskName())){
            GpupExecution oldExe = gpupExeDic.getGpupEx(oldTask.getTaskName());
            response.setStatus(200);
            if(oldExe==null){//its never should happen because task get from some exist gpupExe from GpupManager
                response.setStatus(193);/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // out.println(newTask.getGraphName()+" not found 58");
                // System.out.println(newTask.getGraphName()+" not found 59");////////////////
            }
            else {
                GpupExecution newExeGraph = new GpupExecution(oldExe);
                newExeGraph.setTask(oldTask,oldExe.getRunDetails());
                newExeGraph.setChosenTargetList(oldExe.getChosenTargets());
                newExeGraph.setUserNameExeCreator(exeCreator);

                if(wayToRun.equals(FROM_SCRATCH)){
                    newExeGraph.setExeToFromScratchRun();
                }else{//incremental
                    newExeGraph.setExeToFromIncrementalRun();
                }
                String oldName = oldTask.getTaskName();
                String newName = null;
                int i = 1;
                while(newName == null){
                    if(!gpupExeDic.isGraphExExists(oldName+"("+i+")")){
                        newName = oldName+"("+i+")";
                    }
                    i++;
                }
                newExeGraph.setTaskName(newName);
                gpupExeDic.addGpupsEx(newName,newExeGraph);

                DtoExeGpup newTask = new DtoExeGpup(newExeGraph);
                Gson gson1 = new Gson();
                String newTaskJson = gson1.toJson(newTask);
                out.println(newTaskJson);
            }
        }
    }
}
