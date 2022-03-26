package appController.mainAppController;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class welcomeSignController {

    @FXML
    private ImageView image;


    public void initialize() {
        //translate
        TranslateTransition transition=new TranslateTransition();
        transition.setNode(image);
        transition.setByX(250);
        transition.play();
    }
}
//URL location, ResourceBundle resources
