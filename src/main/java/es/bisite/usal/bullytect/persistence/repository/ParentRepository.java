package es.bisite.usal.bullytect.persistence.repository;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.bisite.usal.bullytect.persistence.entity.ParentEntity;
import es.bisite.usal.bullytect.persistence.entity.UserSystemEntity;

/**
 *
 * @author sergio
 */
@Repository
public interface ParentRepository extends MongoRepository<ParentEntity, ObjectId>, ParentRepositoryCustom{
	ParentEntity findOneByEmail(String email);
	ParentEntity findByConfirmationToken(String confirmationToken);
	Long countByEmail(String email);
	Long countByConfirmationToken(String confirmationToken);
	ParentEntity findByFbId(String fbId);
	Long deleteByConfirmationToken(String confirmationToken);
	Long deleteByActiveFalse();
}
