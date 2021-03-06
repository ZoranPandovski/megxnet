package net.megx.security.oauth.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.megx.model.auth.Consumer;
import net.megx.security.auth.SecurityException;
import net.megx.security.auth.StopSecurityChainException;
import net.megx.security.auth.model.Token;
import net.megx.security.auth.services.ConsumerService;
import net.megx.security.auth.services.UserService;
import net.megx.security.oauth.OAuthServices;
import net.megx.security.oauth.TokenServices;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseOAuthServices implements OAuthServices{
	protected UserService userService;
	protected ConsumerService consumerService;
	protected TokenServices tokenServices;
	
	protected Properties oAuthConfig = new Properties();
	
	protected Log log = LogFactory.getLog(getClass());
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public void setConsumerService(ConsumerService consumerService) {
		this.consumerService = consumerService;
	}
	public void setTokenServices(TokenServices tokenServices) {
		this.tokenServices = tokenServices;
	}
	
	
	
	protected OAuthConsumer getConsumer(String consumerKey) throws OAuthProblemException{
		try {
			if(consumerKey == null){
				throw new InvalidOAuthRequestException(OAuth.Problems.PARAMETER_ABSENT);
			}
			Consumer consumer = consumerService.getConsumerForKey(consumerKey);
			if(consumer == null){
				throw new InvalidOAuthRequestException(OAuth.Problems.CONSUMER_KEY_UNKNOWN, 401);
			}
			OAuthConsumer oaConsumer = new OAuthConsumer(
					consumer.getCallbackUrl(), 
					consumerKey, consumer.getSecret(), 
					null // service provider ?
					);
			oaConsumer.setProperty("name", consumer.getName());
			oaConsumer.setProperty("description", consumer.getDescription());
			return 	oaConsumer;
			
		}catch (OAuthProblemException e) {
			throw e;
		} catch (Exception e) {
			throw new OAuthProblemException(e.getMessage());
		}
	}
	private static OAuthValidator SIMPLE_VALIDATOR = new SimpleOAuthValidator();
	
	protected OAuthValidator getValidator(){
		return SIMPLE_VALIDATOR;
	}
	
	protected void validateMessage(OAuthMessage message, OAuthAccessor accessor) throws OAuthException, IOException, URISyntaxException{
		getValidator().validateMessage(message, accessor);
		/*
		 *  as in the example at http://oauth.googlecode.com/svn/code/java/example/oauth-provider/src/net/oauth/example/provider/servlets/RequestTokenServlet.java
		 */
		{
            // Support the 'Variable Accessor Secret' extension
            // described in http://oauth.pbwiki.com/AccessorSecret
            String secret = message.getParameter(
            		oAuthConfig.getProperty("oauth_1.accessorSecret","oauth_accessor_secret"));
            if (secret != null) {
                accessor.setProperty(OAuthConsumer.ACCESSOR_SECRET, secret);
            }
        }
		
	}
	
	protected OAuthAccessor getAccessorForAccessToken(String aToken)throws IOException, OAuthProblemException{
		if(aToken == null){
			throw new InvalidOAuthRequestException(OAuth.Problems.PARAMETER_ABSENT);
		}
		OAuthAccessor accessor = null;
		Token accessToken = tokenServices.getAccessToken(aToken);
		if(accessToken == null){
			throw new InvalidOAuthRequestException(OAuth.Problems.TOKEN_EXPIRED, 401);
		}
		OAuthConsumer consumer = getConsumer(accessToken.getConsumerKey());
		accessor = new OAuthAccessor(consumer);
		accessor.requestToken = null;
		accessor.tokenSecret = accessToken.getSecret();
		accessor.accessToken = accessToken.getToken();
		accessor.setProperty("authorized", accessToken.isAuthorized());
		return accessor;
	}
	
	protected void handleException(Exception e, HttpServletRequest request,
            HttpServletResponse response, boolean sendBody)
            throws IOException, ServletException, SecurityException {
		/*
        String realm = (request.isSecure())?"https://":"http://";
        realm += request.getLocalName();
        OAuthServlet.handleException(response, e, realm, sendBody);
        */ 
		if(e instanceof StopSecurityChainException){
			throw (SecurityException)e;
		}
		log.debug("Error during OAuth step.",e);
		throw new ServletException("OAuth error",e);
    }
	
	protected OAuthAccessor getAccessorForRequestToken(String token) throws IOException, OAuthProblemException{
		if(token == null){
			throw new InvalidOAuthRequestException(OAuth.Problems.PARAMETER_ABSENT);
		}
		OAuthAccessor accessor = null;
		Token requestToken = tokenServices.getRequestToken(token);
		if(requestToken == null){
			throw new InvalidOAuthRequestException(OAuth.Problems.TOKEN_EXPIRED, 401);
		}
		OAuthConsumer consumer = getConsumer(requestToken.getConsumerKey());
		accessor = new OAuthAccessor(consumer);
		accessor.requestToken = requestToken.getToken();
		accessor.tokenSecret = requestToken.getSecret();
		accessor.accessToken = null;
		accessor.setProperty("authorized", requestToken.isAuthorized());
		accessor.setProperty("oauth_callback", requestToken.getCallbackUrl());
		accessor.setProperty("oauth_verifier", requestToken.getVerifier());
		return accessor;
	}
	
	public class InvalidOAuthRequestException extends OAuthProblemException{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5012131135544203039L;

		public InvalidOAuthRequestException() {
			super();
		}

		public InvalidOAuthRequestException(String problem) {
			super(problem);
			setParameter(HTTP_STATUS_CODE, 400);
		}
		
		public InvalidOAuthRequestException(String problem, int httpCode) {
			super(problem);
			setParameter(HTTP_STATUS_CODE, httpCode);
		}
		
	}
	
}
