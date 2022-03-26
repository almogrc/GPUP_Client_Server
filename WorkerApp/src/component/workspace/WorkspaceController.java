package component.workspace;

import com.google.gson.reflect.TypeToken;
import engine.dto.DtoExeGpup;
import engine.dto.DtoTargetForWorker;
import engine.dto.DtoTaskForRun;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import task.CompilationTask;
import task.SimulationTask;
import task.Task;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.*;

public class WorkspaceController {
    @FXML private ScrollPane workspaceScrollPane;

    @FXML private TableView<DtoTargetForWorker> targetsTableView;

    @FXML private TableColumn<DtoTargetForWorker, String> targetNameTargetsColumn;

    @FXML private TableColumn<DtoTargetForWorker, String> executionNameTargetsColumn;

    @FXML private TableColumn<DtoTargetForWorker, String> taskTypeTargetsColumn;

    @FXML private TableColumn<DtoTargetForWorker, String> statusTargetsColumn;

    @FXML private TableColumn<DtoTargetForWorker, Integer> creditsGotTargetsColumn;

    @FXML private Label creditLabel;

    @FXML private Label threadsInUseLabel;

    @FXML private TableView<DtoExeGpup> exeTableView;

    @FXML private TableColumn<DtoExeGpup,String> executionNameExeColumn;

    @FXML private TableColumn<DtoExeGpup,Integer> totalWorkersExeColumn;

    @FXML private TableColumn<DtoExeGpup,Integer> progressExeColumn;

    @FXML private TableColumn<DtoExeGpup, Integer> totalTargetsExeColumn;

    @FXML private TableColumn<DtoExeGpup,Integer> creditGotExeColumn;

    @FXML private TableColumn<DtoExeGpup,String> pauseColumn;

    @FXML private TextArea logTextArea;

    @FXML private Label userNameLabel;

    private SimpleStringProperty userName;
    private SimpleStringProperty creditWorker;
    private SimpleIntegerProperty threadsOfWorker;
    private SimpleIntegerProperty threadsOfWorkerForLabel;
    private SimpleIntegerProperty threadsOfWorkerFree;
    private SimpleIntegerProperty threadsOfWorkerFreeForLabel;
    private SimpleStringProperty logTask;
    private boolean refresher=false;

    @FXML public void initialize(){
        creditLabel.textProperty().bind(Bindings.concat(creditWorker));
        userNameLabel.textProperty().bind(Bindings.concat(userName));
        threadsInUseLabel.textProperty().bind(Bindings.concat(threadsOfWorkerFree," / " ,threadsOfWorker));
        threadsOfWorkerForLabel.bind(threadsOfWorker);
        threadsOfWorkerFreeForLabel.bind(threadsOfWorkerFree);


        //StringExpression sb = Bindings.concat("", logTask);//todo
        //logTextArea.textProperty().bind(sb);
        logTextArea.setWrapText(true);
        logTextArea.setEditable(false);

        exeTableView.setRowFactory(tv -> {
            TableRow<DtoExeGpup> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    DtoExeGpup rowData = row.getItem();
                    Request request = new Request.Builder()
                            .url(HttpUrl
                                    .parse(Constants.UPDATE_WORKER_BREAKS)
                                    .newBuilder()
                                    .addQueryParameter(EXECUTION_NAME, rowData.getTaskName())
                                    .addQueryParameter(USERNAME, userName.get())
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

                        }
                    });
                }
            });
            return row ;
        });
    }

    public void resetLogs(){
        logTextArea.clear();
    }

    public WorkspaceController(){
        logTask= new SimpleStringProperty("");
        userName= new SimpleStringProperty("");
        creditWorker=new SimpleStringProperty("0");
        threadsOfWorker= new SimpleIntegerProperty(0);
        threadsOfWorkerFree = new SimpleIntegerProperty(0);
        threadsOfWorkerForLabel = new SimpleIntegerProperty(0);
        threadsOfWorkerFreeForLabel = new SimpleIntegerProperty(0);

    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public void startCreditRefresher() {
        Timer timer = new Timer();
        TimerTask listRefresher = new TimerTask() {
            @Override
            public void run() {
                if (refresher) {
                    Request request = new Request.Builder()
                            .url(HttpUrl
                                    .parse(Constants.MONEY_MADE)
                                    .newBuilder()
                                    .addQueryParameter(USERNAME, userName.get())
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
                            if (response.isSuccessful()) {
                                Platform.runLater(()-> {
                                    try {
                                        creditWorker.set(response.body().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });

                            } else {
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        Alert alert = new Alert(Alert.AlertType.ERROR, "money problem", ButtonType.OK);
                                        alert.setTitle("404");
                                    }
                                });
                            }
                        }
                    });

                }
            }
        };
        timer.schedule(listRefresher, 10, 2000);
    }




    public void setActive() {
        refresher=true;
        startCreditRefresher();
        startListRefresherForExeTableView();
        startListRefresherForTargetTableView();
        setNumOfThreadsByServer();
        Thread newGetJobThread=new Thread(new GetTasksTread());
        newGetJobThread.start();

    }

    private void setNumOfThreadsByServer() {
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.GET_NUM_OF_THREADS)
                        .newBuilder()
                        .addQueryParameter(USERNAME,userName.get())
                        .build()
                        .toString())
                .build();

        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            try {
                String json = null;

                try {
                    if (response.isSuccessful()) {
                        json = response.body().string();
                        Integer res = GSON_INSTANCE.fromJson(json, new TypeToken<Integer>() {
                        }.getType());
                        threadsOfWorker.set(res);
                        threadsOfWorkerFree.set(res);
                        System.out.println(threadsOfWorker.get());
                    }
                } catch (IOException e) {
                    System.out.println("Something went wrong: " + e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK);
                    alert.setTitle("404");

                }
            } catch (Exception e) {
            }
        }catch (Exception e){

        }
    }

    public Parent getRoot() {
        return workspaceScrollPane;
    }

    public void startListRefresherForExeTableView() {

        Timer timer=new Timer();
        TimerTask listRefresher=new TimerTask() {
            @Override
            public void run() {
                if(refresher){
                    Request request = new Request.Builder()
                            .url(HttpUrl
                                    .parse(Constants.GET_EXECUTION_GRAPHS)
                                    .newBuilder()
                                    .addQueryParameter(USER_TYPE,WORKER)
                                    .addQueryParameter(USERNAME,userName.get())
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
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            Platform.runLater(() ->{
                                String jsonArrayOfUsersNames = null;
                                try {
                                    jsonArrayOfUsersNames = response.body().string();

                                    List<DtoExeGpup> exeList = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, new TypeToken<List<DtoExeGpup>>(){}.getType());
                                    if(exeList!=null) {
                                        exeList.removeIf(exeGpup -> exeGpup.getIsSignedFor().equals(NO));
                                        initDataExeTable(exeList);
                                    }
                                    if(exeList==null || exeList.isEmpty()){
                                        exeTableView.getItems().clear();
                                        exeTableView.refresh();
                                    }
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
        };
        timer.schedule(listRefresher, 10, 2000);
    }
    public void startListRefresherForTargetTableView() {

        Timer timer=new Timer();
        TimerTask listRefresher=new TimerTask() {
            @Override
            public void run() {
                if(refresher){
                    Request request = new Request.Builder()
                            .url(HttpUrl
                                    .parse(Constants.GET_TARGETS_DONE_BY_WORKER)
                                    .newBuilder()
                                    .addQueryParameter(USERNAME,userName.get())
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
                                String jsonArrayOfTargets = null;
                                try {
                                    jsonArrayOfTargets = response.body().string();
                                    List<DtoTargetForWorker> targetList = GSON_INSTANCE.fromJson(jsonArrayOfTargets, new TypeToken<List<DtoTargetForWorker>>(){}.getType());
                                    initTargetDataTable(targetList);
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
        };
        timer.schedule(listRefresher, 10, 2000);
    }

    private void initTargetDataTable(List<DtoTargetForWorker> targetList) {
        if(targetList!= null && !targetList.isEmpty()) {
            final ObservableList<DtoTargetForWorker> data = FXCollections.observableArrayList(targetList);
            targetNameTargetsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("targetName")
            );
            executionNameTargetsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("exeName")
            );
            statusTargetsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("targetStatus")
            );
            taskTypeTargetsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("kindOfTask")
            );
            creditsGotTargetsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("CreditsReceived")
            );

            targetsTableView.setItems(data);
        }
    }


    public void initDataExeTable(List<DtoExeGpup> exeList) {

        if(exeList!= null && !exeList.isEmpty()) {
            final ObservableList<DtoExeGpup> data = FXCollections.observableArrayList(exeList);
            executionNameExeColumn.setCellValueFactory(
                    new PropertyValueFactory<>("taskName")
            );
            totalWorkersExeColumn.setCellValueFactory(
                    new PropertyValueFactory<>("numOfWorkers")
            );
            progressExeColumn.setCellValueFactory(
                    new PropertyValueFactory<>("progressByPercentage")
            );
            totalTargetsExeColumn.setCellValueFactory(
                    new PropertyValueFactory<>("amountOfTargetsDoneByWorker")
            );
            creditGotExeColumn.setCellValueFactory(
                    new PropertyValueFactory<>("creditsMadeByWorker")
            );
            pauseColumn.setCellValueFactory(
                    new PropertyValueFactory<>("isPaused")
            );

            exeTableView.setItems(data);
        }

    }





    public synchronized Integer getNumOfThreads(){
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return threadsOfWorkerFree.get();
    }

    public synchronized void setNumOfThreads(int newNumOfThread){
        threadsOfWorkerFree.setValue(newNumOfThread);
    }

    public synchronized void NumOfThreadsPP() {
        Platform.runLater(()-> {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadsOfWorkerFree.setValue(threadsOfWorkerFree.get() + 1);
        });

    }

    public synchronized void subToNumOfThreads(int num){
        Platform.runLater(()->{
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        threadsOfWorkerFree.setValue(threadsOfWorkerFree.get()-num);

        });
    }

    public synchronized void initLogs(String log) {
        logTextArea.appendText(log);
    }

    private class GetTasksTread implements Runnable {

        @Override
        public void run() {
            handleJobGetter();
        }

        private void handleJobGetter() {
            while(refresher) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (getNumOfThreads() != 0) {
                    if (!exeTableView.getItems().isEmpty()) {
                        List<DtoExeGpup> currDtoExeList = exeTableView.getItems();
                        String jsonCurrDtoExeList = GSON_INSTANCE.toJson(currDtoExeList);
                        Request request = new Request.Builder()
                                .url(HttpUrl
                                        .parse(GET_JOB)
                                        .newBuilder()
                                        .addQueryParameter(EXECUTIONS_TO_WORK, jsonCurrDtoExeList)
                                        .addQueryParameter(USERNAME, userName.get())
                                        .addQueryParameter(NUM_OF_THREADS, getNumOfThreads().toString())
                                        .build()
                                        .toString())
                                .build();

                        OkHttpClient client = new OkHttpClient();
                        Call call = client.newCall(request);
                        try {
                            Response response = call.execute();
                            try {
                                String jsonDtoTaskForRun = response.body().string();
                                //System.out.println(jsonDtoTaskForRun);
                                List<DtoTaskForRun> dtoTaskForRunList = GSON_INSTANCE.fromJson(jsonDtoTaskForRun, new TypeToken<List<DtoTaskForRun>>(){}.getType());
                                if ( dtoTaskForRunList != null && !dtoTaskForRunList.isEmpty()) {
                                    runJobList(dtoTaskForRunList);
                                    subToNumOfThreads(dtoTaskForRunList.size());
                                }
                            } catch (IOException e) {
                                Platform.runLater(() -> {
                                    System.out.println("Something went wrong: " + e.getMessage());
                                    Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong: " + e.getMessage(), ButtonType.OK);
                                    alert.setTitle("404");
                                });
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR, ioException.getMessage(), ButtonType.OK);
                                alert.setTitle("404");
                            });
                        }
                    }

                } else {

                }
            }

        }
    }

    public void runJobList(List<DtoTaskForRun> dtoTaskForRunList) {
        for(DtoTaskForRun dtoTaskForRun : dtoTaskForRunList){
            Task task;
            if (dtoTaskForRun.getRunDetails().isSimulation()) {
                task = new SimulationTask(dtoTaskForRun.getRunDetails());
            } else {
                task = new CompilationTask(dtoTaskForRun.getRunDetails());
                //task = new CompilationTask();
            }
            task.setDtoTarget(dtoTaskForRun.getDtoTarget());

            task.setJobber(this);
            task.setWorkerName(userName.get());
            Thread t=new Thread(task);
            t.setName(dtoTaskForRun.getDtoTarget().getExecutionName()+":"+dtoTaskForRun.getDtoTarget().getName());
            System.out.println(t.getName());
            t.start();
        }
    }
}


