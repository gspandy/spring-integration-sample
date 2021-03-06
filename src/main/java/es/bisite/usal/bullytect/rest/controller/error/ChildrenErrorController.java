package es.bisite.usal.bullytect.rest.controller.error;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;

import es.bisite.usal.bullytect.rest.ApiHelper;
import es.bisite.usal.bullytect.rest.controller.BaseController;
import es.bisite.usal.bullytect.rest.exception.CommentsBySonNotFoundException;
import es.bisite.usal.bullytect.rest.exception.NoChildrenFoundException;
import es.bisite.usal.bullytect.rest.exception.SonNotFoundException;
import es.bisite.usal.bullytect.rest.response.APIResponse;
import es.bisite.usal.bullytect.rest.response.ChildrenResponseCode;
import es.bisite.usal.bullytect.rest.response.CommentResponseCode;
import es.bisite.usal.bullytect.service.IMessageSourceResolver;
import io.jsonwebtoken.lang.Assert;

/**
 *
 * @author sergio
 */

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ChildrenErrorController extends BaseController {
	

    @ExceptionHandler(SonNotFoundException.class)
    @ResponseBody
    protected ResponseEntity<APIResponse<String>> handleSonNotFoundException(SonNotFoundException resourceNotFound, HttpServletRequest request) {
        return ApiHelper.<String>createAndSendErrorResponse(ChildrenResponseCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND,
        		messageSourceResolver.resolver("son.not.found"));
    }
    
    @ExceptionHandler(CommentsBySonNotFoundException.class)
    @ResponseBody
    protected ResponseEntity<APIResponse<String>> handleCommentsBySonNotFoundException(CommentsBySonNotFoundException commentsBySonNotFound, HttpServletRequest request){
        return ApiHelper.<String>createAndSendErrorResponse(CommentResponseCode.COMMENTS_BY_CHILD_NOT_FOUND, HttpStatus.NOT_FOUND,
        		messageSourceResolver.resolver("comments.by.son.not.found"));
    }
   
    
    @ExceptionHandler(NoChildrenFoundException.class)
    @ResponseBody
    protected ResponseEntity<APIResponse<String>> handleNoChildrenFoundException(NoChildrenFoundException noChildrenFoundException, HttpServletRequest request){
        return ApiHelper.<String>createAndSendResponse(ChildrenResponseCode.NO_CHILDREN_FOUND, HttpStatus.NOT_FOUND,
        		messageSourceResolver.resolver("children.not.found"));
    }
    
    @PostConstruct
    protected void init(){
    	Assert.notNull(messageSourceResolver, "Message Source Resolver can not be a null");
    }
  
}
