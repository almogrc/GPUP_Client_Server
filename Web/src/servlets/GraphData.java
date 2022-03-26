package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.dto.DTOTargetsGraph;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static constants.Constants.GPUP_MANAGER_DIC;

@WebServlet(name="GraphData", urlPatterns="/graph-data")
public class GraphData extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        String graphName= request.getParameter(Constants.GRAPH_NAME);
        GpupManager gpupExManager = (GpupManager)getServletContext().getAttribute(GPUP_MANAGER_DIC);

        PrintWriter out = null;


        if(gpupExManager.isGraphExists(graphName)){
            GpupExecution gpupExecution=gpupExManager.getGpup(graphName);

            if(gpupExecution!=null){
                try {
                    out = response.getWriter();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.setStatus(200);
                Gson gson = new Gson();
                DTOTargetsGraph res = new DTOTargetsGraph(gpupExecution.getDTOTargetList(),
                        gpupExecution.getGraphName(),
                        gpupExecution.getUserNameLoaded(),
                        gpupExecution.getSimulationPrice(),
                        gpupExecution.getCompilationPrice());
                String json = gson.toJson(res);
                out.println(json);
                out.flush();
                //response200(gpupExecution,out);
            }else{
                response.setStatus(404);
                System.out.println("gpupExecution was null");
                out.println("gpupExecution was null");
            }
        }else{
            response.setStatus(404);
            System.out.println("gpupExecution wasn't Exists");
            out.println("gpupExecution was null");
        }
    }

    private void response200(GpupExecution gpupExecution, PrintWriter out) {
        Gson gson = new Gson();
        DTOTargetsGraph res = new DTOTargetsGraph(gpupExecution.getDTOTargetList(),
                                                  gpupExecution.getGraphName(),
                                                  gpupExecution.getUserNameLoaded(),
                                                  gpupExecution.getSimulationPrice(),
                                                  gpupExecution.getCompilationPrice());
        String json = gson.toJson(res);
        out.println(json);
        out.flush();
    }
}
