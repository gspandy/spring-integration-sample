package sanchez.sanchez.sergio.persistence.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import sanchez.sanchez.sergio.persistence.repository.DeviceRepository;

public class DeviceShouldExistsValidator implements ConstraintValidator<DeviceShouldExists, String> {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Override
    public void initialize(DeviceShouldExists constraintAnnotation) {}

    @Override
    public boolean isValid(String deviceId, ConstraintValidatorContext context) {
    	return deviceRepository.countByDeviceId(deviceId) > 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}
