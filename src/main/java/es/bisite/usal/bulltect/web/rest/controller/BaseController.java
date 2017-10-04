package es.bisite.usal.bulltect.web.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ResourceLoader;

import es.bisite.usal.bulltect.i18n.service.IMessageSourceResolverService;

/**
 *
 * @author sergio
 */
public abstract class BaseController {
    
    @Autowired
    protected IMessageSourceResolverService messageSourceResolver;
    
    @Autowired
    protected ApplicationEventPublisher applicationEventPublisher;
    
    @Autowired
    protected ResourceLoader resourceLoader;
    
}
