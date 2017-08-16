package sanchez.sanchez.sergio.service.impl;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import sanchez.sanchez.sergio.exception.GetCommentsProcessException;
import sanchez.sanchez.sergio.exception.InvalidAccessTokenException;
import sanchez.sanchez.sergio.exception.visitor.IExceptionVisitor;
import sanchez.sanchez.sergio.persistence.entity.AlertEntity;
import sanchez.sanchez.sergio.persistence.entity.AlertLevelEnum;
import sanchez.sanchez.sergio.persistence.repository.AlertRepository;
import sanchez.sanchez.sergio.persistence.repository.SocialMediaRepository;
import sanchez.sanchez.sergio.service.IExceptionHandlerService;
import sanchez.sanchez.sergio.util.IVisitable;

/**
 *
 * @author sergio
 */
@Service("errorService")
public class ExceptionHandlerServiceImpl implements IExceptionHandlerService, IExceptionVisitor {

    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerServiceImpl.class);

    private final SocialMediaRepository socialMediaRepository;
    private final AlertRepository alertRepository;
    

    public ExceptionHandlerServiceImpl(SocialMediaRepository socialMediaRepository, AlertRepository alertRepository) {
        this.socialMediaRepository = socialMediaRepository;
        this.alertRepository = alertRepository;
    }
    
    @Override
    public void handleException(Message<Exception> messageException) {
        Throwable cause = messageException.getPayload().getCause();
        if (cause instanceof IVisitable)
                ((IVisitable<IExceptionVisitor>)cause).accept(this);
        else {
            //logger.error(cause.fillInStackTrace().toString());
        }   
    }

    @Override
    public void visit(InvalidAccessTokenException exception) {
    	Assert.notNull(exception.getAccessToken(), "Access Token can not be null");
    	Assert.hasLength(exception.getAccessToken(), "Access Token can not be an empty string");
    	Assert.notNull(exception.getMessage(), "Message can not be null");
    	Assert.hasLength(exception.getMessage(), "Message can not be an empty string");
    	Assert.notNull(exception.getTarget(), "Target can not be null");
    	logger.debug("Save exception as Alert for: " + exception.getTarget().getFullName());
        socialMediaRepository.setAccessTokenAsInvalid(exception.getAccessToken(), exception.getSocialMediaType());
        alertRepository.save(new AlertEntity(AlertLevelEnum.WARNING, exception.getMessage(), exception.getTarget()));
    }
    
    @Override
    public void visit(GetCommentsProcessException exception) {
        logger.error(exception.getCause().toString());
    }
    
   
    @PostConstruct
    protected void init(){
        Assert.notNull(socialMediaRepository, "The SocialMediaRepository can not be null");
        Assert.notNull(alertRepository, "The AlertRepository can not be null");
    }
}
