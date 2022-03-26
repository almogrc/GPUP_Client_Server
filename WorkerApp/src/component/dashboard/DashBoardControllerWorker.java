package component.dashboard;

import com.google.gson.reflect.TypeToken;
import component.api.HttpStatusUpdate;
import component.login.LoginControllerWorker;
import component.userList.UserListController;
import component.userList.UserListRefresher;
import component.workspace.WorkspaceController;
import engine.dto.DtoExeGpup;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static util.Constants.*;

public class DashBoardControllerWorker implements Closeable {


    @FXML private GridPane userListComp;
    @FXML private UserListController userListCompController;

    @FXML private TableView<DtoExeGpup> workerDashBoardTableView;

    @FXML private TableColumn<DtoExeGpup, String> taskNameColumn;

    @FXML private TableColumn<DtoExeGpup, String> createByColumn;

    @FXML private TableColumn<DtoExeGpup, String> taskTypeColumn;

    @FXML private TableColumn<DtoExeGpup, Integer> targetNumColumn;

    @FXML private TableColumn<DtoExeGpup, Integer> leafNum;

    @FXML private TableColumn<DtoExeGpup, Integer> midNum;

    @FXML private TableColumn<DtoExeGpup, Integer> rootNumColumn;

    @FXML private TableColumn<DtoExeGpup, Integer> indepNumColumn;

    @FXML private TableColumn<DtoExeGpup, String> taskStatusColumn;

    @FXML private TableColumn<DtoExeGpup, Integer> workersOnColumn;

    @FXML private TableColumn<DtoExeGpup, Integer> targetPriceColumn;

    @FXML private TableColumn<DtoExeGpup, String> signedForColumn;

    @FXML private Label creditLabel;

    @FXML private Button workSpaceButton;

    @FXML private Label userNameLabel;
    @FXML private Label labelForAviad;

   // @FXML private ListView<String> taskChooserReviewListView;

    private SimpleStringProperty userName;
    private SimpleStringProperty creditWorker;
    private final BooleanProperty autoUpdate;
    private Stage primaryStage;
    private Timer timer;
    private TimerTask creditRefresher;
    private boolean refresher=false;
    //private AppController app;

    public DashBoardControllerWorker(){

        userName= new SimpleStringProperty("");
        creditWorker=new SimpleStringProperty("0");
        autoUpdate = new SimpleBooleanProperty(true);
        
    }

    private void initializeAppPage() throws IOException {
        //appPage//
        FXMLLoader loader= new FXMLLoader();
        URL appPage = getClass().getResource("/appController/mainAppController/mainStage.fxml");
        loader.setLocation(appPage);
        loader.load();
       // app = loader.getController();

        //app.setMainBorderPane(mainBorderPane);
       // app.setRturnPage(dashboardGridPane);
    }

    @FXML private void initialize() {
        //userListCompController.setHttpStatusUpdate(this);


        //actionCommandsComponentController.setChatCommands(this);
        //chatAreaComponentController.setHttpStatusUpdate(this);

        //chatAreaComponentController.autoUpdatesProperty().bind(actionCommandsComponentController.autoUpdatesProperty());
        //usersListController.autoUpdatesProperty().bind(actionCommandsComponentController.autoUpdatesProperty());

        creditLabel.textProperty().bind(Bindings.concat("Credit : ",creditWorker));
        userNameLabel.textProperty().bind(Bindings.concat("User Name : ",userName));
        startCreditRefresher();

        workerDashBoardTableView.setRowFactory(tv -> {


            TableRow<DtoExeGpup> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    DtoExeGpup rowData = row.getItem();
                    if(rowData.getIsSignedFor().equals(YES)){
                        Alert alertUnsigned = new Alert(Alert.AlertType.CONFIRMATION);
                        alertUnsigned.setTitle("Please confirm");
                        alertUnsigned.setContentText("Are you sure you want to stop participating in this task?");
                        Optional<ButtonType> result=alertUnsigned.showAndWait();
                        if(result.isPresent()&&result.get()==ButtonType.OK){
                            updateSign( rowData.getTaskName(),userName.get());
                        }

                    }
                    else{
                        updateSign( rowData.getTaskName(),userName.get());
                    }

                }
            });
            return row ;
        });

/*
        workerDashBoardTableView.setRowFactory(tv -> {
            TableRow<DtoExeGpup> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    DtoExeGpup rowData = row.getItem();
                    addTaskToListBeforeRun(rowData.getTaskName());
                    System.out.println(taskChooserReviewListView.getItems());
                    labelForAviad.setText(taskChooserReviewListView.getItems().get(0));
                }
            });
            return row;
        });
        taskChooserReviewListView.setCellFactory(tv -> {
           // TableRow<DtoExeGpup> row = new TableRow<>();
            ListCell<String> row= new ListCell<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    String taskRow = row.getItem();
                    ObservableList<String> currList= taskChooserReviewListView.getItems();
                    currList.removeIf(taskName -> taskName.equals(taskRow));

                    /*try {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });
            return row ;
        });
        */



    }

    private void updateSign(String taskName, String userName) {
        Request request = new Request.Builder()
                .url(HttpUrl
                        .parse(Constants.UPDATE_SIGN)
                        .newBuilder()
                        .addQueryParameter(EXECUTION_NAME,taskName)
                        .addQueryParameter(USERNAME, userName)
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
                Platform.runLater(()->{
                    if(response.isSuccessful()){
                        try {
                            String answer =  response.body().string();
                            if(answer.contains("show error")){
                                Alert alert = new Alert(Alert.AlertType.ERROR,"Can't sign to this task.", ButtonType.OK);
                                alert.show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    public void updateUserName(String userName) {
        this.userName.setValue(userName);
    }

    /*public void setHttpStatusUpdate(LoginControllerWorker loginControllerWorker) {
    }

     */

    @Override
    public void close() throws IOException {
        userListCompController.close();

    }

    public void setActive() {
        refresher=true;
        userListCompController.startListRefresher();
        startListRefresherForTableView();
        //chatAreaComponentController.startListRefresher();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
    public void startListRefresherForTableView() {

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
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Platform.runLater(() ->{
                                String jsonArrayOfUsersNames = null;
                                try {
                                    jsonArrayOfUsersNames = response.body().string();

                                    List<DtoExeGpup> exeList = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, new TypeToken<List<DtoExeGpup>>(){}.getType());
                                    initDataTable(exeList);
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


    public void initDataTable( List<DtoExeGpup> exeList) {
        if(exeList!= null && !exeList.isEmpty()) {
            final ObservableList<DtoExeGpup> data = FXCollections.observableArrayList(exeList);
            taskNameColumn.setCellValueFactory(
                    new PropertyValueFactory<>("taskName")
            );
            createByColumn.setCellValueFactory(
                    new PropertyValueFactory<>("createBy")
            );
            taskTypeColumn.setCellValueFactory(
                    new PropertyValueFactory<>("taskType")
            );
            targetNumColumn.setCellValueFactory(
                    new PropertyValueFactory<>("numOfTargets")
            );
            leafNum.setCellValueFactory(
                    new PropertyValueFactory<>("numOfLeafs")
            );
            midNum.setCellValueFactory(
                    new PropertyValueFactory<>("numOfMiddle")
            );
            rootNumColumn.setCellValueFactory(
                    new PropertyValueFactory<>("numOfRoots")
            );
            indepNumColumn.setCellValueFactory(
                    new PropertyValueFactory<>("numOfIndependent")
            );
            taskStatusColumn.setCellValueFactory(
                    new PropertyValueFactory<>("taskStatus")
            );
            workersOnColumn.setCellValueFactory(
                    new PropertyValueFactory<>("numOfWorkers")
            );
            targetPriceColumn.setCellValueFactory(
                    new PropertyValueFactory<>("targetPrice")
            );
            signedForColumn.setCellValueFactory(
                    new PropertyValueFactory<>("isSignedFor")
            );


            workerDashBoardTableView.setItems(data);
        }

    }

    @FXML private void openWorkspaceWindow() throws IOException {
        if(workSpaceButton.getText().equals("Start Work")) {
            workSpaceButton.setText("Workspace");
        }
        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setTitle(userName.get()+"'s Workspace");

        FXMLLoader loader = new FXMLLoader();
        URL workspaceFXML = getClass().getResource("/component/workspace/workspace.fxml");
        loader.setLocation(workspaceFXML);
        loader.load();
        WorkspaceController workspaceController = loader.getController();
        workspaceController.setUserName(userName.get());
        workspaceController.setActive();

        Scene scene = new Scene(workspaceController.getRoot(), 800, 620);
        newWindow.setScene(scene);
        newWindow.show();
    }
   /* private void addTaskToListBeforeRun(String rowData){

        ObservableList<String> currList= taskChooserReviewListView.getItems();
        if(!currList.contains(rowData)) {
            currList.add(rowData);
        }
       // taskChooserReviewListView.setItems(FXCollections.observableArrayList(res));
        taskChooserReviewListView.setItems(currList);
        taskChooserReviewListView.refresh();


    }

    */

}
