package appController.taskView.mainTaskView;

import appController.mainAppController.AppController;
import appController.taskView.subcomponents.taskPreference.CompileTaskPrefController;
import appController.taskView.subcomponents.taskPreference.SimulationTaskPrefController;
import appController.taskView.subcomponents.upperTask.TargetChooserUpperTaskController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import engine.dto.DTOTarget;
import engine.dto.DtoCompilationTask;
import engine.dto.DtoNewTask;
import engine.dto.DtoSimulationTask;
import engine.engineGpup.DetailsForTask;
import engine.graph.Target;
import engine.graph.WrapsTarget;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static util.Constants.*;

public class MainTaskViewController {
    @FXML
    private GridPane mainGridPane;
    @FXML
    private GridPane WhatIfUpperTaskGridPane;
    @FXML
    private TargetChooserUpperTaskController WhatIfUpperTaskGridPaneController;
    @FXML
    private TableView<WrapsTarget> PreTaskTableView;
    @FXML
    private TableColumn<WrapsTarget, String> NameColumnPre;
    @FXML
    private TableColumn<WrapsTarget, CheckBox> CheckBoxColumnPre;
    @FXML
    private TableColumn<WrapsTarget, String> LocationColumnPre;
    @FXML
    private TableColumn<WrapsTarget, String> DataColumnPre;
    @FXML
    private Label errorTypeTaskOrNoTargetsOrNoThreadsLabel;
    @FXML
    private Button ProceedButton;
    @FXML
    private ChoiceBox<String> typeOfTaskChoiceBox;
    @FXML
    private BorderPane PrefTaskBorderPane;
    @FXML
    private Label ErrorPrefLabel;
    @FXML
    private Button LoadButton;
    @FXML
    private Button clearLoadedButton;
    @FXML
    private TextField executionNameTextField;

    private AppController appController;
    private CompileTaskPrefController compileTaskPrefController;
    private SimulationTaskPrefController simulationTaskPrefController;
    //private DetailsForTask newDetail;
    private SimpleStringProperty errorTypeTaskOrNoTargetsOrNoThreads;
    private SimpleStringProperty errorPref;
    private SimpleBooleanProperty isFinished;
    //private String TaskName;
    private List<WrapsTarget> wrapTargetList;

    public MainTaskViewController() {
        errorTypeTaskOrNoTargetsOrNoThreads = new SimpleStringProperty("");
        errorPref = new SimpleStringProperty("");
        isFinished = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() throws IOException {
        if (WhatIfUpperTaskGridPaneController != null) {
            WhatIfUpperTaskGridPaneController.setParent(this);
        }
        StringExpression sb = Bindings.concat("", errorTypeTaskOrNoTargetsOrNoThreads);
        errorTypeTaskOrNoTargetsOrNoThreadsLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorPref);
        ErrorPrefLabel.textProperty().bind(sb);


        FXMLLoader loader = new FXMLLoader();
        URL simulationFXML = getClass().getResource("/appController/taskView/subcomponents/taskPreference/simulationTaskPref.fxml");
        loader.setLocation(simulationFXML);
        loader.load();
        simulationTaskPrefController = loader.getController();
        loader = new FXMLLoader();
        URL compileFXML = getClass().getResource("/appController/taskView/subcomponents/taskPreference/compileTaskPref.fxml");
        loader.setLocation(compileFXML);
        loader.load();
        compileTaskPrefController = loader.getController();
        LoadButton.setDisable(true);
    }


    @FXML
    private void handleProceedButtonPressed() {
        if (!someTargetIsChosen()) {
            errorTypeTaskOrNoTargetsOrNoThreads.setValue("Please choose targets from the table above to process.");
        } else if (typeOfTaskChoiceBox.getValue() == null || typeOfTaskChoiceBox.getValue().equals("--Task Type--")) {
            errorTypeTaskOrNoTargetsOrNoThreads.setValue("Please choose type of task to continue.");
        } else {
            errorTypeTaskOrNoTargetsOrNoThreads.setValue("");
            LoadButton.setDisable(false);
            if (typeOfTaskChoiceBox.getValue().equals("Simulation")) {
                PrefTaskBorderPane.setCenter(simulationTaskPrefController.getRoot());
            } else if (typeOfTaskChoiceBox.getValue().equals("Compilation")) {
                PrefTaskBorderPane.setCenter(compileTaskPrefController.getRoot());
            }
            typeOfTaskChoiceBox.setDisable(true);
        }
    }


    @FXML
    private void initLoadedData() {
        //resetLoadedTask();
        boolean isIncremental = false;
        int numOfThreads;
        boolean isSimulation;

        if (!someTargetIsChosen()) {
            errorPref.setValue("Please choose targets from the table above to process.");
            return;
        }
        if (executionNameTextField.getText().isEmpty() || executionNameTextField.getText() == null) {
            errorPref.setValue("Please choose unique name for task");
            return;
        }
        if (typeOfTaskChoiceBox.getValue().equals("Simulation")) {
            isSimulation = true;
            boolean isRandom = simulationTaskPrefController.getIsRandom();
            String processTime = simulationTaskPrefController.getProcessTime();
            String probSuccess = simulationTaskPrefController.getProbSuccessTextFiled();
            String probSuccessWWarnings = simulationTaskPrefController.getProbSuccessWWarningsTextFiled();
            Double probSuccessWWarningsDouble;
            Integer processTimeInteger;
            Double probSuccessDouble;
            try {
                processTimeInteger = Integer.parseInt(processTime);
            } catch (NumberFormatException e) {
                errorPref.setValue("Please enter an integer for process time.");
                return;
            }
            try {
                probSuccessDouble = Double.parseDouble(probSuccess);
                if (probSuccessDouble > 1.0 || probSuccessDouble < 0.0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errorPref.setValue("Please enter a number between 0 to 1 for probability for success.");
                return;
            }
            try {
                probSuccessWWarningsDouble = Double.parseDouble(probSuccessWWarnings);
                if (probSuccessWWarningsDouble > 1.0 || probSuccessWWarningsDouble < 0.0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errorPref.setValue("Please enter a number between 0 to 1 for probability for success with warnings.");
                return;
            }
            errorPref.setValue("");
            DtoNewTask newTask = new DtoNewTask(executionNameTextField.getText(), getChoosenNamesTargets(), appController.getUserName(), new DtoSimulationTask(isRandom, processTimeInteger, probSuccessDouble, probSuccessWWarningsDouble));
            sentRequestForNewTask(newTask);
            return;
        } else if (typeOfTaskChoiceBox.getValue().equals("Compilation")) {//todo
            isSimulation = false;
            PrefTaskBorderPane.setCenter(compileTaskPrefController.getRoot());
            File directoryToCompile = compileTaskPrefController.getDirectoryToCompile();
            File directoryForCompiled = compileTaskPrefController.getDirectoryForCompiled();
            if (directoryToCompile == null) {
                errorPref.setValue("Directory to compile codes from not available.");
                return;
            }
            if (directoryForCompiled == null) {
                errorPref.setValue("Directory for compiled codes not available.");
                return;
            }
            errorPref.setValue("");
            clearLoadedButton.setText("Back & Clear");
            compileTaskPrefController.deactivateMe();
            DtoNewTask newTask = new DtoNewTask(executionNameTextField.getText(), getChoosenNamesTargets(), appController.getUserName(), new DtoCompilationTask(directoryToCompile.getAbsolutePath(), directoryForCompiled.getAbsolutePath()));
            sentRequestForNewTask(newTask);
            return;
        }
    }

    private void sentRequestForNewTask(DtoNewTask newTask) {
        newTask.setGraphName(appController.getGraphName());
        String newTaskJson = GSON_INSTANCE.toJson(newTask, new TypeToken<DtoNewTask>(){}.getType());
        System.out.println(newTaskJson);
        String finalUrl = HttpUrl
                .parse(UPLOAD_NEW_TASK)
                .newBuilder()
                .addQueryParameter(NEW_TASK, newTaskJson)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("404");
                            alert.setContentText("Something went wrong: " + e.getMessage());
                            alert.show();
                            System.out.println("Something went wrong: " + e.getMessage() );
                        }
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.code() == 200) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Task loaded successfully!", ButtonType.OK);
                            alert.setTitle("Success");
                            alert.show();
                        });
                }else {
                    /*
                    if(response.code() == 193){
                        System.out.println("193");
                    }
                    if(response.code() == 194){
                        System.out.println("194");
                    }
                    if(response.code() == 195){
                        System.out.println("195");
                    }
                    if(response.code() == 196){
                        System.out.println("196");
                    }

                     */

                    Platform.runLater(() -> {
                        try {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle(String.valueOf(response.code()));
                            alert.setContentText(response.body().string());
                            alert.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                               // System.out.println("something went wrong:  "+ response.toString());
                            }
                    );
                    System.out.println("response wasnt 200");
                }
            }
        });
    }

    private List<String> getChoosenNamesTargets() {
        List<String> res = new ArrayList<String>();
        for (WrapsTarget curr : wrapTargetList) {
            if (curr.getCheckbox().isSelected()) {
                res.add(curr.getName());
            }
        }
        return res;
    }


    @FXML
    private void handleClearLoadedButton() {
        if (clearLoadedButton.getText().equals("Back & Clear")) {
            clearLoadedButton.setText("Clear");
            WhatIfUpperTaskGridPaneController.activateMe();
            for (WrapsTarget wrapsTarget : getWrapsTargetList()) {
                wrapsTarget.getCheckbox().setDisable(false);
            }
        }
        if (typeOfTaskChoiceBox.getValue() != null && typeOfTaskChoiceBox.getValue().equals("Compilation")) {
            compileTaskPrefController.activateMe();
        }

        LoadButton.setDisable(true);
        resetChoices();
        ProceedButton.setDisable(false);
        typeOfTaskChoiceBox.setDisable(false);
        errorTypeTaskOrNoTargetsOrNoThreads.setValue("");

    }

    public GridPane getRoot() {
        return mainGridPane;
    }

    public void initMainTask() {
        activeMe();
        initWhatIfUpperTaskGridPane();
        initPreTaskTable();
        initTaskPref();
        resetChoices();

    }

    private void activeMe() {
        WhatIfUpperTaskGridPaneController.activateMe();
        typeOfTaskChoiceBox.setDisable(false);
        ProceedButton.setDisable(false);
        LoadButton.setDisable(true);

    }

    private void initTaskPref() {//todo - let only type of task with price
        ObservableList<String> wayToRunOptions = FXCollections.observableArrayList("From Scratch", "Incremental");

        ObservableList<String> taskOptions = FXCollections.observableArrayList();
        if (appController.getDtoTargetGraph().getCompilationPrice() != NOT_EXIST) {
            taskOptions.add("Compilation");
        }
        if (appController.getDtoTargetGraph().getSimulationPrice() != NOT_EXIST) {
            taskOptions.add("Simulation");
        }
        typeOfTaskChoiceBox.setItems(taskOptions);
        typeOfTaskChoiceBox.setValue("--Task Type--");
    }

    private void initPreTaskTable() {//TODO to change the wrapsTarget to targetdto
        wrapTargetList = new ArrayList<WrapsTarget>();
        for (DTOTarget curr : appController.getDTOTargetList()) {
            wrapTargetList.add(new WrapsTarget(curr));
        }
        final ObservableList<WrapsTarget> data = FXCollections.observableArrayList(wrapTargetList);

        NameColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        CheckBoxColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("checkbox")
        );
        LocationColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("location")
        );
        DataColumnPre.setCellValueFactory(
                new PropertyValueFactory<>("data")
        );
        PreTaskTableView.setItems(data);
    }

    public void initWhatIfUpperTaskGridPane() {
        WhatIfUpperTaskGridPaneController.initMe();
    }

    public void setAppController(AppController appController) {
        this.appController = appController;

    }


    public void resetChoices() {
        if (typeOfTaskChoiceBox.getValue() != null && typeOfTaskChoiceBox.getValue().equals("Simulation")) {
            simulationTaskPrefController.resetMe();
        } else if (typeOfTaskChoiceBox.getValue() != null && typeOfTaskChoiceBox.getValue().equals("Compilation")) {
            compileTaskPrefController.resetMe();
        }
        executionNameTextField.clear();

        PrefTaskBorderPane.setCenter(null);
    }

    private void resetPreTaskTable() {
        PreTaskTableView.setItems(null);
    }

    public void resetTaskData() {
        ObservableList<String> dataTaskKind = FXCollections.observableArrayList("--Task Type--");
        WhatIfUpperTaskGridPaneController.resetMe();
        PreTaskTableView.setItems(null);
        resetChoices();
        typeOfTaskChoiceBox.setItems(dataTaskKind);
        LoadButton.setDisable(true);
    }

    public void refreshMyTable() {
        resetPreTaskTable();
        initPreTaskTable();
    }


    public List<String> getTargetNamesList() {
        return appController.getTargetNamesList();
    }

    public List<WrapsTarget> getWrapsTargetList() {
        return wrapTargetList;
    }



    private boolean someTargetIsChosen() {
        for (WrapsTarget wrapsTarget : wrapTargetList) {
            if (wrapsTarget.getCheckbox().isSelected()) {
                return true;
            }
        }
        return false;
    }


    public AppController getAppController() {
        return appController;
    }
}
/*
    private boolean chosenTargetsCanRunIncremental() {//TODO
        List<WrapsTarget> allList = appController.getWrapsTargetList();
        List<WrapsTarget> curList = new ArrayList<WrapsTarget>();

        for (WrapsTarget wrapsTarget : allList) {
            if (wrapsTarget.getCheckbox().isSelected()) {
                curList.add(wrapsTarget);
            }
        }
        for (WrapsTarget wrapsTarget : curList) {
            if (wrapsTarget.getDTOTarget().getTargetStatus().equals(Target.TargetStatus.FROZEN)) {
                return false;
            }
        }
        return true;
    }

 */

    /*@FXML private void handleApplyThreads(){
       if(changeNumberOfThreadsChoiceBox.getValue() != null && !changeNumberOfThreadsChoiceBox.getValue().equals(0)){
           appController.setNumOfThreads(changeNumberOfThreadsChoiceBox.getValue());
       }
    }

     */
/*    public List<DTOTarget> getCurTargetList() {
        List<WrapsTarget> curList = appController.getWrapsTargetList();
        List<DTOTarget> targetList = new ArrayList<DTOTarget>();
        for(WrapsTarget wrapsTarget : curList){
            if(wrapsTarget.getCheckbox().isSelected()){
                targetList.add(wrapsTarget.getDTOTarget());
            }
        }
        return targetList;
    }

    /*@FXML private void handleGetDataButton(){
        status.setValue("");
        if(liveDataOnTargetChoiceBox.getValue()== null || liveDataOnTargetChoiceBox.getValue().equals("--Target Name--")){
            errorGetData.setValue("Please choose a target to continue.");
        }else {
            errorGetData.setValue("");
            showDataInRun(liveDataOnTargetChoiceBox.getValue());

            //serialSets.setValue("Serial sets with target: "+ chosenTarget.getListOfNamesSets());
        }
    }

     */
/*
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
                            status.setValue(response.body().toString());
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

 */
    /*
    public void animationsOff() {
        runTaskTableViewGridPaneController.animationsOff();
    }

    public void animationsOn(){
        runTaskTableViewGridPaneController.animationsOn();
    }

 */

     /* @FXML public void handlePause(){
        if(StopTaskButton.getText().equals("Pause")){
            StopTaskButton.setText("Resume");
            //changeThreadNumberButton.setDisable(false);
            //changeNumberOfThreadsChoiceBox.setDisable(false);
            appController.handlePause(true);
        }else if(StopTaskButton.getText().equals("Resume")){
            //StopTaskButton.setText("Pause");
            //changeThreadNumberButton.setDisable(true);
            //changeNumberOfThreadsChoiceBox.setDisable(true);
            appController.handlePause(false);
        }

    }

    */
     /*public void resetLogs(){
        logTaskTextArea.clear();
    }

     */
    /*
    public void resetTreeView(){
        treeViewBottomUp.setRoot(null);
        treeViewTopDown.setRoot(null);
    }

 */
      /*
    public void initTreeView() {
        initTreeViewTopDown();
        initTreeViewBottomUp();
    }


    private void initTreeViewTopDown(){
        TreeItem<String> rootItem = new TreeItem<>("Top-Down Tree View");
        for(Target target:appController.getTargetList()) {
            if(target.getLocation().equals(Target.Location.ROOT) ||target.getLocation().equals(Target.Location.INDEPENDENT)) {
                TreeItem<String> newRoot = new TreeItem<>(target.getName()+ " - " +target.getTargetStatusValue());
                rootItem.getChildren().add(initTreeViewTopDownRec(newRoot));
            }
        }
        treeViewTopDown.setRoot(rootItem);
    }

    public void initTreeViewBottomUp() {
        TreeItem<String> rootItem = new TreeItem<>("Bottom-Up Tree View");
        for(Target target:appController.getTargetList()) {
            if(target.getLocation().equals(Target.Location.LEAF)) {
                TreeItem<String> newRoot = new TreeItem<>(target.getName()+ " - " +target.getTargetStatusValue());
                rootItem.getChildren().add(initTreeViewBottomUpRec(newRoot));
            }
        }
        treeViewBottomUp.setRoot(rootItem);
    }

     */
     /*
    private TreeItem<String> initTreeViewTopDownRec(TreeItem<String> rootItem){
        Target curRoot = null;
        for(Target target:appController.getTargetList()){
            if(rootItem.getValue().equals(target.getName()+ " - "+target.getTargetStatusValue())){
                curRoot = target;
                if(curRoot.getDependingOn().size() == 0)
                {
                    return new TreeItem<>(curRoot.getName() + " - " + curRoot.getTargetStatusValue());
                }else {
                    for (Target curTarget : curRoot.getDependingOn()) {
                        TreeItem<String> curTargetItem = new TreeItem<>(curTarget.getName() + " - " + curTarget.getTargetStatusValue());
                        rootItem.getChildren().add(initTreeViewTopDownRec(curTargetItem));
                    }
                }
                break;
            }
        }
        return rootItem;

    }

    private TreeItem<String> initTreeViewBottomUpRec(TreeItem<String> rootItem) {
        Target curRoot = null;
        for(Target target:appController.getTargetList()){
            if(rootItem.getValue().equals(target.getName()+ " - "+target.getTargetStatusValue())){
                curRoot = target;
                if(curRoot.getRequiredTo().size() == 0)
                {
                    return new TreeItem<>(curRoot.getName() + " - " + curRoot.getTargetStatusValue());
                }else {
                    for (Target curTarget : curRoot.getRequiredTo()) {
                        TreeItem<String> curTargetItem = new TreeItem<>(curTarget.getName() + " - " + curTarget.getTargetStatusValue());
                        rootItem.getChildren().add(initTreeViewBottomUpRec(curTargetItem));
                    }
                }
                break;
            }
        }
        return rootItem;
    }

     */



/*
    private void runTask(DetailsForTask newDetail) {

        appController.runTask(newDetail,
                successTask::set,
                successWithWarnTask::set,
                failedTask::set,
                skippedTask::set,
                summaryTitle::set,
                logTask::set,
                ()->{
                    isFinished.set(true);
                });
        logTask.addListener((observable, oldValue, newValue) -> {
            logTaskTextArea.appendText(newValue+"\n");
        });

    }

 */
    /*
    private void initGetLiveData(){//TODO reset values
        liveDataOnTargetChoiceBox.setDisable(false);
        GetDataButton.setDisable(false);
        // liveDataOnTargetChoiceBox.setItems(null);
        liveDataOnTargetChoiceBox.setValue("--Target Name--");
        ObservableList<String> TargetsOptions  = FXCollections.observableArrayList(chosenTargetNames());
        liveDataOnTargetChoiceBox.setItems(TargetsOptions);
        getDataTitle.setValue("Get Live data on target:");
    }

 */
    /*
    private void resetLoadedTask(){
        runTaskTableViewGridPaneController.resetLoadedTask();

    }

 */
/*
    public void bindTaskToMainTaskViewControllerComponents(Task<Boolean> myRun, Runnable onFinish) {
        //taskMassage
        taskMessageLabel.textProperty().bind(myRun.messageProperty());

        // task progress bar
        taskProgressBar.progressProperty().bind(myRun.progressProperty());


        // task percent label
        progressPercentLabel.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        myRun.progressProperty(),
                                        100)),
                        " %"));

        // task cleanup upon finish
        //myRun.valueProperty().addListener((observable, oldValue, newValue) -> {
        //  onTaskFinished(Optional.ofNullable());
        //});

        myRun.valueProperty().addListener((observable,oldValue,newValue)->{
            onTaskFinished(Optional.ofNullable(onFinish));
        });
    }

 */
/*
    private <T> void onTaskFinished(Optional<T> onFinish) {
        GetDataButton.setDisable(true);
        liveDataOnTargetChoiceBox.setDisable(true);
        this.taskMessageLabel.textProperty().unbind();
        this.progressPercentLabel.textProperty().unbind();
        this.taskProgressBar.progressProperty().unbind();
        LoadButton.setDisable(true);
        clearLoadedButton.setDisable(false);
       // treeViewTopDown.setRoot(null);
       // treeViewBottomUp.setRoot(null);
        //initTreeView();
        PreTaskTableView.refresh();
        RunTaskButton.setDisable(true);
        StopTaskButton.setDisable(true);
        StopTaskButton.setText("Pause");
        //changeThreadNumberButton.setDisable(true);
        //changeNumberOfThreadsChoiceBox.setDisable(true);
        if(typeOfTaskChoiceBox.getValue()!=null&& typeOfTaskChoiceBox.getValue().equals("Compilation")){
            compileTaskPrefController.activateMe();
        }

    }

 */

/*
    @FXML private void handleRunTask(){
        errorPref.set("");
        blockNonTaskComp();
        LoadButton.setDisable(true);
        clearLoadedButton.setDisable(true);
        if(typeOfTaskChoiceBox.getValue()!=null&& typeOfTaskChoiceBox.getValue().equals("Compilation")){
            compileTaskPrefController.deactivateMe();
        }

    }

 */
    /*
@FXML
public void selectItemBottomUp() {//TODO

}

    @FXML
    public void selectItemTopDown() {

    }

     */
