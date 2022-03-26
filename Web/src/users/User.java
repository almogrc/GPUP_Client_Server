package users;

public class User {
    private String userName;
    private String typeUser;

    public User(String userName,String typeUser){
        this.userName = userName;
        this.typeUser=typeUser;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
