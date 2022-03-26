package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.dto.DtoExeGpup;
import engine.dto.DtoExeListsByStatus;
import engine.dto.DtoTargetExeTableData;
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

@WebServlet(name="GetExeDataForTable", urlPatterns="/exe-data")
public class GetExeDataForTable extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String gpupExeName = request.getParameter(Constants.EXECUTION_NAME);

        GpupExeManager gpupExeDic = (GpupExeManager) getServletContext().getAttribute(GPUP_EXE_DIC);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gpupExeDic != null) {
            response.setStatus(200);
            GpupExecution gpupExe = gpupExeDic.getGpupEx(gpupExeName);
            if(gpupExe!=null) {
                List<DtoTargetExeTableData> frozenList = new ArrayList<DtoTargetExeTableData>();
                List<DtoTargetExeTableData> waitingList = new ArrayList<DtoTargetExeTableData>();
                List<DtoTargetExeTableData> inProcessList = new ArrayList<DtoTargetExeTableData>();
                List<DtoTargetExeTableData> finishedList = new ArrayList<DtoTargetExeTableData>();
                List<DtoTargetExeTableData> skippedList = new ArrayList<DtoTargetExeTableData>();
                for (Target curr : gpupExe.getChosenTargets()) {
                    Target.TargetStatus status = curr.getTargetStatus();
                    switch (status) {
                        case FROZEN:
                            frozenList.add(new DtoTargetExeTableData(curr.getName(), "FROZEN", "NOTFINISHED"));
                            break;
                        case WAITING:
                            waitingList.add(new DtoTargetExeTableData(curr.getName(), "WAITING", "NOTFINISHED"));
                            break;
                        case INPROCESS:
                            inProcessList.add(new DtoTargetExeTableData(curr.getName(), "INPROCESS", "NOTFINISHED"));
                            break;
                        case SKIPPED:
                            skippedList.add(new DtoTargetExeTableData(curr.getName(), "SKIPPED", "NOTFINISHED"));
                            break;
                        case FINISHED: {
                            switch (curr.getFinishStatus()) {
                                case SUCCESS:
                                    finishedList.add(new DtoTargetExeTableData(curr.getName(), "FINISHED", "SUCCESS"));
                                    break;
                                case FAILURE:
                                    finishedList.add(new DtoTargetExeTableData(curr.getName(), "FINISHED", "FAILURE"));
                                    break;
                                case SUCCESS_WITH_WARNINGS:
                                    finishedList.add(new DtoTargetExeTableData(curr.getName(), "FINISHED", "SUCCESS_WITH_WARNINGS"));
                                    break;
                                case NOTFINISHED:
                                    finishedList.add(new DtoTargetExeTableData(curr.getName(), "FINISHED", "NOTFINISHED"));
                                    break;
                                default:
                                    System.out.println("something get mistake FINISHED src/servlets/GetExeDataForTable.java");
                            }
                        }
                        break;
                        default:
                            System.out.println("something get mistake STATUS src/servlets/GetExeDataForTable.java");
                    }

                }

                DtoExeListsByStatus res = new DtoExeListsByStatus(frozenList, waitingList, inProcessList, finishedList, skippedList);
                Gson gson = new Gson();
                String json = gson.toJson(res);
                out.println(json);
            }
            else{
            System.out.println("ERROR! gpupExe was NULL");
            out.println("ERROR! gpupExe wasn't found");
            response.setStatus(404);
            }
        } else {
            System.out.println("ERROR! gpupExeDic was NULL");
            out.println("ERROR! gpupExe was NULL");
            response.setStatus(404);
        }
    }
}