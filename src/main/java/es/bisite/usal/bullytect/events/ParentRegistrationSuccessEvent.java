package es.bisite.usal.bullytect.events;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author sergio
 */
public class ParentRegistrationSuccessEvent extends ApplicationEvent {
    
	private static final long serialVersionUID = 1L;
	private final String identity;

    public ParentRegistrationSuccessEvent(String identity, Object source) {
        super(source);
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }
}
