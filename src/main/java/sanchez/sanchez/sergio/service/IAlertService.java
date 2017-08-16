package sanchez.sanchez.sergio.service;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sanchez.sanchez.sergio.dto.response.AlertDTO;
import sanchez.sanchez.sergio.persistence.entity.AlertLevelEnum;

/**
 * @author sergio
 */
public interface IAlertService {
	Page<AlertDTO> findByParentPaginated(ObjectId id, Pageable pageable);
	Page<AlertDTO> findByParentPaginated(ObjectId id, AlertLevelEnum type, Pageable pageable);
}