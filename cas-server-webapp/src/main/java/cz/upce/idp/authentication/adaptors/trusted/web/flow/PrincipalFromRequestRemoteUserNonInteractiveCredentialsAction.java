package cz.upce.idp.authentication.adaptors.trusted.web.flow;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.adaptors.trusted.authentication.principal.PrincipalBearingCredentials;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.UnauthorizedSsoServiceException;
import org.jasig.cas.web.flow.AbstractNonInteractiveCredentialsAction;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.RequestContext;

public final class PrincipalFromRequestRemoteUserNonInteractiveCredentialsAction
        extends AbstractNonInteractiveCredentialsAction {

    protected Credentials constructCredentialsFromRequest(
            final RequestContext context) {
        RegisteredService registeredService = (RegisteredService) context.getFlowScope().get("registeredService");
        if (registeredService == null || !registeredService.isSsoEnabled()) {
            throw new UnauthorizedSsoServiceException();
        }

        final HttpServletRequest request = WebUtils
                .getHttpServletRequest(context);
        final String remoteUser = request.getRemoteUser();

        if (StringUtils.hasText(remoteUser)) {
            final String strippedRemoteUser = remoteUser.replaceFirst("@.*", "");
            if (logger.isDebugEnabled()) {
                logger.debug("Remote  User [" + remoteUser
                        + "] = [" + strippedRemoteUser
                        + "] found in HttpServletRequest");
            }
            return new PrincipalBearingCredentials(new SimplePrincipal(
                    strippedRemoteUser));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Remote User not found in HttpServletRequest.");
        }

        return null;
    }
}
