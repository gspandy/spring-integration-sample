package es.bisite.usal.bullytect.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.common.collect.Lists;

import es.bisite.usal.bullytect.fcm.operations.DevicesGroupOperation;
import es.bisite.usal.bullytect.fcm.operations.DevicesGroupOperationType;
import es.bisite.usal.bullytect.fcm.operations.FCMNotificationOperation;
import es.bisite.usal.bullytect.fcm.properties.FCMCustomProperties;
import es.bisite.usal.bullytect.fcm.response.FirebaseResponse;
import es.bisite.usal.bullytect.service.IPushNotificationsService;
import io.jsonwebtoken.lang.Assert;

@Service
public class PushNotificationsServiceImpl implements IPushNotificationsService {
	
	private static Logger logger = LoggerFactory.getLogger(PushNotificationsServiceImpl.class);
	
	private final RestTemplate restTemplate;
	private final FCMCustomProperties firebaseCustomProperties;


	public PushNotificationsServiceImpl(RestTemplate restTemplate, FCMCustomProperties firebaseCustomProperties) {
		super();
		this.restTemplate = restTemplate;
		this.firebaseCustomProperties = firebaseCustomProperties;
	}

	@Async
    @Override
    public CompletableFuture<String> createNotificationGroup(String userid, List<String> deviceTokens) {
		Assert.notNull(userid, "User id can not be null");
		Assert.notEmpty(deviceTokens, "Device Tokens can not be null");
		Assert.notNull(firebaseCustomProperties.getGroupPrefix(), "Group Prefix can not be null");
    	Assert.notNull(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be null");
    	Assert.hasLength(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be empty");
		
		String notificationGroupName = String.format("%s_%d", firebaseCustomProperties.getGroupPrefix(), userid);
        return CompletableFuture.supplyAsync(() -> restTemplate.postForObject(firebaseCustomProperties.getNotificationGroupsUrl(), 
				new DevicesGroupOperation(notificationGroupName, deviceTokens), String.class));
    }

    @Async
    @Override
    public CompletableFuture<String> createNotificationGroup(String userid) {
    	Assert.notNull(userid, "User id can not be null");
    	Assert.notNull(firebaseCustomProperties.getGroupPrefix(), "Group Prefix can not be null");
    	Assert.notNull(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be null");
    	Assert.hasLength(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be empty");
    	
    	String notificationGroupName = String.format("%s_%d", firebaseCustomProperties.getGroupPrefix(), userid);
        return CompletableFuture.supplyAsync(() -> restTemplate.postForObject(firebaseCustomProperties.getNotificationGroupsUrl(), 
				new DevicesGroupOperation(notificationGroupName), String.class));
    }

    @Async
    @Override
    public CompletableFuture<String> addDevicesToGroup(String userid, String notificationGroupKey, List<String> deviceTokens) {
    	Assert.notNull(userid, "User id can not be null");
    	Assert.notNull(notificationGroupKey, "Notification Group Key can not be null");
    	Assert.notEmpty(deviceTokens, "Device Tokens can not be empty");
    	Assert.notNull(firebaseCustomProperties.getGroupPrefix(), "Group Prefix can not be null");
    	Assert.notNull(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be null");
    	Assert.hasLength(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be empty");
    	
  	
    	String notificationGroupName = String.format("%s_%d", firebaseCustomProperties.getGroupPrefix(), userid);
        return CompletableFuture.supplyAsync(() -> restTemplate.postForObject(firebaseCustomProperties.getNotificationGroupsUrl(),
                new DevicesGroupOperation(DevicesGroupOperationType.ADD, notificationGroupName, notificationGroupKey, deviceTokens), String.class));
    }

    @Async
    @Override
    public CompletableFuture<String> removeDevicesFromGroup(String userid, String notificationGroupKey, List<String> deviceTokens) {
    	Assert.notNull(userid, "User id can not be null");
    	Assert.notNull(notificationGroupKey, "Notification Group Key can not be null");
    	Assert.notEmpty(deviceTokens, "Device Tokens can not be empty");
    	Assert.notNull(firebaseCustomProperties.getGroupPrefix(), "Group Prefix can not be null");
    	Assert.notNull(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be null");
    	Assert.hasLength(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be empty");
    	
    	String notificationGroupName = String.format("%s_%d", firebaseCustomProperties.getGroupPrefix(), userid);
        return CompletableFuture.supplyAsync(() ->restTemplate.postForObject(firebaseCustomProperties.getNotificationGroupsUrl(),
                new DevicesGroupOperation(DevicesGroupOperationType.REMOVE, notificationGroupName, notificationGroupKey, deviceTokens), String.class));
    }

    @Async
    @Override
    public CompletableFuture<String> addDeviceToGroup(String userid, String notificationGroupKey, String deviceToken ) {
    	Assert.notNull(userid, "User id can not be null");
    	Assert.notNull(notificationGroupKey, "Notification Group Key can not be null");
    	Assert.notNull(deviceToken, "Device Token can not be empty");
    	Assert.notNull(firebaseCustomProperties.getGroupPrefix(), "Group Prefix can not be null");
    	Assert.notNull(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be null");
    	Assert.hasLength(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be empty");
    	
    	String notificationGroupName = String.format("%s_%d", firebaseCustomProperties.getGroupPrefix(), userid);
        return CompletableFuture.supplyAsync(() ->restTemplate.postForObject(firebaseCustomProperties.getNotificationGroupsUrl(),
                new DevicesGroupOperation(DevicesGroupOperationType.ADD, notificationGroupName, notificationGroupKey, 
                		Lists.newArrayList(deviceToken)), String.class));
    }

    @Async
    @Override
    public CompletableFuture<String> removeDeviceFromGroup(String userid, String notificationGroupKey, String deviceToken) {
    	Assert.notNull(userid, "User id can not be null");
    	Assert.notNull(notificationGroupKey, "Notification Group Key can not be null");
    	Assert.notNull(deviceToken, "Device Token can not be empty");
    	Assert.notNull(firebaseCustomProperties.getGroupPrefix(), "Group Prefix can not be null");
    	Assert.notNull(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be null");
    	Assert.hasLength(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be empty");
    	
    	String notificationGroupName = String.format("%s_%d", firebaseCustomProperties.getGroupPrefix(), userid);
        return CompletableFuture.supplyAsync(() -> restTemplate.postForObject(firebaseCustomProperties.getNotificationGroupsUrl(),
                new DevicesGroupOperation(DevicesGroupOperationType.REMOVE, notificationGroupName, notificationGroupKey, 
                		Lists.newArrayList(deviceToken)), String.class));
    }
    
    @Override
	public CompletableFuture<FirebaseResponse> send(FCMNotificationOperation fcmNotificationOperation) {
    	Assert.notNull(fcmNotificationOperation, "FCM Notification operation can not be null");
    	Assert.notNull(firebaseCustomProperties.getGroupPrefix(), "Group Prefix can not be null");
    	Assert.notNull(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be null");
    	Assert.hasLength(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be empty");
    	return CompletableFuture.supplyAsync(() -> restTemplate.postForObject(firebaseCustomProperties.getNotificationSendUrl(), 
    			fcmNotificationOperation, FirebaseResponse.class));
	}
    
    @Override
	public CompletableFuture<Void> updateDeviceToken(String userid, String notificationGroupKey,
			String oldDeviceToken, String newDeviceToken) {
    	Assert.notNull(userid, "User id can not be null");
    	Assert.notNull(notificationGroupKey, "Notification Group Key can not be null");
    	Assert.notNull(oldDeviceToken, "Old Device Token can not be empty");
    	Assert.notNull(newDeviceToken, "New Device Token can not be empty");
    	Assert.notNull(firebaseCustomProperties.getGroupPrefix(), "Group Prefix can not be null");
    	Assert.notNull(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be null");
    	Assert.hasLength(firebaseCustomProperties.getNotificationGroupsUrl(), "Notification Group Url can not be empty");
    	
    	String notificationGroupName = String.format("%s_%d", firebaseCustomProperties.getGroupPrefix(), userid);
    	
    	return CompletableFuture.allOf(
    			CompletableFuture.supplyAsync(() -> restTemplate.postForObject(firebaseCustomProperties.getNotificationGroupsUrl(),
    	                new DevicesGroupOperation(DevicesGroupOperationType.REMOVE, notificationGroupName, notificationGroupKey, 
    	                		Lists.newArrayList(oldDeviceToken)), String.class)),
    			CompletableFuture.supplyAsync(() ->restTemplate.postForObject(firebaseCustomProperties.getNotificationGroupsUrl(),
    	                new DevicesGroupOperation(DevicesGroupOperationType.ADD, notificationGroupName, notificationGroupKey, 
    	                		Lists.newArrayList(newDeviceToken)), String.class)));
    
  
	}
	
	@PostConstruct
	protected void init(){
		Assert.notNull(restTemplate, "Rest Template can not be null");
		Assert.notNull(firebaseCustomProperties, "Firebase Custom Properties can not be null");
		Assert.hasLength(firebaseCustomProperties.getAppServerKey(), "App Server Key can not be empty");
		logger.debug("App Server Key -> " + firebaseCustomProperties.getAppServerKey());
	}

	
}
