package managerServices;

import users.User;
import users.Worker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private final HashMap<String, User> usersMap;
    private final HashMap<String, Worker> workersMap;

    public UserManager() {
        usersMap=new HashMap<String, User>();
        workersMap=new HashMap<String,Worker>();
    }

    public synchronized void addUser(String userName, String typeUser,int numOfThreads,int money) {
        usersMap.put(userName,new User(userName,typeUser));
        workersMap.put(userName,new Worker(userName,numOfThreads,money));
    }

    public void addUser(String userName, String typeUser) {
        usersMap.put(userName,new User(userName,typeUser));
    }
    /*
    public synchronized void removeUser(String username) {
        usersSet.remove(username);
    }

     */

    public synchronized Set<String> getUsers() {
        return usersMap.keySet();
    }

    public synchronized Set<String> getUsersWithType() {
        Set<String> res=new HashSet<String>();
        for(String name:usersMap.keySet()){
            String newUsersWithExtraData=name+" "+usersMap.get(name).getTypeUser();
            Worker worker=workersMap.get(name);
            /*if(worker!=null){
                    newUsersWithExtraData+=" Threads:"+worker.getNumOfThreads()+
                                            " Money: "+worker.getMoney();
                }

             */
            res.add(newUsersWithExtraData);
        }
        return res;
    }

    public boolean isUserExists(String username) {
        return usersMap.containsKey(username);
    }

    public HashMap<String, User> getUsersMap() {
        return usersMap;
    }

    public HashMap<String, Worker> getWorkersMap() {
        return workersMap;
    }

    public synchronized Integer getMoneyForWorker(String userName) {
        return workersMap.get(userName).getMoney();
    }
}
