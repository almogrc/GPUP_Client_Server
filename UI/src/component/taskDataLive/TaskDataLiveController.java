package component.taskDataLive;

import com.google.gson.reflect.TypeToken;
import engine.dto.DtoFinalRunResult;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.*;

public class TaskDataLiveController {
    @FXML private ProgressBar taskProgressBar;

    @FXML private Label progressPercentLabel;

    @FXML private ChoiceBox<String> liveDataOnTargetChoiceBox;

    @FXML private Label GetDataLabel;

    @FXML private Label StatusLabel;

    @FXML private Button GetDataButton;

    @FXML private Label errorGetDataLabel;

    @FXML private Label taskMessageLabel;

    @FXML private Label summaryTitleLabel;

    @FXML private Label FailedTaskLabel;

    @FXML private Label successTaskLabel;

    @FXML private Label skippedTaskLabel;

    @FXML private Label successWithWarnTaskLabel;

    private SimpleStringProperty getDataTitle;
    private SimpleStringProperty status;
    private SimpleStringProperty summaryTitle;
    private SimpleStringProperty failedTask;
    private SimpleStringProperty successTask;
    private SimpleStringProperty skippedTask;
    private SimpleStringProperty successWithWarnTask;
    private SimpleStringProperty errorGetData;

    private String executionName;//TODO

    public TaskDataLiveController() {
        status = new SimpleStringProperty("");
        errorGetData = new SimpleStringProperty("");
        getDataTitle = new SimpleStringProperty("");
        summaryTitle = new SimpleStringProperty("");
        failedTask = new SimpleStringProperty("");
        successTask = new SimpleStringProperty("");
        skippedTask = new SimpleStringProperty("");
        successWithWarnTask = new SimpleStringProperty("");
    }

    @FXML public  void initialize(){
        StringExpression sb = Bindings.concat("", summaryTitle);
        summaryTitleLabel.textProperty().bind(sb);
        sb = Bindings.concat("", failedTask);
        FailedTaskLabel.textProperty().bind(sb);
        sb = Bindings.concat("", successTask);
        successTaskLabel.textProperty().bind(sb);
        sb = Bindings.concat("", skippedTask);
        skippedTaskLabel.textProperty().bind(sb);
        sb = Bindings.concat("", successWithWarnTask);
        successWithWarnTaskLabel.textProperty().bind(sb);
        sb = Bindings.concat("", getDataTitle);
        GetDataLabel.textProperty().bind(sb);
        sb = Bindings.concat("", status);
        StatusLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorGetData);
        errorGetDataLabel.textProperty().bind(sb);
        //liveDataOnTargetChoiceBox.setDisable(true);//todo- depend in task (if running - you can)
        //GetDataButton.setDisable(true);//todo- depend in task (if running - you can)


    }


    @FXML private void handleGetDataButton(){
        status.setValue("");
        if(liveDataOnTargetChoiceBox.getValue()== null || liveDataOnTargetChoiceBox.getValue().equals("--Target Name--")){
            errorGetData.setValue("Please choose a target to continue.");
        }else {
            errorGetData.setValue("");
            showDataInRun(liveDataOnTargetChoiceBox.getValue());

            //serialSets.setValue("Serial sets with target: "+ chosenTarget.getListOfNamesSets());
        }
    }
    private void showDataInRun(String targetName) {
        if(executionName!=null){
            String finalUrl = HttpUrl
                    .parse(Constants.GENERIC_DATA)
                    .newBuilder()
                    .addQueryParameter(EXECUTION_NAME, executionName)
                    .addQueryParameter(TARGET_NAME, targetName)
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            {
                                System.out.println("mainTaskViewController handleDataOnTarget ");
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("404");
                                alert.setContentText("Something went wrong: " + e.getMessage());
                                alert.show();
                            }
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() == 200) {
                        Platform.runLater(()->{
                            try {
                                status.set(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        Platform.runLater(() ->
                        {
                            System.out.println("response wasnt 200");
                            System.out.println("mainTaskViewController handleDataOnTarget ");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("404");
                            alert.setContentText(response.body().toString());
                            alert.show();
                        });
                    }
                }
            });
        }
        else{
            errorGetData.setValue("executionName was null. Please try again later");
        }
    }
    public void initGetLiveData(){//TODO reset values
        liveDataOnTargetChoiceBox.setDisable(false);
        GetDataButton.setDisable(false);
        // liveDataOnTargetChoiceBox.setItems(null);
        liveDataOnTargetChoiceBox.setValue("--Target Name--");
        //ObservableList<String> TargetsOptions  = FXCollections.observableArrayList(chosenTargetNames());//todo
        //liveDataOnTargetChoiceBox.setItems(TargetsOptions);//todo
        getDataTitle.setValue("Get Live data on target:");
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    private Timer timerProcessBar;
    private TimerTask timerTaskProcessBar;
    private Boolean askData=true;
    public void startProgressBarRefresher() {
        timerProcessBar = new Timer();
        timerTaskProcessBar=new TimerTask() {
            @Override
            public void run() {
                if(askData){
                    getDataForProcessBar();
                }
            }

            private void getDataForProcessBar() {
                String finalUrl = HttpUrl
                        .parse(Constants.PROGRESS_BAR)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME, executionName)
                        .build()
                        .toString();

                HttpClientUtil.runAsync(finalUrl, new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() ->{
                            System.out.println("Something went wrong: " + e.getMessage());
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ e.getMessage(), ButtonType.OK);
                            alert.setTitle("404");
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.code() != 200) {
                            String responseBody = response.body().string();
                            Platform.runLater(() ->{
                                System.out.println("Something went wrong: " + responseBody);
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ responseBody, ButtonType.OK);
                                alert.setTitle("Something went wrong");
                            });
                        } else {
                            Platform.runLater(() -> {
                                try {
                                    double res=Double.parseDouble(response.body().string());
                                    taskProgressBar.setProgress(res);
                                    progressPercentLabel.setText(String.valueOf((int)(res*100))+"%");
                                    if((int)res==1){
                                        updateWhenFinished();
                                    }
                                } catch (IOException e) {
                                    System.out.println("Something went wrong: " + e.getMessage());
                                    Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: "+ e.getMessage(), ButtonType.OK);
                                    alert.setTitle("Something went wrong");
                                }
                            });
                        }
                    }
                });
            }
        };
        timerProcessBar.schedule(timerTaskProcessBar, 1, REFRESH_RATE);
    }

    private void updateWhenFinished() {
        askData=false;
        finalResult();
        updateTaskToOver();
    }


    private void finalResult(){
        String finalUrl = HttpUrl
                .parse(Constants.FINAL_RESULT)
                .newBuilder()
                .addQueryParameter(EXECUTION_NAME, executionName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("Something went wrong: " + e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK);
                    alert.setTitle("404");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        System.out.println("Something went wrong: " + responseBody);
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + responseBody, ButtonType.OK);
                        alert.setTitle("Something went wrong");
                    });
                } else {
                    Platform.runLater(() -> {
                        DtoFinalRunResult res = null;
                        try {
                            res = GSON_INSTANCE.fromJson(response.body().string(), new TypeToken<DtoFinalRunResult>(){}.getType());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        summaryTitle.set("Summary");
                        failedTask.set("Failed: " + res.getNumOfFailed());
                        successTask.set("Success: " + res.getNumOfSuccess());
                        skippedTask.set("Skipped: " + res.getNumOfSkipped());
                        successWithWarnTask.set("Success With Warning: " + res.getNumOfSuccessWithWarning());
                    });
                }
            }
        });
    }

    public void setAskData(Boolean ask){
        askData=ask;
    }


    public void newTaskLayout() {
        liveDataOnTargetChoiceBox.setDisable(true);
        GetDataButton.setDisable(true);

    }

    public void pausedLayout() {
        liveDataOnTargetChoiceBox.setDisable(false);
        GetDataButton.setDisable(false);
    }

    public void activatedLayout() {
        if(liveDataOnTargetChoiceBox.disableProperty().get()) {
            liveDataOnTargetChoiceBox.setDisable(false);
            GetDataButton.setDisable(false);
        }
    }

    public void stoppedLayout() {
        if(!liveDataOnTargetChoiceBox.disableProperty().get()) {
            liveDataOnTargetChoiceBox.setDisable(true);
            GetDataButton.setDisable(true);
        }
    }

    public void overLayout() {
        liveDataOnTargetChoiceBox.setDisable(true);
        GetDataButton.setDisable(true);
    }

    public void setTargetsListForChoiceBox(List<String> names){
        if(liveDataOnTargetChoiceBox.getItems()!=null && liveDataOnTargetChoiceBox.getItems().isEmpty()) {
            ObservableList<String> targetList = FXCollections.observableArrayList(names);
            liveDataOnTargetChoiceBox.setItems(targetList);
        }
    }

    public void setTaskName(String taskName) {
        this.executionName=taskName;
        startProgressBarRefresher();
    }

    private void updateTaskToOver() {
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_TASK_STATUS)
                .newBuilder()
                .addQueryParameter(EXECUTION_NAME, executionName)
                .addQueryParameter(TASK_STATUS, OVER)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("Something went wrong: " + e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK);
                    alert.setTitle("404");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        System.out.println("Something went wrong: " + responseBody);
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + responseBody, ButtonType.OK);
                        alert.setTitle("Something went wrong");
                    });
                } else {
                    System.out.println(response.body().string());
                    Platform.runLater(() -> {
                    });
                }
            }
        });
    }
}
