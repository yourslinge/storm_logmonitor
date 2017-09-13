package cn.zl.storm_logmonitor.logMonitor.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月15日 上午10:06:34
 */
public class MailAuthenticator extends Authenticator{

	String userName;
	String userPassWord;
	public MailAuthenticator() {
		super();
	}
	public MailAuthenticator(String userName, String userPassWord) {
		super();
		this.userName = userName;
		this.userPassWord = userPassWord;
	}
	public PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(userName, userPassWord);
	}
	
}
