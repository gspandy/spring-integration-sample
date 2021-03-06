package es.bisite.usal.bullytect.security.userdetails;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface CommonUserDetailsAware<T> extends UserDetails {
	
	final String ID = "id";
    final String EMAIL = "EMAIL";
    final String PASSWORD = "PASSWORD";
    final String FIRST_NAME = "FIRST_NAME";
    final String LAST_NAME = "LAST_NAME";
    final String LAST_PASSWORD_RESET_DATE = "LAST_PASSWORD_RESET_DATE";
    
    
    T getUserId();
    String getEmail();
    String getPassword();
    String getFirstName();
    String getLastName();
    String getFullName();
    Date getLastPasswordResetDate();
    
}
