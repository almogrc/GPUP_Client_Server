package servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.GpupExeManager;
import managerServices.UserManager;
import users.Worker;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.*;

@WebServlet(name="GetTargetsDoneByWorker", urlPatterns="/get-targets-done-by-worker")
public class GetTargetsDoneByWorker extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        String userName = request.getParameter(USERNAME);
        UserManager userManager = (UserManager) getServletContext().getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
        boolean flag=false;

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(userManager!=null){
            Worker worker = userManager.getWorkersMap().get(userName);
            if(worker!=null){
                response.setStatus(200);
                Gson gson = new Gson();
                String jsonTargetsList = gson.toJson(worker.getDtoTargetForWorkerList());
                out.println(jsonTargetsList);
            }
        }




    }
}
