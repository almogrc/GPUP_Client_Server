package component.runTaskTable;

import appController.taskView.mainTaskView.MainTaskViewController;

import com.google.gson.reflect.TypeToken;
import component.runTaskTable.myRectangle.MyRectangle;
import engine.dto.DtoExeListsByStatus;
import engine.dto.DtoTargetExeTableData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;

import static util.Constants.*;

public class RunTableController {

    @FXML private GridPane runViewGridPane;
    @FXML private TableView<MyRectangle> frozenTV;
    @FXML private TableColumn<MyRectangle, StackPane> frozenCol;
    @FXML private TableView<MyRectangle> waitingTV;
    @FXML private TableColumn<MyRectangle, StackPane> waitingCol;
    @FXML private TableView<MyRectangle> inProcessTV;
    @FXML private TableColumn<MyRectangle, StackPane> inProcessCol;
    @FXML private TableView<MyRectangle> finishedTV;
    @FXML private TableColumn<MyRectangle, StackPane> finishedCol;
    @FXML private TableView<MyRectangle> skippedTV;
    @FXML private TableColumn<MyRectangle, StackPane> skippedCol;



    private MainTaskViewController mainTaskViewController;
    private List<MyRectangle> frozenList = new ArrayList<>();
    private List<MyRectangle> waitingList = new ArrayList<>();
    private List<MyRectangle> inProcessList = new ArrayList<>();
    private List<MyRectangle> finishedList = new ArrayList<>();
    private List<MyRectangle> skippedList = new ArrayList<>();
    private ObservableList<MyRectangle> myRectangleList;
    private List<MyRectangle> temp;
    private int millisForAnimations =1;
    private Boolean askData=false;


    private Timer timer;
    private TimerTask timerTask;
    private String taskName;

    @FXML
    public void initialize(){
    }

    public RunTableController(){
        askData=false;
    }

    public void setAskData(Boolean ask){
        this.askData=ask;
    }



    public void startTablesRefresher() {
        timer = new Timer();
        timerTask=new TimerTask() {
            @Override
            public void run() {
                if(askData){
                    getData();
                }
            }
        };
        timer.schedule(timerTask, 1, REFRESH_RATE);
    }

    private void getData() {
        String finalUrl = HttpUrl
                .parse(Constants.EXE_DATA)
                .newBuilder()
                .addQueryParameter(EXECUTION_NAME, taskName)
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
                        String res = null;
                        try {
                            res=response.body().string();
                            DtoExeListsByStatus exeListsByStatus=GSON_INSTANCE.fromJson(res,new TypeToken<DtoExeListsByStatus>(){}.getType());
                            refreshTables(exeListsByStatus);
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

    private void refreshTables(DtoExeListsByStatus exeListsByStatus) {
        final ObservableList<MyRectangle> frozenMyRectangleList = FXCollections.observableArrayList(createRectangleList(exeListsByStatus.getFrozenList()));
        frozenCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        frozenTV.setItems(frozenMyRectangleList);


        final ObservableList<MyRectangle> waitingMyRectangleList = FXCollections.observableArrayList(createRectangleList(exeListsByStatus.getWaitingList()));
        waitingCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        waitingTV.setItems(waitingMyRectangleList);


        final ObservableList<MyRectangle> inProcessMyRectangleList = FXCollections.observableArrayList(createRectangleList(exeListsByStatus.getInProcessList()));
        inProcessCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        inProcessTV.setItems(inProcessMyRectangleList);


        final ObservableList<MyRectangle> finishedMyRectangleList = FXCollections.observableArrayList(createRectangleList(exeListsByStatus.getFinishedList()));
        finishedCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        finishedTV.setItems(finishedMyRectangleList);


        final ObservableList<MyRectangle> skippedMyRectangleList = FXCollections.observableArrayList(createRectangleList(exeListsByStatus.getSkippedList()));
        skippedCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        skippedTV.setItems(skippedMyRectangleList);
    }

    public List<MyRectangle> createRectangleList(List<DtoTargetExeTableData> list){
        List<MyRectangle> res= new ArrayList<MyRectangle>();
        for(DtoTargetExeTableData curr:list){
            res.add(new MyRectangle(curr));
        }
        return res;
    }

    public void setTaskName(String taskName) {
        this.taskName=taskName;
        startTablesRefresher();
    }

/*
    public void initRunTable(){

        temp= new ArrayList<MyRectangle>();
        for(Target curr: mainTaskViewController.getCurTargetList()){
            temp.add(new MyRectangle(curr));
        }
        for(Target curr: mainTaskViewController.getCurTargetList()){
            switch(curr.getTargetStatus()) {
                case FROZEN: {
                    frozenList.add(new MyRectangle(curr));
                break;
                }
                case SKIPPED:
                {
                    skippedList.add(new MyRectangle(curr));
                    break;
                }
                case WAITING:
                {
                    waitingList.add(new MyRectangle(curr));
                    break;
                }
                case INPROCESS:
                {
                   inProcessList.add(new MyRectangle(curr));
                    break;
                }
                case FINISHED:
                {
                    finishedList.add(new MyRectangle(curr));
                break;
                }

                default:
                    // code block
            }

        }

        myRectangleList = FXCollections.observableArrayList(temp);
        FilteredList<MyRectangle> filteredFrozen = new FilteredList<MyRectangle>(FXCollections.observableArrayList(frozenList) ,t-> t.setAnimation(millisForAnimations));
        FilteredList<MyRectangle> filteredSkipped = new FilteredList<MyRectangle>(FXCollections.observableArrayList(skippedList) ,t-> t.setAnimation(millisForAnimations));
        FilteredList<MyRectangle> filteredWaiting = new FilteredList<MyRectangle>(FXCollections.observableArrayList(waitingList) ,t-> t.setAnimation(millisForAnimations));
        FilteredList<MyRectangle> filteredInProcess = new FilteredList<MyRectangle>(FXCollections.observableArrayList(inProcessList) ,t-> t.setAnimation(millisForAnimations));
        FilteredList<MyRectangle> filteredFinished = new FilteredList<MyRectangle>(FXCollections.observableArrayList(finishedList) ,t-> t.setAnimation(millisForAnimations));

        initFrozenRunTable(filteredFrozen);
        initSkippedRunTable(filteredSkipped);
        initWaitingRunTable(filteredWaiting);
        initInProcessRunTable(filteredInProcess);
        initFinishedRunTable(filteredFinished);



        for(MyRectangle currRectangle:temp){
            currRectangle.getStatusProperty().addListener((observable, oldValue, newValue) -> {
                synchronized (this) {
                    switch (oldValue) {
                        case "Frozen": {
                            boolean found = false;
                            frozenTV.setItems(FXCollections.observableArrayList(frozenList));
                            Iterator<MyRectangle> iter = frozenList.iterator();
                            while (iter.hasNext() && !found) {
                                MyRectangle test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.FROZEN)) {
                                    iter.remove();
                                    found = true;
                                }
                                if (found) {
                                    frozenTV.setItems(FXCollections.observableArrayList(frozenList));
                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.WAITING)) {
                                        waitingList.add(test);
                                        waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                                    } else if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                        skippedList.add(test);
                                        skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    }
                                }
                                test.setAnimation(millisForAnimations);
                            }

                        }
                        break;
                        case "Skipped": {
                            skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                            boolean found = false;
                            Iterator<MyRectangle> iter = skippedList.iterator();
                            while (iter.hasNext() && !found) {
                                MyRectangle test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                    iter.remove();
                                    skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.FROZEN)) {
                                        frozenList.add(test);
                                        frozenTV.setItems(FXCollections.observableArrayList(frozenList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.WAITING)){
                                        waitingList.add(test);
                                        waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.INPROCESS)){
                                        inProcessList.add(test);
                                        inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                                    }
                                    found = true;
                                }
                                    test.setAnimation(millisForAnimations);
                            }
                        }
                        break;
                        case "Waiting": {

                            boolean found = false;
                            waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                            MyRectangle test;
                            Iterator<MyRectangle> iter = waitingList.iterator();
                            while (iter.hasNext() && !found) {
                                test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.WAITING)) {
                                    iter.remove();
                                    waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.INPROCESS)) {
                                        inProcessList.add(test);
                                        inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                                    } else if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                        skippedList.add(test);
                                        skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    }
                                    found = true;
                                }
                                test.setAnimation(millisForAnimations);
                            }

                            break;
                        }
                        case "In Process": {
                            boolean found = false;
                            inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                            Iterator<MyRectangle> iter = inProcessList.iterator();
                            while (iter.hasNext() && !found) {
                                MyRectangle test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.INPROCESS)) {
                                    iter.remove();
                                    found = true;
                                }
                                if (found) {
                                    inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.FINISHED)) {
                                        finishedList.add(test);
                                        finishedTV.setItems(FXCollections.observableArrayList(finishedList));
                                        //finishedTV.refresh();
                                    } else if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                        skippedList.add(test);
                                        skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    }
                                }
                                test.setAnimation(millisForAnimations);
                            }

                            break;
                        }
                        case "Finished": {
                            boolean found = false;
                            finishedTV.setItems(FXCollections.observableArrayList(finishedList));
                            Iterator<MyRectangle> iter = finishedList.iterator();
                            while (iter.hasNext() && !found) {
                                MyRectangle test = (MyRectangle) iter.next();
                                if (!test.getTarget().getTargetStatus().equals(Target.TargetStatus.FINISHED)) {
                                    iter.remove();
                                    found = true;
                                }
                                if (found) {
                                    finishedTV.setItems(FXCollections.observableArrayList(finishedList));

                                    if (test.getTarget().getTargetStatus().equals(Target.TargetStatus.SKIPPED)) {
                                        skippedList.add(test);
                                        skippedTV.setItems(FXCollections.observableArrayList(skippedList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.FROZEN)){
                                        frozenList.add(test);
                                        frozenTV.setItems(FXCollections.observableArrayList(frozenList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.WAITING)){
                                        waitingList.add(test);
                                        waitingTV.setItems(FXCollections.observableArrayList(waitingList));
                                    }else if(test.getTarget().getTargetStatus().equals(Target.TargetStatus.INPROCESS)){
                                        inProcessList.add(test);
                                        inProcessTV.setItems(FXCollections.observableArrayList(inProcessList));
                                    }
                                }
                                test.setAnimation(millisForAnimations);
                            }
                            break;
                        }

                        default: {
                            break;
                        }
                    }
                    if(frozenList.isEmpty() && inProcessList.isEmpty() && waitingList.isEmpty()){
                        frozenTV.refresh();
                        skippedTV.refresh();
                        finishedTV.refresh();
                        inProcessTV.refresh();
                        waitingTV.refresh();
                    }

                }
            });
        }


    }


 */

    /*
    public void resetMe(){//TODO maybe clear lists
        frozenTV.refresh();
        skippedTV.refresh();
        finishedTV.refresh();
        inProcessTV.refresh();
        waitingTV.refresh();
        resetLoadedTask();
    }

     */
/*
    private void initFrozenRunTable(FilteredList<MyRectangle> filteredFrozen){
        frozenCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        frozenTV.setItems(filteredFrozen);
    }

    private void initSkippedRunTable(FilteredList<MyRectangle> filteredSkipped){
        skippedCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        skippedTV.setItems(filteredSkipped);

    }

    private void initWaitingRunTable(FilteredList<MyRectangle> filteredWaiting){
        waitingCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        waitingTV.setItems(filteredWaiting);

    }

    private void initInProcessRunTable(FilteredList<MyRectangle> filteredInProcess){
        inProcessCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        inProcessTV.setItems(filteredInProcess);

    }

    private void initFinishedRunTable(FilteredList<MyRectangle> filteredFinished){
        finishedCol.setCellValueFactory(
                new PropertyValueFactory<>("myRectangle")
        );
        finishedTV.setItems(filteredFinished);

    }

 */

/*
    public void setParent(MainTaskViewController mainTaskViewController) {
        this.mainTaskViewController=mainTaskViewController;
    }


 */
    /*
    public void resetLoadedTask() {
        frozenList.clear();
        frozenTV.setItems(FXCollections.observableArrayList(frozenList));
        skippedList.clear();
        skippedTV.setItems(FXCollections.observableArrayList(frozenList));
        finishedList.clear();
        finishedTV.setItems(FXCollections.observableArrayList(frozenList));
        inProcessList.clear();
        inProcessTV.setItems(FXCollections.observableArrayList(frozenList));
        waitingList.clear();
        waitingTV.setItems(FXCollections.observableArrayList(frozenList));
    }

    public void animationsOff() {
        millisForAnimations = 1;
    }

    public void animationsOn() {
        millisForAnimations = 2000;
    }

     */
}
