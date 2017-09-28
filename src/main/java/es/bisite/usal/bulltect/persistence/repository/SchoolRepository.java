package es.bisite.usal.bulltect.persistence.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import es.bisite.usal.bulltect.persistence.entity.SchoolEntity;


/**
 *
 * @author sergio
 */
@Repository
public interface SchoolRepository extends MongoRepository<SchoolEntity, ObjectId> {
	Page<SchoolEntity> findAllByNameLike(String name, Pageable pageable);
	@Query(value="{}", fields="{ 'id' : 0, 'name' : 1, 'residence': 0, 'location': 0, 'province': 0, 'tfno': 0, 'email': 0}")
	Iterable<String> getAllSchoolNames();
}
