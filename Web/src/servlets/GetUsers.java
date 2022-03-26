package servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import managerServices.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import static constants.Constants.*;

@WebServlet(name="GetUsers", urlPatterns="/get-users")
public class GetUsers extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        UserManager userManager = (UserManager) getServletContext().getAttribute(USER_MANAGER_ATTRIBUTE_NAME);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setStatus(200);
        Set<String> res = new HashSet<>();
        if (userManager != null) {
            res=userManager.getUsersWithType();
            Gson gson=new Gson();
            String json = gson.toJson(res);
            out.println(json);
        }
        else {
            out.println("");
        }

    }
}
