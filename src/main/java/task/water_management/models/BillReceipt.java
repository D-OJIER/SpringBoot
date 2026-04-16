package task.water_management.models;

public class BillReceipt {
    private int totalLitres, totalCost;

    public BillReceipt(int totalLitres, int totalCost) {
        this.totalLitres = totalLitres;
        this.totalCost = totalCost;
    }

    public int getTotalLitres() {
        return totalLitres;
    }

    public int getTotalCost() {
        return totalCost;
    }
}
