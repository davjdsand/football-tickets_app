import java.time.LocalDate;

public class Match {

    private int match_id;
    private String home_team_name;
    private String away_team_name;
    private String stadium_name;
    private String match_date;
    private double ticket_price;
    private String location;
    private String image_url;

    public Match(int match_id, String home_team, String away_team, String stadium_name,
                 String date, double price, String location, String image_url) {

        this.match_id = match_id;
        this.home_team_name = home_team;
        this.away_team_name = away_team;
        this.stadium_name = stadium_name;
        this. match_date = date;
        this.ticket_price = price;
        this.location = location;
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return String.format(
                "{" +
                        "\"id\": %d, " +
                        "\"teamHome\": \"%s\", " +
                        "\"teamAway\": \"%s\", " +
                        "\"matchDate\": \"%s\", " +
                        "\"price\": %.2f, " +
                        "\"location\": \"%s\", " +
                        "\"image_url\": \"%s\"" +
                        "}",
                match_id, home_team_name, away_team_name, match_date, ticket_price, location, image_url
        );
    }
    public int getMatchID () {
        return this.match_id; // specific match taht i clicked on
    }

    public  void setHomeTeam (String name) {
        this.home_team_name = name;
    }
    public String getHomeTeam () {
        return this.home_team_name;
    }

    public  void setAwAYTeam (String name) {
        this.away_team_name = name;
    }
    public  String getAwAYTeam () {
        return this.away_team_name;
    }

    public void setDate (String date) {
        this.match_date = date;
    }
    public String getDate () {
       return this.match_date;
    }

    public void setPrice(double price) {
        this.ticket_price = price;
    }
    public double getPrice() {
        return this.ticket_price;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String setLocation() {
        return this.location;
    }

}
