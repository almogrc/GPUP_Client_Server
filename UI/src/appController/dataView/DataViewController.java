package appController.dataView;

import appController.mainAppController.AppController;
import com.google.gson.Gson;
import component.mainTask.MainTaskController;
import engine.dto.DTOTarget;
import engine.dto.DtoExeGpup;
import engine.graph.SerialSet;
import engine.graph.Target;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static util.Constants.*;

public class DataViewController {
    @FXML private GridPane MainScrollPaneData;
    @FXML private VBox SummaryDataVbox;
    @FXML private Label TotalTargetsLabel;
    @FXML private Label TotalRootsLabel;
    @FXML private Label TotalMidLabel;
    @FXML private Label TotalLeafLabel;
    @FXML private Label TotalIndepLabel;
    @FXML private Tab pathLocatorTab;
    @FXML private ChoiceBox<String> pathFromTargetChoice;
    @FXML private ChoiceBox<String> pathToTargetChoice;
    @FXML private ChoiceBox<String> pathRatioChoice;
    @FXML private ListView<String> PathList;
    @FXML private Button pathButtonStart;
    @FXML private ChoiceBox<String> WhatIfTargetChoice;
    @FXML private ChoiceBox<String> WhatIfRatioChoice;
    @FXML private ListView<String> ListWhatIf;
    @FXML private Button WhatIfStartButton;
    @FXML private ChoiceBox<String> CircleTargetChoice;
    @FXML private ListView<String> CircleList;
    @FXML private Button CircleStartButton;
    @FXML private TableView<DTOTarget> TargetsDataTable;
    @FXML private TableColumn<DTOTarget, String> TargetNameColumn;
    @FXML private TableColumn<DTOTarget, String> TargetLocationColumn;
    @FXML private TableColumn<DTOTarget, String> TargetDataColumn;
    @FXML private TableColumn<DTOTarget, ?> ReqColumn;
    @FXML private TableColumn<DTOTarget, String> ReqDirColumn;
    @FXML private TableColumn<DTOTarget, String> ReqIndirColumn;
    @FXML private TableColumn<DTOTarget, ?> DepColumn;
    @FXML private TableColumn<DTOTarget, String> DepDirColumn;
    @FXML private TableColumn<DTOTarget, String> DepIndirColumn;
    @FXML private TableColumn<DTOTarget, String> SerialSetsTargetColumn;
    //@FXML private TableView<SerialSet> SerialSetsTable;
    @FXML private TableColumn<SerialSet, String> SerialSetNameColumn;
    @FXML private TableColumn<SerialSet, List<String>> TISColumn;
    @FXML private Label errorPathLabel;
    @FXML private Label errorWhatIfLabel;
    @FXML private Label errorCircleLabel;
    @FXML private GridPane upperGridPaneData;
    @FXML private Button showGraphVizButton;
    @FXML private Button selectPathButton;
    @FXML private TextField insertFileNameTextField;
    //@FXML private Label serialSetMainLabel;

    @FXML private Label PriceSimulationLabel;
    @FXML private Label PriceCompilationLabel;
    @FXML private Label graphNameLabel;
    @FXML private Label UserNameUploadedLabel;

    private SimpleIntegerProperty totalTargets;
    private SimpleIntegerProperty totalRoots;
    private SimpleIntegerProperty totalIndep;
    private SimpleStringProperty graphName;
    private SimpleIntegerProperty totalMidd;
    private SimpleIntegerProperty totalLeaves;
    private SimpleStringProperty errorCircle;
    private SimpleStringProperty errorPath;
    private SimpleStringProperty errorWhatIf;
    private AppController appController;
    private SimpleBooleanProperty isPathSelected;
    private File selectedPathForGraphViz;
    private ImageView imageView;

    private SimpleIntegerProperty priceSimulation;
    private SimpleIntegerProperty priceCompilation;
    private SimpleStringProperty userNameUploaded;


    public DataViewController(){
        totalTargets = new SimpleIntegerProperty(0);
        totalRoots = new SimpleIntegerProperty(0);
        totalIndep = new SimpleIntegerProperty(0);
        totalMidd = new SimpleIntegerProperty(0);
        totalLeaves = new SimpleIntegerProperty(0);
        priceSimulation = new SimpleIntegerProperty(0);
        priceCompilation = new SimpleIntegerProperty(0);
        userNameUploaded = new SimpleStringProperty("");
        graphName=new SimpleStringProperty("");
        errorCircle  = new SimpleStringProperty("");
        errorPath = new SimpleStringProperty("");
        errorWhatIf = new SimpleStringProperty("");
        this.isPathSelected = new SimpleBooleanProperty(false);


    }
    @FXML
    private void initialize(){
        StringExpression sb = Bindings.concat("Total Number Of Targets: ", totalTargets);
        TotalTargetsLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Roots: ", totalRoots);
        TotalRootsLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Middles: ", totalMidd);
        TotalMidLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Leaves: ", totalLeaves);
        TotalLeafLabel.textProperty().bind(sb);
        sb = Bindings.concat("Number Of Independents: ", totalIndep);
        TotalIndepLabel.textProperty().bind(sb);
        sb = Bindings.concat("Graph's Name: ", graphName);
        graphNameLabel.textProperty().bind(sb);
        sb = Bindings.concat("Simulation Price: ", priceSimulation);
        PriceSimulationLabel.textProperty().bind(sb);
        sb = Bindings.concat("Compilation Price: ", priceCompilation);
        PriceCompilationLabel.textProperty().bind(sb);
        sb = Bindings.concat("User's Name Uploaded: ", userNameUploaded);
        UserNameUploadedLabel.textProperty().bind(sb);


        sb = Bindings.concat("", errorPath);
        errorPathLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorWhatIf);
        errorWhatIfLabel.textProperty().bind(sb);
        sb = Bindings.concat("", errorCircle);
        errorCircleLabel.textProperty().bind(sb);



        //showGraphVizButton.disableProperty().bind(isPathSelected.not());
        //TotalIndepLabel.textProperty().bind(Bindings.format("%,d",totalLeaves));

    }

    private void openNewWindow(DTOTarget rowData) throws IOException {
        // New window (Stage)


        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setTitle("test");

        FXMLLoader loader = new FXMLLoader();
        loader= new FXMLLoader();
        URL dataViewFXML = getClass().getResource("/component/mainTask/mainTask.fxml");
        loader.setLocation(dataViewFXML);
        loader.load();
        MainTaskController mainTaskController = loader.getController();


        Scene scene = new Scene(mainTaskController.getRoot(), 800, 600);
        newWindow.setScene(scene);
        newWindow.show();
    }

    public GridPane getRoot(){return MainScrollPaneData;}

    public void setAppController(AppController appController) {this.appController=appController; }

    public void resetUIData(){
        resetSummary();
        resetDataTable();
        //resetSerialSetsTable();
        resetPathTab();
        resetWhatIfTab();
        resetCircleTab();
        resetImage();
    }

    private void resetImage() {
        isPathSelected.set(false);
        imageView=null;
        //insertFileNameTextField.clear();
    }

    private void resetSerialSetsTable() {
        //SerialSetsTable.setItems(null);
    }

    private void resetPathTab() {
        ObservableList<String> dataTarget= FXCollections.observableArrayList("--Target Name--");
        ObservableList<String> dataRatio = FXCollections.observableArrayList("--Ratio Choice--");
        PathList.setItems(null);
        errorPath.setValue("");
       // pathFromTargetChoice.setValue("--Target Name--");
        pathFromTargetChoice.setItems(dataTarget);
       // pathToTargetChoice.setValue("--Target Name--");
        pathToTargetChoice.setItems(dataTarget);
       // pathRatioChoice.setValue("--Ratio Choice--");
        pathRatioChoice.setItems(dataRatio);
    }

    private void resetCircleTab() {
        ObservableList<String> dataTarget= FXCollections.observableArrayList("--Target Name--");

        CircleList.setItems(null);
        errorCircle.setValue("");
        CircleTargetChoice.setItems(dataTarget);
        //CircleTargetChoice.setValue("--Target Name--");
    }

    private void resetWhatIfTab() {
        ObservableList<String> dataTarget= FXCollections.observableArrayList("--Target Name--");
        ObservableList<String> dataRatio = FXCollections.observableArrayList("--Ratio Choice--");
        ListWhatIf.setItems(null);
        errorWhatIf.setValue("");
        WhatIfTargetChoice.setItems(dataTarget);
       // WhatIfTargetChoice.setValue("--Target Name--");
        WhatIfRatioChoice.setItems(dataRatio);
       // WhatIfRatioChoice.setValue("--Ratio Choice--");
       // pathButtonStart.disabledProperty();
        //WhatIfStartButton.disabledProperty();
        //CircleStartButton.disabledProperty();
    }

    private void resetDataTable() {
        TargetsDataTable.setItems(null);
    }

    private void resetSummary() {
        totalTargets.set(0);
        totalRoots.set(0);
        totalIndep.set(0);
        totalMidd.set(0);
        totalLeaves.set(0);
        priceSimulation.set(0);
        priceCompilation.set(0);
        userNameUploaded.set("");
        graphName.set("");
    }

    public void updateUIData(){
        upperGridPaneData.prefWidthProperty().bind(appController.getRoot().widthProperty());
        initSummary();
        initDataTable();
        //initSerialSetsTable();
        initPathTab();
        initWhatIfTab();
        initCircleTab();

    }

    private void initPathTab() {
        ObservableList<String> TargetOptions = FXCollections.observableArrayList(appController.getTargetNamesList());
        ObservableList<String> RatioOptions  = FXCollections.observableArrayList("Required For","Depends On");
        //pathFromTargetChoice.setValue("--Target Name--"); // this statement shows default value
        pathFromTargetChoice.setItems(TargetOptions);
        //pathToTargetChoice.setValue("--Target Name--"); // this statement shows default value
        pathToTargetChoice.setItems(TargetOptions);
       // pathRatioChoice.setValue("--Ratio Choice--"); // this statement shows default value
        pathRatioChoice.setItems(RatioOptions);


    }

    private void initWhatIfTab(){
        ObservableList<String> TargetOptions = FXCollections.observableArrayList(appController.getTargetNamesList());
        ObservableList<String> RatioOptions  = FXCollections.observableArrayList("Required For","Depends On");
      //  WhatIfTargetChoice.setValue("--Target Name--"); // this statement shows default value
        WhatIfTargetChoice.setItems(TargetOptions);
       // WhatIfRatioChoice.setValue("--Ratio Choice--"); // this statement shows default value
        WhatIfRatioChoice.setItems(RatioOptions);
    }

    private void initCircleTab() {
        ObservableList<String> TargetOptions = FXCollections.observableArrayList(appController.getTargetNamesList());
      //  CircleTargetChoice.setValue("--Target Name--"); // this statement shows default value
        CircleTargetChoice.setItems(TargetOptions);
    }

    public void initSummary() {

        totalTargets.setValue(appController.getNumOfTargets());
        totalIndep.setValue(appController.getNumOfIndependent());
        totalLeaves.setValue(appController.getNumOfLeaf());
        totalRoots.setValue(appController.getNumOfRoot());
        totalMidd.setValue(appController.getNumOfMiddle());
        priceSimulation.setValue(appController.getPriceSimulation());
        priceCompilation.setValue(appController.getPriceCompilation());
        userNameUploaded.setValue(appController.getUserNameUploaded());
        graphName.setValue(appController.getGraphName());

    }

    public void initDataTable() {

        final ObservableList<DTOTarget> data = FXCollections.observableArrayList(appController.getDTOTargetList());

        TargetNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        TargetDataColumn.setCellValueFactory(
                new PropertyValueFactory<>("data")
        );
        TargetLocationColumn.setCellValueFactory(
                new PropertyValueFactory<>("location")
        );
        ReqDirColumn.setCellValueFactory(
                new PropertyValueFactory<>("numOfDirectRequiredFor")
        );
        ReqIndirColumn.setCellValueFactory(
                new PropertyValueFactory<>("numOfIndirectRequiredFor")
        );
        DepDirColumn.setCellValueFactory(
                new PropertyValueFactory<>("numOfDirectDependsOn")
        );
        DepIndirColumn.setCellValueFactory(
                new PropertyValueFactory<>("numOfIndirectDependsOn")
        );
        TargetsDataTable.setItems(data);
    }

    /*
    public void initSerialSetsTable() {

        final ObservableList<SerialSet> data = FXCollections.observableArrayList(appController.getListOfSerialSet());

        SerialSetNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        TISColumn.setCellValueFactory(
                new PropertyValueFactory<>("targetSetNamesOfSerialSet")
        );

        SerialSetsTable.setItems(data);
    }

     */

    @FXML
    void handleWhatIfButtonOnAction(ActionEvent event) {
        if (WhatIfTargetChoice.getValue() == null || WhatIfTargetChoice.getValue().equals("--Target Name--")) {
            errorWhatIf.setValue("Please choose a target to continue");
        } else if (WhatIfRatioChoice.getValue() == null || WhatIfRatioChoice.getValue().equals("--Ratio Choice--")) {
            errorWhatIf.setValue("Please choose ratio to continue");
        } else {//valid
            errorWhatIf.setValue("");

            String isRequiredString;
            if (WhatIfRatioChoice.getValue().equals("Required For")) {
                isRequiredString = "true";
            } else {
                isRequiredString = "false";
            }
            String finalUrl = HttpUrl
                    .parse(Constants.WHAT_IF)
                    .newBuilder()
                    .addQueryParameter(GRAPH_NAME, graphName.getValue())
                    .addQueryParameter(TARGET_NAME, WhatIfTargetChoice.getValue())
                    .addQueryParameter(RATIO, isRequiredString)
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
                            }
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    if (response.code() == 200) {
                        System.out.println("response was 200 - what-if");
                        Platform.runLater(() -> {
                            Gson gson = new Gson();
                            List<String> userArray = null;
                            try {
                                userArray = gson.fromJson(response.body().string(), ArrayList.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final ObservableList<String> data = FXCollections.observableArrayList(userArray);
                            ListWhatIf.setItems(data);
                        });
                    } else {
                        Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("404");
                                    alert.setContentText("Something went wrong: " + response.toString());
                                    alert.show();
                                }
                        );
                        System.out.println("response wasnt 200");
                    }
                }
            });

        }

    }

/*
    @FXML
    void showGraphVizOnAction(ActionEvent event) {
        if(imageView==null){
            if(selectedPathForGraphViz !=null){
                appController.getGraphViz(selectedPathForGraphViz.getAbsolutePath() ,insertFileNameTextField.getText());
                File file = new File(selectedPathForGraphViz.toString()+"\\"+insertFileNameTextField.getText()+".png");
                Image image=new Image(file.toURI().toString(),1000,800,true,true);
                imageView=new ImageView();
                imageView.setImage(image);

            }
        }
        StackPane root = new StackPane();
        ScrollPane scrollPane=new ScrollPane();
        scrollPane.setContent(root);
        Scene scene = new Scene(scrollPane, 600, 400);

        root.getChildren().add(imageView);


        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Graph Viz");



        //root.getChildren().add();

        stage.setScene(scene);
        stage.show();
     }

 */

    @FXML
    void selectPathOnAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Path");
        selectedPathForGraphViz = directoryChooser.showDialog(null);

        if (selectedPathForGraphViz == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File error");
            alert.setContentText("No File Was Chosen");
            alert.show();
            return;

        }else {
            isPathSelected.set(true);
        }
    }
    @FXML
    void handleCircleButtonOnAction(ActionEvent event) {
        if(CircleTargetChoice.getValue()== null || CircleTargetChoice.getValue().equals("--Target Name--")){
            errorCircle.setValue("Please choose a target to continue");
        }
        else{
            errorCircle.setValue("");

            String finalUrl = HttpUrl
                    .parse(Constants.FIND_CIRCLE)
                    .newBuilder()
                    .addQueryParameter(GRAPH_NAME, appController.getGraphName())
                    .addQueryParameter(TARGET_NAME, (CircleTargetChoice.getValue()).toString())
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->{
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
                        System.out.println("response was 200 - find-circle");
                        Platform.runLater(() -> {

                            final ObservableList<String> data;
                            try {
                                data = FXCollections.observableArrayList(response.body().string());
                                CircleList.setItems(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        System.out.println("response wasn't 200");
                        Platform.runLater(()->{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("404");
                            alert.setContentText("Something went wrong: " + response.body().toString());
                            alert.show();
                        });
                    }
                }
            });
        }
    }

    @FXML
    void handlePathButtonOnAction(ActionEvent event) {
        if(pathFromTargetChoice.getValue() == null || pathFromTargetChoice.getValue().equals("--Target Name--")){
            errorPath.setValue("Please choose starting target to continue");
        }else if(pathToTargetChoice.getValue()  == null ||  pathToTargetChoice.getValue().equals("--Target Name--")){
            errorPath.setValue("Please choose ending target to continue");
        }else if(pathRatioChoice.getValue() == null || pathRatioChoice.getValue().equals("--Ratio Choice--")){
            errorPath.setValue("Please choose ratio to continue");
        }
        else{
            errorPath.setValue("");

            String required = "false";
            if(pathRatioChoice.getValue().equals("Required For")){
                required="true";
            }

            String finalUrl = HttpUrl
                    .parse(Constants.GET_ALL_PATHS)
                    .newBuilder()
                    .addQueryParameter(GRAPH_NAME, appController.getGraphName())
                    .addQueryParameter(FIRST_TARGET, pathFromTargetChoice.getValue())
                    .addQueryParameter(SECOND_TARGET, pathToTargetChoice.getValue())
                    .addQueryParameter(RATIO, required)
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->{
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
                            List<String> res = null;
                            try {
                                res = Arrays.asList(response.body().string().split("\n"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final ObservableList<String> data = FXCollections.observableArrayList(res);
                            PathList.setItems(data);
                        });
                    } else {
                        System.out.println("response wasnt 200");
                        Platform.runLater(()->{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("404");
                            alert.setContentText("Something went wrong: " + response.body().toString());
                            alert.show();
                        });
                    }
                }
            });
        }
    }


    public void hendleRowClicked(MouseEvent mouseEvent) {
    }
}
