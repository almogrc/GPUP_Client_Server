package component.mainTask;

import com.google.gson.reflect.TypeToken;
import component.controlPanel.ControlPanelController;
import component.runTaskTable.RunTableController;
import component.taskDataLive.TaskDataLiveController;
import engine.dto.DTOLayoutTask;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.*;

public class MainTaskController {

    @FXML private GridPane runTaskTableViewGridPane;
    @FXML private RunTableController runTaskTableViewGridPaneController;
    @FXML private GridPane taskDataLiveGridpane;
    @FXML private TaskDataLiveController taskDataLiveGridpaneController;
    @FXML private BorderPane controlPanelBorderPanel;
    private ControlPanelController controlPanelController;
    @FXML private TextArea logTaskTextArea;
    @FXML private ScrollPane mainTaskScrollPane;

    private Timer timer;
    private TimerTask taskLayoutRefresher;
    private final BooleanProperty autoUpdate;
    private boolean refresher=true;//todo change to true when needed
    int indexTextArea=0;
    private SimpleStringProperty logTask;
    private String taskName;

    public MainTaskController(){
        autoUpdate = new SimpleBooleanProperty();
        logTask= new SimpleStringProperty("");
    }

    @FXML void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL controlPanelFXML = getClass().getResource("/component/controlPanel/controlPanel.fxml");
        loader.setLocation(controlPanelFXML);
        loader.load();
        controlPanelController = loader.getController();


        //StringExpression sb = Bindings.concat("", logTask);
       // logTaskTextArea.textProperty().bind(sb);
       logTaskTextArea.setWrapText(true);
       logTaskTextArea.setEditable(false);

        runTaskTableViewGridPaneController.setAskData(true);
    }


    public ScrollPane getRoot(){
        return mainTaskScrollPane;
    }

    public void activeControlPanel(String taskStatus,String taskName){
        controlPanelBorderPanel.setCenter(controlPanelController.getRoot());
        if(taskStatus.equals("New Run")){
            controlPanelController.newTaskLayout();
        }else if(taskStatus.equals("Paused")){
            controlPanelController.pausedLayout();
        }else if(taskStatus.equals("Activated")){
            controlPanelController.activatedLayout();
        }else if(taskStatus.equals("Stopped")) {
            controlPanelController.stoppedLayout();
        }else if(taskStatus.equals("Over")){
            controlPanelController.overLayout();
        }
        controlPanelController.setTaskName(taskName);
    }
    public void deActiveControlPanel(){
        controlPanelBorderPanel.setCenter(null);
    }

    public void resetLogs(){
        logTaskTextArea.clear();
    }


    public void setTaskName(String taskName) {
        this.taskName=taskName;
        runTaskTableViewGridPaneController.setTaskName(taskName);
        taskDataLiveGridpaneController.setTaskName(taskName);
    }

    public void activeLiveData(String taskStatus,List<String> targetListNames) {
        if(taskStatus.equals("New Run")){
            taskDataLiveGridpaneController.newTaskLayout();
        }else if(taskStatus.equals("Paused")){
            taskDataLiveGridpaneController.pausedLayout();
            taskDataLiveGridpaneController.setTargetsListForChoiceBox(targetListNames);
        }else if(taskStatus.equals("Activated")){
            taskDataLiveGridpaneController.activatedLayout();
            taskDataLiveGridpaneController.setTargetsListForChoiceBox(targetListNames);
        }else if(taskStatus.equals("Stopped")) {
            taskDataLiveGridpaneController.stoppedLayout();
        }else if(taskStatus.equals("Over")){
            taskDataLiveGridpaneController.overLayout();
        }
    }

    public void activeMainTask(){
        startRefresherForLayout();
        runTaskTableViewGridPaneController.startTablesRefresher();//todo
        taskDataLiveGridpaneController.startProgressBarRefresher();
        setRefresherForLog();
    }

    public void startRefresherForLayout() {
        Timer timer = new Timer();
        taskLayoutRefresher = new TimerTask() {
            @Override
            public void run() {
                if (refresher) {
                    Request request = new Request.Builder()
                            .url(HttpUrl
                                    .parse(Constants.LAYOUT_TASK)
                                    .newBuilder()
                                    .addQueryParameter(EXECUTION_NAME, taskName)
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
                                    jsonLayoutTaskData = response.body().string();
                                    DTOLayoutTask layoutData = GSON_INSTANCE.fromJson(jsonLayoutTaskData, new TypeToken<DTOLayoutTask>(){}.getType());
                                    activeControlPanel(layoutData.getExecutionStatus(),taskName);
                                    activeLiveData(layoutData.getExecutionStatus(), layoutData.getTargetNames());

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
            }
        };
        timer.schedule(taskLayoutRefresher, 10, 2000);

    }

    public void setRefresherForLog(){
        Timer timer = new Timer();
        taskLayoutRefresher = new TimerTask() {
            @Override
            public void run() {
                if (refresher) {
                    Request request = new Request.Builder()
                            .url(HttpUrl
                                    .parse(Constants.GET_LOG)
                                    .newBuilder()
                                    .addQueryParameter(EXECUTION_NAME, taskName)
                                    .addQueryParameter(INDEX, String.valueOf(indexTextArea))
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
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            Platform.runLater(() -> {
                                String jsonLayoutTaskData = null;
                                try {
                                    if(response.isSuccessful()) {
                                        jsonLayoutTaskData = response.body().string();
                                        List<String> res = GSON_INSTANCE.fromJson(jsonLayoutTaskData, new TypeToken<List<String>>(){}.getType());
                                        indexTextArea += res.size();
                                        for (String s : res) {
                                            logTaskTextArea.appendText(s);
                                            logTaskTextArea.appendText("\n");

                                        }
                                    }
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
            }
        };
        timer.schedule(taskLayoutRefresher, 1000, 2000);

    }
}
