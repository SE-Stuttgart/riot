package de.uni_stuttgart.riot.rest;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.thing.commons.ThingInfo;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;

@Path("thing")
@Consumes("application/json")
@Produces("application/json")
public class ThingService {
    
    @GET
    public Collection<ThingInfo> getMyThings() throws DatasourceFindException, JsonProcessingException{
        ThingLogic logic = new ThingLogic();
        ObjectMapper mapper = new ObjectMapper();
        Collection<ThingInfo> result = logic.getAllThings(1);
        for (ThingInfo thingInfo : result) {
            System.out.println(mapper.writeValueAsString(thingInfo));
        }
        return result;
    }

}
