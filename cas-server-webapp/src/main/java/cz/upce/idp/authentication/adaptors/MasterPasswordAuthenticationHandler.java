package cz.upce.idp.authentication.adaptors;

import cz.upce.idp.authentication.adaptors.utils.TOTPUtils;
import cz.upce.idp.authentication.principal.MasterPasswordCredentials;
import java.util.Collections;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

public final class MasterPasswordAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    public static class MasterPasswordAuthenticationException extends AuthenticationException {

        private static final long serialVersionUID = -1771045657713564526L;

        public MasterPasswordAuthenticationException(String code, String msg) {
            super(code, msg);
        }
    }

    @NotNull
    private AuthenticationHandler masterHandler;
    @NotNull
    private NamedParameterJdbcTemplate jdbcTemplate;
    @NotNull
    @Size(min = 6)
    private String query;

    public MasterPasswordAuthenticationHandler() {
        setClassToSupport(MasterPasswordCredentials.class);
    }

    public void setMasterHandler(AuthenticationHandler masterHandler) {
        this.masterHandler = masterHandler;
    }

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) throws AuthenticationException {
        boolean authenticated = masterHandler.authenticate(credentials);
        if (!authenticated) {
            return false;
        }
        if (checkTotpAuthenticate((MasterPasswordCredentials) credentials)) {
            log.info("User {} successfully authenticated as {}", credentials.getUsername(), ((MasterPasswordCredentials) credentials).getFakeusername());
            credentials.setUsername(((MasterPasswordCredentials) credentials).getFakeusername());
            return true;
        } else {
            throw new MasterPasswordAuthenticationException("TOTP Error", "Error authenticating");
        }
    }

    private boolean checkTotpAuthenticate(final MasterPasswordCredentials credentials) {
        try {
            log.info("User {} requested fake authentication as {}",
                    credentials.getUsername(),
                    credentials.getFakeusername());

            String secret = jdbcTemplate.queryForObject(query, Collections.singletonMap("username", credentials.getUsername()),
                    String.class);

            if (!StringUtils.hasText(secret)) {
                log.error("User {} requested fake authentication as {} and is not authorised!",
                        credentials.getUsername(),
                        credentials.getFakeusername());
                return false;
            }
            try {
                if (!TOTPUtils.checkCode(secret,
                        Long.parseLong(credentials.getTotp()))) {
                    log.error("Error checking TOTP code for {}", credentials.getUsername());
                    return false;
                }
            } catch (Exception ex) {
                log.error("Error checking TOTP code", ex);
                return false;
            }

            return true;
        } catch (Exception ex) {
            log.error("Error master password authenticating", ex);
            return false;
        }
    }
}
