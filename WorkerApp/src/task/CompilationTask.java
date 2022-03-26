package task;

import com.google.gson.reflect.TypeToken;
import component.workspace.WorkspaceController;
import engine.dto.DTOTarget;
import engine.dto.DTOTaskOnTarget;
import engine.engineGpup.DetailsForTask;
import engine.engineGpup.Tasks.MyRunTask;
import engine.graph.Target;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static util.Constants.*;

public class CompilationTask implements  Task ,Runnable{
private File directoryToCompile;
private File directoryForCompiled;
private DTOTarget dtoTarget;
//private SimpleStringProperty message;
//private MyRunTask myRunTask;
private String workerName;
private DTOTaskOnTarget dtoTaskOnTarget;
private WorkspaceController workspaceController;


    public CompilationTask(DetailsForTask runDetails/*, MyRunTask myRunTask*/) {
          this.directoryForCompiled=runDetails.getDirectoryForCompiled();
          this.directoryToCompile=runDetails.geDirectoryToCompile();
          //this.myRunTask=myRunTask;
        }

    public void setDtoTarget(DTOTarget dtoTarget) {
        this.dtoTarget = dtoTarget;
    }

    @Override
    public void setJobber(WorkspaceController workspaceController) {
       this.workspaceController = workspaceController;
    }

    @Override
    public DTOTaskOnTarget getDTOTaskOnTarget(){
        return dtoTaskOnTarget;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    @Override
    public void run() {
        dtoTaskOnTarget = new DTOTaskOnTarget(dtoTarget.getName(), dtoTarget.getData(), dtoTarget.getTargetStatus());

        if (dtoTarget.getTargetStatus().equals("WAITING")) {
            dtoTarget.setTargetStatus("INPROCESS");
            updateTargetStatus(dtoTarget,"INPROCESS");

            addLogs(dtoTarget.getName() + " started compilation process",dtoTarget);
            String[] command = {"javac", "-d", directoryForCompiled.getPath(), "-cp", directoryToCompile.getPath(), directoryToCompile.getPath() + "/" + dtoTarget.getData().replace(".", "/").concat(".java")};
            addLogs("Going to compile: javac" + "-d" + (directoryToCompile.getPath()) + "-cp" + (directoryForCompiled.getPath()) + (directoryToCompile.getPath()) + "/" + dtoTarget.getData().replace(".", "/").concat(".java"),dtoTarget);
            try {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

                LocalDateTime startingTime = LocalDateTime.now();
                dtoTaskOnTarget.setStartingTime(dtf.format(startingTime));

                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                LocalDateTime endingTime = LocalDateTime.now();
                dtoTaskOnTarget.setStartingTime(dtf.format(endingTime));
                addLogs("Compiler processed file for " + Duration.between(startingTime, endingTime).toMillis() + " ms.",dtoTarget);
                if ((process.getErrorStream().read() != -1) || (process.exitValue() != 0)) {
                    dtoTarget.setFinishStatus("FAILURE");
                    updateFinishStatus(dtoTarget,"FAILURE");
                    addLogs(dtoTarget.getName() + " failed to compile.",dtoTarget);


                    List<String> skippedList = new ArrayList<String>();
                    makeSkipped(dtoTarget,skippedList);

                    //myRunTask.makeSkipped(dtoTarget, skippedList);
                    addLogs("Errors: " + process.getErrorStream(),dtoTarget);
                    dtoTaskOnTarget.setTargetsSkipped(skippedList);
                } else {
                    dtoTarget.setFinishStatus("SUCCESS");
                    updateFinishStatus(dtoTarget,"SUCCESS");
                    addLogs(dtoTarget.getName() + " succeeded to compile.",dtoTarget);
                }
                dtoTarget.setTargetStatus("FINISHED");
                updateTargetStatus(dtoTarget,"FINISHED");

                List<String> openedTargets = new ArrayList<>();
                getAllOpenedTargets(dtoTarget,openedTargets);
                dtoTaskOnTarget.addOpenedTarget(openedTargets);

                dtoTaskOnTarget.setStatus(dtoTarget.getTargetStatus());
                dtoTaskOnTarget.setFinishStatus(dtoTarget.getFinishStatus());

                // message.set(dtoTarget.getName() + " finished with " + dtoTarget.getFinishStatus().toString());
                writeToFileToServer(dtoTaskOnTarget);
                workspaceController.NumOfThreadsPP();
            } catch (IOException | InterruptedException ioException) {}

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




    private void addLogs(String string, DTOTarget dtoTarget) {
        addLog(string,dtoTarget);

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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() ->{
                    try {
                        System.out.println(response.body().string());
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
