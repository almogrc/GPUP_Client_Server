package component.login;


import component.api.HttpStatusUpdate;
import component.dashboard.DashboardController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

import static util.Constants.*;

public class LoginController implements HttpStatusUpdate {

    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorMessageLabel;


    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private Stage primaryStage;

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);


        /*
        HttpClientUtil.setCookieManagerLoggingFacility(line ->
              Platform.runLater(() ->
                    updateHttpStatusLine(line)));

         */
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.USER_LOGIN)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter(USER_TYPE, ADMIN)
                .addQueryParameter(MONEY, "0")
                .addQueryParameter(NUM_OF_THREADS, "0")
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
                if (!response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        switchToDashBoard();
                        //chatAppMainController.switchToChatRoom();מחליף למסך הצאט
                    });
                }
            }
        });


    }




    private void switchToDashBoard(){
        FXMLLoader fxmlLoader = new FXMLLoader();
            URL adminDashboardPage = getClass().getResource("/component/dashboard/dashboard.fxml");
            fxmlLoader.setLocation(adminDashboardPage);
        try {
            Parent root= fxmlLoader.load();

        //adminDashboardPage//
            Scene scene = new Scene(root, 1044, 654);
            primaryStage.setScene(scene);
            DashboardController adminDashboard = fxmlLoader.getController();
            //adminDashboard.PrimeryStage(primaryStage);
            adminDashboard.updateUserName(userNameTextField.getText());
            adminDashboard.setActive();
            adminDashboard.onDashboardUp();
            adminDashboard.setPrimaryStage(primaryStage);
            adminDashboard.setRefresher(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

