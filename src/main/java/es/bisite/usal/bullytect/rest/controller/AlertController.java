package es.bisite.usal.bullytect.rest.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.bisite.usal.bullytect.dto.request.AddAlertDTO;
import es.bisite.usal.bullytect.dto.response.AlertDTO;
import es.bisite.usal.bullytect.persistence.constraints.group.ICommonSequence;
import es.bisite.usal.bullytect.persistence.entity.AlertLevelEnum;
import es.bisite.usal.bullytect.rest.ApiHelper;
import es.bisite.usal.bullytect.rest.exception.NoAlertsFoundException;
import es.bisite.usal.bullytect.rest.exception.SocialMediaNotFoundException;
import es.bisite.usal.bullytect.rest.response.APIResponse;
import es.bisite.usal.bullytect.rest.response.AlertResponseCode;
import es.bisite.usal.bullytect.security.userdetails.CommonUserDetailsAware;
import es.bisite.usal.bullytect.security.utils.CurrentUser;
import es.bisite.usal.bullytect.security.utils.OnlyAccessForAdmin;
import es.bisite.usal.bullytect.security.utils.OnlyAccessForParent;
import es.bisite.usal.bullytect.service.IAlertService;

import java.util.Optional;
import javax.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@RestController("RestAlertsController")
@Validated
@RequestMapping("/api/v1/alerts/")
@Api(tags= "alerts", value = "/alerts/", description = "Punto de entrada para el manejo de Alertas", produces = "application/json")
public class AlertController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(AlertController.class);
	
	private final IAlertService alertService;
	
	public AlertController(IAlertService alertService) {
		super();
		this.alertService = alertService;
	}
	
	@RequestMapping(value = { "/", "/all" }, method = RequestMethod.GET)
	@OnlyAccessForAdmin
    @ApiOperation(value = "GET_ALL_ALERT", nickname = "GET_ALL_ALERT", notes="Get all alerts in the system", 
    	response = PagedResources.class)
	public ResponseEntity<APIResponse<PagedResources<Resource<AlertDTO>>>> getAllAlerts(
    		@ApiIgnore @PageableDefault Pageable pageable,
    		@ApiIgnore PagedResourcesAssembler<AlertDTO> pagedAssembler) throws Throwable {
		
		final Page<AlertDTO> alertsPage = alertService.findPaginated(pageable);
		
		if(alertsPage.getNumberOfElements() == 0) {
			throw new NoAlertsFoundException();
		}
		
		return ApiHelper.<PagedResources<Resource<AlertDTO>>>createAndSendResponse(AlertResponseCode.ALL_ALERTS, 
        		HttpStatus.OK, pagedAssembler.toResource(alertsPage));
    }

	@RequestMapping(value = { "/self", "/self/all" }, method = RequestMethod.GET)
	@OnlyAccessForParent
    @ApiOperation(value = "GET_ALL_SELF_ALERT", nickname = "GET_ALL_SELF_ALERT", notes="Get all alerts for the currently authenticated user", 
    	response = PagedResources.class)
	public ResponseEntity<APIResponse<PagedResources<Resource<AlertDTO>>>> getAllSelfAlerts(
    		@ApiIgnore @PageableDefault Pageable pageable,
    		@ApiIgnore PagedResourcesAssembler<AlertDTO> pagedAssembler,
    		@ApiIgnore @CurrentUser CommonUserDetailsAware<ObjectId> selfParent) throws Throwable {
		
		Page<AlertDTO> alertsPage = alertService.findByParentPaginated(selfParent.getUserId(), pageable);
		
		if(alertsPage.getNumberOfElements() == 0) {
			throw new NoAlertsFoundException();
		}
		
		return ApiHelper.<PagedResources<Resource<AlertDTO>>>createAndSendResponse(AlertResponseCode.ALL_SELF_ALERTS, 
        		HttpStatus.OK, pagedAssembler.toResource(alertsPage));
    }
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ApiOperation(value = "CREATE_ALERT", nickname = "CREATE_ALERT", notes="Create Alert", 
		response = AlertDTO.class)
	public ResponseEntity<APIResponse<AlertDTO>> addAlert(
			@ApiParam(value = "alert", required = true)
				@Validated(ICommonSequence.class) @RequestBody AddAlertDTO alert) throws Throwable {
		return Optional.ofNullable(alertService.save(alert))
        		.map(alertResource -> ApiHelper.<AlertDTO>createAndSendResponse(AlertResponseCode.ALERT_CREATED, HttpStatus.OK, alertResource))
        		.orElseThrow(() -> { throw new SocialMediaNotFoundException(); }); 
	}
	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@OnlyAccessForParent
    @ApiOperation(value = "GET_INFO_ALERTS", nickname = "GET_INFO_ALERTS", notes="Get all info alerts for the currently authenticated user", 
    	response = PagedResources.class)
	public ResponseEntity<APIResponse<PagedResources<Resource<AlertDTO>>>> getInfoAlerts(
    		@ApiIgnore @PageableDefault Pageable pageable,
    		@ApiIgnore PagedResourcesAssembler<AlertDTO> pagedAssembler,
    		@CurrentUser CommonUserDetailsAware<ObjectId> selfParent) throws Throwable {
		
		Page<AlertDTO> alertsPage = alertService.findByParentPaginated(selfParent.getUserId(), AlertLevelEnum.INFO, pageable);
		
		if(alertsPage.getNumberOfElements() == 0) {
			throw new NoAlertsFoundException();
		}
		
		return ApiHelper.<PagedResources<Resource<AlertDTO>>>createAndSendResponse(AlertResponseCode.ALL_INFO_ALERTS, 
        		HttpStatus.OK, pagedAssembler.toResource(alertsPage));
    }
	
	@RequestMapping(value = "/warning", method = RequestMethod.GET)
	@OnlyAccessForParent
    @ApiOperation(value = "GET_WARNING_ALERTS", nickname = "GET_WARNING_ALERTS", notes="Get warning alerts for the currently authenticated user", 
    	response = PagedResources.class)
	public ResponseEntity<APIResponse<PagedResources<Resource<AlertDTO>>>> getWarningAlerts(
    		@ApiIgnore @PageableDefault Pageable pageable,
    		@ApiIgnore PagedResourcesAssembler<AlertDTO> pagedAssembler,
    		@ApiIgnore @CurrentUser CommonUserDetailsAware<ObjectId> selfParent) throws Throwable {
		
		Page<AlertDTO> alertsPage = alertService.findByParentPaginated(selfParent.getUserId(), AlertLevelEnum.WARNING, pageable);
		
		if(alertsPage.getNumberOfElements() == 0) {
			throw new NoAlertsFoundException();
		}
		
		return ApiHelper.<PagedResources<Resource<AlertDTO>>>createAndSendResponse(AlertResponseCode.ALL_WARNING_ALERTS, 
        		HttpStatus.OK, pagedAssembler.toResource(alertsPage));
    }
	
	@RequestMapping(value = "/danger", method = RequestMethod.GET)
	@OnlyAccessForParent
    @ApiOperation(value = "GET_DANGER_ALERTS", nickname = "GET_DANGER_ALERTS", notes="Get danger alerts for the currently authenticated user",
    	response = PagedResources.class)
	public ResponseEntity<APIResponse<PagedResources<Resource<AlertDTO>>>> getDangerAlerts(
    		@ApiIgnore @PageableDefault Pageable pageable,
    		@ApiIgnore PagedResourcesAssembler<AlertDTO> pagedAssembler,
    		@ApiIgnore @CurrentUser CommonUserDetailsAware<ObjectId> selfParent) throws Throwable {
		
		Page<AlertDTO> alertsPage = alertService.findByParentPaginated(selfParent.getUserId(), AlertLevelEnum.DANGER, pageable);
		
		if(alertsPage.getNumberOfElements() == 0) {
			throw new NoAlertsFoundException();
		}
		
		return ApiHelper.<PagedResources<Resource<AlertDTO>>>createAndSendResponse(AlertResponseCode.ALL_DANGER_ALERTS, 
        		HttpStatus.OK, pagedAssembler.toResource(alertsPage));
    }
	
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	@OnlyAccessForParent
    @ApiOperation(value = "GET_SUCCESS_ALERTS", nickname = "GET_SUCCESS_ALERTS", notes="Get success alerts for the currently authenticated user", 
    	response = PagedResources.class)
	public ResponseEntity<APIResponse<PagedResources<Resource<AlertDTO>>>> getSuccessAlerts(
    		@ApiIgnore @PageableDefault Pageable pageable,
    		@ApiIgnore PagedResourcesAssembler<AlertDTO> pagedAssembler,
    		@ApiIgnore @CurrentUser CommonUserDetailsAware<ObjectId> selfParent) throws Throwable {
		
		Page<AlertDTO> alertsPage = alertService.findByParentPaginated(selfParent.getUserId(), AlertLevelEnum.SUCCESS, pageable);
		
		if(alertsPage.getNumberOfElements() == 0) {
			throw new NoAlertsFoundException();
		}
		
		return ApiHelper.<PagedResources<Resource<AlertDTO>>>createAndSendResponse(AlertResponseCode.ALL_DANGER_ALERTS, 
        		HttpStatus.OK, pagedAssembler.toResource(alertsPage));
    }
		
	@PostConstruct
	protected void init(){
		Assert.notNull(alertService, "Alert Service cannot be a null");
	}
}
