package task.water_management.models;

public class ApartmentType {

    private String name;
    private int fixedBase, perPersonAllowance;

    public ApartmentType(String name, int fixedBase, int perPersonAllowance) {
        this.name = name;
        this.fixedBase = fixedBase;
        this.perPersonAllowance = perPersonAllowance;
    }

    public int calculateBaseWater(int numberOfResidents){
        return this.fixedBase + (this.perPersonAllowance*numberOfResidents);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFixedBase(int fixedBase) {
        this.fixedBase = fixedBase;
    }

    public void setPerPersonAllowance(int perPersonAllowance) {
        this.perPersonAllowance = perPersonAllowance;
    }

    public String getName() {
        return name;
    }

    public int getFixedBase() {
        return fixedBase;
    }

    public int getPerPersonAllowance() {
        return perPersonAllowance;
    }
}
