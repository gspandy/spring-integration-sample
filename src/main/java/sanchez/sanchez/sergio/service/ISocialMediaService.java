package sanchez.sanchez.sergio.service;

import java.util.List;

import sanchez.sanchez.sergio.dto.response.SocialMediaDTO;

/**
 *
 * @author sergio
 */
public interface ISocialMediaService {
    List<SocialMediaDTO> getSocialMediaByUser(String id);
    SocialMediaDTO getSocialMediaById(String id);
}