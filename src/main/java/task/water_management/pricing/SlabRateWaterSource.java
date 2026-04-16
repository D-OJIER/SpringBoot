package task.water_management.pricing;

// Naming the class SlabRateWaterSource since pricing is slab based logic

import task.water_management.models.Slab;

import java.util.List;

public class SlabRateWaterSource implements WaterSource{

    private final String name;
    private final List<Slab> slabs;

    public SlabRateWaterSource(String name, List<Slab> slabs) {
        this.name = name;
        this.slabs = slabs;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double calculateCost(int litres){
        double totalCost = 0;
        int remainingWater = litres;

        for (Slab slab : slabs) {
            if (remainingWater <= 0)
                break;
            int waterInThisSlab = Math.min(remainingWater, slab.getCapacity());

            totalCost += waterInThisSlab * slab.getRate();
            remainingWater -= waterInThisSlab;
        }

        return totalCost;
    }
}
