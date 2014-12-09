package cz.upce.idp.authentication.principal;

import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class UsernamePasswordTokenCredentials extends UsernamePasswordCredentials {

    private static final long serialVersionUID = 1607907854347781399L;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UsernamePasswordTokenCredentials that = (UsernamePasswordTokenCredentials) o;

        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null) {
            return false;
        }
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null) {
            return false;
        }
        if (token != null ? !token.equals(token) : that.token != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
