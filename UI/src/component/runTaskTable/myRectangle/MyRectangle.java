package component.runTaskTable.myRectangle;

import engine.dto.DtoTargetExeTableData;
import engine.graph.Target;
import javafx.animation.FillTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static util.Constants.*;


public class MyRectangle extends SimpleStringProperty {

    private DtoTargetExeTableData dtoTargetExeTableData;
    private Text text;
    private Rectangle rectangle;
    private StackPane myRectangle;
    private Color endColor=null;
    private Color startColor=null;

    public MyRectangle(Target target)
    {
        myRectangle=new StackPane();
        rectangle=new Rectangle();
        rectangle.setHeight(50);
        rectangle.setWidth(70);
        text=new Text(target.getName());
        myRectangle.getChildren().addAll(rectangle,text);
        StackPane.setAlignment(rectangle, Pos.CENTER); //set it to the Center Left(by default it's on the center)
        StackPane.setAlignment(text, Pos.CENTER); //set it to the Center Left(by default it's on the center)


    }

    public MyRectangle(DtoTargetExeTableData dtoTargetExeTableData) {
        myRectangle=new StackPane();
        this.dtoTargetExeTableData=dtoTargetExeTableData;
        rectangle=new Rectangle();
        rectangle.setHeight(50);
        rectangle.setWidth(70);
        text=new Text(dtoTargetExeTableData.getName());
        myRectangle.getChildren().addAll(rectangle,text);
        setAnimation(1);
        StackPane.setAlignment(rectangle, Pos.CENTER); //set it to the Center Left(by default it's on the center)
        StackPane.setAlignment(text, Pos.CENTER); //set it to the Center Left(by default it's on the center)
    }


    /*
    public String getStatus(){
        return dtoTargetExeTableData.getStatus();
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
*/

    public StackPane getMyRectangle(){
        return myRectangle;
    }




    public boolean setAnimation(int millisForAnimations) {
        Color newEnd = null;

        switch(dtoTargetExeTableData.getStatus()) {
            case FROZEN:{
                newEnd= Color.rgb(194,216,242,1);
            }
                break;
            case SKIPPED:{
                newEnd=Color.rgb(159, 159, 159,1);
            }
                break;
            case WAITING: {
                newEnd=Color.rgb(148,112,205,1);
            }
            break;
            case  IN_PROCESS: {
                newEnd=Color.rgb(255, 138, 101,1);
            }
            break;
            case FINISHED:{
                switch(dtoTargetExeTableData.getFinishStatus()) {
                    case SUCCESS:
                        newEnd=Color.rgb(165, 214, 167,1);
                        break;
                    case FAILURE:
                        newEnd=Color.rgb(229, 115, 115,1);
                        break;
                    case SUCCESS_WITH_WARNING:
                        newEnd=Color.rgb(255, 245, 157,1);
                        break;
                    case NOT_FINISHED:
                        newEnd=Color.rgb(77, 182, 172,1);
                        break;
                    default:
                        System.out.println("G.P.U.P/UI/src/appController/taskView/subcomponents/runTable/myRectangle/MyRectangle.java  setAnimation  Small Switch Case");
                }
            }
            break;
            default:
                System.out.println("G.P.U.P/UI/src/appController/taskView/subcomponents/runTable/myRectangle/MyRectangle.java  setAnimation  Big Switch Case");
        }
        endColor=newEnd;
        FillTransition ft = new FillTransition(Duration.millis(millisForAnimations), this.rectangle, startColor, endColor);
        //ft.setCycleCount(2);
        //ft.setAutoReverse(true);
        ft.play();

        startColor =endColor;
        return true;
    }






    //public SimpleStringProperty getStatusProperty(){return target.targetStatusProperty();}

}

