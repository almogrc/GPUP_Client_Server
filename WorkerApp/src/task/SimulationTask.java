package task;

import com.google.gson.reflect.TypeToken;
import component.workspace.WorkspaceController;
import engine.dto.DTOTarget;
import engine.engineGpup.DetailsForTask;
import engine.dto.DTOTaskOnTarget;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static util.Constants.*;


public class SimulationTask implements Task, Serializable ,Runnable{
    //private final MyRunTask myRunTask;
    private int processingTime;
    private double probabilityForSuccess;
    private double probabilityForSuccessWithWarnings;
    private boolean isRandom;
    private DTOTarget dtoTarget;
    private DTOTaskOnTarget dtoTaskOnTarget;
    private String workerName;
    private WorkspaceController workspaceController;

    //private SimpleStringProperty message;


    public SimulationTask(DetailsForTask runDetails/*, MyRunTask myRunTask*/) {
        this.processingTime=runDetails.getProcessingTime();
        this.probabilityForSuccess = runDetails.getProbabilityForSuccess();
        this.probabilityForSuccessWithWarnings = runDetails.getProbabilityForSuccessWithWarnings();
        this.isRandom = runDetails.isRandom();
        //this.myRunTask=myRunTask;

    }


    @Override
    public void setJobber(WorkspaceController workspaceController){
        this.workspaceController=workspaceController;
    }

    public DTOTaskOnTarget getDTOTaskOnTarget(){
        return dtoTaskOnTarget;
    }

    @Override
    public void setDtoTarget(DTOTarget dtoTarget) {
        this.dtoTarget=dtoTarget;
    }
    public String getWorkerName() {
        return workerName;
    }
    @Override
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    @Override
    public void run() {
        dtoTaskOnTarget = new DTOTaskOnTarget(dtoTarget.getName(), dtoTarget.getData(), dtoTarget.getTargetStatus());

        if(dtoTarget.getTargetStatus().equals("WAITING")){



            //todo update status
            dtoTarget.setTargetStatus("INPROCESS");
            updateTargetStatus(dtoTarget,"INPROCESS");
            //message.set(target.getName()+ " is "+target.getTargetStatus().toString());



            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

            LocalDateTime startingTime = LocalDateTime.now();
            dtoTaskOnTarget.setStartingTime(dtf.format(startingTime));


            int realProcessTime=handleRandomProcessTime();


            addLogs(dtoTarget.getName()+ " going to sleep for "+realProcessTime+" ms\n"+dtoTarget.getName()+ " started sleeping",dtoTarget);



            try {
                Thread.sleep(realProcessTime);
            } catch (InterruptedException e) {
            }

            addLogs(dtoTarget.getName()+ " woke up",dtoTarget);

            dtoTaskOnTarget.setProcessingTime(realProcessTime);

            Random r = new Random();
            double outcome = r.nextDouble();


            if(outcome<=probabilityForSuccess){
                double isSuccessWithWarnings = r.nextDouble();
                if(isSuccessWithWarnings<=probabilityForSuccessWithWarnings){
                    dtoTarget.setFinishStatus("SUCCESS_WITH_WARNINGS");
                    updateFinishStatus(dtoTarget,"SUCCESS_WITH_WARNINGS");
                }else{
                    dtoTarget.setFinishStatus("SUCCESS");
                    updateFinishStatus(dtoTarget,"SUCCESS");
                }
            }else{
                dtoTarget.setFinishStatus("FAILURE");
                updateFinishStatus(dtoTarget,"FAILURE");

                List<String> skippedList = new ArrayList<>();
                makeSkipped(dtoTarget,skippedList);

                dtoTaskOnTarget.setTargetsSkipped(skippedList);


            }

            dtoTarget.setTargetStatus("FINISHED");
            updateTargetStatus(dtoTarget,"FINISHED");

            List<String> openedTargets = new ArrayList<>();
            getAllOpenedTargets(dtoTarget,openedTargets);
            dtoTaskOnTarget.addOpenedTarget(openedTargets);

            //message.set(dtoTarget.getName()+ " is "+ dtoTarget.getTargetStatus().toString()+" "+ dtoTarget.getFinishStatus().toString());

        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

        dtoTaskOnTarget.setFinishStatus(dtoTarget.getFinishStatus());
        LocalDateTime endingTime = LocalDateTime.now();
        dtoTaskOnTarget.setEndingTime(dtf.format(endingTime));
        dtoTaskOnTarget.setStatus(dtoTarget.getTargetStatus());
        dtoTaskOnTarget.setFinishStatus(dtoTarget.getFinishStatus());




        writeToFileToServer(dtoTaskOnTarget);
        workspaceController.NumOfThreadsPP();

    }

    private void getAllOpenedTargets(DTOTarget dtoTarget, List<String> openedTargets) {
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.GET_OPEN_TARGETS)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME,dtoTarget.getExecutionName())
                        .addQueryParameter(TARGET_NAME,dtoTarget.getName())
                        .build()
                        .toString())
                .build();

        OkHttpClient client =new OkHttpClient();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            Platform.runLater(() ->{
                try {
                    if(response.isSuccessful()) {
                        String jsonOpenedList = response.body().string();
                        List<String> openListRes=  GSON_INSTANCE.fromJson(jsonOpenedList, new TypeToken<List<String>>(){}.getType());
                        openedTargets.addAll(openListRes);
                    }
                } catch (IOException e) {
                    Platform.runLater(() ->{
                        System.out.println("Something went wrong: " + e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    });
                }
            });
        } catch (IOException e) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR,  e.getMessage(), ButtonType.OK);
                    alert.setTitle("404");
                }
            });
        }


    }

    private void addLogs(String string, DTOTarget dtoTarget) {
        addLog(string,dtoTarget);
    }

    private void makeSkipped(DTOTarget dtoTarget,List<String> skippedList){
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.UPDATE_SKIPPED)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME,dtoTarget.getExecutionName())
                        .addQueryParameter(TARGET_NAME,dtoTarget.getName())
                        .build()
                        .toString())
                .build();

        OkHttpClient client =new OkHttpClient();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            Platform.runLater(() ->{
                try {
                    if(response.isSuccessful()) {
                        String jsonSkippedList = response.body().string();
                        List<String> skippedListRes=  GSON_INSTANCE.fromJson(jsonSkippedList, new TypeToken<List<String>>(){}.getType());
                        skippedList.addAll(skippedListRes);

                    }
                } catch (IOException e) {
                    Platform.runLater(() ->{
                        System.out.println("Something went wrong: " + e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    });
                }
            });
        } catch (IOException e) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR,  e.getMessage(), ButtonType.OK);
                    alert.setTitle("404");
                }
            });
        }

    }

    private void updateFinishStatus(DTOTarget dtoTarget, String status){
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.UPDATE_FINISH_STATUS)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME,dtoTarget.getExecutionName())
                        .addQueryParameter(TARGET_NAME,dtoTarget.getName())
                        .addQueryParameter(TARGET_FINISH_STATUS,status)
                        .build()
                        .toString())
                .build();

        OkHttpClient client =new OkHttpClient();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            Platform.runLater(() ->{
                try {
                    if(response.isSuccessful()) {
                        workspaceController.initLogs(response.body().string());
                    }
                } catch (IOException e) {
                    Platform.runLater(() ->{
                        System.out.println("Something went wrong: " + e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    });
                }
            });
        } catch (IOException e) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR,  e.getMessage(), ButtonType.OK);
                    alert.setTitle("404");
                }
            });
        }

    }

    private void updateTargetStatus(DTOTarget dtoTarget, String status) {
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.UPDATE_TARGET_STATUS)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME,dtoTarget.getExecutionName())
                        .addQueryParameter(TARGET_NAME,dtoTarget.getName())
                        .addQueryParameter(TARGET_STATUS,status)
                        .addQueryParameter(USERNAME,workerName)
                        .build()
                        .toString())
                .build();

        OkHttpClient client =new OkHttpClient();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            Platform.runLater(() ->{
                try {
                    if(response.isSuccessful()) {
                        workspaceController.initLogs(response.body().string());
                    }
                } catch (IOException e) {
                    Platform.runLater(() ->{
                        System.out.println("Something went wrong: " + e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    });
                }
            });
        } catch (IOException e) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR,  e.getMessage(), ButtonType.OK);
                    alert.setTitle("404");
                }
            });
        }
    }

    private int handleRandomProcessTime() {
        int realProcessTime;
        if(isRandom){
            if(processingTime!=0){
                Random r = new Random();
                realProcessTime = r.nextInt(processingTime-0) + 0;
            }else{
                realProcessTime=0;
            }
        }else{
            realProcessTime=processingTime;
        }
        return realProcessTime;
    }

    private void writeToFileToServer(DTOTaskOnTarget newDto){

        String jsonNewTask = GSON_INSTANCE.toJson(newDto);
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.WRITE_TASK_TO_FILE)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME,dtoTarget.getExecutionName())
                        .addQueryParameter(TARGET_NAME,dtoTarget.getName())
                        .addQueryParameter(TASK_RUN_SUMMARY,jsonNewTask)
                        .build()
                        .toString())
                .build();

        OkHttpClient client =new OkHttpClient();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            Platform.runLater(() ->{
                try {
                    if(response.isSuccessful()) {
                        System.out.println(response.body().string());
                    }
                } catch (IOException e) {
                    Platform.runLater(() ->{
                        System.out.println("Something went wrong: " + e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    });
                }
            });
        } catch (IOException e) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR,  e.getMessage(), ButtonType.OK);
                    alert.setTitle("404");
                }
            });
        }
    }




    private void addLog(String newLog,DTOTarget dtoTarget){
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.ADD_LOG)
                        .newBuilder()
                        .addQueryParameter(LOG,newLog)
                        .addQueryParameter(EXECUTION_NAME,dtoTarget.getExecutionName())
                        .build()
                        .toString())
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR,  e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                Platform.runLater(() ->{
                    try {
                        //System.out.println(response.body().string());
                        workspaceController.initLogs(response.body().string());
                    } catch (IOException e) {
                        Platform.runLater(() ->{
                            System.out.println("Something went wrong: " + e.getMessage());
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ e.getMessage(), ButtonType.OK);
                            alert.setTitle("404");
                        });
                    }
                });
            }
        });
    }


}






















 /* public void setProcessingTime(int processingTime){this.processingTime=processingTime;}
    public void setProbabilityForSuccess(double probabilityForSuccess){this.probabilityForSuccess=probabilityForSuccess;}
    public void setProbabilityForSuccessWithWarnings(double probabilityForSuccessWithWarnings){this.probabilityForSuccessWithWarnings=probabilityForSuccessWithWarnings;}*/
