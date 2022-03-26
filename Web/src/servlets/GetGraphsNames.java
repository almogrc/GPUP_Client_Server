package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupManager;
import managerServices.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import static constants.Constants.GPUP_MANAGER_DIC;
import static constants.Constants.USER_MANAGER_ATTRIBUTE_NAME;

@WebServlet(name="GetGraphsNames", urlPatterns="/get-graphs-names")
public class GetGraphsNames extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        GpupManager scGpupExManager = (GpupManager) getServletContext().getAttribute(GPUP_MANAGER_DIC);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setStatus(200);
        if(scGpupExManager!=null){
            Gson gson=new Gson();
            Set<String> usersList = scGpupExManager.getAllGraphsNames();
            String json = gson.toJson(usersList);

            out.println(json);
        }else{
            out.println("");
        }
    }
}
