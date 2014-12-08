package cz.upce.idp.authentication.web.flow;

import javax.validation.constraints.NotNull;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class InitialFlowSetupAction extends AbstractAction {

    @NotNull
    private ServicesManager servicesManager;

    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
        final Service service = (Service) context.getFlowScope().get("service");
        RegisteredService registeredService = servicesManager.findServiceBy(service);
        if (registeredService != null && logger.isDebugEnabled()) {
            logger.debug("Placing registeredService in FlowScope: " + registeredService.getName());
        }
        context.getFlowScope().put("registeredService", registeredService);
        return result("success");
    }

}
