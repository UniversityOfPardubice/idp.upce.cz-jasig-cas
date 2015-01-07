package cz.upce.idp.authentication.adaptors;

import cz.upce.idp.authentication.principal.MasterPasswordCredentials;
import cz.upce.owad.totputils.CombinedAuthenticator;
import javax.validation.constraints.NotNull;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public final class MasterPasswordAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    public static class MasterPasswordAuthenticationException extends AuthenticationException {

        private static final long serialVersionUID = 1889389023022318802L;

        public MasterPasswordAuthenticationException(String code, Throwable cause) {
            super(code, cause);
        }
    }

    @NotNull
    private AuthenticationHandler masterHandler;
    @NotNull
    private CombinedAuthenticator combinedAuthenticator;

    public MasterPasswordAuthenticationHandler() {
        setClassToSupport(MasterPasswordCredentials.class);
    }

    public void setMasterHandler(AuthenticationHandler masterHandler) {
        this.masterHandler = masterHandler;
    }

    public void setCombinedAuthenticator(CombinedAuthenticator combinedAuthenticator) {
        this.combinedAuthenticator = combinedAuthenticator;
    }

    @Override
    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) throws AuthenticationException {
        boolean authenticated = masterHandler.authenticate(credentials);
        if (!authenticated) {
            return false;
        }
        try {
            checkTotpAuthenticate((MasterPasswordCredentials) credentials);
        } catch (cz.upce.owad.totputils.AuthenticationException ex) {
            throw new MasterPasswordAuthenticationException("Error authenticating Master password", ex);
        }
        log.info("User {} successfully authenticated as {}", credentials.getUsername(), ((MasterPasswordCredentials) credentials).getFakeusername());
        credentials.setUsername(((MasterPasswordCredentials) credentials).getFakeusername());
        return true;
    }

    private void checkTotpAuthenticate(final MasterPasswordCredentials credentials) throws cz.upce.owad.totputils.AuthenticationException {
        log.info("User {} requested fake authentication as {}",
                credentials.getUsername(),
                credentials.getFakeusername());
        combinedAuthenticator.checkSecuredPasswordOrTotp(credentials.getUsername(), credentials.getTotp());
    }
}
