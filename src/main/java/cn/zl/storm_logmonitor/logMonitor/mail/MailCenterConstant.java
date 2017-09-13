package cn.zl.storm_logmonitor.logMonitor.mail;
/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月15日 上午9:38:45
 */
public class MailCenterConstant {

	public static final String PROTOCOL = "smtp";
	//设置发件人使用的SMTP服务器
	public static final String SMTP_SERVER="smtp.163.com";
	//邮件发送，发件人使用的邮箱地址
	public static final String FROM_ADDRESS="yourslinge@163.com";
	//发件人使用第三方邮件客户端进行发送邮件，而不是通过网页发送邮件；所以，下面的用户名和密码是收件人
	//登陆第三方邮件客户端的用户名和密码，这里的密码是指授权码，也就是你开启邮箱pop服务时，要求你输入的授权码
	public static final String USER="yourslinge@163.com";
	public static final String PWD="zl123456";
}
