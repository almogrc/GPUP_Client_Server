package component.login;

import component.api.HttpStatusUpdate;
import component.dashboard.DashBoardControllerWorker;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static util.Constants.*;
import static util.Constants.NUM_OF_THREADS;

public class LoginControllerWorker implements HttpStatusUpdate {

        @FXML  public TextField userNameTextField;
        @FXML  public Label errorMessageLabel;
        @FXML  private ChoiceBox<Integer> threadCountChoiceBox;

        private final StringProperty errorMessageProperty = new SimpleStringProperty("");
        private Stage primaryStage;
        private Integer threadNum = 0;

        @FXML
        public void initialize() {
            errorMessageLabel.textProperty().bind(errorMessageProperty);
            initChoiceBox();

        /*
        HttpClientUtil.setCookieManagerLoggingFacility(line ->
              Platform.runLater(() ->
                    updateHttpStatusLine(line)));

         */
        }

        private void initChoiceBox(){
            int i;
            List<Integer> threadNumList=new ArrayList<Integer>();
            ObservableList<Integer> threadNumOptions;
            for(i = 1;i<=5;i++){
                threadNumList.add(i);
                }
            threadNumOptions = FXCollections.observableArrayList(threadNumList);
            threadCountChoiceBox.setItems(threadNumOptions);

        }
        @FXML

        private void switchToDashBoard(){
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL workerDashboardPage = getClass().getResource("/component/dashboard/dashBoardWorker.fxml");
            fxmlLoader.setLocation(workerDashboardPage);
            try {
                Parent root= fxmlLoader.load();

                //workerDashboardPage//
                Scene scene = new Scene(root, 1050, 750);
                primaryStage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DashBoardControllerWorker workerDashboard = fxmlLoader.getController();
            //adminDashboard.PrimeryStage(primaryStage);
            workerDashboard.updateUserName(userNameTextField.getText());
            //workerDashboard.setHttpStatusUpdate(this);
            workerDashboard.setActive();
            workerDashboard.setPrimaryStage(primaryStage);

        }
        @FXML
        private void userNameKeyTyped(KeyEvent event) {
            errorMessageProperty.set("");
        }

        @FXML
        private void quitButtonClicked(ActionEvent e) {
            Platform.exit();
        }

        public void setPrimaryStage(Stage primaryStage) {
            this.primaryStage=primaryStage;
        }

        @Override
        public void updateHttpLine(String line) {

        }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }
        if (threadCountChoiceBox.getValue() == null) {
            errorMessageProperty.set("No threads had been chosen. You can't login with no threads.");
            return;
        }
        threadNum= threadCountChoiceBox.getValue();
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(USER_LOGIN)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter(USER_TYPE, WORKER)
                .addQueryParameter(MONEY, "0")
                .addQueryParameter(NUM_OF_THREADS,threadNum.toString())
                .build()
                .toString();

        ////updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        switchToDashBoard();
                        errorMessageProperty.setValue("");
                        //chatAppMainController.switchToChatRoom();מחליף למסך הצאט
                    });
                }
            }
        });


    }





/*
    private void updateHttpStatusLine(String data) {
        chatAppMainController.updateHttpLine(data);
    }

 */


    /*
    public void setChatAppMainController(ChatAppMainController chatAppMainController) {
        this.chatAppMainController = chatAppMainController;
    }

     */


    }

