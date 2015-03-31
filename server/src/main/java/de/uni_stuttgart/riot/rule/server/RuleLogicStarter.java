package de.uni_stuttgart.riot.rule.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class starts the {@link RuleLogic} on server startup.
 * 
 * @author Philipp Keck
 */
public class RuleLogicStarter implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        RuleLogic.getRuleLogic(); // That's all we need.
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
