package engine.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Graph implements Serializable {

    public void setSize(int size) {
        this.size = size;
    }

    public boolean[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(boolean[][] matrix) {
        this.matrix = matrix;
    }

    private int size;
    private boolean[][] matrix;
    //for exemple this matrix
    //      1   2   3
    //  1       T   T
    //  2
    //  3       T

    //Represents graph with 3 vertexes (1,2,3):
    //      3
    //      ^\
    //      | \
    //      |  \
    //      |   \
    //      |   \/
    //      1--->2
    //     the edges is: (1,3) , (1,2) , (3,2)
    public Graph(int size){
        this.size=size;
        matrix = new boolean[size][size];
    }

    public Graph(Graph another) {
        this.setSize(another.getSize());
        matrix = new boolean[size][size];

        boolean[][] anotherMatrix=another.getMatrix();
        for(int i=0;i<this.size;i++){
            for(int j=0;j<this.size;j++){
                if(anotherMatrix[i][j]){
                    this.matrix[i][j]=true;
                }else {
                    this.matrix[i][j]=false;
                }
            }
        }
    }

    public int getSize(){
        return matrix.length;
    }

    public void addEdge(int vertexFrom , int vertexTo) {
        matrix[vertexFrom][vertexTo]=true;
    }

    public void removeAdge(int vertexFrom , int vertexTo){
        matrix[vertexFrom][vertexTo]=false;
    }

    public List<Integer> myDependentOn(int vertex){//dependent on //getNeighborhood
        List<Integer> arr=new ArrayList();

        //Go through the entire row of vertex and add to arr all the places where there is true
        // i.e. there is an arc from vertex to i
        for(int i=0;i<matrix.length;i++){
            if(matrix[i][vertex]==true){
                arr.add(i);
            }
        }
        return arr;
    }

    public List<Integer> myRequiredFor(int vertex){//required for //imNeighborOf
        List<Integer> arr=new ArrayList();

        //Go through the entire column of vertex and add to arr all the places where there is true
        // i.e. there is an arc from i to vertex
        for(int i=0;i<matrix.length;i++){
            if(matrix[vertex][i]==true){
                arr.add(i);
            }
        }
        return arr;
    }

    public Boolean isEdgeExist(int i,int j){
        return matrix[i][j];
    }

    public Target.Location getLocation(int i) {
        int sizeOfMyDependentOn = myDependentOn(i).size();
        int sizeOfMyRequiredFor =myRequiredFor(i).size();

        if(sizeOfMyDependentOn == 0 && sizeOfMyRequiredFor == 0){
            return Target.Location.INDEPENDENT;
        }else if(sizeOfMyDependentOn != 0 && sizeOfMyRequiredFor == 0){
            return Target.Location.ROOT;
        }else if(sizeOfMyDependentOn == 0 && sizeOfMyRequiredFor != 0){
            return Target.Location.LEAF;
        }else{
            return Target.Location.MIDDLE;
        }
    }

    public List<ArrayList<Integer>> getAllPaths(int indexFrom, int indexTo) {
        Stack<Integer> stack1=new Stack<Integer>();
        List<ArrayList<Integer>> res = new ArrayList<ArrayList<Integer>>();
        stack1.push(indexFrom);
        recAllPaths(indexFrom, indexTo,stack1,res);
        return res;
    }

    private void recAllPaths(int indexFrom, int indexTo, Stack<Integer> stack1, List<ArrayList<Integer>> arrayListList) {
        if(indexFrom==indexTo){//if true -> get all elements (from bottom to up) in stack1 to new list in arrayListList
            ArrayList<Integer>newArrList=new ArrayList<Integer>();
            Stack<Integer> stack2=new Stack<Integer>();

            while(!stack1.isEmpty()){//move all elements from stack1 to stack2 to remain order
                stack2.push(stack1.pop());
            }

            while(!stack2.isEmpty()){//move all elements from stack2 to stack1
                newArrList.add(stack2.peek());
                stack1.push(stack2.pop());
            }
            arrayListList.add(newArrList);
        }else{
            for(int neighbor : myRequiredFor(indexFrom)){
                if(!stack1.contains(neighbor)) {
                    stack1.push(neighbor);
                    recAllPaths(neighbor, indexTo, stack1, arrayListList);
                    stack1.pop();
                }
            }
        }
    }

}
