package engine.dto;

public class DTOTargetsData implements Dto{

    private int numOfTargets;
    private int numOfLeaf;
    private int numOfMiddle;
    private int numOfRoot;
    private int numOfIndependents;

    public DTOTargetsData(int numOfTargets, int numOfLeaf, int numOfMiddle, int numOfRoot, int numOfIndependents) {
        this.numOfTargets=numOfTargets;
        this.numOfLeaf=numOfLeaf;
        this.numOfMiddle=numOfMiddle;
        this.numOfRoot=numOfRoot;
        this.numOfIndependents= numOfIndependents;
    }

    public String toString(){
        return  "***********************************************************************************\n" +
                "The graph's data is: " +
                "\n1. The number of targets is: "+numOfTargets+
                "\n2. The number of 'Leaves' is: "+numOfLeaf+
                "\n3. The number of 'Middles' is: "+numOfMiddle+
                "\n4. The number of 'Roots' is: "+numOfRoot+
                "\n5. The number of 'Independents' is: "+numOfIndependents+
                "\n***********************************************************************************";
    }
}
