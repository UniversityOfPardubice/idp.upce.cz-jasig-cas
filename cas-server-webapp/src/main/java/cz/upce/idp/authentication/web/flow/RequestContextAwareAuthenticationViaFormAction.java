package cz.upce.idp.authentication.web.flow;

import cz.upce.idp.authentication.principal.RequestContextAware;
import javax.validation.constraints.NotNull;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.web.flow.AuthenticationViaFormAction;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

public class RequestContextAwareAuthenticationViaFormAction {

    @NotNull
    private AuthenticationViaFormAction masterFormAction;

    public void setMasterFormAction(AuthenticationViaFormAction masterFormAction) {
        this.masterFormAction = masterFormAction;
    }

    public final void doBind(final RequestContext context, final Credentials credentials) throws Exception {
        if (credentials instanceof RequestContextAware) {
            ((RequestContextAware) credentials).setRequestContext(context);
        }
        masterFormAction.doBind(context, credentials);
    }

    public final String submit(final RequestContext context, final Credentials credentials, final MessageContext messageContext) throws Exception {
        if (credentials instanceof RequestContextAware) {
            ((RequestContextAware) credentials).setRequestContext(context);
        }
        return masterFormAction.submit(context, credentials, messageContext);
    }
}
