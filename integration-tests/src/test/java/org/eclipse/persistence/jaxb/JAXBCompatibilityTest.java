package org.eclipse.persistence.jaxb;

import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.usermanagement.request.LoginRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.RefreshRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.AuthenticationResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.PermissionResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.RoleResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.TokenResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.UserResponse;

/**
 * This test ensures that the entities used for the REST interface are compatible with the JAXB requirements. If they are not, Jersey will
 * only return a 500 Internal Server Error without any meaningful error message in the log file. This test is placed in the JAXB package and
 * uses internal JAXB APIs to directly validate the classes with the EclipseLink JAXB implementation, which is used by GlassFish internally.
 *
 * @author Philipp Keck
 */
public class JAXBCompatibilityTest {

    /**
     * Tests the given classes by instantiating a JAXBContext for them.
     *
     * @param classesToBeBound
     *            The classes.
     * @return The context, if successful (can be ignored).
     * @throws JAXBException
     *             When the classes are erroneous. The exception will contain the specific reason.
     */
    public static JAXBContext testEntityClasses(@SuppressWarnings("rawtypes") Class... classesToBeBound) throws JAXBException {
        TypeMappingInfo[] typeMappingInfo = Arrays.stream(classesToBeBound).map((clazz) -> {
            TypeMappingInfo tmi = new TypeMappingInfo();
            tmi.setType(clazz);
            return tmi;
        }).toArray((count) -> new TypeMappingInfo[count]);

        JAXBContext context = new JAXBContext(new JAXBContext.TypeMappingInfoInput(typeMappingInfo, null, ClassLoader.getSystemClassLoader()));
        if (context.isRefreshable()) {
            context.postInitialize();
        }
        return context;
    }

    @Test
    public void testUserManagementClasses() throws JAXBException {
        testEntityClasses(UserRequest.class, LoginRequest.class, RefreshRequest.class);
        testEntityClasses(UserResponse.class, TokenResponse.class, RoleResponse.class, PermissionResponse.class, AuthenticationResponse.class);
    }

    @Test
    public void testServerClasses() throws JAXBException {
        // TODO Add content here
    }

}
