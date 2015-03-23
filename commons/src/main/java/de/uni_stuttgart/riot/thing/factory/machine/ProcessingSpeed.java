package de.uni_stuttgart.riot.thing.factory.machine;

/**
 * The Processing Speed for the factory machine.
 */
public enum ProcessingSpeed {

    /** The slow. */
    SLOW(1),

    /** The medium. */
    MEDIUM(5),

    /** The fast. */
    FAST(10);

    private final int materialConsumption;

    private ProcessingSpeed(int materialConsumption) {
        this.materialConsumption = materialConsumption;
    }

    /**
     * Gets the material consumption.
     *
     * @return the material consumption
     */
    public int getMaterialConsumption() {
        return materialConsumption;
    }
}
