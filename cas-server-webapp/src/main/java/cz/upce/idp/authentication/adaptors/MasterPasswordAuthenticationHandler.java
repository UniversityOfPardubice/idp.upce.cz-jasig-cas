package cz.upce.idp.authentication.adaptors;

import cz.upce.idp.authentication.adaptors.utils.TOTPUtils;
import cz.upce.idp.authentication.principal.MasterPasswordCredentials;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.AuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public final class MasterPasswordAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    public static class MasterPasswordAuthenticationException extends AuthenticationException {

        private static final long serialVersionUID = -1771045657713564526L;

        public MasterPasswordAuthenticationException(String code, String msg) {
            super(code, msg);
        }
    }
    private AuthenticationHandler masterHandler;
    private DataSource dataSource;

    public MasterPasswordAuthenticationHandler() {
        setClassToSupport(MasterPasswordCredentials.class);
    }

    public void setMasterHandler(AuthenticationHandler masterHandler) {
        this.masterHandler = masterHandler;
    }

    public AuthenticationHandler getMasterHandler() {
        return masterHandler;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
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
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            log.info("User {} requested fake authentication as {}",
                    credentials.getUsername(),
                    credentials.getFakeusername());

            connection = dataSource.getConnection();
            stmt = connection.prepareStatement("SELECT username, TOTP_secret FROM UPCE_TOTP WHERE username=?");
            stmt.setString(1, credentials.getUsername());
            resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                log.error("User {} requested fake authentication as {} and is not authorised!",
                        credentials.getUsername(),
                        credentials.getFakeusername());
                return false;
            }
            if (!resultSet.getString(1).equals(credentials.getUsername())) {
                log.error("User {} requested fake authentication as {} and usernames did not match ({}!={})!",
                        credentials.getUsername(),
                        credentials.getFakeusername(),
                        credentials.getUsername(),
                        resultSet.getString(1));
                return false;
            }
            try {
                if (!TOTPUtils.checkCode(resultSet.getString(2),
                        Long.parseLong(credentials.getTotp()),
                        30,
                        4)) {
                    log.error("Error checking TOTP code for {}", credentials.getUsername());
                    return false;
                }
            } catch (Exception ex) {
                log.error("Error checking TOTP code", ex);
                return false;
            }

            return true;
        } catch (SQLException ex) {
            log.error("Error master password authenticating", ex);
            return false;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    log.info("Error closing resultset", ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    log.info("Error closing statement", ex);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    log.info("Error closing connection", ex);
                }
            }
        }
    }
}
