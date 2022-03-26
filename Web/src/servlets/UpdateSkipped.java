package servlets;

import com.google.gson.Gson;
import constants.Constants;
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

@WebServlet(name="UpdateSkipped", urlPatterns="/update-skipped")
public class UpdateSkipped  extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);
        String targetName = request.getParameter(Constants.TARGET_NAME);


        GpupExeManager gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gpupExeDic != null) {
            GpupExecution gpupExe = gpupExeDic.getGpupEx(gpupExeName);
            if(gpupExe == null){
                response.setStatus(404);
                out.println("Can not find gpup execution update skipped "+gpupExeName);
            }else{
                Target target=gpupExe.getTarget(targetName);
                if(target==null){
                    response.setStatus(404);
                    out.println("Can not find target "+targetName+" in "+ gpupExeName);
                }else{
                    response.setStatus(200);
                    List<Target> skippedlist=gpupExe.makeSkipped(target);
                    List<String>res=new ArrayList<String>();
                    for(Target curr:skippedlist){
                        res.add(curr.getName());
                    }

                    Gson gson=new Gson();
                    String resJson=gson.toJson(res);
                    out.println(resJson);
                }
            }
        }

        else{
            response.setStatus(404);
            out.println("Can not find exeDic");
        }
    }
}
