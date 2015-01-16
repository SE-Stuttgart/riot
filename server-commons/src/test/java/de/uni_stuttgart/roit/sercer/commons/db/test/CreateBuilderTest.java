package de.uni_stuttgart.roit.sercer.commons.db.test;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.server.commons.db.CreateBuilder;

public class CreateBuilderTest {

    @Test
    public void test() {
        LinkedList<Class> cl = new LinkedList<Class>();
        cl.add(User.class);
        cl.add(Token.class);
        System.out.println(CreateBuilder.buildCreateStatment(cl));
    }

}
