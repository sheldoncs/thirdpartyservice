package uwi.third.thirdparty.service;

import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uwi.third.thirdparty.service.ActiveDirectory.User;

@Service
public class LDAPService {

	Logger logger = LoggerFactory.getLogger(LDAPService.class);
	
    private  DirContext context;
   
	
	public  Boolean authenticate(String username, String password) throws NamingException {
		return ActiveDirectory.getConnection(username, password, "cavehill.uwi.edu","cavehillsrv1");	
	} 
	public User getUser(String username) {
		return ActiveDirectory.getUser(username);
	}
	public String getUserDetails(String username, String password, Hashtable<String, String> environment) throws NamingException {
		
		context = new InitialDirContext(environment);
		// Create the search controls         
	      SearchControls searchCtls = new SearchControls();
	      
	    //Specify the attributes to return
	      String returnedAtts[]={"mail"};
	      searchCtls.setReturningAttributes(returnedAtts);
	      //OU=Cave Hill,DC=cavehill,DC=uwi,DC=edu
	      NamingEnumeration answer = context.search("OU=Staff,OU=Accounts,OU=Cave Hill,DC=cavehill,DC=uwi, DC=edu", "saMAccountName="
	    		  + username, searchCtls);
	    		   
	    		  if (answer.hasMore()) {
	    		  Attributes attrs = ((SearchResult) answer.next()).getAttributes();
	    		  System.out.println(attrs.get("distinguishedName"));
	    		  System.out.println(attrs.get("givenname"));
	    		  System.out.println(attrs.get("sn"));
	    		  System.out.println(attrs.get("mail"));
	    		  System.out.println(attrs.get("telephonenumber"));
	    		  System.out.println(attrs.get("canonicalName"));
	    		  System.out.println(attrs.get("userAccountControl"));
	    		  System.out.println(attrs.get("accountExpires"));
	    		  }
		context.close();
		
		
		return null;
	}
	
	public Boolean authenticateUser (Hashtable<String, String> environment) throws Exception  {
		
		logger.info("Authenticating user");
		context = new InitialDirContext(environment);
		
		
//		String principal = String.format("%s@cavehill.uwi.edu", username);
//		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//		environment.put(Context.PROVIDER_URL, "LDAP://cavehillsrv1.cavehill.uwi.edu:389");
//		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
//		environment.put(Context.SECURITY_PRINCIPAL, principal);
//		environment.put(Context.SECURITY_CREDENTIALS, password);
		
		return true;
	}
}
