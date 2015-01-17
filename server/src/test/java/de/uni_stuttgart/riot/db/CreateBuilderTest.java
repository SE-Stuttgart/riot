package de.uni_stuttgart.riot.db;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.server.commons.db.CreateBuilder;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.remote.RemoteThing;
import de.uni_stuttgart.riot.thing.remote.RemoteThingAction;

public class CreateBuilderTest {

    @Test
    public void test() {
        LinkedList<Class> cl = new LinkedList<Class>();
        cl.add(Property.class);
        cl.add(RemoteThing.class);
        cl.add(RemoteThingAction.class);
        cl.add(Action.class);
        System.out.println(CreateBuilder.buildCreateStatment(cl));
    }

}
