package sample.model;

public class User {
    private String name, userName, id, password, access;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", access='" + access + '\'' +
                '}';
    }

    public User(String name, String userName, String id, String password, String access) {
        this.name = name;
        this.userName = userName;
        this.id = id;
        this.password = password;
        this.access = access;
    }

    public User() {

    }

}
