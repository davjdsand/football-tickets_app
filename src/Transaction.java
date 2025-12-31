import java.time.LocalDate;

public class Transaction {

    private int id; // non editable
    private String username;
    private double price;
    private String zone; //  E, W, S, N
    private int seat_number;
    private int match_id;

    public Transaction(int id, String name, Double price, int seat, int matchid, String zone) {
        this.id = id;
        this.username = name;
        this.price = price;
        this.zone = zone;
        this.match_id = matchid;
        this.seat_number = seat;
    }

    // allows frontend to read the data and save it
    // when user click 'save'


    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }

    public void setName(String new_name){
        this.username = new_name;
    }
    public String getName() {
        return this.username;
    }

    public void setPrice(double new_price){
        this.price = new_price;
    }
    public double getPrice() {
        return this.price;
    }

    public void setZone (String new_zone) {
        this.zone = new_zone;
    }
    public String getZone () {
        return this.zone;
    }

    public void setSeat (int new_seat) {
        this.seat_number = new_seat;
    }
    public int getSeat () {
        return this.seat_number;
    }

    public void setMatchID (int new_id) {
        this.match_id = new_id;
    }
    public int getMatchId() {
        return this.match_id;
    }



}
