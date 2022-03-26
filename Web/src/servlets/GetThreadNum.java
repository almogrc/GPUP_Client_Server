package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.UserManager;
import users.Worker;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.USER_MANAGER_ATTRIBUTE_NAME;

@WebServlet(name="GetThreadNum", urlPatterns="/get-num-of-threads")

public class GetThreadNum extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String userName= request.getParameter(Constants.USERNAME);
        UserManager userManager = (UserManager) getServletContext().getAttribute(USER_MANAGER_ATTRIBUTE_NAME);

        Worker worker = userManager.getWorkersMap().get(userName);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(worker!=null){
            response.setStatus(200);
            Gson gson = new Gson();
            String json = gson.toJson(worker.getNumOfThreads());
            out.println(json);
        }
        else{
            response.setStatus(404);
        }

    }
}
