
package es.bisite.usal.bullytect.service.impl;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.ArrayMap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentListResponse;
import com.google.api.services.youtube.model.CommentThreadListResponse;

import es.bisite.usal.bullytect.exception.GetCommentsProcessException;
import es.bisite.usal.bullytect.exception.InvalidAccessTokenException;
import es.bisite.usal.bullytect.mapper.IYoutubeCommentMapper;
import es.bisite.usal.bullytect.persistence.entity.CommentEntity;
import es.bisite.usal.bullytect.persistence.entity.SocialMediaTypeEnum;
import es.bisite.usal.bullytect.service.IMessageSourceResolver;
import es.bisite.usal.bullytect.service.IYoutubeService;
import es.bisite.usal.bullytect.util.StreamUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import org.springframework.util.Assert;

/**
 *
 * @author sergio
 */
@Service
@Profile({"dev", "prod"})
public class YoutubeServiceImpl implements IYoutubeService {
    
    private Logger logger = LoggerFactory.getLogger(YoutubeServiceImpl.class);
    
    private final ApplicationContext appCtx;
    private final IYoutubeCommentMapper youtubeCommentMapper;
    private final IMessageSourceResolver messageSourceResolver;

    public YoutubeServiceImpl(ApplicationContext appCtx, IYoutubeCommentMapper youtubeCommentMapper, IMessageSourceResolver messageSourceResolver) {
        this.appCtx = appCtx;
        this.youtubeCommentMapper = youtubeCommentMapper;
        this.messageSourceResolver = messageSourceResolver;
    }
    
    
    private ChannelListResponse getChannelsForCurrentUser(final YouTube youTube) {
    	try {
    		return youTube.channels().list("snippet").setMine(Boolean.TRUE).execute();
    	}
    	catch (IOException e) {
			throw new UncheckedIOException(e);
		}
    	
    }
    
    
    private CommentThreadListResponse getCommentThreadLists(final YouTube youTube, final String channelId) {
    	try {
			return youTube.commentThreads().list("snippet")
					.setAllThreadsRelatedToChannelId(channelId)
			        .execute();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
    }
    
    private CommentListResponse getRepliesForComment(final YouTube youTube, final String parentId){
    	try {
    		return youTube.comments().list("snippet")
    				.setParentId(parentId).execute();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
    	
    }
    
    
    private Stream<Comment> getCommentRelatedToChannelAfterThan(final YouTube youTube, final String channelId, Date startDate) {
    	
		return Optional.ofNullable(getCommentThreadLists(youTube, channelId))
				.map(commentThreadListResult -> commentThreadListResult.getItems()).map(Collection::stream)
				.filter(Objects::nonNull).orElse(Stream.empty())

				.flatMap(commentThread -> {
					Comment topLevelComment = commentThread.getSnippet().getTopLevelComment();

					return commentThread.getReplies() != null ?

							StreamUtils.concat(
									Optional.ofNullable(getRepliesForComment(youTube, topLevelComment.getId()))
											.map(commentListResponse -> commentListResponse.getItems())
											.map(Collection::stream).filter(Objects::nonNull).orElse(Stream.empty()),
									topLevelComment)

							: Stream.of(topLevelComment);

				}).filter(comment -> {
					
					String authorChannelId = ((ArrayMap<String, String>)comment.getSnippet().getAuthorChannelId()).getValue(0);
					Date publishAt = new Date(comment.getSnippet().getPublishedAt().getValue());
					return !authorChannelId.equals(channelId) && ( startDate != null ? publishAt.after(startDate) : true);
					
				});
    }
    
    
    
    @Override
    public List<CommentEntity> getCommentsLaterThan(Date startDate, String accessToken) {
        
        logger.debug("Call Youtube Data API for accessToekn : " + accessToken + " on thread: " + Thread.currentThread().getName());
        List<CommentEntity> userComments = new ArrayList<>();
        try {
            YouTube youTube = appCtx.getBean(YouTube.class, accessToken);
            // Get Channels for User
            userComments = Optional.ofNullable(getChannelsForCurrentUser(youTube))
            	.map(channelResult -> channelResult.getItems())
            	.map(Collection::stream)
            	.filter(Objects::nonNull)
            	.orElse(Stream.empty())
            	.flatMap(channel -> getCommentRelatedToChannelAfterThan(youTube, channel.getId(), startDate))
            	.map(comment -> youtubeCommentMapper.youtubeCommentToCommentEntity(comment))
            	.collect(Collectors.toList());
            
            logger.debug("·Total Youtube Comments: " + userComments.size());
        
        } catch (Throwable e) {
        	if(e.getCause() instanceof GoogleJsonResponseException && ((GoogleJsonResponseException)e.getCause()).getDetails().getCode() == 401) {
        		logger.debug("InvalidAccessTokenException FOR YOUTUBE");
        		throw new InvalidAccessTokenException(
                		messageSourceResolver.resolver("invalid.access.token", new Object[]{ SocialMediaTypeEnum.YOUTUBE.name() }),
                		SocialMediaTypeEnum.YOUTUBE, accessToken);
        	}
            throw new GetCommentsProcessException(e.toString());
        }
        return userComments;
    }
    
    @PostConstruct
    protected void init(){
        Assert.notNull(youtubeCommentMapper, "Youtube Comment Mapper cannot be null");
        Assert.notNull(appCtx, "ApplicationContext cannot be null");
        Assert.notNull(messageSourceResolver, "The Message Source Resolver can not be null");
    }

}
