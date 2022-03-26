package component.chooseGraph;

import appController.mainAppController.AppController;
import com.google.gson.Gson;
import component.api.HttpStatusUpdate;
import component.dashboard.DashboardController;
import engine.dto.DTOTargetsGraph;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static util.Constants.GRAPH_NAME;
import static util.Constants.GSON_INSTANCE;

public class ChooseGraphController implements Closeable {
        private Timer timer;
        private TimerTask listRefresher;
        private final BooleanProperty autoUpdate;
        private HttpStatusUpdate httpStatusUpdate;
@FXML
private ChoiceBox<String> graphsChoiceBox;

@FXML
private Button chooseGraphButton;
private DashboardController upController;

        public ChooseGraphController() {
                autoUpdate = new SimpleBooleanProperty();
        }

        @FXML
        public void initialize(){
        }

        public void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate) {
                this.httpStatusUpdate = httpStatusUpdate;

        }

        public BooleanProperty autoUpdatesProperty() {
                return autoUpdate;
        }

        private void updateXMKChoiceBox(List<String> graphsList) {
                Platform.runLater(() -> {
                        List<String> list = graphsChoiceBox.getItems().stream().collect(Collectors.toList());
                        for(String newGraph:graphsList){
                                if(!list.contains(newGraph)){
                                        graphsChoiceBox.getItems().add(newGraph);
                                }
                        }
                });
        }

        public void close() {
                graphsChoiceBox.getItems().clear();
                if (listRefresher != null && timer != null) {
                        listRefresher.cancel();
                        timer.cancel();
                }
        }

        public void startListRefresher() {
                listRefresher = new ChooseGraphRefresher(
                        autoUpdate,
                        this::updateXMKChoiceBox);
                timer = new Timer();
                timer.schedule(listRefresher, 0, 2000);
        }

        @FXML
        void chooseGraphOnAction(ActionEvent event) {
                if(graphsChoiceBox.getValue()!=null){
                        String finalUrl = HttpUrl
                                .parse(Constants.GRAPH_DATA)
                                .newBuilder()
                                .addQueryParameter(GRAPH_NAME, graphsChoiceBox.getValue())
                                .build()
                                .toString();

                        HttpClientUtil.runAsync(finalUrl, new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        Platform.runLater(new Runnable() {
                                                public void run() {
                                                        Alert alert = new Alert(Alert.AlertType.ERROR,  e.getMessage(), ButtonType.OK);
                                                }
                                        });
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        if (response.code() == 200) {
                                                Platform.runLater(()->{
                                                        setTargetsDataInAppPage(response);
                                                        upController.switchToAppPage();

                                                });
                                        } else {
                                                Platform.runLater(new Runnable() {
                                                        public void run() {
                                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                                alert.setTitle("Failed with code : "+response.code());
                                                        }
                                                });
                                        }
                                }
                        });

                }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("404");
                        alert.setContentText("Please choose a graph!");
                        alert.show();
                }
        }

        public void setUpController(DashboardController dashboardController) {
                this.upController =dashboardController;
        }

        private void setTargetsDataInAppPage(Response response) {
                String responseBody;
                Gson gson = new Gson();
                if(response.isSuccessful()) {//TODO
                        try {
                                responseBody = response.body().string();
                                DTOTargetsGraph temp=GSON_INSTANCE.fromJson(responseBody, DTOTargetsGraph.class);
                                AppController app= upController.getAppController();
                                app.setTargetsData(temp);
                        } catch (IOException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("404");
                                alert.setContentText("Something went wrong: " + e.getMessage());
                                alert.show();
                        }
                }
        }
}
