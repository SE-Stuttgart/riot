package de.uni_stuttgart.riot.rule;

/**
 * The status of a rule. In particular, this determines whether the rule is executed or not.
 * 
 * @author Philipp Keck
 */
public enum RuleStatus {

    /**
     * The rule is active, i.e. it should be executed by the server.
     */
    ACTIVE,

    /**
     * The rule produced an error during execution and must be fixed.
     */
    FAILED,

    /**
     * The rule could not find one or more of its referenced parameters and cannot be executed.
     */
    FAILED_REFERENCES,

    /**
     * The rule has been deactivated on purpose (by the user).
     */
    DEACTIVATED

}
