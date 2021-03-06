package es.bisite.usal.bullytect.mapper;

import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import es.bisite.usal.bullytect.dto.response.CommentDTO;
import es.bisite.usal.bullytect.persistence.entity.CommentEntity;

/**
 * @author sergio
 */
@Mapper
public interface ICommentEntityMapper {
    
    @Mappings({
        @Mapping(expression="java(commentEntity.getId().toString())", target = "identity" ),
        @Mapping(source = "commentEntity.sonEntity.fullName", target = "user"),
        @Mapping(source = "commentEntity.createdTime", target = "createdTime", dateFormat = "dd/MM/yyyy")
    })
    @Named("commentEntityToCommentDTO")
    CommentDTO commentEntityToCommentDTO(CommentEntity commentEntity); 
	
    @IterableMapping(qualifiedByName = "commentEntityToCommentDTO")
    List<CommentDTO> commentEntitiesToCommentDTOs(List<CommentEntity> commentEntities);
    
}
