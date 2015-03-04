package de.uni_stuttgart.riot.thing.house.coffeemachine;

/**
 * Different cup size settings for the coffee machine.
 * 
 * @author Philipp Keck
 */
public enum CupSize {

    /**
     * A small cup.
     */
    SMALL(50, 10),

    /**
     * A medium cup.
     */
    MEDIUM(75, 15),

    /**
     * A large cup.
     */
    LARGE(100, 20),

    /**
     * Two medium cups.
     */
    DOUBLE(150, 30);

    private final double waterConsumption;
    private final int beanConsumption;

    private CupSize(double waterConsumption, int beanConsumption) {
        this.waterConsumption = waterConsumption;
        this.beanConsumption = beanConsumption;
    }

    /**
     * Gets the water consumption.
     * 
     * @return The water consumption for the given cup size.
     */
    public double getWaterConsumption() {
        return waterConsumption;
    }

    /**
     * Gets the bean consumption.
     * 
     * @return The bean consumption for the given cup size, if 100% brew strength is used.
     */
    public int getBeanConsumption() {
        return beanConsumption;
    }

}
