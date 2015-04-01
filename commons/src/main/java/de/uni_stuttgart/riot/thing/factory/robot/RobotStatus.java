package de.uni_stuttgart.riot.thing.factory.robot;

/**
 * The Enum Robot Status.
 */
public enum RobotStatus {

    /** robot is on the way to get the processed pieces from the machine. */
    ON_THE_WAY_EMPTY_MODUS,

    /** robot is on the way to fill machine with material. */
    ON_THE_WAY_FILL_MODUS,

    /** robot is waiting. */
    IDLE,

    /** robot is in error state. */
    ERROR

}
