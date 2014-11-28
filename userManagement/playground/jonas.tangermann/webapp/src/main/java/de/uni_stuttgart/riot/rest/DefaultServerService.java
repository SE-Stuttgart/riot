package de.uni_stuttgart.riot.rest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalMap;

public class DefaultServerService implements
		ServerService {

	@Override
	public boolean isPermitted(String permission, String tokenValue) {
		SimplePrincipalCollection token = new SimplePrincipalCollection();
		token.add(tokenValue, Manager.getUsermanagementManager().getrealmName());
		return TokenUtil.isTokenValid(tokenValue) && SecurityUtils.getSecurityManager().isPermitted(token, permission);
	}

}
