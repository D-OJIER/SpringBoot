package task.water_management.models;

public class Guest {
    private int daysStayed;

    public Guest(int daysStayed) {
        this.daysStayed = daysStayed;
    }

    public int getWaterConsumed() {
        return this.daysStayed * 10;
    }

    public int getDaysStayed() {
        return daysStayed;
    }

    public void setDaysStayed(int daysStayed) {
        this.daysStayed = daysStayed;
    }
}
