package engine.graph;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class WrapperTarget {

    private String name;
    private String data;
    private Target.Location location;
    private List<Target> dependsOn=new ArrayList<Target>();
    private List<Target> requiredFor=new ArrayList<Target>();
    private Target.Finish finishStatus;
    private Target.TargetStatus targetStatus;
    private List<SerialSet> listOfSets;
}
