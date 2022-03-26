package component.howToRunPopUp;



import com.google.gson.reflect.TypeToken;
import component.mainTask.MainTaskController;
import engine.dto.DtoExeGpup;
import engine.dto.DtoNewTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static util.Constants.*;

public class PopUpRunQuestionController {

    @FXML private GridPane howToRunPopUpGridPane;

    @FXML private Button fromScratchButton;

    @FXML private Button incrementalButton;

    private DtoExeGpup data;
    private String userName;
    private String taskName;


    public void setDtoExeGpup(DtoExeGpup rowData) {
        this.data = rowData;
    }

    public Parent getRoot() {
        return howToRunPopUpGridPane;
    }

    @FXML
    void handleFromScratch(ActionEvent event) {
        if (data != null && userName!=null) {

            String oldTaskJson = GSON_INSTANCE.toJson(data, new TypeToken<DtoExeGpup>() {
            }.getType());
            String finalUrl = HttpUrl
                    .parse(UPLOAD_NEW_TASK_FROM_OLD)
                    .newBuilder()
                    .addQueryParameter(OLD_TASK, oldTaskJson)
                    .addQueryParameter(RUN_WAY, FROM_SCRATCH)
                    .addQueryParameter(USERNAME, userName)

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
                                System.out.println("Something went wrong: " + e.getMessage());
                            }
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    if (response.code() == 200) {
                        Platform.runLater(() -> {
                            try {
                                String jsonNewTask = response.body().string();
                                DtoExeGpup newExe = GSON_INSTANCE.fromJson(jsonNewTask, new TypeToken<DtoExeGpup>(){}.getType());
                                openNewWindow(newExe);
                            } catch (IOException e) {
                                Platform.runLater(() -> {
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("Something went wrong");
                                            alert.setContentText("Something went wrong: " + e.getMessage());
                                            alert.show();
                                            System.out.println("Something went wrong:  + e.getMessage()");
                                        }
                                );
                            }
                        });
                    } else {

                        Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle(String.valueOf(response.code()));
                                    alert.setContentText("Something went wrong: " + response.toString());
                                    alert.show();
                                    System.out.println("something went wrong:  " + response.toString());
                                }
                        );
                        System.out.println("response wasnt 200");
                    }
                }
            });
        }
    }

    @FXML
    void handleIncremental(ActionEvent event) {
        if (data != null && userName!=null) {

            String oldTaskJson = GSON_INSTANCE.toJson(data, new TypeToken<DtoExeGpup>() {
            }.getType());
            String finalUrl = HttpUrl
                    .parse(UPLOAD_NEW_TASK_FROM_OLD)
                    .newBuilder()
                    .addQueryParameter(OLD_TASK, oldTaskJson)
                    .addQueryParameter(RUN_WAY, INCREMENTAL)
                    .addQueryParameter(USERNAME, userName)

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
                                System.out.println("Something went wrong: " + e.getMessage());
                            }
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    if (response.code() == 200) {
                        Platform.runLater(() -> {
                            try {
                                // errorPref.setValue(response.body().string());
                                String jsonNewTask = response.body().string();
                                DtoExeGpup newExe = GSON_INSTANCE.fromJson(jsonNewTask, new TypeToken<DtoExeGpup>(){}.getType());
                               openNewWindow(newExe);
                            } catch (IOException e) {
                                Platform.runLater(() -> {
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("Something went wrong");
                                            alert.setContentText("Something went wrong: " + e.getMessage());
                                            alert.show();
                                            System.out.println("Something went wrong:  + e.getMessage()");
                                        }
                                );
                            }
                        });
                    } else {

                        Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle(String.valueOf(response.code()));
                                    alert.setContentText("Something went wrong: " + response.toString());
                                    alert.show();
                                    System.out.println("something went wrong:  " + response.toString());
                                }
                        );
                        System.out.println("response wasnt 200");
                    }
                }
            });
        }
    }
    private void openNewWindow(DtoExeGpup rowData) throws IOException {
        // New window (Stage)

        Stage newWindow = new Stage();
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setTitle("Run Screen Of "+rowData.getTaskName());

        FXMLLoader loader = new FXMLLoader();
        loader= new FXMLLoader();
        URL dataViewFXML = getClass().getResource("/component/mainTask/mainTask.fxml");
        loader.setLocation(dataViewFXML);
        loader.load();
        MainTaskController mainTaskController = loader.getController();
        mainTaskController.setTaskName(rowData.getTaskName());
        //mainTaskController.activeControlPanel(rowData.getTaskStatus(),taskName);//admin!
        mainTaskController.activeControlPanel(rowData.getTaskStatus(),rowData.getTaskName());//admin!
        //mainTaskController.activeLiveData(rowData.getTaskStatus(),null);
        mainTaskController.activeMainTask();

        howToRunPopUpGridPane.getScene().getWindow().hide();

        Scene scene = new Scene(mainTaskController.getRoot(), 850, 900);
        newWindow.setScene(scene);
        newWindow.show();

    }

    public void setUserName(String userName) {
        this.userName= userName;
    }

    public void setTaskName(String taskName) {
        this.taskName=taskName;
    }
}


