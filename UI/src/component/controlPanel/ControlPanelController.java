package component.controlPanel;

import com.google.gson.reflect.TypeToken;
import engine.dto.DTOLayoutTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToolBar;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;

import static util.Constants.*;

public class ControlPanelController {

    @FXML private ToolBar controlPanel;
    @FXML private Button runButton;
    @FXML private Button pauseButton;
    @FXML private Button resumeButton;
    @FXML private Button stopButton;

    private String taskName;

    public ControlPanelController(){

    }
    @FXML void initialize(){

    }

    public ToolBar getRoot(){
        return controlPanel;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @FXML public void handleRun(){
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.RUN_EXECUTION)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME, taskName)//todo check if its really it
                        .addQueryParameter(TYPE_OF_ACTION, RUN)
                        .build()
                        .toString())
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    String jsonLayoutTaskData = null;
                    try {
                       response.body().string();

                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            System.out.println("Something went wrong: " + e.getMessage());
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK);
                            alert.setTitle("404");
                        });
                    }
                });
            }
        });

    }
    @FXML public void handlePause(){//todo
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.RUN_EXECUTION)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME, taskName)//todo check if its really it
                        .addQueryParameter(TYPE_OF_ACTION, PAUSE)
                        .build()
                        .toString())
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    String jsonLayoutTaskData = null;
                    try {
                        response.body().string();

                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            System.out.println("Something went wrong: " + e.getMessage());
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK);
                            alert.setTitle("404");
                        });
                    }
                });
            }
        });

    }
    @FXML public void handleResume(){//todo
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.RUN_EXECUTION)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME, taskName)//todo check if its really it
                        .addQueryParameter(TYPE_OF_ACTION, RESUME)
                        .build()
                        .toString())
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    String jsonLayoutTaskData = null;
                    try {
                        response.body().string();

                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            System.out.println("Something went wrong: " + e.getMessage());
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK);
                            alert.setTitle("404");
                        });
                    }
                });
            }
        });

    }
    @FXML public void handleStop(){
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.RUN_EXECUTION)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME, taskName)//todo check if its really it
                        .addQueryParameter(TYPE_OF_ACTION, STOP)
                        .build()
                        .toString())
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                        alert.setTitle("404");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    String jsonLayoutTaskData = null;
                    try {
                        response.body().string();

                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            System.out.println("Something went wrong: " + e.getMessage());
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK);
                            alert.setTitle("404");
                        });
                    }
                });
            }
        });
    }

    public void newTaskLayout() {
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        runButton.setDisable(false);
        resumeButton.setDisable(true);

    }

    public void pausedLayout() {
        pauseButton.setDisable(true);
        stopButton.setDisable(false);
        runButton.setDisable(true);
        resumeButton.setDisable(false);
    }

    public void activatedLayout() {
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
        runButton.setDisable(true);
        resumeButton.setDisable(true);
    }

    public void stoppedLayout() {
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        runButton.setDisable(true);
        resumeButton.setDisable(true);
    }

    public void overLayout() {
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        runButton.setDisable(true);
        resumeButton.setDisable(true);
    }
}