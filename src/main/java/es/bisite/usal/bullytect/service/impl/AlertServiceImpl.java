package es.bisite.usal.bullytect.service.impl;

import javax.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import es.bisite.usal.bullytect.dto.request.AddAlertDTO;
import es.bisite.usal.bullytect.dto.response.AlertDTO;
import es.bisite.usal.bullytect.mapper.AlertEntityMapper;
import es.bisite.usal.bullytect.persistence.entity.AlertEntity;
import es.bisite.usal.bullytect.persistence.entity.AlertLevelEnum;
import es.bisite.usal.bullytect.persistence.repository.AlertRepository;
import es.bisite.usal.bullytect.service.IAlertService;

@Service
public class AlertServiceImpl implements IAlertService {

    private final AlertRepository alertRepository;
    private final AlertEntityMapper alertMapper;

    public AlertServiceImpl(AlertRepository alertRepository, AlertEntityMapper alertMapper) {
        super();
        this.alertRepository = alertRepository;
        this.alertMapper = alertMapper;
    }

    @Override
    public Page<AlertDTO> findByParentPaginated(ObjectId id, Pageable pageable) {
        Page<AlertEntity> alertsPage = alertRepository.findByParentIdOrderByCreateAtDesc(id, pageable);
        return alertsPage.map(new Converter<AlertEntity, AlertDTO>() {
            @Override
            public AlertDTO convert(AlertEntity alertEntity) {
                return alertMapper.alertEntityToAlertDTO(alertEntity);
            }
        });
    }

    @Override
    public Page<AlertDTO> findByParentPaginated(ObjectId id, AlertLevelEnum level, Pageable pageable) {
        Page<AlertEntity> alertsPage = alertRepository.findByLevelAndParentIdOrderByCreateAtDesc(level, id, pageable);
        return alertsPage.map(new Converter<AlertEntity, AlertDTO>() {
            @Override
            public AlertDTO convert(AlertEntity alertEntity) {
                return alertMapper.alertEntityToAlertDTO(alertEntity);
            }
        });
    }

    @Override
    public AlertDTO save(AddAlertDTO alert) {
        final AlertEntity alertToSave = alertMapper.addAlertDTOToAlertEntity(alert);
        final AlertEntity alertSaved = alertRepository.save(alertToSave);
        return alertMapper.alertEntityToAlertDTO(alertSaved);
    }

    @Override
    public Page<AlertDTO> findPaginated(Pageable pageable) {
        Page<AlertEntity> alertsPage = alertRepository.findAll(pageable);
        return alertsPage.map(new Converter<AlertEntity, AlertDTO>() {
            @Override
            public AlertDTO convert(AlertEntity a) {
                return alertMapper.alertEntityToAlertDTO(a);
            }
        });
    }

    @Override
    public Long getTotalAlerts() {
        return alertRepository.count();
    }

    @PostConstruct
    protected void init() {
        Assert.notNull(alertRepository, "Alert Repository cannot be null");
    }
}
