package es.bisite.usal.bullytect.util;

public final class Utils {
	
	public static String getObfuscatedEmail(String mail){
		int pos = mail.indexOf('@');
		return pos >= 0 ? "...".concat(mail.substring(pos)): mail;
	}

}
