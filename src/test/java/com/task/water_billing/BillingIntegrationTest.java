package com.task.water_billing;

import com.task.water_billing.billing.service.BillingService;
import com.task.water_billing.property.entity.*;
import com.task.water_billing.property.repository.*;
import com.task.water_billing.telemetry.entity.DailyLog;
import com.task.water_billing.telemetry.repository.DailyLogRepository;
import com.task.water_billing.water.entity.*;
import com.task.water_billing.water.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BillingIntegrationTest {

    @Autowired private BlockRepository blockRepo;
    @Autowired private ApartmentTypeRepository typeRepo;
    @Autowired private ApartmentRepository apartmentRepo;

    @Autowired private WaterSourceRepository sourceRepo;
    @Autowired private WaterRateRepository rateRepo;
    @Autowired private ApartmentSourceConfigRepository configRepo;

    @Autowired private DailyLogRepository logRepo;
    @Autowired private BillingService billingService;

    @Test
    void testMultiSourceBilling() {

        // 🥇 Create Block
        Block block = new Block();
        block.setName("A");
        block = blockRepo.save(block);

        // 🥈 Create Apartment Type
        ApartmentType type = new ApartmentType();
        type.setTypeName("2BHK");
        type.setBaseOccupancy(3);
        type.setLitresPerPerson(135);
        type = typeRepo.save(type);

        // 🥉 Create Apartment
        Apartment apartment = new Apartment();
        apartment.setApartmentNumber("101");
        apartment.setBlock(block);
        apartment.setType(type);
        apartment.setStatus("ACTIVE");
        apartment = apartmentRepo.save(apartment);

        // 🏅 Create Sources
        WaterSource city = new WaterSource();
        city.setName("City Water");
        city.setType("VARIABLE");
        city = sourceRepo.save(city);

        WaterSource borewell = new WaterSource();
        borewell.setName("Borewell");
        borewell.setType("VARIABLE");
        borewell = sourceRepo.save(borewell);

        // 🏆 Add Slabs (City)
        WaterRate r1 = new WaterRate();
        r1.setMinLitres(0);
        r1.setMaxLitres(500);
        r1.setRatePerLitre(0.5);
        r1.setSource(city);
        rateRepo.save(r1);

        WaterRate r2 = new WaterRate();
        r2.setMinLitres(500);
        r2.setMaxLitres(1000);
        r2.setRatePerLitre(1.0);
        r2.setSource(city);
        rateRepo.save(r2);

        // Borewell slab
        WaterRate r3 = new WaterRate();
        r3.setMinLitres(0);
        r3.setMaxLitres(1000);
        r3.setRatePerLitre(0.2);
        r3.setSource(borewell);
        rateRepo.save(r3);

        // ⚖️ Configure split (60/40)
        ApartmentSourceConfig c1 = new ApartmentSourceConfig();
        c1.setApartment(apartment);
        c1.setSource(city);
        c1.setRatioPercent(60);
        configRepo.save(c1);

        ApartmentSourceConfig c2 = new ApartmentSourceConfig();
        c2.setApartment(apartment);
        c2.setSource(borewell);
        c2.setRatioPercent(40);
        configRepo.save(c2);

        // 🔥 Create Daily Log
        DailyLog log = new DailyLog();
        log.setApartment(apartment);
        log.setLogDate(LocalDate.now());
        log.setTotalLitresConsumed(1000);
        log.setGuestCount(0);
        log = logRepo.save(log);

        // ⚔️ Trigger Billing
        billingService.processDailyLog(log.getId());

        // 🔍 Fetch updated log
        DailyLog updated = logRepo.findById(log.getId()).orElseThrow();

        // ✅ ASSERT RESULT
        assertEquals(430.0, updated.getDayCost(), 0.1);
    }
}