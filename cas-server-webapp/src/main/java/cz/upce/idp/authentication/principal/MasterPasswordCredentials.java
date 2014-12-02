package cz.upce.idp.authentication.principal;

import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

public class MasterPasswordCredentials extends UsernamePasswordCredentials {

    /**
     * Unique ID for serialization.
     */
    private static final long serialVersionUID = -6980489614020732462L;
    /**
     * The TOTP code.
     */
    private String totp;
    /**
     * The fake username.
     */
    private String fakeusername;

    /**
     * @return Returns the totp.
     */
    public final String getTotp() {
        return this.totp;
    }

    /**
     * @param totp The TOTP to set.
     */
    public final void setTotp(final String totp) {
        this.totp = totp;
    }

    /**
     * @return Returns the fake userName.
     */
    public final String getFakeusername() {
        return this.fakeusername;
    }

    /**
     * @param fakeUserName The fake userName to set.
     */
    public final void setFakeusername(final String fakeusername) {
        this.fakeusername = fakeusername;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MasterPasswordCredentials that = (MasterPasswordCredentials) o;

        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null) {
            return false;
        }
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null) {
            return false;
        }
        if (totp != null ? !totp.equals(that.totp) : that.totp != null) {
            return false;
        }
        if (fakeusername != null ? !fakeusername.equals(that.fakeusername) : that.fakeusername != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (totp != null ? totp.hashCode() : 0);
        result = 31 * result + (fakeusername != null ? fakeusername.hashCode() : 0);
        return result;
    }
}
