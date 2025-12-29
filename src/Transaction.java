import java.time.LocalDate;

public class Transaction {

    private int id; // non editable
    private String name;
    private String location;
    private LocalDate date;
    private double price;

    public Transaction(int id, String name, String location, LocalDate date, Double price) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.price = price;
        this.date = date;
    }

    // allows frontend to read the data and save it
    // when user click 'save'
    public void setName(String new_name){
        this.name = new_name;
    }
    public String getName() {
        return this.name;
    }

    public void setLocation(String new_loc){
        this.location = new_loc;
    }
    public String getLocation() {
        return this.location;
    }

    public void setPrice(double new_price){
        this.price = new_price;
    }
    public double getPrice() {
        return this.price;
    }

    public void setDate(LocalDate new_date){
        this.date = new_date;
    }
    public LocalDate getDate() {
        return this.date;
    }


}
