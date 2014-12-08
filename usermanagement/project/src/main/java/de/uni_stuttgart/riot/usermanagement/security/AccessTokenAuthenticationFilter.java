package de.uni_stuttgart.riot.usermanagement.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * 
 * Filter for securing resources which need authentication using an access token. The Filter will get the access token from the request
 * header and authenticate the subject based on the token.
 * 
 * @author Marcel Lehwald
 *
 */
public class AccessTokenAuthenticationFilter extends AuthenticationFilter {

    protected static final String AUTHENTICATION_HEADER = "Access-Token";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        return false;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject user = SecurityUtils.getSubject();

        // get access token from request header
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String accessToken = httpRequest.getHeader(AUTHENTICATION_HEADER);

        try {
            user.login(new AccessTokenAuthentication(accessToken));
        } catch (AuthenticationException e) {
            return false;
        }

        return user.isAuthenticated();
    }

}
