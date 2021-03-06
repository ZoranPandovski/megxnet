package net.megx.security.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.megx.model.auth.Consumer;
import net.megx.security.auth.model.Role;
import net.megx.security.auth.model.User;
import net.megx.security.auth.services.ConsumerService;
import net.megx.security.auth.services.UserService;
import net.megx.security.auth.services.db.DBConsumerService;
import net.megx.security.auth.services.db.DBUserService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConsumerServiceTest extends DBServiceTest{
	
	private ConsumerService consumerService;
	private UserService userService;
	
	private Consumer defaultConsumer;
	
	public static String NAME = "ConsumerName";
	public static String DESCRIPTION = "Description for the consumer";
	public static String KEY = "NzA1NDZhMTktZmMwOC00NmI2LTg0ZTUtNDg4ZWRmODE0ZjYy";
	public static String SECRET = "NIaWgKMJOkAYm6vdFpgGHpPOfF4mXFmvqL-pb698hsiD3y-bZ5FyGGMD7z0pqFk0w7Ol2qD7n-GdJ-HeW3Srqd";
	public static boolean OOB = false;
	public static Date EXPIRATION_DATE = new Date();
	public static boolean TRUSTED = false;
	public static String RESOURCE = "Consumer resource";
	public static String ROLES = "ConsumerRoles";
	public static String LOG_NAME = "testUser";
	public static String CALLBACK_URL = "Call/BackURL";
	
	public static String NEW_NAME = "NEWConsumerName";
	public static String NEW_DESCRIPTION = "NEWDescription for the consumer";
	public static String NEW_KEY = "NeW1NDZhMTktZmMwOC00NmI2LTg0ZTUtNDg4ZWRmODE0ZjYy";
	public static String NEW_SECRET = "NewWgKMJOkAYm6vdFpgGHpPOfF4mXFmvqL-pb698hsiD3y-bZ5FyGGMD7z0pqFk0w7Ol2qD7n-GdJ-HeW3Srqd";
	public static Date NEW_EXPIRATION_DATE = new Date();
	public static String NEW_RESOURCE = "NEWConsumer resource";
	public static String NEW_ROLES = "NEWConsumerRoles";
	public static String NEW_LOG_NAME = "NEWtestUser";
	public static String NEW_CALLBACK_URL = "NEWCall/BackURL";
	
	
	
	public static String USER_DESCRIPTION = "Description fro the new user";
	public static String FIRST_NAME = "Test";
	public static String LAST_NAME = "User";
	public static String INITIALS = "TU";
	public static String EMAIL = "testUser@testsite.com";
	public static boolean DISABLED = false;
	public static boolean EXTERNAL = false;
	public static Date JOINED_ON = new Date();
	public static Date LAST_LOGIN = new Date();
	public static String PASSWORD = "password";
	public static String PROVIDER = "external-provider.com";
	public static Role ROLE_USER = new Role();
	public static Role ROLE_ADMIN = new Role();
	public static Role ROLE_CMS_ADMIN = new Role();
	static{
		ROLE_USER.setLabel("user");
		ROLE_ADMIN.setLabel("admin");
		ROLE_CMS_ADMIN.setLabel("cmsAdmin");
	}
	
	private User createNewUser(String username){
		User user = new User();
		user.setDescription(DESCRIPTION);
		user.setDisabled(DISABLED);
		user.setEmail(EMAIL);
		user.setExternal(EXTERNAL);
		user.setFirstName(FIRST_NAME);
		user.setInitials(INITIALS);
		user.setLastName(LAST_NAME);
		user.setJoinedOn(JOINED_ON);
		user.setLastlogin(LAST_LOGIN);
		user.setLastName(LAST_NAME);
		user.setLogin(username);
		user.setPassword(PASSWORD);
		user.setProvider(PROVIDER);
		List<Role> roles = new ArrayList<Role>(1);
		roles.add(ROLE_USER);
		user.setRoles(roles);
		return user;
	}
	
	@Before
	public void setup() throws Exception{
		consumerService = buildService(DBConsumerService.class);
		userService = buildService(DBUserService.class);
		
		User testUser = createNewUser(LOG_NAME);
		User updatedUser = createNewUser(NEW_LOG_NAME);
		userService.addUser(testUser);
		userService.addUser(updatedUser);
		
		defaultConsumer = createNewConsumer();
		consumerService.addConsumer(defaultConsumer);
		
		
	}
	
	private Consumer createNewConsumer(){
		Consumer consumer = new Consumer();
		consumer.setCallbackUrl(CALLBACK_URL);
		consumer.setDescription(DESCRIPTION);
		consumer.setExpirationDate(EXPIRATION_DATE);
		consumer.setKey(KEY);
		consumer.setLogname(LOG_NAME);
		consumer.setName(NAME);
		consumer.setOob(OOB);
		//consumer.setResource(RESOURCE);
		//consumer.setRoles(ROLES);
		consumer.setSecret(SECRET);
		consumer.setTrusted(TRUSTED);
		return consumer;
	}
	
	@Test
	public void testGetConsumer() throws Exception{
		Consumer consumer = consumerService.getConsumer(NAME);
		Assert.assertNotNull("Consumer is NULL", consumer);
		Assert.assertEquals("Callback URL does not match", CALLBACK_URL, consumer.getCallbackUrl());
		Assert.assertEquals("Description does not match", DESCRIPTION ,consumer.getDescription());
		Assert.assertEquals("Expiration date does not match", EXPIRATION_DATE, consumer.getExpirationDate());
		Assert.assertEquals("Key does not match",  KEY, consumer.getKey());
		Assert.assertEquals("Log name does not match", LOG_NAME, consumer.getLogname());
		Assert.assertEquals("Name does not match", NAME, consumer.getName());
		//Assert.assertEquals("Resource does not match", RESOURCE, consumer.getResource());
		//Assert.assertEquals("Role does not match", ROLES, consumer.getRoles());
		Assert.assertEquals("Secret does not match", SECRET, consumer.getSecret());
	}
	
	@Test
	public void testGetConsumerForKey() throws Exception{
		Consumer consumer = consumerService.getConsumerForKey(KEY);
		Assert.assertNotNull("Consumer is NULL", consumer);
		Assert.assertEquals("Callback URL does not match", CALLBACK_URL, consumer.getCallbackUrl());
		Assert.assertEquals("Description does not match", DESCRIPTION ,consumer.getDescription());
		Assert.assertEquals("Expiration date does not match", EXPIRATION_DATE, consumer.getExpirationDate());
		Assert.assertEquals("Key does not match",  KEY, consumer.getKey());
		Assert.assertEquals("Log name does not match", LOG_NAME, consumer.getLogname());
		Assert.assertEquals("Name does not match", NAME, consumer.getName());
		//Assert.assertEquals("Resource does not match", RESOURCE, consumer.getResource());
		//Assert.assertEquals("Role does not match", ROLES, consumer.getRoles());
		Assert.assertEquals("Secret does not match", SECRET, consumer.getSecret());
	}
	
	@Test
	public void testGetConsumerForKeyAndName() throws Exception{
		Consumer consumer = consumerService.getConsumerForKeyAndName(KEY, NAME);
		Assert.assertNotNull("Consumer is NULL", consumer);
		Assert.assertEquals("Callback URL does not match", CALLBACK_URL, consumer.getCallbackUrl());
		Assert.assertEquals("Description does not match", DESCRIPTION ,consumer.getDescription());
		Assert.assertEquals("Expiration date does not match", EXPIRATION_DATE, consumer.getExpirationDate());
		Assert.assertEquals("Key does not match",  KEY, consumer.getKey());
		Assert.assertEquals("Log name does not match", LOG_NAME, consumer.getLogname());
		Assert.assertEquals("Name does not match", NAME, consumer.getName());
		//Assert.assertEquals("Resource does not match", RESOURCE, consumer.getResource());
		//Assert.assertEquals("Role does not match", ROLES, consumer.getRoles());
		Assert.assertEquals("Secret does not match", SECRET, consumer.getSecret());
	}
	
	@Test
	public void testAddConsumer() throws Exception{
		consumerService.removeConsumer(defaultConsumer);
		consumerService.addConsumer(defaultConsumer);
		Consumer retrievedConsumer = consumerService.getConsumer(NAME);
		Assert.assertNotNull("Consumer is NULL", retrievedConsumer);
		Assert.assertEquals("Callback URL does not match", CALLBACK_URL, retrievedConsumer.getCallbackUrl());
		Assert.assertEquals("Description does not match", DESCRIPTION ,retrievedConsumer.getDescription());
		Assert.assertEquals("Expiration date does not match", EXPIRATION_DATE, retrievedConsumer.getExpirationDate());
		Assert.assertEquals("Key does not match",  KEY, retrievedConsumer.getKey());
		Assert.assertEquals("Log name does not match", LOG_NAME, retrievedConsumer.getLogname());
		Assert.assertEquals("Name does not match", NAME, retrievedConsumer.getName());
		//Assert.assertEquals("Resource does not match", RESOURCE, retrievedConsumer.getResource());
		//Assert.assertEquals("Role does not match", ROLES, retrievedConsumer.getRoles());
		Assert.assertEquals("Secret does not match", SECRET, retrievedConsumer.getSecret());
	}
	
	@Test
	public void testRemoveConsumer() throws Exception{
		consumerService.removeConsumer(defaultConsumer);
		Consumer retrievedConsumer = consumerService.getConsumer(NAME);
		Assert.assertNull("Consumer is NOT NULL", retrievedConsumer);
		consumerService.addConsumer(defaultConsumer);
	}
	
	@Test
	public void testUpdateConsumer() throws Exception{
		Consumer retrievedConsumer = consumerService.getConsumer(NAME);
		retrievedConsumer.setCallbackUrl(NEW_CALLBACK_URL);
		retrievedConsumer.setDescription(NEW_DESCRIPTION);
		retrievedConsumer.setExpirationDate(NEW_EXPIRATION_DATE);
		//retrievedConsumer.setKey(NEW_KEY); - the consumer key cannot be updated
		//retrievedConsumer.setLogname(NEW_LOG_NAME); - the owner of the consumer cannot be changed.
		retrievedConsumer.setName(NEW_NAME);
		//retrievedConsumer.setResource(NEW_RESOURCE);
		//retrievedConsumer.setRoles(NEW_ROLES);
		retrievedConsumer.setSecret(NEW_SECRET);
		consumerService.updateConsumer(retrievedConsumer);
		Consumer updatedConsumer = consumerService.getConsumer(NEW_NAME);
		Assert.assertNotNull("Retrieved consumer should not be null.",updatedConsumer);
		Assert.assertEquals("Callback URL does not match", updatedConsumer.getCallbackUrl(), NEW_CALLBACK_URL);
		Assert.assertEquals("Description does not match", updatedConsumer.getDescription(), NEW_DESCRIPTION);
		Assert.assertEquals("Expiration date does not match", updatedConsumer.getExpirationDate(), NEW_EXPIRATION_DATE);
		//Assert.assertEquals("Key does not match", updatedConsumer.getKey(), NEW_KEY);
		//Assert.assertEquals("Log name does not match", updatedConsumer.getLogname(), NEW_LOG_NAME);
		Assert.assertEquals("Name does not match", updatedConsumer.getName(), NEW_NAME);
		//Assert.assertEquals("Resource does not match", updatedConsumer.getResource(), NEW_RESOURCE);
		//Assert.assertEquals("Role does not match", updatedConsumer.getRoles(), NEW_ROLES);
		Assert.assertEquals("Secret does not match", updatedConsumer.getSecret(), NEW_SECRET);
	}
	
	/*@Test
	public void testGetConsumersForUser() throws Exception{
		List<Consumer> consumers = consumerService.getConsumersForUser(LOG_NAME);
		Assert.assertTrue("Default consumer is not present in the list of consumers", consumers.contains(defaultConsumer));
	}*/
	
	@After
	public void tearDown() throws Exception{
		try{
			consumerService.removeConsumer(defaultConsumer);
		}finally{
			userService.removeUser(LOG_NAME);
			userService.removeUser(NEW_LOG_NAME);
		}
	}
}
