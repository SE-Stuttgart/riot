package de.uni_stuttgart.riot.userManagement.security;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.authz.aop.RoleAnnotationHandler;

/**
 * The AuthorizationFilterBinding will register all authorization annotations of shiro to be usable  as part of 
 * the JAX-RS implementation of jersey.
 * 
 * @author Marcel Lehwald
 *
 */
public class AuthorizationFilterBinding implements DynamicFeature {

	private static List<Class<? extends Annotation>> annotations = Collections.unmodifiableList(Arrays.asList(
					RequiresPermissions.class,
					RequiresRoles.class,
					RequiresAuthentication.class,
					RequiresUser.class,
					RequiresGuest.class));

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		for (Class<? extends Annotation> annotation : annotations) {
			if (resourceInfo.getResourceMethod().isAnnotationPresent(annotation)) {
				Annotation authzClass = resourceInfo.getResourceMethod().getAnnotation(annotation);

				context.register(AuthorizationFilter.valueOf(authzClass));
			}
		}
	}

}
