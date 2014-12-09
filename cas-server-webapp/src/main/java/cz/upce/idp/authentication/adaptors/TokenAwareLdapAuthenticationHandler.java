package cz.upce.idp.authentication.adaptors;

import cz.upce.idp.authentication.adaptors.utils.TOTPUtils;
import cz.upce.idp.authentication.principal.UsernamePasswordTokenCredentials;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jasig.cas.adaptors.ldap.FastBindLdapAuthenticationHandler;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.services.RegisteredService;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.webflow.execution.RequestContext;

public class TokenAwareLdapAuthenticationHandler extends FastBindLdapAuthenticationHandler {

    @NotNull
    private NamedParameterJdbcTemplate jdbcTemplate;
    @NotNull
    @Size(min = 6)
    private String totpEnabledQuery;
    @NotNull
    @Size(min = 6)
    private String tokenQuery;
    @NotNull
    @Size(min = 6)
    private String totpSecretQuery;

    public TokenAwareLdapAuthenticationHandler() {
        setClassToSupport(UsernamePasswordTokenCredentials.class);
    }

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void setTotpEnabledQuery(String totpEnabledQuery) {
        this.totpEnabledQuery = totpEnabledQuery;
    }

    public void setTokenQuery(String tokenQuery) {
        this.tokenQuery = tokenQuery;
    }

    public void setTotpSecretQuery(String totpSecretQuery) {
        this.totpSecretQuery = totpSecretQuery;
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
        Map<String, String> usernameQueryParameters = Collections.singletonMap("username", credentials.getUsername());
        try {
            Boolean totpEnabled = jdbcTemplate.queryForObject(totpEnabledQuery, usernameQueryParameters, Boolean.class);

            if (totpEnabled != null && totpEnabled) {
                return checkTotp(credentials);
            }
            return checkToken(credentials);
        } catch (Exception ex) {
            log.error("Error authenticating second factor", ex);
            return false;
        }
    }

    private boolean checkTotp(UsernamePasswordTokenCredentials credentials) throws NoSuchAlgorithmException, InvalidKeyException {
        log.info("Authenticating '{}' with TOTP", credentials.getUsername());
        Map<String, String> usernameQueryParameters = Collections.singletonMap("username", credentials.getUsername());
        String secret = jdbcTemplate.queryForObject(totpSecretQuery, usernameQueryParameters, String.class);
        return TOTPUtils.checkCode(secret, Long.parseLong(credentials.getToken()));
    }

    private boolean checkToken(UsernamePasswordTokenCredentials credentials) {
        log.info("Authenticating '{}' with static token", credentials.getUsername());
        Map<String, String> usernameQueryParameters = Collections.singletonMap("username", credentials.getUsername());
        String token = jdbcTemplate.queryForObject(tokenQuery, usernameQueryParameters, String.class);
        return credentials.getToken() != null && credentials.getToken().equals(token);
    }
}
