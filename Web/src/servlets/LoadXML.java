package servlets;

import constants.Constants;
import engine.engineGpup.GpupExecution;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import managerServices.GpupManager;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static constants.Constants.GPUP_MANAGER_DIC;

@WebServlet(name="LoadXML", urlPatterns="/load-XML")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXML extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();

        //response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        Collection<Part> parts = request.getParts();
        //StringBuilder fileContent = new StringBuilder();


        if (getServletContext().getAttribute(GPUP_MANAGER_DIC) == null) {// if attribute GPUP_EX_SET not exist so gpupExecutionSet=null and we create new gpupExecutionSet
            getServletContext().setAttribute(GPUP_MANAGER_DIC, new GpupManager());
        }
        GpupManager scGpupExManager = (GpupManager) getServletContext().getAttribute(GPUP_MANAGER_DIC);

        for (Part part : parts) {
            GpupExecution gpupExecution;
            try {
                //try to create new gpupExecution from inputStream. if there have error its return to the client
                gpupExecution = new GpupExecution(part.getInputStream(),request.getParameter(Constants.USERNAME));
            } catch (JAXBException e) {
                System.out.println("row 47 Load XML servlet: "+ e.getMessage());
                out.println(e.getMessage());
                return;
            }

            if (scGpupExManager.addGpup(gpupExecution.getGraphName(), gpupExecution)) {
                response.setStatus(200);
                out.println("Added successfully");
            } else {
                out.println("The addition was not made. Existing file");
            }
        }





        /*
    private void printPart(Part part, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("Parameter Name: ").append(part.getName()).append("\n")
                .append("Content Type (of the file): ").append(part.getContentType()).append("\n")
                .append("Size (of the file): ").append(part.getSize()).append("\n")
                .append("Part Headers:").append("\n");

        for (String header : part.getHeaderNames()) {
            sb.append(header).append(" : ").append(part.getHeader(header)).append("\n");
        }

        out.println(sb.toString());
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private void printFileContent(String content, PrintWriter out) {
        out.println("File content:");
        out.println(content);
    }

         */
    }
}
