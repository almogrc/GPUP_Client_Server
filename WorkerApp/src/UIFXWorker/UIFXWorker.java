package UIFXWorker;

import component.login.LoginControllerWorker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class UIFXWorker extends Application {

        private LoginControllerWorker login;

        public static void main(String[] args) {
            UIFXWorker uifx=new UIFXWorker();
            uifx.launch(args);

        }
        //נקודה נקודה - לתת נתיב אבסולוטי מהsrc



        @Override
        public void start(Stage primaryStage) {

            primaryStage.setMinHeight(400);
            primaryStage.setMinWidth(400);
            primaryStage.setTitle("GPUP-Worker");

            FXMLLoader fxmlLoader = new FXMLLoader();


            //loginPage//
            URL loginPage = getClass().getResource("/component/login/loginWorker.fxml");
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
        }
}
