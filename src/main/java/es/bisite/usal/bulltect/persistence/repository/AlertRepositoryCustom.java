package es.bisite.usal.bulltect.persistence.repository;

import java.util.List;
import org.bson.types.ObjectId;

import es.bisite.usal.bulltect.web.dto.response.AlertsBySonDTO;

/**
 * @author sergio
 */
public interface AlertRepositoryCustom {
	void setAsDelivered(List<ObjectId> alertIds);
	void setAsDelivered(ObjectId alertId);
	List<AlertsBySonDTO> getAlertsBySon(List<String> sonIds);
}
