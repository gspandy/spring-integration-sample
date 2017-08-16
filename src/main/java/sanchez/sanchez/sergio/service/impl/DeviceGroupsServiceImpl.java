package sanchez.sanchez.sergio.service.impl;

import java.util.Set;
import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.lang.Assert;
import sanchez.sanchez.sergio.dto.response.DeviceDTO;
import sanchez.sanchez.sergio.dto.response.DeviceGroupDTO;
import sanchez.sanchez.sergio.mapper.DeviceEntityMapper;
import sanchez.sanchez.sergio.mapper.DeviceGroupEntityMapper;
import sanchez.sanchez.sergio.persistence.entity.DeviceEntity;
import sanchez.sanchez.sergio.persistence.entity.DeviceGroupEntity;
import sanchez.sanchez.sergio.persistence.entity.ParentEntity;
import sanchez.sanchez.sergio.persistence.repository.DeviceGroupRepository;
import sanchez.sanchez.sergio.persistence.repository.DeviceRepository;
import sanchez.sanchez.sergio.persistence.repository.ParentRepository;
import sanchez.sanchez.sergio.service.IDeviceGroupsService;

@Service
public class DeviceGroupsServiceImpl implements IDeviceGroupsService {
	
	private final DeviceGroupRepository deviceGroupRepository;
	private final DeviceRepository deviceRepository;
	private final DeviceEntityMapper deviceEntityMapper;
	private final DeviceGroupEntityMapper deviceGroupEntityMapper;
	private final ParentRepository parentRepository;

	public DeviceGroupsServiceImpl(DeviceGroupRepository deviceGroupRepository, 
			DeviceRepository deviceRepository, DeviceEntityMapper deviceEntityMapper, 
			DeviceGroupEntityMapper deviceGroupEntityMapper, ParentRepository parentRepository) {
		super();
		this.deviceGroupRepository = deviceGroupRepository;
		this.deviceRepository = deviceRepository;
		this.deviceEntityMapper = deviceEntityMapper;
		this.deviceGroupEntityMapper = deviceGroupEntityMapper;
		this.parentRepository = parentRepository;
	}

	@Override
	public DeviceGroupDTO getDeviceGroupByName(String name) {
		DeviceGroupEntity deviceGroup = deviceGroupRepository.findByNotificationKeyName(name);
		return deviceGroupEntityMapper.deviceGroupEntityToDeviceGroupDTO(deviceGroup);
	}

	@Override
	public DeviceGroupDTO createDeviceGroup(String name, String key, ObjectId owner) {
		ParentEntity parentEntity = parentRepository.findOne(owner);
		DeviceGroupEntity deviceGroupSaved = deviceGroupRepository.save(new DeviceGroupEntity(name, key, parentEntity));
		return deviceGroupEntityMapper.deviceGroupEntityToDeviceGroupDTO(deviceGroupSaved);
	}

	@Override
	public DeviceGroupDTO createDeviceGroup(String name, String key, ObjectId owner, Set<DeviceEntity> devices) {
		ParentEntity parentEntity = parentRepository.findOne(owner);
		DeviceGroupEntity deviceGroupSaved = deviceGroupRepository.save(new DeviceGroupEntity(name, key, parentEntity));
		for(DeviceEntity device: devices){
			device.setDeviceGroup(deviceGroupSaved);
		}
		deviceRepository.save(devices);
		return deviceGroupEntityMapper.deviceGroupEntityToDeviceGroupDTO(deviceGroupSaved);
	}

	@Override
	public DeviceDTO addDeviceToGroup(String registrationToken, String deviceGroupId) {
		DeviceGroupEntity deviceGroup = deviceGroupRepository.findOne(new ObjectId(deviceGroupId));
		DeviceEntity deviceSaved = deviceRepository.save(new DeviceEntity(registrationToken, deviceGroup));
		return deviceEntityMapper.deviceEntityToDeviceDTO(deviceSaved);
	}

	@Override
	public DeviceDTO removeDevice(String registrationToken) {
		DeviceEntity deviceRemoved = deviceRepository.deleteByRegistrationToken(registrationToken);
		return deviceEntityMapper.deviceEntityToDeviceDTO(deviceRemoved);
	}

	@Override
	public Iterable<DeviceDTO> getDevicesFromGroup(String groupName) {
		return deviceEntityMapper.deviceEntitiesToDeviceDTO(deviceRepository.findByDeviceGroupNotificationKeyName(groupName));
	}

	@Override
	public String getNotificationKey(String groupName) {
		return deviceGroupRepository.getNotificationKey(groupName);
	}
	
	@PostConstruct
	protected void init(){
		Assert.notNull(deviceGroupRepository, "Device Group Repository can not be null");
		Assert.notNull(deviceRepository, "Device Repository can not be null");
		Assert.notNull(deviceEntityMapper, "Device Entity Mapper can not be null");
		Assert.notNull(deviceGroupEntityMapper, "Device Group Entity Mapper can not be null");
		Assert.notNull(parentRepository, "Parent Repository can not be null");
	}

}