package task.water_management.service;

import org.springframework.stereotype.Service;
import task.water_management.config.GlobalConfig;
import task.water_management.models.*;
import task.water_management.pricing.WaterSource;

import java.util.List;

@Service
public class WaterBillService {

    private final GlobalConfig globalConfig;

    public WaterBillService(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public BillReceipt generateBill(ApartmentType apartment, int residents, List<WaterAllocation> allocations, List<Guest> guests) {

        double totalCost = 0;
        int totalLiters = 0;
        int guestLiters = 0;

        for (Guest guest : guests) {
            guestLiters += guest.getWaterConsumed();
        }

        if (guestLiters > 0) {
            WaterSource tankerSource = globalConfig.getAvailableWaterSources().stream()
                    .filter(source -> source.getName().equals("Tanker Water"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Tanker source not found!"));

            totalCost += tankerSource.calculateCost(guestLiters);
            totalLiters += guestLiters;
        }

        int baseWater = apartment.calculateBaseWater(residents);
        totalLiters += baseWater;

        int totalRatioWeight = allocations.stream().mapToInt(WaterAllocation::getRatioWeight).sum();

        if (totalRatioWeight > 0) {
            for (WaterAllocation alloc : allocations) {
                int allocatedLiters = (int) Math.round((double) alloc.getRatioWeight() / totalRatioWeight * baseWater);

                totalCost += alloc.getSource().calculateCost(allocatedLiters);
            }
        }

        return new BillReceipt(totalLiters, (int) Math.round(totalCost));
    }
}