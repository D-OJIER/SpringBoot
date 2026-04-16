package task.water_management.config;

import org.springframework.stereotype.Service;
import task.water_management.models.ApartmentType;
import task.water_management.models.Slab;
import task.water_management.pricing.FlatRateWaterSource;
import task.water_management.pricing.SlabRateWaterSource;
import task.water_management.pricing.WaterSource;

import java.util.ArrayList;
import java.util.List;

@Service
public class GlobalConfig {

    private double corpRate = 1.0;
    private double borewellRate = 1.5;

    private List<ApartmentType> apartmentTypes = new ArrayList<>();
    private List<WaterSource> availableWaterSources = new ArrayList<>();

    public GlobalConfig() {
        apartmentTypes.add(new ApartmentType("1 BHK",300,150));
        apartmentTypes.add(new ApartmentType("2 BHK",450,150));
        apartmentTypes.add(new ApartmentType("3 BHK",600,150));

        availableWaterSources.add(new FlatRateWaterSource("Corporation", 1.0));
        availableWaterSources.add(new FlatRateWaterSource("Borewell", 1.5));

        List<Slab> standardTankerSlabs = new ArrayList<>();
        standardTankerSlabs.add(new Slab(500, 2.0));
        standardTankerSlabs.add(new Slab(1000, 3.0));
        standardTankerSlabs.add(new Slab(1500, 5.0));
        standardTankerSlabs.add(new Slab(Integer.MAX_VALUE, 8.0));
        availableWaterSources.add(new SlabRateWaterSource("Tanker Water", standardTankerSlabs));

    }

    public void setBorewellRate(double borewellRate) {
        this.borewellRate = borewellRate;
    }

    public void setCorpRate(double corpRate) {
        this.corpRate = corpRate;
    }

    public double getCorpRate() {
        return corpRate;
    }

    public double getBorewellRate() {
        return borewellRate;
    }

    public List<ApartmentType> getApartmentTypes() {
        return apartmentTypes;
    }

    public List<WaterSource> getAvailableWaterSources() {
        return availableWaterSources;
    }

    public void addApartmentType(ApartmentType newType){
        this.apartmentTypes.add(newType);
    }

    public void addWaterSource(WaterSource newSource){
        this.availableWaterSources.add(newSource);
    }

}
