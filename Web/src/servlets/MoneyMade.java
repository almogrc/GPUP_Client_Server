package servlets;

import constants.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.concurrent.WorkerStateEvent;
import managerServices.UserManager;
import users.Worker;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.USER_MANAGER_ATTRIBUTE_NAME;

@WebServlet(name="MoneyMade", urlPatterns="/money-made")
public class MoneyMade extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {


        UserManager userManager = (UserManager) getServletContext().getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
        String userName= request.getParameter(Constants.USERNAME);


        PrintWriter out = null;
        try {
            out = response.getWriter();
            Integer money = userManager.getMoneyForWorker(userName);

                out.println(money.toString());
                response.setStatus(200);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
