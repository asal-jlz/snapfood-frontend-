package model;

public class Courier extends User {

    private double monthlyEarnings;

    public Courier() {
        super();
    }

    // Getter and setter for monthlyEarnings
    public double getMonthlyEarnings() {
        return monthlyEarnings;
    }

    public void setMonthlyEarnings(double monthlyEarnings) {
        this.monthlyEarnings = monthlyEarnings;
    }

    // You can add other courier-specific methods here
}
