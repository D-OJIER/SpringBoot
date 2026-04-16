package task.water_management.models;

public class Slab {
    private int capacity;
    private double rate;

    public Slab(int capacity, double rate) {
        this.capacity = capacity;
        this.rate = rate;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
