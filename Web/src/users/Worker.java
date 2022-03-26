package users;

import engine.dto.DtoTargetForWorker;

import java.util.ArrayList;
import java.util.List;

public class Worker {
    private String userName;
    private int numOfThreads;
    private Integer money;
    private List<DtoTargetForWorker> dtoTargetForWorkerList= new ArrayList<>();

    public Worker(String userName,int numOfThreads,int money){
        this.money=money;
        this.numOfThreads=numOfThreads;
        this.userName=userName;
    }
    public Worker(){
        this.money=0;
        this.numOfThreads=0;
        this.userName="";
    }


    public List<DtoTargetForWorker> getDtoTargetForWorkerList() {
        return dtoTargetForWorkerList;
    }

    public void setDtoTargetForWorkerList(List<DtoTargetForWorker> dtoTargetForWorkerList) {
        this.dtoTargetForWorkerList = dtoTargetForWorkerList;
    }
    public void addDtoTargetForWorkerList(DtoTargetForWorker dtoTargetForWorker) {
        this.dtoTargetForWorkerList.add(dtoTargetForWorker);
    }


    public String getUserName() {
        return userName;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public synchronized Integer getMoney() {
        return money;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    public synchronized void setMoney(int money) {
        this.money = money;
    }

    public synchronized void addMoney(int price) {
        this.money+=price;
    }
}
