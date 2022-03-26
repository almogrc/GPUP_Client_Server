package uifx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import component.login.LoginController;

import java.io.IOException;
import java.net.URL;


public class UIFX extends Application {
    private LoginController login;

    public static void main(String[] args) {
        UIFX uifx=new UIFX();
        uifx.launch(args);

    }
    //נקודה נקודה - לתת נתיב אבסולוטי מהsrc



    @Override
    public void start(Stage primaryStage) {

        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        primaryStage.setTitle("GPUP");

        FXMLLoader fxmlLoader = new FXMLLoader();


        //loginPage//
        URL loginPage = getClass().getResource("/component/login/login.fxml");
        try {
            fxmlLoader.setLocation(loginPage);
            Parent root = fxmlLoader.load();
            login = fxmlLoader.getController();

            Scene scene = new Scene(root, 400, 400);
            //--------------------//
            login.setPrimaryStage(primaryStage);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }




        /*
        //CSSFX.start();
        FXMLLoader loader = new FXMLLoader();
        URL appFXML = getClass().getResource("/appController/mainAppController/mainStage.fxml");
        loader.setLocation(appFXML);
        ScrollPane root = loader.load();

        primaryStage.setTitle("G.P.U.P");

        AppController appController= loader.getController();
        //GpupExecution gpupExecution= new GpupExecution(appController);
        appController.setPrimaryStage(primaryStage);
        //appController.setGpupExecution(gpupExecution);

        loader= new FXMLLoader();
        URL dataViewFXML = getClass().getResource("/appController/dataView/targetsData.fxml");
        loader.setLocation(dataViewFXML);
        loader.load();
        DataViewController dataViewController = loader.getController();

        appController.setDataViewController(dataViewController);
        dataViewController.setAppController(appController);

        loader= new FXMLLoader();
        URL taskViewFXML = getClass().getResource("/appController/taskView/mainTaskView/mainTaskView.fxml");
        loader.setLocation(taskViewFXML);
        loader.load();
        MainTaskViewController mainTaskViewController = loader.getController();

        appController.setTaskViewController(mainTaskViewController);
        mainTaskViewController.setAppController(appController);

        Scene scene = new Scene(root, 1250,600);
        primaryStage.setScene(scene);
        primaryStage.show();

         */
    }
}