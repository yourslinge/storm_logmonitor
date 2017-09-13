package cn.zl.storm_logmonitor.logMonitor.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月15日 下午12:44:00
 * 邮件模块开发需要注意的事项：
 * 1.授权认证（以自己的网易邮箱为例）
 * 登陆自己的网易邮箱，点击设置->开启pop服务，这时需要你填写授权密码，此授权密码就是你登陆第三发邮件客户端的密码。
 * 2.开发邮件模块
 * 发件人的邮箱密码是授权密码，而不是你网页登陆邮箱的邮箱密码
 */
public class MessageSender {

	private static final Logger logger = Logger.getLogger(MessageSender.class);
	
	public static void main(String[] args) {
		List<String> receiver = new ArrayList<String>();
		receiver.add("1105604427@qq.com");
//		receiver.add("1005698493@qq.com");
		MailInfo mailInfo = new MailInfo("系统", "你出错了", receiver, null);
		boolean sendMailStatus = sendMail(mailInfo);
		System.out.println(sendMailStatus);
	}
	//发送邮件-邮件内容为文本格式
	public static boolean sendMail(MailInfo mailInfo){
		try {
			Message mailMessage = generateBaseInfo(mailInfo);
			//设置邮件主要内容
			mailMessage.setText(mailInfo.getMailContent());
			//发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	//邮件内容为html格式
	public static boolean senHtmlMail(MailInfo mailInfo){
		try {
			Message mailMessage = generateBaseInfo(mailInfo);
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getMailContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			Transport.send(mailMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static Message generateBaseInfo(MailInfo mailInfo) throws Exception{
		// 判断是否需要身份认证
		MailAuthenticator authenticator = null;
		Properties prop = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if(mailInfo.isAuthValidate()){
			authenticator = new MailAuthenticator(mailInfo.getUserName(), mailInfo.getUserPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(prop, authenticator);
		// 根据session创建一个邮件消息
		Message mailMessage = null;
		mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		Address from = new InternetAddress(mailInfo.getFromAddress(), mailInfo.getFromUserName());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		//多个接收者
		if(mailInfo.getToAddress()!=null && mailInfo.getToAddress().contains(",")){
			//Message.RecipientType.TO属性表示接收者的类型为TO
//			System.out.println(mailInfo.getToAddress());
//			InternetAddress[] address = InternetAddress.parse(mailInfo.getToAddress());
//			for (InternetAddress internetAddress : address) {
//				mailMessage.setRecipient(Message.RecipientType.TO, internetAddress);
//				Thread.sleep(3000);
//			}
			mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailInfo.getToAddress()));
		}else{
			//一个接收者
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
		}
		// 创建邮件的抄送者地址，并设置到邮件消息中
		if(StringUtils.isNotBlank(mailInfo.getCcAddress())){
			if(mailInfo.getCcAddress().contains(",")){
				mailMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(mailInfo.getCcAddress()));
			}else{
				Address cc = new InternetAddress(mailInfo.getCcAddress());
				mailMessage.setRecipient(Message.RecipientType.CC, cc);
			}
		}
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getMailSubject());
		// 设置邮件消息发送的时间
		mailMessage.setSentDate(new Date());
		return mailMessage;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
