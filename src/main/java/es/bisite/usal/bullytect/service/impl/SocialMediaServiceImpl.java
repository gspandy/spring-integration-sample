package es.bisite.usal.bullytect.service.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import es.bisite.usal.bullytect.dto.request.SaveSocialMediaDTO;
import es.bisite.usal.bullytect.dto.response.SocialMediaDTO;
import es.bisite.usal.bullytect.mapper.SocialMediaEntityMapper;
import es.bisite.usal.bullytect.persistence.entity.SocialMediaEntity;
import es.bisite.usal.bullytect.persistence.entity.SocialMediaTypeEnum;
import es.bisite.usal.bullytect.persistence.repository.SocialMediaRepository;
import es.bisite.usal.bullytect.service.IItegrationFlowService;
import es.bisite.usal.bullytect.service.ISocialMediaService;

/**
 * @author sergio
 */
@Service
public class SocialMediaServiceImpl implements ISocialMediaService {
    
    private final SocialMediaRepository socialMediaRepository;
    private final SocialMediaEntityMapper socialMediaMapper;
    private final IItegrationFlowService itegrationFlowService;

    public SocialMediaServiceImpl(SocialMediaRepository socialMediaRepository, 
    		SocialMediaEntityMapper socialMediaMapper, IItegrationFlowService itegrationFlowService) {
        this.socialMediaRepository = socialMediaRepository;
        this.socialMediaMapper = socialMediaMapper;
        this.itegrationFlowService = itegrationFlowService;
    }
    
    @Override
    public List<SocialMediaDTO> getSocialMediaByUser(String id) {
        List<SocialMediaEntity> socialMediaEntities = socialMediaRepository.findBySonEntityId(new ObjectId(id));
        return socialMediaMapper.socialMediaEntitiesToSocialMediaDTO(socialMediaEntities);
    }   
    
    @Override
    public SocialMediaDTO getSocialMediaById(String id) {
        SocialMediaEntity socialMediaEntity = socialMediaRepository.findOne(new ObjectId(id));
        return socialMediaMapper.socialMediaEntityToSocialMediaDTO(socialMediaEntity);
    }
    
   
	@Override
	public SocialMediaDTO create(SaveSocialMediaDTO addSocialMediaDTO) {
		SocialMediaEntity socialMediaEntityToSave = socialMediaMapper.addSocialMediaDTOToSocialMediaEntity(addSocialMediaDTO);
		SocialMediaEntity socialMediaEntitySaved = socialMediaRepository.save(socialMediaEntityToSave);
		return socialMediaMapper.socialMediaEntityToSocialMediaDTO(socialMediaEntitySaved);
	}
	
	@Override
	public List<SocialMediaDTO> getInvalidSocialMediaById(String id) {
		List<SocialMediaEntity> socialMediaEntities = socialMediaRepository.findByIdAndInvalidTokenTrue(new ObjectId(id));
		return socialMediaMapper.socialMediaEntitiesToSocialMediaDTO(socialMediaEntities);
	}
	
	@Override
	public Page<SocialMediaDTO> findPaginated(Pageable pageable) {
		Page<SocialMediaEntity> socialMediaEntities =  socialMediaRepository.findAll(pageable);
        return socialMediaEntities.map(new Converter<SocialMediaEntity, SocialMediaDTO>(){
            @Override
            public SocialMediaDTO convert(SocialMediaEntity socialMediaEntity) {
               return socialMediaMapper.socialMediaEntityToSocialMediaDTO(socialMediaEntity);
            }
        });
	}
	
	@Override
	public SocialMediaDTO getSocialMediaByTypeAndSonId(String type, String sonId) {
		return socialMediaMapper.socialMediaEntityToSocialMediaDTO(
				socialMediaRepository.findByTypeAndSonEntityId(SocialMediaTypeEnum.valueOf(type), new ObjectId(sonId)));
	}
	
	@Override
	public SocialMediaDTO update(SaveSocialMediaDTO socialMedia) {
		Assert.notNull(socialMedia, "Social Media can not be null");
		SocialMediaDTO socialMediaUpdated = null;
		SocialMediaEntity socialMediaEntityToUpdate = socialMediaRepository.findByTypeAndSonEntityId(
				SocialMediaTypeEnum.valueOf(socialMedia.getType()), new ObjectId(socialMedia.getSon()));
		if(socialMediaEntityToUpdate != null) {
			socialMediaEntityToUpdate.setAccessToken(socialMedia.getAccessToken());
			socialMediaEntityToUpdate.setScheduledFor(itegrationFlowService.getDateForNextPoll().getTime());
			SocialMediaEntity socialMediaEntityUpdated = socialMediaRepository.save(socialMediaEntityToUpdate);
			socialMediaUpdated = socialMediaMapper.socialMediaEntityToSocialMediaDTO(socialMediaEntityUpdated);
		}
		return socialMediaUpdated;
	}
	
	@Override
	public SocialMediaDTO save(SaveSocialMediaDTO socialMedia) {
		Assert.notNull(socialMedia, "Social Media can not be null");
		SocialMediaEntity socialMediaEntityToSave = socialMediaRepository.findByTypeAndSonEntityId(
				SocialMediaTypeEnum.valueOf(socialMedia.getType()), new ObjectId(socialMedia.getSon()));
		if(socialMediaEntityToSave != null) {
			socialMediaEntityToSave.setAccessToken(socialMedia.getAccessToken());
			socialMediaEntityToSave.setScheduledFor(itegrationFlowService.getDateForNextPoll().getTime());
		} else {
			socialMediaEntityToSave= socialMediaMapper.addSocialMediaDTOToSocialMediaEntity(socialMedia);
		}
		SocialMediaEntity socialMediaEntitySaved = socialMediaRepository.save(socialMediaEntityToSave);
		return socialMediaMapper.socialMediaEntityToSocialMediaDTO(socialMediaEntitySaved);
	}
        
    @Override
    public List<SocialMediaDTO> deleteSocialMediaByUser(String id) {
        Assert.notNull(id, "Id can not be null");
        Assert.hasLength(id, "Id can not be empty");
        List<SocialMediaEntity> socialMediaEntities = socialMediaRepository.deleteBySonEntityId(new ObjectId(id));
        return socialMediaMapper.socialMediaEntitiesToSocialMediaDTO(socialMediaEntities);
    }
    
    
    @Override
    public SocialMediaDTO deleteSocialMediaById(String id) {
        Assert.notNull(id, "Id can not be null");
        Assert.hasLength(id, "Id can not be empty");
        SocialMediaEntity socialMediaEntityDeleted = socialMediaRepository.deleteById(new ObjectId(id));
        return socialMediaMapper.socialMediaEntityToSocialMediaDTO(socialMediaEntityDeleted);
    }
    
    @Override
	public List<SocialMediaDTO> getValidSocialMediaById(String id) {
    	List<SocialMediaEntity> socialMediaEntities = socialMediaRepository.findByIdAndInvalidTokenFalse(new ObjectId(id));
		return socialMediaMapper.socialMediaEntitiesToSocialMediaDTO(socialMediaEntities);
	}
	
	@PostConstruct
    protected void init(){
        Assert.notNull(socialMediaRepository, "Social Media Repository cannot be null");
        Assert.notNull(socialMediaMapper, "Social Media Mapper cannot be null");
        Assert.notNull(itegrationFlowService, "Integration Flow Service can not be null");
	}
}
