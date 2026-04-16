package task.water_management.pricing;

public interface WaterSource {
    String getName();
    double calculateCost(int litres);
}
