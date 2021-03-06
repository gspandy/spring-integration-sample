package es.bisite.usal.bullytect.rest.controller;

import java.util.Optional;
import javax.validation.Valid;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.bisite.usal.bullytect.dto.request.AddSchoolDTO;
import es.bisite.usal.bullytect.dto.response.SchoolDTO;
import es.bisite.usal.bullytect.dto.response.ValidationErrorDTO;
import es.bisite.usal.bullytect.persistence.constraints.ValidObjectId;
import es.bisite.usal.bullytect.rest.ApiHelper;
import es.bisite.usal.bullytect.rest.exception.NoSchoolsFoundException;
import es.bisite.usal.bullytect.rest.exception.SchoolNotFoundException;
import es.bisite.usal.bullytect.rest.hal.ISchoolHAL;
import es.bisite.usal.bullytect.rest.response.APIResponse;
import es.bisite.usal.bullytect.rest.response.SchoolResponseCode;
import es.bisite.usal.bullytect.security.utils.OnlyAccessForAdmin;
import es.bisite.usal.bullytect.service.ISchoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController("RestSchoolController")
@Validated
@RequestMapping("/api/v1/schools/")
@Api(tags = "schools", value = "/schools/", description = "Manejo de la información del Colegio", produces = "application/json")
public class SchoolController extends BaseController implements ISchoolHAL {

    private static Logger logger = LoggerFactory.getLogger(SchoolController.class);
    
    private final ISchoolService schoolService;

    public SchoolController(ISchoolService schoolService) {
        this.schoolService = schoolService;
    }
    
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiOperation(value = "GET_ALL_SCHOOLS", nickname = "GET_ALL_SCHOOLS", notes = "Get all Schools",
            response = PagedResources.class)
    public ResponseEntity<APIResponse<PagedResources<Resource<SchoolDTO>>>> getAllSchools(
    		@ApiIgnore @PageableDefault Pageable pageable, 
    		@ApiIgnore PagedResourcesAssembler<SchoolDTO> pagedAssembler) throws Throwable {
    	
    	Page<SchoolDTO> schoolPage = schoolService.findPaginated(pageable);

    	if(schoolPage.getTotalElements() == 0)
    		throw new NoSchoolsFoundException();
    	
    	return ApiHelper.<PagedResources<Resource<SchoolDTO>>>createAndSendResponse(SchoolResponseCode.ALL_SCHOOLS, 
        		HttpStatus.OK, pagedAssembler.toResource(addLinksToSchool((schoolPage))));
    	
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "FIND_SCHOOLS_BY_NAME", nickname = "FIND_SCHOOLS_BY_NAME", notes = "Find Schools by name",
            response = PagedResources.class)
    public ResponseEntity<APIResponse<PagedResources<Resource<SchoolDTO>>>> getSchoolsByName(
    		@ApiParam(value = "name", required = true) 
    			@Valid @NotBlank(message = "{school.name.notblank}") 
    				@RequestParam(value = "name", required = false) String name,
    		@ApiIgnore @PageableDefault Pageable pageable, 
    		@ApiIgnore PagedResourcesAssembler<SchoolDTO> pagedAssembler) throws Throwable {
    	
    	Page<SchoolDTO> schoolPage = schoolService.findByNamePaginated(name, pageable);
    	
    	if(schoolPage.getTotalElements() == 0)
    		throw new NoSchoolsFoundException();
    	
    	return ApiHelper.<PagedResources<Resource<SchoolDTO>>>createAndSendResponse(SchoolResponseCode.SCHOOLS_BY_NAME, 
        		HttpStatus.OK, pagedAssembler.toResource(addLinksToSchool((schoolPage))));
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "GET_SCHOOL_BY_ID", nickname = "GET_SCHOOL_BY_ID", notes = "Get School By Id")
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message= "School By Id", response = SchoolDTO.class)
    })
    public ResponseEntity<APIResponse<SchoolDTO>> getSchoolById(
    		@ApiParam(name = "id", value = "Identificador del Centro escolar", required = true)
    			@Valid @ValidObjectId(message = "{school.id.notvalid}")
    		 		@PathVariable String id) throws Throwable {
        logger.debug("Get User with id: " + id);
        
        return Optional.ofNullable(schoolService.getSchoolById(id))
                .map(schoolResource -> addLinksToSchool(schoolResource))
                .map(schoolResource -> ApiHelper.<SchoolDTO>createAndSendResponse(SchoolResponseCode.SINGLE_SCHOOL, 
                		HttpStatus.OK, schoolResource))
                .orElseThrow(() -> { throw new SchoolNotFoundException(); });
    }
   
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @OnlyAccessForAdmin
    @ApiOperation(value = "CREATE_SCHOOL", nickname = "CREATE_SCHOOL", notes = "Create School")
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message= "School Saved", response = SchoolDTO.class),
    		@ApiResponse(code = 403, message = "Validation Errors", response = ValidationErrorDTO.class)
    })
    public ResponseEntity<APIResponse<SchoolDTO>> saveSchool(
    		@ApiParam(value = "school", required = true) 
				@Valid @RequestBody AddSchoolDTO school) throws Throwable {        
        return Optional.ofNullable(schoolService.save(school))
                .map(schoolResource -> addLinksToSchool(schoolResource))
                .map(schoolResource -> ApiHelper.<SchoolDTO>createAndSendResponse(SchoolResponseCode.SCHOOL_SAVED, 
                		HttpStatus.OK, schoolResource))
                .orElseThrow(() -> { throw new SchoolNotFoundException(); });
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @OnlyAccessForAdmin
    @ApiOperation(value = "DELETE_SCHOOL", nickname = "DELETE_SCHOOL", notes = "Delete School")
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message= "School Deleted", response = SchoolDTO.class)
    })
    public ResponseEntity<APIResponse<SchoolDTO>> deleteSchool(
    		@ApiParam(name = "id", value = "Identificador del Centro escolar", required = true)
    			@Valid @ValidObjectId(message = "{school.id.notvalid}")
    		 		@PathVariable String id) throws Throwable {        
    	
        return Optional.ofNullable(schoolService.delete(id))
                .map(schoolResource -> ApiHelper.<SchoolDTO>createAndSendResponse(SchoolResponseCode.SCHOOL_DELETED, 
                		HttpStatus.OK, schoolResource))
                .orElseThrow(() -> { throw new SchoolNotFoundException(); });
    }
}
