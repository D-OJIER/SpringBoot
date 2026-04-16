package task.water_management.models;

import task.water_management.pricing.WaterSource;

public class WaterAllocation {
    private WaterSource source;
    private  int ratioWeight;

    public WaterAllocation(WaterSource source, int ratioWeight) {
        this.source = source;
        this.ratioWeight = ratioWeight;
    }

    public WaterSource getSource() {
        return source;
    }

    public int getRatioWeight() {
        return ratioWeight;
    }
}
