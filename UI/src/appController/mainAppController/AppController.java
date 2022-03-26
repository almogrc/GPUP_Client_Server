package appController.mainAppController;
import appController.dataView.DataViewController;
import appController.taskView.mainTaskView.MainTaskViewController;
import component.dashboard.DashboardController;
import engine.dto.DTOTarget;
import engine.dto.DTOTargetsGraph;
import engine.dto.Dto;
import engine.engineGpup.DetailsForTask;
import engine.engineGpup.GpupExecution;
import engine.graph.SerialSet;
import engine.graph.WrapsTarget;

import javafx.animation.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

public class AppController {

    @FXML private Button xmlFileLoaderButton;
    @FXML private ImageView image;
    @FXML private Button showTargetsDataButton;
    @FXML private Button runTaskButton;
    @FXML private BorderPane MainBorderPane;
    @FXML private ScrollPane mainScrollBar;

    private SimpleBooleanProperty isFileSelected;

    private Stage primaryStage;
    private GpupExecution gpupExecution;
    private DataViewController dataViewController;
    private MainTaskViewController mainTaskViewController;
    private BorderPane mainBorderPane;
    private DashboardController returnDashboardGridPane;
    private DTOTargetsGraph dtoTargetGraph;
    private Scene dashboardScene;
    private String userName;
    // private TempController tempController;

    public AppController(){
        this.isFileSelected = new SimpleBooleanProperty(false);

    }
    @FXML void backToDashboardButtonHandle(){//todo
        primaryStage.close();
        if(dashboardScene == null) {
            dashboardScene = new Scene(returnDashboardGridPane.getRoot(), 1044, 654);
        }
        primaryStage.setScene(dashboardScene);
        primaryStage.show();
        //MainBorderPane.setCenter(returnDashboardGridPane.getRoot());
    }

    @FXML private void initialize() throws IOException {
       animationsSetter();
        FXMLLoader loader= new FXMLLoader();
        URL dataViewFXML = getClass().getResource("/appController/dataView/targetsData.fxml");
        loader.setLocation(dataViewFXML);
        loader.load();
        DataViewController dataViewController = loader.getController();

        setDataViewController(dataViewController);
        dataViewController.setAppController(this);

        loader= new FXMLLoader();
        URL taskViewFXML = getClass().getResource("/appController/taskView/mainTaskView/mainTaskView.fxml");
        loader.setLocation(taskViewFXML);
        loader.load();
        MainTaskViewController mainTaskViewController = loader.getController();

        setTaskViewController(mainTaskViewController);
        mainTaskViewController.setAppController(this);


        //mainScrollBar.setFitToHeight(true);
       //mainScrollBar.setFitToWidth(true);
    }

    public void animationsSetter() {
        RotateTransition rotateTransition =new RotateTransition();
        rotateTransition.setNode(image);
        rotateTransition.setDuration(Duration.millis(2500));
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.setByAngle(720);
        rotateTransition.play();
        FadeTransition fadeTransition=new FadeTransition();
        fadeTransition.setNode(image);
        fadeTransition.setDuration(Duration.millis(2500));
        fadeTransition.setInterpolator(Interpolator.LINEAR);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    /*
    @FXML void AnimationsOff(ActionEvent event) {
        mainTaskViewController.animationsOff();
    }

    @FXML void animationsOn(ActionEvent event) {
        mainTaskViewController.animationsOn();
    }

     */

    public BorderPane getRoot(){ return MainBorderPane; }

    /*
    @FXML
    public void handleLoadXMLClicked(ActionEvent actionEvent) throws JAXBException, FileNotFoundException {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML file","*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        isFileSelected.set(true);
        try {
            gpupExecution.initGpupExecution(selectedFile);
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File error");
            alert.setContentText(e.getMessage());
            alert.show();
        }

        //MainBorderPane.setCenter(tempController.getRoot());
        dataViewController.resetUIData();
        mainTaskViewController.resetTaskData();

    }

     */

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    public void setGpupExecution(GpupExecution gpupExecution) {
        this.gpupExecution=gpupExecution;
    }

    @FXML public void clickSTDButtonListener(ActionEvent event) {

        MainBorderPane.setCenter(dataViewController.getRoot());
        dataViewController.resetUIData();
        dataViewController.updateUIData();

    }

    @FXML void handleRunTaskClicked(ActionEvent event) {
        MainBorderPane.setCenter(mainTaskViewController.getRoot());

        //mainTaskViewController.initTreeView();
        mainTaskViewController.initMainTask();
    }

    public void setDataViewController(DataViewController dataViewController) { this.dataViewController=dataViewController; }

    public List<DTOTarget> getDTOTargetList(){return dtoTargetGraph.getListDTOTarget();}

    public DTOTargetsGraph getDtoTargetGraph(){return dtoTargetGraph;}

    public List<String> getTargetNamesList(){
        return dtoTargetGraph.getTargetNamesList();
    }

    public int getNumOfTargets(){ return dtoTargetGraph.getListDTOTarget().size();}

    public int getNumOfIndependent(){ return dtoTargetGraph.getNumOfIndependent();}

    public int getNumOfLeaf(){return dtoTargetGraph.getNumOfLeaf();}

    public int getNumOfMiddle(){return dtoTargetGraph.getNumOfMiddle();}

    public int getNumOfRoot(){return dtoTargetGraph.getNumOfRoot();}

    public WrapsTarget getWrapsTargetByName(String name){return gpupExecution.getWrapsTargetByName(name);}

    public List<SerialSet> getListOfSerialSet(){return gpupExecution.getListOfSerialSet(); }

    public Dto getCircles(String targetName) {
       return gpupExecution.getCircleOfTarget(targetName);
    }

    public Dto getPaths(String from, String to, boolean required) {
        return gpupExecution.getAllPathsBetweenTargets(from,to,required);
    }


    public void setTaskViewController(MainTaskViewController mainTaskViewController) {
        this.mainTaskViewController=mainTaskViewController;
    }


    public List<WrapsTarget> getWrapsTargetList(){return gpupExecution.getWrapsTargetList();}

    /*
    public int getNumOfThreads() {
        return gpupExecution.getNumOfThreads();
    }

     */

    /*public void runTask(DetailsForTask newDetail,
                        Consumer<String>successSummary,
                        Consumer<String>successWWSummary,
                        Consumer<String>failureSummary,
                        Consumer<String>skippedSummary,
                        Consumer<String>summary,
                        Consumer<String> logData,
                        Runnable onFinish) {
        gpupExecution.runTask(newDetail,onFinish);
        //gpupExecution.runTask(newDetail,successSummary,successWWSummary,failureSummary,skippedSummary,summary,logData,onFinish);
    }

     */

    /*
    public void bindTaskToUIComponents(Task<Boolean> myRun,Runnable onFinish) {
        mainTaskViewController.bindTaskToMainTaskViewControllerComponents(myRun, onFinish);
    }

     */
    @FXML
    void handleDefaultCSSChoice(ActionEvent event) {
        primaryStage.getScene().getStylesheets().removeAll(this.getClass().getResource("Modern.css").toExternalForm(),this.getClass().getResource("Sea.css").toExternalForm());
    }

    @FXML
    void handleModernCSSChoice(ActionEvent event) {
        primaryStage.getScene().getStylesheets().remove(this.getClass().getResource("Sea.css").toExternalForm());
        primaryStage.getScene().getStylesheets().add(this.getClass().getResource("Modern.css").toExternalForm());
    }

    @FXML
    void handleSeaCSSChoice(ActionEvent event) {
        primaryStage.getScene().getStylesheets().remove(this.getClass().getResource("Modern.css").toExternalForm());
        primaryStage.getScene().getStylesheets().add(this.getClass().getResource("Sea.css").toExternalForm());
        primaryStage.getScene().fillProperty().setValue(Color.rgb(73,173,154,1));


    }

    public void handlePause(boolean isStopTask) {
        gpupExecution.stopTask(isStopTask);
    }

    /*
    public void setNumOfThreads(int numOfThreads) {
        gpupExecution.setNumOfThreads(numOfThreads);
    }

     */

    public void getGraphViz(String outPutPath, String filesNames) {
        gpupExecution.getGraphUsingGraphViz(outPutPath, filesNames);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane=mainBorderPane;
    }

    public void setReturnPage(DashboardController dashboardGridPane) {
        this.returnDashboardGridPane=dashboardGridPane;
    }


    public void setTargetsData(DTOTargetsGraph dtoTargetGraph) {
        this.dtoTargetGraph=dtoTargetGraph;
    }

    public String getGraphName(){
        return dtoTargetGraph.getGraphName();
    }


    public int getPriceSimulation() {
        return dtoTargetGraph.getSimulationPrice();
    }

    public int getPriceCompilation() {
        return dtoTargetGraph.getCompilationPrice();
    }

    public String getUserNameUploaded() {
        return dtoTargetGraph.getXMLUploadedUserName();
    }

    public void setScene(Scene scene) {
        this.dashboardScene =scene;
    }

    public void setUserName(String name) {
        this.userName=name;
    }

    public String getUserName(){return userName;}

}
