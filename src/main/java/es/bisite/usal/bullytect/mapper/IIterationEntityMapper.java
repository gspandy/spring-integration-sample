package es.bisite.usal.bullytect.mapper;

import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import es.bisite.usal.bullytect.dto.response.IterationDTO;
import es.bisite.usal.bullytect.persistence.entity.IterationEntity;

/**
 * @author sergio
 */
@Mapper
public interface IIterationEntityMapper {
    
    @Mappings({
        @Mapping(source = "iterationEntity.startDate", target = "startDate", dateFormat = "dd/MM/yyyy HH:mm:ss"),
        @Mapping(source = "iterationEntity.finishDate", target = "finishDate", dateFormat = "dd/MM/yyyy HH:mm:ss")
    })
    @Named("iterationEntityToIterationDTO")
    IterationDTO iterationEntityToIterationDTO(IterationEntity iterationEntity); 
	
    @IterableMapping(qualifiedByName = "iterationEntityToIterationDTO")
    List<IterationDTO> iterationEntitiesToIterationDTOs(List<IterationEntity> iterationEntities);
}
