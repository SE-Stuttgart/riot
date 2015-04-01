package de.uni_stuttgart.riot.thing.factory.machine;

/**
 * The Enum MachineStatus.
 */
public enum MachineStatus {
    /** machine is full. **/
    FULL,

    /** machine is running. */
    RUNNING,

    /** machine is waiting. */
    IDLE,

    /** machine is out of material. */
    OUT_OF_MATERIAL,

    /** machine is in error state. */
    ERROR

}
