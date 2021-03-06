package es.bisite.usal.bullytect.mapper;

import com.restfb.types.Comment;

import es.bisite.usal.bullytect.persistence.entity.CommentEntity;

import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

/**
 * @author sergio
 */
@Mapper
public interface IFacebookCommentMapper {
    
    @Mappings({ 
        @Mapping(target = "id", ignore=true),
        @Mapping(source = "facebookComment.likeCount", target = "likes"),
        @Mapping(expression="java(es.bisite.usal.bullytect.persistence.entity.SocialMediaTypeEnum.FACEBOOK)", target = "socialMedia")
    })
    @Named("facebookCommentToCommentEntity")
    CommentEntity facebookCommentToCommentEntity(Comment facebookComment); 
	
    @IterableMapping(qualifiedByName = "facebookCommentToCommentEntity")
    List<CommentEntity> facebookCommentsToCommentEntities(List<Comment> facebookComments);
       
}
