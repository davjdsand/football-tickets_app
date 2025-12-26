


public class TestUsers {

    public static void main (String[] args) {
        System.out.println("Testing user ceation\n");

        User user = new User(101, "Maria_TradWife32", "colonie_urbana7", "USER");
        User boss = new User(999, "Admin_barosan", "sefu", "ADMIN");

        // for regular user
        System.out.println("Username: " + user.getUsername());
        System.out.println("Role: " + user.getRole());
        System.out.println("JSON Output:  " + user.toString());

        // for admin
        System.out.println("Username: " + boss.getUsername());
        System.out.println("Role: " + boss.getRole());
        System.out.println("JSON Output: " + boss.toString());


        checkPermission(user);
        checkPermission(boss);


    }


    public static void checkPermission(User u) {
        if ("ADMIN".equals(u.getRole())) {
            System.out.println("✅ " + u.getUsername() + " has admin acces");
        } else {
            System.out.println("❌ " + u.getUsername() + "is a user. Acces denied!");
        }
    }


}
