package task.water_management.pricing;

// Naming the class FlatRateWaterSource since pricing is constant

public class FlatRateWaterSource implements WaterSource {
    private final String name;
    private final double ratePerLitre;

    public FlatRateWaterSource(String name, double ratePerLitre) {
        this.name = name;
        this.ratePerLitre = ratePerLitre;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double calculateCost(int litres){
        return litres*this.ratePerLitre;
    }
}