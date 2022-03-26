package component.dashboard;

import appController.mainAppController.AppController;
import com.google.gson.reflect.TypeToken;
import component.chooseGraph.ChooseGraphController;
import component.howToRunPopUp.PopUpRunQuestionController;
import component.mainTask.MainTaskController;
import component.userList.UserListController;
import engine.dto.DtoExeGpup;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBException;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import component.api.HttpStatusUpdate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import static util.Constants.*;

public class DashboardController implements Closeable, HttpStatusUpdate{
@FXML private BorderPane mainBorderPane;

@FXML private GridPane dashboardGridPane;

@FXML private Label userNameLabel;

@FXML private ListView<String> usersListView;

@FXML private ListView<?> waitingTaskListView;

@FXML private Button xmlFileLoaderButton;

@FXML private ChoiceBox<String> graphsCoiseBox;

@FXML private Button chooseGraphButton;

@FXML private Label messageLabel;

@FXML private GridPane usersList;
@FXML private UserListController usersListController;

@FXML private GridPane chooseGraphPane;
@FXML private ChooseGraphController chooseGraphPaneController;

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

    @FXML private Label errorChosenTaskLabel;

    @FXML private Button nextToTaskButton;


    //private Stage primaryStage;
    private SimpleBooleanProperty isFileSelected;
    private SimpleStringProperty userName;
    private SimpleStringProperty errorChosenTask;
    private SimpleStringProperty errormessage;
    private AppController app;
    private Scene appScene;
    private Stage primaryStage;
    private boolean refresher=false;

    public DashboardController() throws IOException {
        this.isFileSelected = new SimpleBooleanProperty(false);
        this.errorChosenTask=new SimpleStringProperty("");
        this.errormessage=new SimpleStringProperty("");
        userName= new SimpleStringProperty("");
    }

    private void initializeAppPage() throws IOException {
        //appPage//
        FXMLLoader loader= new FXMLLoader();
        URL appPage = getClass().getResource("/appController/mainAppController/mainStage.fxml");
        loader.setLocation(appPage);
        loader.load();
        app = loader.getController();

        //app.setMainBorderPane(mainBorderPane);
        app.setReturnPage(this);

    }

    @FXML private void initialize() throws IOException {

         //usersListController.setHttpStatusUpdate(this);
        //chooseGraphController.setHttpStatusUpdate(this);
        //chooseGraphController.setUpController(this);

        //actionCommandsComponentController.setChatCommands(this);
        //chatAreaComponentController.setHttpStatusUpdate(this);

        //chatAreaComponentController.autoUpdatesProperty().bind(actionCommandsComponentController.autoUpdatesProperty());
        //usersListController.autoUpdatesProperty().bind(actionCommandsComponentController.autoUpdatesProperty());

        //chooseGraphButton.disableProperty().bind(isFileSelected.not());
        userNameLabel.textProperty().bind(Bindings.concat("User Name : ",userName));
        messageLabel.textProperty().bind(Bindings.concat("",errormessage));
        initializeAppPage();
        chooseGraphPaneController.setUpController(this);

        workerDashBoardTableView.setRowFactory(tv -> {
            TableRow<DtoExeGpup> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    DtoExeGpup rowData = row.getItem();
                   // System.out.println("Double click on: "+rowData.getTaskName());
                }
            });
            return row ;
        });


        workerDashBoardTableView.setRowFactory(tv -> {
            TableRow<DtoExeGpup> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    DtoExeGpup rowData = row.getItem();

                    try {
                        if(rowData.getTaskStatus().equals("Over")){
                            openPopUp(rowData);
                        }else {//todo check if stopped forever cant run
                            openNewWindow(rowData);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });
            return row ;
        });
    }
    private void openPopUp(DtoExeGpup rowData) throws IOException {//todo only when over

        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setTitle("How to run "+rowData.getTaskName());

        FXMLLoader loader = new FXMLLoader();
        loader= new FXMLLoader();
        URL popUpFXML = getClass().getResource("/component/howToRunPopUp/popUp.fxml");
        loader.setLocation(popUpFXML);
        loader.load();
        PopUpRunQuestionController popUpRunQuestionController = loader.getController();
        popUpRunQuestionController.setDtoExeGpup(rowData);
        popUpRunQuestionController.setUserName(userName.get());
        popUpRunQuestionController.setTaskName(rowData.getTaskName());

        Scene scene = new Scene(popUpRunQuestionController.getRoot(), 500, 600);
        newWindow.setScene(scene);
        newWindow.show();
    }

    private void openNewWindow(DtoExeGpup rowData) throws IOException {
        // New window (Stage)



        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setTitle("Run Screen Of "+rowData.getTaskName()+" task made from "+rowData.getGraphName()+" graph" );

        FXMLLoader loader = new FXMLLoader();
        loader= new FXMLLoader();
        URL dataViewFXML = getClass().getResource("/component/mainTask/mainTask.fxml");
        loader.setLocation(dataViewFXML);
        loader.load();
        MainTaskController mainTaskController = loader.getController();
        mainTaskController.setTaskName(rowData.getTaskName());
        mainTaskController.activeControlPanel(rowData.getTaskStatus(),rowData.getTaskName());//admin!
        //mainTaskController.activeLiveData(rowData.getTaskStatus(),null);
        mainTaskController.activeMainTask();


        Scene scene = new Scene(mainTaskController.getRoot(), 850, 950);
        newWindow.setScene(scene);
        newWindow.show();

    }


    @FXML
    public void handleLoadXMLClicked(ActionEvent actionEvent) throws JAXBException, FileNotFoundException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.XML")
        );
        isFileSelected.set(true);
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                            "file",
                            selectedFile.getName(),
                            RequestBody.create(
                                    selectedFile,
                                    MediaType.parse("application/octet-stream"))
                    ).build();

            Request request = new Request.Builder()
                    .url(HttpUrl
                            .parse(Constants.LOAD_XML)
                            .newBuilder()
                            .addQueryParameter("username", userName.getValue())
                            .build()
                            .toString())
                    .method("POST", body)
                    .build();

            HttpClientUtil.runAsync(request, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Platform.runLater(() ->
                            errormessage.setValue("Something went wrong: fail" + e.getMessage())
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            Platform.runLater(() ->
                                    errormessage.setValue(responseBody)));
                }
            });
        }
            /*Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File error");
            alert.setContentText(e.getMessage());
            alert.show();*/


        //dataViewController.resetUIData();
        //mainTaskViewController.resetTaskData();

    }

    public GridPane getRoot(){
        return dashboardGridPane;
    }

    public void updateUserName(String userName) {
        this.userName.setValue(userName);
    }

    @FXML
    void handleaddXMLClicked(ActionEvent event) {


    }

    @FXML
    void handleChooseClicked(ActionEvent event) {


    }

    public void switchToAppPage(){
        primaryStage.close();
        app.setPrimaryStage(primaryStage);
        app.setUserName(userName.get());
        app.setScene(primaryStage.getScene());
        if(appScene == null) {
            appScene = new Scene(app.getRoot(), 1044, 654);
        }
        primaryStage.setScene(appScene);
        primaryStage.show();
        app.getRoot().setCenter(null);

    }



    @Override
    public void updateHttpLine(String line) {

    }

    @Override
    public void close() throws IOException {
        usersListController.close();
        chooseGraphPaneController.close();
    }

    public void setActive() {
        refresher=true;
        usersListController.startListRefresher();
        chooseGraphPaneController.startListRefresher();
        startListRefresherForTableView();
        //chatAreaComponentController.startListRefresher();s
    }

    public void setInActive() {
        try {
            usersListController.close();
            chooseGraphPaneController.close();
            //chatAreaComponentController.close();
        } catch (Exception ignored) {}
    }

    public void setMessage(String please_choice_graph) {
        messageLabel.setText("Please choice graph");
    }

    public AppController getAppController(){
        return app;
    }
/*
    public void PrimeryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

 */
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
                            .addQueryParameter(USER_TYPE,ADMIN)
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
                    new PropertyValueFactory<>("priceForExe")
            );


            workerDashBoardTableView.setItems(data);
        }

    }

    public void onDashboardUp(){
        BooleanProperty bool= chooseGraphPaneController.autoUpdatesProperty();
        bool.set(true);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setRefresher(boolean flag) {
        this.refresher=flag;
    }
}
