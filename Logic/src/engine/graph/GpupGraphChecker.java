package engine.graph;

import generated.GPUPConfiguration;
import generated.GPUPDescriptor;
import generated.GPUPTarget;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;


public class GpupGraphChecker {
    final static String JAXB_XML_PACKAGE_NAME = "generated";
    public final static int NOT_EXIST = 0;

    private Graph graph;
    private  List<GPUPTarget> arrTargets;
    private GPUPDescriptor.GPUPSerialSets serialSets;
    //GPUPDescriptor.GPUPSerialSets.GPUPSerialSet serialSets;

    private GPUPDescriptor descriptor;/////
    private int simulationPrice=NOT_EXIST;
    private int compilationPrice=NOT_EXIST;

    public GpupGraphChecker(File file) throws JAXBException, FileNotFoundException {
        descriptor = JAXBTransform(file);
        int targetsSize=descriptor.getGPUPTargets().getGPUPTarget().size();
        arrTargets =descriptor.getGPUPTargets().getGPUPTarget();
        serialSets =descriptor.getGPUPSerialSets();
        graph = new Graph(targetsSize);

        insertEdgesIfExist(targetsSize, descriptor);
        IsThereAnyDoubleDependency();
        checkSerialSetsTargetNames();
        checkSerialSetsNames();
    }

    public GpupGraphChecker(InputStream inputStream) throws JAXBException {
        descriptor = JAXBTransform(inputStream);
        int targetsSize=descriptor.getGPUPTargets().getGPUPTarget().size();
        arrTargets =descriptor.getGPUPTargets().getGPUPTarget();
        serialSets =descriptor.getGPUPSerialSets();
        simulationPrice=NOT_EXIST;
        compilationPrice=NOT_EXIST;
        for(int i=0;i<descriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask().size();i++)
        {
            if(descriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask().get(i).getName().equals("Simulation")){
                simulationPrice=descriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask().get(i).getPricePerTarget();
            }else if(descriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask().get(i).getName().equals("Compilation")){
                compilationPrice=descriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask().get(i).getPricePerTarget();
            }else{
                System.out.println("something went wrong in- public GpupGraphChecker(InputStream inputStream) throws JAXBException");
            }

        }
        graph = new Graph(targetsSize);

        insertEdgesIfExist(targetsSize, descriptor);
        IsThereAnyDoubleDependency();
        checkSerialSetsTargetNames();
        checkSerialSetsNames();
    }


    private void checkSerialSetsNames() {
        int size=0;
        if (serialSets != null){
           size = serialSets.getGPUPSerialSet().size();
        }
        Set<String> setOfStringSetNames=new HashSet<String>();
        for(int i = 0; i<size ;i++){
            if(setOfStringSetNames.contains(serialSets.getGPUPSerialSet().get(i).getName())){
                throw new RuntimeException(serialSets.getGPUPSerialSet().get(i).getName() + " is not a unique Serial Set name! please enter a valid XML file to continue.");
            }
            else{
                setOfStringSetNames.add(serialSets.getGPUPSerialSet().get(i).getName());
            }
        }
    }

    private void checkSerialSetsTargetNames(){
        int size=0;
       if(serialSets!=null) {
           size = serialSets.getGPUPSerialSet().size();
       }
       List<String> targetsNamesFromSerialSets = new ArrayList<String>();
       GPUPDescriptor.GPUPSerialSets.GPUPSerialSet currSerialSet;
       String targetsNames;

       for(int i =0;i <size;i++){
           currSerialSet=serialSets.getGPUPSerialSet().get(i);
           targetsNames = currSerialSet.getTargets();
           targetsNamesFromSerialSets= Arrays.asList(targetsNames.split(","));
           for(String name:targetsNamesFromSerialSets){
               boolean found = false;
               for(int j = 0; j<arrTargets.size() &&!found;j++){
                   if(arrTargets.get(j).getName().equalsIgnoreCase(name)){
                       found=true;
                   }
               }
               if(found == false)
                   throw new RuntimeException(name + " is not a real target & was found in a serial set! please enter a valid XML file to continue.");

           }
       }
    }

    private void IsThereAnyDoubleDependency(){
        for(int i=0;i<graph.getSize();i++) {
            for (int j = 0; j < graph.getSize(); j++) {
                if (graph.isEdgeExist(i, j) && graph.isEdgeExist(j, i)) {
                   throw new RuntimeException("There is a 'Double Dependency' between two targets in graph,"+ arrTargets.get(i).getName()+" and "+ arrTargets.get(j)+" please enter a valid XML file to continue");

                }
            }
        }

    }

    private void insertEdgesIfExist(int targetsSize, GPUPDescriptor descriptor){
        List<String> namesTarget=new ArrayList<String>(targetsSize);

        //{A,B,C,A}
        for(int i=0;i<targetsSize;i++){//get all the target names to namesTarget array
            String newTargetName=descriptor.getGPUPTargets().getGPUPTarget().get(i).getName();
            for(String currName:namesTarget){
                if(newTargetName.equalsIgnoreCase(currName)){
                    throw new RuntimeException(newTargetName+" already exist in graph, please insert a valid XML file to continue.");
                }
            }
            namesTarget.add(descriptor.getGPUPTargets().getGPUPTarget().get(i).getName());// target name is valid.

        }

        //The following loop puts into the graph all the edges according to the requiredFor or the dependsOn of the file
        for(int i=0;i<targetsSize;i++){

            if(descriptor.getGPUPTargets().getGPUPTarget().get(i).getGPUPTargetDependencies()!=null){
                int dependencySize = descriptor.getGPUPTargets().getGPUPTarget().get(i).getGPUPTargetDependencies().getGPUGDependency().size();

                for(int j=0;j<dependencySize;j++){

                    String currentNeighbor=descriptor.getGPUPTargets().getGPUPTarget().get(i).getGPUPTargetDependencies().getGPUGDependency().get(j).getValue();
                    if(namesTarget.contains(currentNeighbor)){

                        int indexOfNeighbor = namesTarget.indexOf(currentNeighbor);
                        if(descriptor.getGPUPTargets().getGPUPTarget().get(i).getGPUPTargetDependencies().getGPUGDependency().get(j).getType().equals("requiredFor")){
                            graph.addEdge(i,indexOfNeighbor);

                        }else if(descriptor.getGPUPTargets().getGPUPTarget().get(i).getGPUPTargetDependencies().getGPUGDependency().get(j).getType().equals("dependsOn")){
                            graph.addEdge(indexOfNeighbor,i);
                        }
                    }else{
                        throw new RuntimeException(currentNeighbor + " does not exist in graph, please enter a valid XML file to continue.");

                    }
                }
            }
        }

    }

    private GPUPDescriptor JAXBTransform(InputStream inputStream) throws JAXBException {
        GPUPDescriptor descriptor = null;
        try {
            descriptor = deserializeFrom(inputStream);

        } catch (JAXBException e) {///מה עושים אם נפל הגאקסוי
            throw  e;
        }
        return descriptor;
    }

    private GPUPDescriptor JAXBTransform(File file) throws JAXBException, FileNotFoundException {
        GPUPDescriptor descriptor = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            descriptor = deserializeFrom(inputStream);

        } catch (JAXBException | FileNotFoundException e) {///מה עושים אם נפל הגאקסוי
            throw  e;
        }
        return descriptor;
    }

    private static GPUPDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        //GPUPDescriptor test= (GPUPDescriptor) u.unmarshal(in);
        return (GPUPDescriptor) u.unmarshal(in);
    }

    public Graph getGraph() {
        return graph;
    }

    public int getSimulationPrice() {
        return simulationPrice;
    }


    public int getCompilationPrice() {
        return compilationPrice;
    }
/*    public String getPathForTaskFolder() {
        return descriptor.getGPUPConfiguration().getGPUPWorkingDirectory();
    }

 */


}
