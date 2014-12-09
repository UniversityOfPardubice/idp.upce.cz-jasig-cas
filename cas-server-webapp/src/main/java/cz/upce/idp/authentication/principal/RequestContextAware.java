package cz.upce.idp.authentication.principal;

import org.springframework.webflow.execution.RequestContext;

public interface RequestContextAware {

    void setRequestContext(RequestContext requestContext);
}
