package cz.upce.idp.authentication.adaptors;

import cz.upce.idp.authentication.principal.UsernamePasswordTokenCredentials;
import cz.upce.owad.totputils.AuthenticationException;
import cz.upce.owad.totputils.CombinedAuthenticator;
import java.security.NoSuchAlgorithmException;
import javax.validation.constraints.NotNull;
import org.jasig.cas.adaptors.ldap.FastBindLdapAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.services.RegisteredService;
import org.springframework.webflow.execution.RequestContext;

public class TokenAwareLdapAuthenticationHandler extends FastBindLdapAuthenticationHandler {

    @NotNull
    private CombinedAuthenticator combinedAuthenticator;

    public void setCombinedAuthenticator(CombinedAuthenticator combinedAuthenticator) {
        this.combinedAuthenticator = combinedAuthenticator;
    }

    public TokenAwareLdapAuthenticationHandler() throws NoSuchAlgorithmException {
        setClassToSupport(UsernamePasswordTokenCredentials.class);
    }

    @Override
    protected boolean postAuthenticate(Credentials cred, boolean authenticated) {
        if (!authenticated) {
            return false;
        }
        UsernamePasswordTokenCredentials credentials = (UsernamePasswordTokenCredentials) cred;
        RequestContext requestContext = credentials.getRequestContext();
        RegisteredService registeredService = (RegisteredService) requestContext.getFlowScope().get("registeredService");
        if (registeredService == null || !registeredService.isTwoFactor()) {
            return true;
        }
        try {
            combinedAuthenticator.checkSecuredPasswordOrTotp(credentials.getUsername(), credentials.getToken());
        } catch (AuthenticationException ex) {
            log.error("Error authenticating second factor", ex);
            return false;
        }
        return true;
    }
}
