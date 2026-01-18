
// for now, im just mocking the database
// Mock db == create a fake database just for tests

import java.util.ArrayList;
import java.util.List;

public class Database {

    // create a table with users
    private static final List<User> user_table = new ArrayList<>();
    // create a table with games
    public static List<Match> matches_table = new ArrayList<>();
    // create a table with transactions
    public static List<Transaction> transactions_table = new ArrayList<>();


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

    // check login method
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


    // method for removing a match -- admin only
    public static void removeMatch(int id) {
        Match match_to_delete = null;
        for (Match match: matches_table ) {
            if (match.getMatchID() == id) {
                match_to_delete = match;
                break;
            }
        }
        if (match_to_delete != null) {
            boolean remove = matches_table.remove(match_to_delete);
        }
    }

    // method to update a match
    public static boolean updateMatch(int id, String home, String away, String date, String location, double price) {
        for (Match m: matches_table) {
            if (m.getMatchID() == id) {
                m.setHomeTeam(home);
                m.setAwAYTeam(away);
                m.setLocation(location);
                m.setPrice(price);
                m.setDate(date);
                return true;
            }
        }
        return false; // id not found
    }

    // method to add a new match
    public static void addMatch(String home, String away, String stadium_name, String date, String location ,double price, String image_url) {
        int max_id = - 1;
        for (Match m: matches_table) {
            if (max_id < m.getMatchID()) {
                max_id = m.getMatchID();
            }
        }
        int new_id = max_id + 1;

        Match new_match = new Match(new_id, home, away, stadium_name, date, price, location, image_url);
        matches_table.add(new_match);
    }

    // method to add a new transaction
    public static void addTransaction (String zone, String username, int match_id, int seat_nr, double price) {
        int max_id = -1;
        for (Transaction t: transactions_table) {
            if (max_id < t.getId()) {
                max_id = t.getId();
            }
        }
        int new_id = max_id + 1;
        Transaction new_T = new Transaction(new_id, username,price, seat_nr,match_id, zone);
        transactions_table.add(new_T);
    }

    // find taken seats for a specific match
    public  static List<Integer> getTakenSeats(int target_match_id, String target_zone) {

        List<Integer> taken_seats = new ArrayList<>();
        for (Transaction t: transactions_table) {

            // check if we are looking for the correct match
            if (t.getMatchId() == target_match_id && t.getZone().equals(target_zone)) {
                taken_seats.add(t.getSeat());
            }
        }

        return taken_seats; // will return a list like [2, 14, 22]
    }








}
