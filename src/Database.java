
// for now, im just mocking the database
// Mock db == create a fake database just for tests

import java.util.ArrayList;
import java.util.List;

public class Database {

    // create a table with users
    private static List<User> user_table = new ArrayList<>();
    // create a table with games
    private static List<Match> matches_table = new ArrayList<>();



    // load some datas
    static {

        // load users
        user_table.add(new User(101, "David_baros", "creatine", "USER"));
        user_table.add(new User(102, "Andarda", "Disney", "USER"));
        user_table.add(new User(103, "GigiBecali69", "FCSB", "USER"));
        user_table.add(new Admin(1, "Andrei_Adminu", "Observator1"));

        // load matches
        matches_table.add(new Match(1, "Real Madrid", "Barcelona", "Bernabeu",
                "2025-05-20", 150.00, "Madrid, Spain", "elclasico.png"));

        matches_table.add(new Match(2, "FCSB", "Dinamo", "Arena Nationala",
                "2025-06-01", 50.00, "Bucharest, Romania", "steauadinamo.png"));

        matches_table.add(new Match(3, "Liverpool", "Man City", "Anfield",
                "2025-08-12", 200.00, "Liverpool, UK", "cityliv.png"));

    }


    // add user method
    public static User signUp(String username, String password) {
        for (User user : user_table) {
            if (user.getUsername().equals(username)) {
                return null; // username alrewady exist in database
            }
        }

        // add the username to database
        User temp = new User(user_table.size()+ 1, username, password, "USER");
        user_table.add(temp);
        return temp; // succesfull registration
    }

    public static User checkLogin(String uname, String password) {
        for (User u : user_table) {
            if (u.getUsername().equals(uname) && u.getPassword().equals(password)) {
                return u;
            }
        }
        // if no user found
        return null;
    }

    // method for matches
    public static List<Match> getMatches() {
        return matches_table;
    }



}
