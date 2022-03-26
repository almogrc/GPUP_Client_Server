package engine.engineGpup;
//task3//import appController.mainAppController.AppController;
import com.sun.jmx.remote.internal.ArrayQueue;
import engine.engineGpup.Tasks.MyRunTask;
import engine.graph.*;
import engine.dto.*;
import graphViz.GraphViz;
import javafx.scene.control.CheckBox;


import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@XmlRootElement
public class GpupExecution implements Execution {
    final int NO_NEED_TO_RUN_ON = -1;
    private static final String NEW_RUN = "New Run";
    private static final String ACTIVATED = "Activated";
    private static final String PAUSED  = "Paused";
    private static final String STOPPED = "Stopped";
    private static final String OVER = "Over";




    private TargetGraph targetGraph;
    private List<WrapsTarget> wrapsTargetList;

    private String userNameExeCreator;
    
    private MyRunTask myRun;
    private String taskName;

    private String executionStatus;
    private List<String> workersList=new ArrayList<String>();
    private List<String> activeWorkersList=new ArrayList<String>();

    private String userUploadedName;
    private String pathForTaskFolder;
    private String folder;
    private int simulationPrice;
    private int compilationPrice;
    private DetailsForTask runDetails;
    private Queue<Target> targetsThatCanRun = new ArrayDeque<>();
    private List<String> log=new ArrayList<String>();




    //task3//private AppController appController;
    //private  ExecutorService threadExecutor;
    //private int numOfThreads;
    //private ThreadPoolExecutor executor;
    private Thread gpupRunner;
    //private boolean isRunTaskFromScratch;
    //private BlockingQueue<Runnable> runQ;
    //private Runnable onFinish;
    private boolean wasRunBefore = false;
    private int presentOfFinish = 0 ;
    //private Task task;


/*
    private Consumer<String> skippedSummary;
    private Consumer<String> successSummary;
    private Consumer<String> successWWSummary;
    private Consumer<String> failureSummary;
    private Consumer<String> summary;
    private Consumer<String> logData;

 */

    /*
    public GpupExecution(){
        threadExecutor.shutdown();
    }
*/

    /*task3
    public GpupExecution(AppController appController) {
        //this.fileName = new SimpleStringProperty();
        task = null;
        this.appController =appController;
    }*/
/*
    public GpupExecution(File file) throws JAXBException, FileNotFoundException {
        initGpupExecution(file);
    }

 */


    public GpupExecution(GpupExecution another){
        this.targetGraph=new TargetGraph(another.targetGraph);

        this.wrapsTargetList= new ArrayList<WrapsTarget>();
        /*for(Target curr: targetGraph.getTargetList()){
            WrapsTarget curWrapTarget = new WrapsTarget(new DTOTarget(curr));
            for(WrapsTarget wrapsTarget :another.wrapsTargetList ){
                if(wrapsTarget.getName().equals(curWrapTarget.getName())){

                }
            }
            wrapsTargetList.add(curWrapTarget);
        }

         */

        this.setTaskName(another.getTaskName());
        this.executionStatus=NEW_RUN;

        this.setUserUploadedName(another.getUserUploaded());
        this.pathForTaskFolder="/gpup-working-dir";
        this.setSimulationPrice(another.getSimulationPrice());
        this.setCompilationPrice(another.getCompilationPrice());

        //this.targetsThatCanRun= another.targetsThatCanRun;
      //  this.runDetails=new DetailsForTask(another.getRunDetails());
    }

    public GpupExecution(InputStream inputStream, String userUploadedName) throws JAXBException, FileNotFoundException {
        GpupGraphChecker graphChecker = new GpupGraphChecker(inputStream);
        //pathForTaskFolder = graphChecker.getPathForTaskFolder();

        pathForTaskFolder="/gpup-working-dir";
        //task=null;
        this.userUploadedName=userUploadedName;
        targetGraph=new TargetGraph(graphChecker.getGraph(),inputStream);

        simulationPrice=graphChecker.getSimulationPrice();
        compilationPrice=graphChecker.getCompilationPrice();
        wrapsTargetList= new ArrayList<WrapsTarget>();
        for(Target curr: targetGraph.getTargetList()){
            wrapsTargetList.add(new WrapsTarget(new DTOTarget(curr,this.taskName)));
        }
    }

    private void setCompilationPrice(int compilationPrice) {
        this.compilationPrice=compilationPrice;
    }

    private void setSimulationPrice(int simulationPrice) {
        this.simulationPrice=simulationPrice;
    }

    private void setUserUploadedName(String userUploadedName) {
        this.userUploadedName=userUploadedName;
    }

    public List<String> getActiveWorkersList() {
        return activeWorkersList;
    }

    public void setActiveWorkersList(List<String> activeWorkersList) {
        this.activeWorkersList = activeWorkersList;
    }


    public GpupExecution(InputStream inputStream, DtoNewTask newTask, String userNameLoaded,TargetGraph oldTargetGraph) throws JAXBException, FileNotFoundException {
        GpupGraphChecker graphChecker = new GpupGraphChecker(inputStream);
        this.userUploadedName=userNameLoaded;
        this.targetGraph=new TargetGraph(graphChecker.getGraph(),inputStream);
        this.simulationPrice=graphChecker.getSimulationPrice();
        this.compilationPrice=graphChecker.getCompilationPrice();
        this.wrapsTargetList= new ArrayList<WrapsTarget>();
        this.pathForTaskFolder="/gpup-working-dir";
        this.executionStatus=NEW_RUN;
        this.taskName=newTask.getTaskName();
        this.userNameExeCreator=newTask.getUserNameCreator();
        if(newTask.isSimulation()){
            DtoSimulationTask dtoSimulationTask=newTask.getDtoSimulationTask();
            this.runDetails=new DetailsForTask(dtoSimulationTask.getProcessTimeInteger(),dtoSimulationTask.getProbSuccessDouble(),dtoSimulationTask.getProbSuccessWWarningsDouble(),dtoSimulationTask.getRandom(),true);
        }else{
            DtoCompilationTask dtoCompilation=newTask.getDtoCompilationTask();
            this.runDetails=new DetailsForTask(dtoCompilation);//todo
        }
        for(Target curr:this.targetGraph.getTargetList()){
            Target oldTarget=oldTargetGraph.getTarget(curr.getName());
            curr.setFinishStatus(oldTarget.getFinishStatus());
            curr.setTargetStatus(oldTarget.getTargetStatus());
        }
        for(Target curr: targetGraph.getTargetList()){
            wrapsTargetList.add(new WrapsTarget(new DTOTarget(curr,this.taskName)));
        }
    }

    public TargetGraph getTargetGraph(){
        return targetGraph;
    }

    public String getGraphName(){return targetGraph.getGraphName();}

    /*
    public void initGpupExecution(File file) throws JAXBException, FileNotFoundException {
        GpupGraphChecker graphChecker = new GpupGraphChecker(file);
        //pathForTaskFolder = graphChecker.getPathForTaskFolder();
        pathForTaskFolder="/gpup-working-dir";
        task=null;
        targetGraph=new TargetGraph(graphChecker.getGraph(),file);
        wrapsTargetList= new ArrayList<WrapsTarget>();
        for(Target curr: targetGraph.getTargetList()){
            wrapsTargetList.add(new WrapsTarget(new DTOTarget(curr)));
        }
    }

     */

    /*
    public List<String> getListOfNamesSetsForTargetName(String targetName){
        for(WrapsTarget wrapsTarget: wrapsTargetList){
            if(wrapsTarget.getName().equals(targetName)){
                return wrapsTarget.getListOfNamesSets();
            }
        }
        return null;
    }

     */

    public WrapsTarget getWrapsTargetByName(String name){////////////////////////////
        for(WrapsTarget wrapsTarget: wrapsTargetList){
            if(wrapsTarget.getName().equals(name)){
                return wrapsTarget;
            }
        }
        return null;
    }

    public int getPresentOfFinish() {
        return presentOfFinish;
    }

    public void setPresentOfFinish(int presentOfFinish) {
        this.presentOfFinish = presentOfFinish;
    }

    public void setWorkersList(List<String> workersList) {
        this.workersList = workersList;
    }
    /*
    private void setThreadPool(int numOfThreads) {
        //threadExecutor = Executors.newFixedThreadPool(numOfThreads);
        this.numOfThreads=numOfThreads;
        this.executor = new ThreadPoolExecutor(numOfThreads,numOfThreads,2, TimeUnit.HOURS, new LinkedBlockingQueue<Runnable>());

    }

     */



    /*private Dto runTaskIncremental() throws InterruptedException, IOException {
        List<Target> realTargets=getChosenTargets();
        //the for update the indegree of the vertex
        List<Integer> indegree = new ArrayList<Integer>(targetGraph.getNumOfTargets());
        for (int i = 0; i < targetGraph.getNumOfTargets(); i++) {
            indegree.add(0);
            for(Target currTarget:targetGraph.getTarget(i).getDependingOn()){
                if((currTarget.getFinishStatus().equals(Target.Finish.FAILURE)||currTarget.getFinishStatus().equals(Target.Finish.NOTFINISHED))&&realTargets.contains(currTarget)){
                    indegree.set(i, indegree.get(i)+1);
                }
            }
        }

        for (int i = 0; i < targetGraph.getNumOfTargets(); i++){
            Target currTarget=targetGraph.getTarget(i);
            if(realTargets.contains(currTarget)) {
                //if target success/with warnings we don't want to run on him
                if (currTarget.getFinishStatus().equals(Target.Finish.SUCCESS) || currTarget.getFinishStatus().equals(Target.Finish.SUCCESS_WITH_WARNINGS)) {
                    indegree.set(i, NO_NEED_TO_RUN_ON);
                } else {//target was failure or no_finished so we want initialize him to frozen and NOTFINISHED
                    targetGraph.getTarget(i).setTargetStatus(Target.TargetStatus.FROZEN);
                    targetGraph.getTarget(i).setFinishStatus(Target.Finish.NOTFINISHED);
                }
            }else{
                indegree.set(i, NO_NEED_TO_RUN_ON);
            }

        }



        return runTask(indegree);
    }*/

    /*private Dto runTaskFromScratch() throws InterruptedException, IOException {
        List<Target> realTargets=getChosenTargets();
        //the for update the indegree of the vertex
        List<Integer> indegree = new ArrayList<Integer>(targetGraph.getNumOfTargets());
        Target currTarget;
        int realDependOn;
        for (int i = 0; i < targetGraph.getNumOfTargets(); i++) {
            realDependOn=0;
            currTarget=targetGraph.getTarget(i);
            if(realTargets.contains(currTarget)){
                for(Target depTarget:currTarget.getDependingOn()){
                    if(realTargets.contains(depTarget)){
                        realDependOn++;
                    }
                }
                indegree.add(realDependOn);
            }else{
                indegree.add(NO_NEED_TO_RUN_ON);
            }
        }
        return runTask(indegree);
    }*/

   // private Dto runTask(List<Integer> indegree) throws InterruptedException, IOException {
   //     Date startDate = new Date();
//
   //     List<DTOTaskOnTarget> dtoArr=new ArrayList<DTOTaskOnTarget>();
   //     Queue<Target> queue = new ArrayDeque<Target>();
//
   //     //this for insert to the queue all the vertex that there inDegree is 0
   //     for (int i = 0; i < targetGraph.getNumOfTargets(); i++) {
   //         if (indegree.get(i) == 0) {
   //             targetGraph.getTarget(i).setTargetStatus(Target.TargetStatus.WAITING);
   //             queue.add(targetGraph.getTarget(i));
   //         }
   //     }
//
   //     ExecutorService threadExecutor = Executors.newFixedThreadPool(3);
//
   //     while (!queue.isEmpty()) {
   //         Target currentTarget = queue.poll();
//
//
   //         threadExecutor.execute(task);
//
   //         //change *all* the dependent on currentTarget to skipped
   //         if (currentTarget.getFinishStatus().equals(Target.Finish.FAILURE)) {
   //             List<Target> skippedList=new ArrayList<Target>();
   //             makeSkipped(currentTarget,skippedList);
   //             newDto.setTargetsSkipped(skippedList);
   //         }
//
   //         //foreach neighbor of currentTarget
   //         for (Target neighbor : currentTarget.getRequiredTo()) {
   //             //indegree[i]=indegree[i]-1
   //             int indexOfNeighbor = targetGraph.getIndex(neighbor);
   //             indegree.set(indexOfNeighbor, indegree.get(indexOfNeighbor) - 1);
//
   //             //if indegree's neighbor is 0 push to queue
   //             //neighbor can't finish, because only a target that has been run on them can have a finish, and it is not possible to run on a target unless its indegree is 0, and only now the target 'neighbor' get to 0
   //             if (indegree.get(indexOfNeighbor) == 0) {
   //                 //if neighbor status is frozen it means it is possible to run him so his status changed to frozen
   //                 if (neighbor.getTargetStatus().equals(Target.TargetStatus.FROZEN)) {
   //                     neighbor.setTargetStatus(Target.TargetStatus.WAITING);
   //                 }
   //                 queue.add(neighbor);
   //                 newDto.addOpenedTarget(neighbor);
   //             }
   //         }
   //         dtoArr.add(newDto);
   //         writeToFile(newDto);
   //     }
//
   //     for (int i =0 ; i <indegree.size() ;i++) {
   //         if(indegree.get(i)>0){
   //             //there is a circle
   //             dtoArr.add(new DTOTaskOnTarget(targetGraph.getTarget(i).getName(),targetGraph.getTarget(i).getData(),targetGraph.getTarget(i).getTargetStatus() ));
   //         }
   //     }
   //     Date endDate = new Date();
   //     //
   //     return new DtoTaskOnTargets(LocalTime.ofSecondOfDay((endDate.getTime() - startDate.getTime())/ 1000),dtoArr);
   // }
/*
    private void writeToFile(DTOTaskOnTarget newDto) throws IOException {
        try (Writer out1 = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(folder+"//"+newDto.getName()+".log"), "UTF-8"))) {
            out1.write(newDto.toString());
        }
        catch (Exception e){
             throw e;
        }
    }

 */
    /*
    ////maybe not needed
    private void makeSkipped(Target currentTarget,List<Target> skippedList,List<Target> realRun) {
        if(realRun.contains(currentTarget)){
            if(currentTarget.getLocation().equals(Target.Location.ROOT)){
                return;
            }
            else{
                for(Target neighbor:currentTarget.getRequiredTo()) {
                    if(realRun.contains(neighbor)){
                        neighbor.setTargetStatus(Target.TargetStatus.SKIPPED);
                        if (!skippedList.contains(neighbor)) {
                            skippedList.add(neighbor);
                            makeSkipped(neighbor, skippedList, realRun);
                        }
                    }
                }
            }
        }
    }

 */



    public Dto getCircleOfTarget(String targetName){
        Target target= targetGraph.getTarget(targetName);

        List<String> res=new ArrayList<String>();
        boolean cont=true;
        for(Target neighbor:target.getRequiredTo()){
            List<ArrayList<String>> allPathsFromNeighbor=targetGraph.getAllPaths(neighbor.getName(),target.getName());

            //for all paths
            for(ArrayList<String> arr : allPathsFromNeighbor){
                //if target name contain in arr there circle that target in
                if(arr.contains(target.getName())){

                    //move all target name to res until see target's name
                    for(int i=0;i<arr.size() && cont;i++){
                        if(arr.get(i).equals(target.getName())){
                            cont=false;

                        }
                        res.add(arr.get(i));

                    }
                }
            }
        }

        return new DtoTargetCircle(res,target.getName());
    }

    /*
    @Override
    public void readObjectFromFile(String filePath){
        File file=new File(filePath+".dat");
        if(!file.exists()){
            throw new RuntimeException("file does not exists");
        }
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(filePath+".dat"))){
            this.targetGraph=(TargetGraph) in.readObject();
            this.wasRunBefore=(boolean) in.readObject();
            this.pathForTaskFolder=(String)in.readObject();
            this.task=(Task) in.readObject();
            this.folder=(String) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
            throw new IllegalArgumentException(" couldn't load instance from file");
        }
    }

    @Override
    public void writeObjectToFile(String path){
        checkIfGraphIsLoaded();

        path +=".dat";
        validatePath(path);
        try(ObjectOutputStream out=
                    new ObjectOutputStream(
                            new FileOutputStream(path))){
            out.writeObject(targetGraph);
            out.writeObject(wasRunBefore);
            out.writeObject(pathForTaskFolder);
            out.writeObject(task);
            out.writeObject(folder);
            out.flush();
        }catch (IOException e){
            throw new IllegalArgumentException("couldn't write instance to file " + e.getMessage());
        }
    }


     */
    private void validatePath(String path) {
        File tempFile = new File(path);
        if(!tempFile.getParentFile().exists()){
            throw new RuntimeException("file does not exist");
        }
    }

    private void checkIfGraphIsLoaded() {
        if(targetGraph==null){
            throw new RuntimeException("There's no any data to save.");
        }
    }



    /** Creates parent directories if necessary. Then returns file */
    private static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory + "/" + filename);
    }

    private void createFileForTask(String typeOfTask) {
        Date date=new Date();

        //SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");


        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
        folder=pathForTaskFolder+"\\"+ typeOfTask+" - "+sdf.format(date);
        File file=fileWithDirectoryAssurance(pathForTaskFolder,"\\"+ typeOfTask+" - "+sdf.format(date));
        File temp=new File(pathForTaskFolder);
        if(!temp.exists()){
            throw new RuntimeException("There no path like: "+ pathForTaskFolder);
        }
        if(!file.mkdir()){
            throw new RuntimeException("Can not open folder.");
        }
    }

/*
    public int getNumOfThreads() {
        return targetGraph.getMaxParallelism();
    }


 */
    /*
    @Override
    public void run() {
        Dto res =null;
        setThreadPool(runDetails.getNumOfThreads());
        if(runDetails.isIncremental()){
            handleIncremental();
        }else{
            handleScratch();
        }
        runTask();
        wasRunBefore = true;
        //return res;
    }
    */



    public void runTask(
                        //DetailsForTask details,
                        //Consumer<String> successSummary,
                        //Consumer<String> successWWSummary,
                        //Consumer<String> failureSummary,
                        //Consumer<String> skippedSummary,
                        //Consumer<String> summary,
                        //Consumer<String> logData,
                       ) {
        //this.runDetails=details;
        //this.successSummary=successSummary;
        //this.successWWSummary=successWWSummary;
        //this.failureSummary=failureSummary;
        //this.skippedSummary=skippedSummary;
        //this.summary=summary;
        //this.onFinish=onFinish;
        //this.logData=logData;
        // createFileForTask("Simulation");


        //setThreadPool(runDetails.getNumOfThreads());


        if(runDetails.isSimulation()){
            createFileForTask("Simulation");
        }else if(!runDetails.isSimulation()){
            createFileForTask("Compilation");
        }
        List<Target> chosenTargets = new ArrayList<>();
        for(WrapsTarget wrapsTarget:wrapsTargetList){
            chosenTargets.add(getTarget(wrapsTarget.getName()));
        }
        myRun=new MyRunTask(this.runDetails,
                chosenTargets,
                targetsThatCanRun,
                //executor,
                //successSummary,
                //successWWSummary,
                //failureSummary,
                //skippedSummary,
                //summary,
                folder,
                this);
                //logData);
        //task3//appController.bindTaskToUIComponents(myRun,onFinish);
        //myRun.run();

        gpupRunner = new Thread(myRun);
        gpupRunner.setName("gpupRunner");
        gpupRunner.start();

    }


    private void handleIncremental() {
        List<Target> curList= getChosenTargets();
        for(Target curr: curList){
            if(curr.getFinishStatus().equals(Target.Finish.FAILURE)||curr.getTargetStatus().equals(Target.TargetStatus.SKIPPED)){
                curr.setTargetStatus(Target.TargetStatus.FROZEN);
                curr.setFinishStatus(Target.Finish.NOTFINISHED);
            }
        }
    }

    private void handleScratch() {
        List<Target> curList= getChosenTargets();
        for(Target curr: curList){
            curr.setTargetStatus(Target.TargetStatus.FROZEN);
            curr.setFinishStatus(Target.Finish.NOTFINISHED);
        }
    }

    /*
    public void stopRunning(){
        if(gpupRunner!=null){
            gpupRunner.stop();
        }
    }

     */
    public synchronized List<Target> makeSkipped(Target currentTarget) {
        List<Target> chosenTarget=new ArrayList<>();
        for(WrapsTarget curr: wrapsTargetList){
            chosenTarget.add(getTarget(curr.getName()));
        }

        List<Target> skippedList= new ArrayList<>();
        recSkipped(currentTarget, skippedList,chosenTarget);
        return skippedList;
    }

    private void recSkipped(Target currentTarget, List<Target> skippedList, List<Target>chosenTargets) {
        if(chosenTargets.contains(currentTarget)){
            if(currentTarget.getLocation().equals(Target.Location.ROOT)){
                return;
            }
            else{
                for(Target neighbor: currentTarget.getRequiredTo()) {
                    if(chosenTargets.contains(neighbor)){

                        if(!neighbor.getTargetStatus().equals(Target.TargetStatus.SKIPPED)){
                            neighbor.setTargetStatus(Target.TargetStatus.SKIPPED);
                            addLog(neighbor.getName()+ " is "+neighbor.getTargetStatus().toString());

                        }

                        if (!skippedList.contains(neighbor)) {
                            skippedList.add(neighbor);
                            recSkipped(neighbor, skippedList,chosenTargets);
                        }
                    }
                }
            }
        }
    }
    public synchronized void addLog(String newLog){
        log.add(newLog);
    }

    public Dto getTargetsData(){
        return new DTOTargetsData(targetGraph.getNumOfTargets(),
                targetGraph.getNumOfLeaf(),
                targetGraph.getNumOfMiddle(),
                targetGraph.getNumOfRoot(),
                targetGraph.getNumOfIndependent());
    }

    @Override
    public Dto getTargetData(String targetName) {
        Target target = targetGraph.getTarget(targetName);
        return new DTOTargetData(target.getName(),
                target.getLocation(),
                target.getRequiredToTargetsName(),
                target.getDependingOnTargetsName(),
                target.getData());

    }

    @Override
    public Dto getAllPathsBetweenTargets(String targetFrom, String targetTo,boolean isRequired) {
        if(isRequired){
            return new DTOPathsBetweenTargets(targetGraph.getTarget(targetFrom).getName(), targetGraph.getTarget(targetTo).getName(),targetGraph.getAllPaths(targetFrom, targetTo), isRequired);
        }else{
            return new DTOPathsBetweenTargets(targetGraph.getTarget(targetFrom).getName(), targetGraph.getTarget(targetTo).getName(),targetGraph.getAllPaths(targetTo, targetFrom), isRequired);
        }

    }

    //public boolean getWasRunBefore(){ return wasRunBefore;}


    public List<Target> getChosenTargets(){
        List<Target> res= new ArrayList<Target>();
        for(WrapsTarget wrapsTarget:wrapsTargetList){
            res.add(getTarget(wrapsTarget.getName()));
        }
        
        return res;
    }/////////////////////////////////

    public List<SerialSet> getListOfSerialSet(){return targetGraph.getListOfSerialSet(); }

    public List<String> getAllOfIndirect(String targetName, boolean isRequired) {
        Target target= targetGraph.getTarget(targetName);
        Set<Target> targetSet;
        List<String> res=new ArrayList<String>();
        res.add(targetName);
        if(isRequired)
        {
            targetSet = target.getAllOfIndirectRequiredFor();
            for(Target targetTemp:targetSet){
                res.add(targetTemp.getName());
            }
        }
        else {
            targetSet = target.getAllOfIndirectDependsOn();;
            for(Target targetTemp:targetSet){
                res.add(targetTemp.getName());
            }
        }
        if(res.size()==0)
        {
            if(isRequired){
                res.add("There are no targets that directly/indirectly required for "+targetName+".");
            }
            else {
                res.add("There are no targets that directly/indirectly depends on "+targetName+".");
            }
        }

        return res;
    }

    public List<WrapsTarget> getWrapsTargetList(){
        return wrapsTargetList;
    }////////////////////////////////

    public List<DTOTarget> getDTOTargetList(){
        List<DTOTarget> res=new ArrayList<DTOTarget>();
        for(Target target :targetGraph.getTargetList()){
            res.add(new DTOTarget(target,this.taskName));
        }
        return res;
    }

    public List<String> getTargetNamesList(){
        List<String> res=new ArrayList<String>();
        for (WrapsTarget wrapsTarget:wrapsTargetList) {
            res.add(wrapsTarget.getName());
        }
        return res;
    }

    public int getNumOfTargets(){ return wrapsTargetList.size();}

    public int getNumOfIndependent(){
        int res=0;
        for (WrapsTarget wrapsTarget:wrapsTargetList) {
            if(wrapsTarget.getDTOTarget().getLocation().equals((Target.Location.INDEPENDENT).toString())){
                res++;
            }
        }
        return res;
        }

    public int getNumOfLeaf(){
        int res=0;
        for (WrapsTarget wrapsTarget:wrapsTargetList) {
            if(wrapsTarget.getDTOTarget().getLocation().equals((Target.Location.LEAF).toString())){
                res++;
            }
        }
        return res;
    }

    public int getNumOfMiddle(){
        int res=0;
        for (WrapsTarget wrapsTarget:wrapsTargetList) {
            if(wrapsTarget.getDTOTarget().getLocation().equals((Target.Location.MIDDLE).toString())){
                res++;
            }
        }
        return res;
    }

    public int getNumOfRoot(){
        int res=0;
        for (WrapsTarget wrapsTarget:wrapsTargetList) {
            if(wrapsTarget.getDTOTarget().getLocation().equals((Target.Location.ROOT).toString())){
                res++;
            }
        }
        return res;
    }

    public void stopTask(boolean isStopTask) {//todo
    }
    /*
        public void stopTask(boolean isStopTask){
        if(isStopTask){
            gpupRunner.suspend();

            runQ =executor.getQueue();

            executor.shutdown();

        }else{
            gpupRunner.resume();
            this.executor = new ThreadPoolExecutor(numOfThreads, numOfThreads, 2, TimeUnit.HOURS, runQ);

            myRun=new MyRunTask(runDetails,
                    getChosenTargets(),
                    executor,
                    successSummary,
                    successWWSummary,
                    failureSummary,
                    skippedSummary,
                    summary,
                    folder,
                    logData);
            gpupRunner = new Thread(myRun);
            gpupRunner.setName("gpupRunner");
            gpupRunner.start();

            //task3//appController.bindTaskToUIComponents(myRun,onFinish);
            //myRun.setThreadPoolExecutor(this.executor);
            //myRun.setThreadPoolExecutor(temp);

        }
    }

     */


    /*
    public void setNumOfThreads(int numOfThreads){
        this.numOfThreads=numOfThreads;
    }


     */






    public void getGraphUsingGraphViz(String outPutPath, String filesNames) {

        try {
            GraphViz gv = new GraphViz(getExeFromEnvironmentVar(), outPutPath);
            gv.addln(gv.start_graph());
            gv.addln(targetGraph.getStringGraph());
            gv.addln(gv.end_graph());
            gv.increaseDpi(); // 106 dpi
            System.out.println(gv.getDotSource());
            String type = "png";

            String representationType = "dot";

            File out = new File(outPutPath + File.separator + filesNames + "." + type);
            gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, representationType), out);
            saveDotTextFile(outPutPath, filesNames, gv);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error - dot executable not found");
        }
    }

    private void saveDotTextFile(String outPutPath, String filesNames, GraphViz gv) {
        try {
            FileWriter fw = new FileWriter(outPutPath + File.separator + filesNames + ".viz");
            fw.write(gv.getDotSource());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error - could not write the dot txt file");
        }
    }

    private String getExeFromEnvironmentVar() {
        return Arrays.stream(System.getenv("Path").split(";"))
                .filter(path -> path.contains("Graphviz"))
                .map(path -> path.concat("\\dot.exe"))
                .findFirst()
                .orElse("C:\\Program Files\\Graphviz\\bin\\dot.exe");
    }


    public String getUserUploaded(){return userUploadedName;}

    public String getUserNameLoaded() {
        return userUploadedName;
    }

    public int getSimulationPrice() {
        return simulationPrice;
    }
    public int getCompilationPrice() {
        return compilationPrice;
    }

    public Target getTarget(String targetName) {
        return targetGraph.getTarget(targetName);
    }

    public DetailsForTask getRunDetails(){return runDetails;}

    public int getPriceForExe() {
        if(runDetails.isSimulation()){
            return wrapsTargetList.size()*simulationPrice;
        }else{
            return wrapsTargetList.size()*compilationPrice;
        }
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName){
        this.taskName=taskName;
    }

    public String getUserNameExeCreator() {
        return userNameExeCreator;
    }

    public void setUserNameExeCreator(String userNameExeCreator){
        this.userNameExeCreator=userNameExeCreator;
    }

    public String getExeStatus() {
        return executionStatus;
    }

    public void setExeStatus(String executionStatus){
        this.executionStatus=executionStatus;
    }

    public synchronized int getNumOfWorkers() {
        return workersList.size();
    }

    public List<String> getWorkersList() {
        return workersList;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setTask(DtoNewTask newTask) {
        //this.workersList=newTask.getTargetListName();todo insertWorkersList
        this.taskName=newTask.getTaskName();
        this.userNameExeCreator=newTask.getUserNameCreator();
        if(newTask.isSimulation()){
            DtoSimulationTask dtoSimulationTask=newTask.getDtoSimulationTask();
            this.runDetails=new DetailsForTask(dtoSimulationTask.getProcessTimeInteger(),dtoSimulationTask.getProbSuccessDouble(),dtoSimulationTask.getProbSuccessWWarningsDouble(),dtoSimulationTask.getRandom(),true);
        }else{
            DtoCompilationTask dtoCompilation=newTask.getDtoCompilationTask();
            this.runDetails=new DetailsForTask(dtoCompilation);//todo
        }
    }
    public void setTask(DtoExeGpup oldTask, DetailsForTask runDetails) {

        this.userNameExeCreator=oldTask.getCreateBy();
        this.runDetails=runDetails;



    }


    public void setChosenTargetList(DtoNewTask newTask) {
        List<String> chosenTargetNames = newTask.getTargetListName();
        for(Target curr: targetGraph.getTargetList()){
            for(String targetName:chosenTargetNames){
                if(curr.getName().equals(targetName)) {
                    WrapsTarget curWrapTarget = new WrapsTarget(new DTOTarget(curr,this.taskName));
                    wrapsTargetList.add(curWrapTarget);
                }
            }
        }
    }

    public void setChosenTargetList(List<Target> chosenTargets) {
        for(Target curr: chosenTargets) {
            WrapsTarget curWrapTarget = new WrapsTarget(new DTOTarget(curr,this.taskName));
            wrapsTargetList.add(curWrapTarget);
        }
    }

    public void setExeToFromScratchRun() {
        for(WrapsTarget wrapsTarget: wrapsTargetList){
            wrapsTarget.setStatus("FROZEN");
            wrapsTarget.setFinishStatus("NOTFINISHED");
            getTarget(wrapsTarget.getName()).setFinishStatus(Target.Finish.NOTFINISHED);
            getTarget(wrapsTarget.getName()).setTargetStatus(Target.TargetStatus.FROZEN);
        }
    }

    public void setExeToFromIncrementalRun() {
        for(WrapsTarget wrapsTarget: wrapsTargetList){
            Target target=getTarget(wrapsTarget.getName());
            if(target.getTargetStatus().equals(Target.TargetStatus.SKIPPED)||target.getFinishStatus().equals(Target.Finish.FAILURE)){
                wrapsTarget.setStatus("FROZEN");
                wrapsTarget.setFinishStatus("NOTFINISHED");
                target.setFinishStatus(Target.Finish.NOTFINISHED);
                target.setTargetStatus(Target.TargetStatus.FROZEN);
           }
        }
    }

    public synchronized Target getTargetToProcess() {
        if(!targetsThatCanRun.isEmpty() && targetsThatCanRun!=null) {
            return targetsThatCanRun.remove();
        }
        return null;
    }


    public synchronized List<String> allOpenedTargets(Target target) {
        List<Target> chosenTarget=new ArrayList<>();
        for(WrapsTarget curr: wrapsTargetList){
            chosenTarget.add(getTarget(curr.getName()));
        }
        List<String> openList=new ArrayList<String>();
        for(Target curr: target.getRequiredTo()){
            if(chosenTarget.contains(curr)){
                boolean Open=true;

                for(Target currFather: curr.getDependingOn()){
                    if(chosenTarget.contains(currFather)) {
                        if(!(currFather.getTargetStatus().equals(Target.TargetStatus.FINISHED)||currFather.getTargetStatus().equals(Target.TargetStatus.SKIPPED))){
                            Open = false;
                        }
                    }
                }
                if(Open){
                    openList.add(curr.getName());
                }
            }
        }

        return openList;
    }

    public void updatePresentOfFinish(int numOfEnd) {
        this.presentOfFinish = numOfEnd*100/ getChosenTargets().size();
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public void setExecutionStatus(String exeStatus){
        this.executionStatus=exeStatus;
    }


}


