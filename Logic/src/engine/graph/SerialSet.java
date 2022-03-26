package engine.graph;

import java.util.HashSet;
import java.util.Set;

public class SerialSet {
    private String name;
    private Set<Target> targetListOfSerialSet;

    public SerialSet(String name,Set<Target> targetListOfSerialSet){
        this.name = name;
        this.targetListOfSerialSet=targetListOfSerialSet;
    }

    public String getName(){
        return this.name;
    }

    public Set<String> getTargetSetNamesOfSerialSet(){
        Set<String> resNames = new HashSet<String>();
        for(Target target: targetListOfSerialSet)
            resNames.add(target.getName());
        return resNames;
    }

    public Set<Target> getTargetSetOfSerialSet(){
        return this.targetListOfSerialSet;
    }
}
