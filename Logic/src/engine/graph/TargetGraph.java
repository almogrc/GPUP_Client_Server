package engine.graph;

import generated.GPUPConfiguration;
import generated.GPUPDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

public class TargetGraph implements Serializable {
    final static String JAXB_XML_PACKAGE_NAME = "generated";

    private Graph graph;
    private List<Target> targetList;
    private List<SerialSet> ListOfSerialSet=new ArrayList<SerialSet>();
    private String name;
    private int priceListCompilation;
    private int priceListSimulation;
    //private int maxThreads;

    //private Target.Location independent;

    public TargetGraph(Graph graph, File file) throws JAXBException, FileNotFoundException {
        this.graph=graph;
        GPUPDescriptor descriptor=extracted(file);
        createTargetList(descriptor);
        insertDependOnAndRequire();
        insertSets(descriptor);
        insertGraphName(descriptor);
        insertPriceList(descriptor);
        insertSetPerTarget();
        GPUPConfiguration gpupConfiguration = descriptor.getGPUPConfiguration();
        //maxThreads =gpupConfiguration.getGPUPMaxParallelism();
    }

    public TargetGraph(Graph graph, InputStream inputStream) throws JAXBException, FileNotFoundException {
        this.graph=graph;
        try {
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GPUPDescriptor descriptor=extracted(inputStream);
        createTargetList(descriptor);
        insertDependOnAndRequire();
        insertSets(descriptor);
        insertGraphName(descriptor);
        insertPriceList(descriptor);
        insertSetPerTarget();
        GPUPConfiguration gpupConfiguration = descriptor.getGPUPConfiguration();
        //maxThreads =gpupConfiguration.getGPUPMaxParallelism();
    }


    public TargetGraph(TargetGraph another) {
        this.graph=new Graph(another.graph);
        this.targetList=new ArrayList<>();
        for(int i=0;i<another.targetList.size();i++){
            targetList.add(new Target(another.targetList.get(i)));
        }
        insertDependOnAndRequire();

        this.ListOfSerialSet=new ArrayList<SerialSet>();
        this.setName(another.getName());
        this.setPriceListCompilation(another.getPriceListCompilation());
        this.setPriceListSimulation(another.getPriceListSimulation());
    }


    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setTargetList(List<Target> targetList) {
        this.targetList = targetList;
    }

    public void setListOfSerialSet(List<SerialSet> listOfSerialSet) {
        ListOfSerialSet = listOfSerialSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriceListCompilation() {
        return priceListCompilation;
    }

    public void setPriceListCompilation(int priceListCompilation) {
        this.priceListCompilation = priceListCompilation;
    }

    public int getPriceListSimulation() {
        return priceListSimulation;
    }

    public void setPriceListSimulation(int priceListSimulation) {
        this.priceListSimulation = priceListSimulation;
    }


    private void insertPriceList(GPUPDescriptor descriptor) {
        List<GPUPConfiguration.GPUPPricing.GPUPTask>temp=descriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask();
        for(int i=0;i<temp.size();i++){
            if(temp.get(i).getName().equals("Simulation")){
                this.priceListSimulation=temp.get(i).getPricePerTarget();
            }
            else if(temp.get(i).getName().equals("Compilation")){
                this.priceListCompilation=temp.get(i).getPricePerTarget();
            }
        }
    }

    private void insertGraphName(GPUPDescriptor descriptor) {
        this.name=descriptor.getGPUPConfiguration().getGPUPGraphName();
    }

    private void insertSetPerTarget() {
        for(Target curTarget:targetList){
            List<SerialSet> listOfSetsForCurTarget=new ArrayList<SerialSet>();

            for(SerialSet tempSet:ListOfSerialSet){
                if(tempSet.getTargetSetOfSerialSet().contains(curTarget)){
                    listOfSetsForCurTarget.add(tempSet);
                }
            }
            curTarget.addSerialSets(listOfSetsForCurTarget);
        }
    }

    private void insertSets(GPUPDescriptor descriptor) {
        int setsSize=0;
        if(descriptor.getGPUPSerialSets()!=null) {
            setsSize = descriptor.getGPUPSerialSets().getGPUPSerialSet().size();
        }
        for(int i=0 ; i<setsSize ; i++){
            Set<Target> newSet=new HashSet<Target>();
            GPUPDescriptor.GPUPSerialSets.GPUPSerialSet tempSet=descriptor.getGPUPSerialSets().getGPUPSerialSet().get(i);
            List<String>tempNameList = Arrays.asList(tempSet.getTargets().split(","));
            for(String targetName:tempNameList){
                newSet.add(getTarget(targetName));
            }
            SerialSet serialSet = new SerialSet(tempSet.getName(),newSet);
            ListOfSerialSet.add(serialSet);
        }
    }

    private void insertDependOnAndRequire() {
        for(int i=0;i<targetList.size();i++){
            for (Integer vertex: graph.myDependentOn(i)) {
                targetList.get(i).addDependingOn(targetList.get(vertex));
            }

            for (Integer vertex: graph.myRequiredFor(i)) {
                targetList.get(i).addRequiredFor(targetList.get(vertex));
            }
        }
    }

    private void createTargetList(GPUPDescriptor descriptor) {
        targetList=new ArrayList<Target>();
        for(int i=0;i<descriptor.getGPUPTargets().getGPUPTarget().size();i++){
            Target newTarget=new Target(descriptor.getGPUPTargets().getGPUPTarget().get(i).getName(),
                                        descriptor.getGPUPTargets().getGPUPTarget().get(i).getGPUPUserData(),
                                        graph.getLocation(i));
            targetList.add(newTarget);
        }
    }

    private GPUPDescriptor extracted(File file) throws FileNotFoundException, JAXBException {
        GPUPDescriptor descriptor=null;
        try {
            InputStream inputStream = new FileInputStream(file);
            descriptor = deserializeFrom(inputStream);
        } catch (JAXBException | FileNotFoundException e) {
            throw e;
        }
        return descriptor;
    }

    private GPUPDescriptor extracted(InputStream inputStream) throws FileNotFoundException, JAXBException {
        GPUPDescriptor descriptor=null;
        try {
            descriptor = deserializeFrom(inputStream);
        } catch (JAXBException e) {
            throw e;
        }
        return descriptor;
    }

    private static GPUPDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (GPUPDescriptor) u.unmarshal(in);
        //GPUPDescriptor test= (GPUPDescriptor) u.unmarshal(in);
    }

    public int getNumOfTargets(){ return targetList.size();}

    public int getNumOfIndependent(){
        int res=0;
        for (Target target:targetList) {
            if(target.getLocation()==Target.Location.INDEPENDENT){
                res++;
            }
        }
        return res;
    }

    public int getNumOfLeaf(){
        int res=0;
        for (Target target:targetList) {
            if(target.getLocation()== Target.Location.LEAF){
                res++;
            }
        }
        return res;
    }

    public int getNumOfMiddle(){
        int res=0;
        for (Target target:targetList) {
            if(target.getLocation()==Target.Location.MIDDLE){
                res++;
            }
        }
        return res;
    }

    public int getNumOfRoot(){
        int res=0;
        for (Target target:targetList) {
            if(target.getLocation()==Target.Location.ROOT){
                res++;
            }
        }
        return res;
    }
    /*
    public int getMaxParallelism(){return maxThreads;}

     */

    public Target getTarget(String targetName) {
        Target res=null;
        for (Target target:targetList) {
            if(targetName.equalsIgnoreCase(target.getName())){
                return target;
            }
        }
        throw new RuntimeException(targetName+" does not exist");
    }

    public List<ArrayList<String>> getAllPaths(String targetNameFrom, String targetNameTo){
        int targetFromIndex = 0,targetToIndex = 0;

        Target targetFrom = getTarget(targetNameFrom);
        Target targetTo = getTarget(targetNameTo);

        for(int i=0;i<targetList.size();i++){
            if(targetList.get(i).equals(targetFrom)){
                targetFromIndex=i;
            }
            if(targetList.get(i).equals(targetTo)){
                targetToIndex=i;
            }
        }



        List<ArrayList<Integer>> indexPaths=graph.getAllPaths(targetFromIndex,targetToIndex);
        return convertIndexPathsToNamePathsString(indexPaths);

    }

    private List<ArrayList<String>> convertIndexPathsToNamePathsString(List<ArrayList<Integer>> indexPaths) {
        List<ArrayList<String>>res= new ArrayList<ArrayList<String>>();
        for(ArrayList<Integer> arr:indexPaths){
            ArrayList<String> newPath=new ArrayList<String>();
            for(Integer i:arr){
                newPath.add(getTargetNameByIndex(i));
            }
            res.add(newPath);
        }
        return res;
    }

    private String getTargetNameByIndex(Integer i) {
        return targetList.get(i).getName();
    }



    public Target getTarget(int i) {
        return targetList.get(i);
    }

    public int getIndex(Target neighbor) {
        return targetList.indexOf(neighbor);
    }

    public void setListTarget(List<Target> targetList) {
        this.targetList=targetList;
    }

    public List<Target> getTargetList(){return targetList;}

    public List<SerialSet> getListOfSerialSet(){return ListOfSerialSet;}

    public List<String> getTargetNamesList(){
        List<String> res = new ArrayList<String>();
        for(Target target:targetList)
            res.add(target.getName());
        return res;
    }

    public String getStringGraph(){
        String res = "";
        for(int i=0;i<graph.getSize();i++){
            res+=targetList.get(i).getName();

            List<Integer> arr=graph.myDependentOn(i);
            if(arr.size()!=0){
                res+="->";
                for(int j=0;j<arr.size();j++){
                    res+=targetList.get(arr.get(j));
                    if(j!=arr.size()-1){
                        res+=",";
                    }
                }
            }
            res+="\n";
        }
        return res;
    }

    public String getGraphName(){return name;}
}

