

public class User {

    protected int id;
    protected String username;
    protected String password;
    protected String role; // if im admin or a simpler buyer


    public User(int ID, String username, String pswrd, String role) {
        this.id = ID;
        this.username = username;
        this.password = pswrd;
        this.role =role;
    }

    // getter for username
    public String getUsername() {

        return username;
    }
    // getter for password
    public String getPassword() {

        return password;
    }

    //getter for role
    public String getRole() {
        return role;
    }


    @Override
    public String toString() {
        return String.format("{\"id\": %d, \"username\": \"%s\", \"role\": \"%s\"}", id, username, role);
    }



}
