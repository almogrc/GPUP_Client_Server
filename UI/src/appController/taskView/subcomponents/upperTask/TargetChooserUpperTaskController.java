package appController.taskView.subcomponents.upperTask;


import appController.taskView.mainTaskView.MainTaskViewController;
import com.google.gson.Gson;
import engine.graph.WrapperTarget;
import engine.graph.WrapsTarget;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static util.Constants.*;

public class TargetChooserUpperTaskController {

    @FXML private ChoiceBox<String> WhatIfTargetChoice;
    @FXML private ChoiceBox<String> WhatIfRatioChoice;
    @FXML private Button addWhatIfButton;
    @FXML private Button addAllButton;
    @FXML private Button clearAllButton;
    @FXML private Label errorWhatIfLabel;

    private MainTaskViewController mainTaskViewController;
    private SimpleStringProperty errorWhatIf;
    @FXML
    private void initialize(){
        StringExpression sb = Bindings.concat("", errorWhatIf);
        errorWhatIfLabel.textProperty().bind(sb);
    }

    public TargetChooserUpperTaskController(){
        errorWhatIf = new SimpleStringProperty("");
    }
    public void initMe(){
        ObservableList<String> TargetOptions = FXCollections.observableArrayList(mainTaskViewController.getTargetNamesList());
        ObservableList<String> RatioOptions  = FXCollections.observableArrayList("Required For","Depends On");
        WhatIfTargetChoice.setItems(TargetOptions);
        WhatIfRatioChoice.setItems(RatioOptions);
    }

    public void resetMe(){
        ObservableList<String> dataTarget= FXCollections.observableArrayList("--Target Name--");
        ObservableList<String> dataRatio = FXCollections.observableArrayList("--Ratio Choice--");
        errorWhatIf.setValue("");
        WhatIfTargetChoice.setItems(dataTarget);
        WhatIfRatioChoice.setItems(dataRatio);
    }
    @FXML
    void addWhatIfButtonOnAction(ActionEvent event) {
        if( WhatIfTargetChoice.getValue()== null || WhatIfTargetChoice.getValue().equals("--Target Name--")){
            errorWhatIf.setValue("Please choose a target to continue.");
        }else if( WhatIfRatioChoice.getValue()== null || WhatIfRatioChoice.getValue().equals("--Ratio Choice--")){
            errorWhatIf.setValue("Please choose a ratio to continue.");
        }
        else{//valid
            errorWhatIf.setValue("");
            String required= "false";
            if(WhatIfRatioChoice.getValue().equals("Required For")) {
                required = "true";
            }

            String finalUrl = HttpUrl
                    .parse(Constants.WHAT_IF)
                    .newBuilder()
                    .addQueryParameter(GRAPH_NAME, mainTaskViewController.getAppController().getGraphName())
                    .addQueryParameter(TARGET_NAME, WhatIfTargetChoice.getValue())
                    .addQueryParameter(RATIO, required)
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
                        System.out.println("response was 200 - what-if task area");
                        Platform.runLater(() -> {
                            Gson gson = new Gson();
                            List<String> whatIfListString = null;
                            try {
                                whatIfListString = gson.fromJson(response.body().string(), ArrayList.class);
                                if(!whatIfListString.contains(WhatIfTargetChoice.getValue())){
                                    whatIfListString.add(WhatIfTargetChoice.getValue());
                                }
                                for(WrapsTarget curr:mainTaskViewController.getWrapsTargetList()){
                                    if(whatIfListString.contains(curr.getName())){
                                        curr.setCheckbox(true);
                                    }
                                }
                            } catch (IOException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Something went wrong");
                                alert.setContentText("Something went wrong: " + e.getMessage());
                                alert.show();
                            }
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
    @FXML
    void clearAllButtonOnAction(ActionEvent event) {
        errorWhatIf.setValue("");
        List<WrapsTarget> wrapsTargetList = mainTaskViewController.getWrapsTargetList();
        for(WrapsTarget curr :wrapsTargetList){
            curr.setCheckbox(false);
        }
    }
    @FXML
    void addAllButtonOnAction(ActionEvent event) {
        errorWhatIf.setValue("");
        List<WrapsTarget> wrapsTargetList = mainTaskViewController.getWrapsTargetList();
        for(WrapsTarget curr :wrapsTargetList){
            curr.setCheckbox(true);
        }
    }

    public void setParent(MainTaskViewController mainTaskViewController) {
        this.mainTaskViewController = mainTaskViewController;
    }

    public void disableMe() {
        addAllButton.setDisable(true);
        addWhatIfButton.setDisable(true);
        clearAllButton.setDisable(true);
        WhatIfTargetChoice.setDisable(true);
        WhatIfRatioChoice.setDisable(true);
    }
    public void activateMe() {
        addAllButton.setDisable(false);
        addWhatIfButton.setDisable(false);
        clearAllButton.setDisable(false);
        WhatIfTargetChoice.setDisable(false);
        WhatIfRatioChoice.setDisable(false);
    }

}