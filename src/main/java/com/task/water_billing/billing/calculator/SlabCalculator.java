package com.task.water_billing.billing.calculator;

import com.task.water_billing.water.entity.WaterRate;

import java.math.BigDecimal;
import java.util.List;

public class SlabCalculator {

    public BigDecimal calculate(List<WaterRate> slabs, double usage) {

        BigDecimal total = BigDecimal.ZERO;

        for (WaterRate slab : slabs) {

            if (usage <= 0) break;

            double range = slab.getMaxLitres() - slab.getMinLitres();
            double applicable = Math.min(usage, range);

            total = total.add(
                    BigDecimal.valueOf(applicable * slab.getRatePerLitre())
            );

            usage -= applicable;
        }

        return total;
    }
}