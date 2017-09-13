package cn.zl.storm_logmonitor.logMonitor.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;











import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.zl.storm_logmonitor.logMonitor.dao.LogMonitorDao;
import cn.zl.storm_logmonitor.logMonitor.domain.App;
import cn.zl.storm_logmonitor.logMonitor.domain.Message;
import cn.zl.storm_logmonitor.logMonitor.domain.Record;
import cn.zl.storm_logmonitor.logMonitor.domain.Rule;
import cn.zl.storm_logmonitor.logMonitor.domain.User;
import cn.zl.storm_logmonitor.logMonitor.mail.MailInfo;
import cn.zl.storm_logmonitor.logMonitor.mail.MessageSender;
import cn.zl.storm_logmonitor.logMonitor.sms.SMSBase;


/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月8日 下午5:02:43
 */
public class MonitorHandler {
	
	private static Logger logger = Logger.getLogger(MonitorHandler.class);
	//定义一个map，其中appId为Key，以该appId下的所有rule为Value
	private static Map<String,List<Rule>> ruleMap;
	//定义一个map,其中appId为Key，以该appId下的所有user为Value
	private static Map<String,List<User>> userMap;
	//定义一个list，用来封装所有的用户信息
	private static List<User> userList;
	//定义一个list，用来封装所有的应用信息
	private static List<App> appList;
	//定时加载配置文件的标识
	private static boolean reloaded = false;
	static{
		load();
	}
	
	public static boolean trigger(Message message){
		//如果规则模型为空，需要初始化加载规则模型
		if(ruleMap==null){
			load();
		}
		System.out.println(message.getAppId());
		//从规则模型中获取当前appid配置的规则
		List<Rule> ruleList = ruleMap.get(message.getAppId());
		for (Rule rule : ruleList) {
			//如果日志中包含过滤过的关键词，即为匹配成功
			if(message.getLine().contains(rule.getKeyword())){
				//装配一些信息
				message.setRuleId(rule.getId()+"");
				message.setKeyword(rule.getKeyword());
				return true;
			}
		}
		return false;
	}
	/**
     * 解析输入的日志，将数据按照一定的规则进行分割。
     * 判断日志是否合法，主要校验日志所属应用的appId是否存在
     *
     * @param line 一条日志
     * @return
     */
	public static Message parser(String line){
		//日志切分
		String[] messageArr = line.split("\\$\\$\\$\\$\\$");
		//日志校验
		if(messageArr.length!=2){
			return null;
		}
		//isBlank：判断某字符串是否为空或长度为0或由空白符(whitespace)构成
		if(StringUtils.isBlank(messageArr[0])||StringUtils.isBlank(messageArr[1])){
			return null;
		}
		//检查当前的日志所属应用的appId是否经过授权,即当前appId是否属于该监控平台监控
		if(appIdisValid(messageArr[0].trim())){
			Message message = new Message();
			message.setAppId(messageArr[0].trim());
			message.setLine(messageArr[1]);
			return message;
		}
		return null;
	}
	/**
     * 验证appid是否经过授权
     * @param id 输入appId
     */
	public static boolean appIdisValid(String id){
		
		try {
			for (App app : appList) {
				if(app.getId()==Integer.parseInt(id)){
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	 /**
     * 加载数据模型，主要是用户列表、应用列表、应用-规则模型、应用-用户模型。
     */
	public static synchronized void load(){
		if(userList == null){
			userList = loadUserList();
		}
		if(appList == null){
			appList = loadAppList();
		}
		if(ruleMap == null){
			ruleMap = loadRuleMap();
		}
		if(userMap == null){
			userMap = loadUserMap();
		}
	}
	/**
	 * 封装应用与用户对应的map
	 * @return
	 */
	public static Map<String,List<User>> loadUserMap(){
		//以应用的appId为key，以应用的所有负责人的userList对象为value。
		Map<String,List<User>> map = new HashMap<String, List<User>>();
		for(App app : appList){
			String userIds = app.getUserId();
			List<User> userListInApp = map.get(app.getId()+"");
			if(userListInApp==null){
				userListInApp = new ArrayList<User>();
				map.put(app.getId()+"", userListInApp);
			}
			String[] userIdArr = userIds.split(",");
			for (String userId : userIdArr) {
				userListInApp.add(queryUserById(userId));
			}
			map.put(app.getId()+"", userListInApp);
		}
		return map;
	}
	/**
	 * 通过用户编号获取用户的JavaBean
	 * @param id 用户id
	 * @return  返回封装用户信息的javabean
	 */
	public static User queryUserById(String id){
		for(User user:userList){
			if(user.getId()==Integer.parseInt(id)){
				return user;
			}
		}
		return null;
	}
	
	/**
	 * @param Map<String,List<Rule>>  String-->appId
	 *        就是将Rule表中的appId作为key，此appId对于的一条信息封装成Rule作为value
	 * @return
	 */
	public static Map<String,List<Rule>> loadRuleMap(){
		Map<String,List<Rule>> map = new HashMap<String, List<Rule>>();
		LogMonitorDao logMonitorDao = new LogMonitorDao();
		//rule表中，就包含appId字段
		List<Rule> ruleList = logMonitorDao.getRuleList();
		for (Rule rule : ruleList) {
			//每一个appId 所对应的rule  注意appId的类型在每个类中或者表中是不同的
			List<Rule> ruleListByAppId = map.get(rule.getAppId()+"");
			//如果数据map中，没有此rule集合，则创建一个，并将其保存到map中，
			if(ruleListByAppId==null){
				ruleListByAppId = new ArrayList<Rule>();
				map.put(rule.getAppId()+"", ruleListByAppId);
			}
			//将appId 对应的rule加入到集合中
			ruleListByAppId.add(rule);
			map.put(rule.getAppId()+"", ruleListByAppId);
		}
		return map;
	}
	//访问数据库获取所有有效的app列表
	public static List<App> loadAppList(){
		return new LogMonitorDao().getAppList();
	}
	//访问数据库获取所有有效用户的列表
	public static List<User> loadUserList(){
		return new LogMonitorDao().getUserList();
	}
	//通过appId编号，获取当前app的所有负责人列表
	public static List<User> getUserIdsByAppId(String appId){
		return userMap.get(appId);
	}
	//告警模块，用来发送邮件和短信
	public static void notifly(String appId,Message message){
		List<User> users = getUserIdsByAppId(appId);
//		for (User user : users) {
//			System.out.println(user.toString());
//		}
		//发送邮件
		if(sendMail(appId, users, message)){
			message.setIsEmail(1);
			System.out.println("success");
		}
		//发送短信
		/*
		if(sendSMS(appId, users, message)){
			message.setIsPhone(1);
			System.out.println("success");
		}
		*/
	}
	/**
     * 发送邮件
     * 后期可以改造为将邮件数据发送到外部的消息队里中，然后创建一个worker去发送短信。
     * @param appId
     * @param userList
     * @param message
     * @return
     */
	private static boolean sendMail(String appId,List<User> userList,Message message){
		List<String> receiver = new ArrayList<String>();
		//找到用户的邮箱
		for (User user : userList) {
			receiver.add(user.getEmail());
		}
//		for (String string : receiver) {
//			
//			System.out.println(string);
//		}
		//找到App的名称
		for(App app : appList){
			if(app.getId() == Integer.parseInt(appId.trim())){
				message.setAppName(app.getName());
				break;
			}
		}
		if(receiver.size()>=1){
			String date = DateUtils.getDateTime();
//			String content = "系统【"+message.getAppName()+"】在"+date+"触发规则"+message.getRuleId()+"，过滤关键字为："+message.getKeyword()+" 错误内容："+message.getLine();
			String content = "xinxixueyuan";
			MailInfo mailInfo = new MailInfo("系统", "你出错了", receiver, null);
			return MessageSender.sendMail(mailInfo);		
		}
		return false;
	}
	
	/**
     * 发送短信的模块
     * 由于短信资源匮乏，目前该功能不开启，默认true，即短信发送成功。
     * 目前发送短信功能使用的是外部接口，外面接口的并发性没法保证，会影响storm程序运行的效率。
     *  后期可以改造为将短信数据发送到外部的消息队里中，然后创建一个worker去发送短信。
     * @param appId
     * @param users
     * @param message
     * @return
     */
	private static boolean sendSMS(String appId,List<User> users,Message message){
		List<String> mobileList = new ArrayList<String>();
		for (User user : users) {
			mobileList.add(user.getMobile());
		}
		for(App app : appList){
			if(app.getId()==Integer.parseInt(appId)){
				message.setAppName(app.getName());
				break;
			}
		}
		String content = "系统【" + message.getAppName() + "】在 " + DateUtils.getDateTime() + " 触发规则 " + message.getRuleId() + ",关键字：" + message.getKeyword();
		
		return SMSBase.sendSms(listToStringFormat(mobileList), content);
	}
	/**
     * 将list转换为String
     * @param list
     * @return
     */
	private static String listToStringFormat(List<String> list){
		StringBuilder builder = new StringBuilder();
		int size = list.size();
		for(int i =0;i<size;i++){
			if(i == size-1){
				builder.append(list.get(i));
			}else{
				builder.append(list.get(i)+",");
			}
		}
		return null;
	}
	
	 /**
     * 配置scheduleLoad重新加载底层数据模型。
     */
    /**
     * thread 4
     * thread 3
     * thread 2
     */
	public static synchronized void reloadDataModel(){
//      * thread 1  reloaded = true   ----> reloaded = false
//      * thread 2  reloaded = false
//      * thread 2  reloaded = false
//      * thread 2  reloaded = false
		if(reloaded){
			appList = loadAppList();
			ruleMap = loadRuleMap();
			reloaded = false;
		}
		
	}
	 /**
     * 定时加载配置信息
     * 配合reloadDataModel模块一起使用。
     * 主要实现原理如下：
     * 1，获取分钟的数据值，当分钟数据是10的倍数，就会触发reloadDataModel方法，简称reload时间。
     * 2，reloadDataModel方式是线程安全的，在当前worker中只有一个线程能够操作。
     * 3，为了保证当前线程操作完毕之后，其他线程不再重复操作，设置了一个标识符reloaded。
     *      在非reload时间段时，reloaded一直被置为true；
     *      在reload时间段时，第一个线程进入reloadDataModel后，加载完毕之后会将reloaded置为false。
     */
	public static void scheduleLoad(){
		//2017-4-9 09:54:00
		String date = DateUtils.getDateTime();
		int now = Integer.parseInt(date.split(":")[1]);
		if(now % 10 == 0){
			reloadDataModel();
		}else{
			reloaded = true;
		}
	}
	
	public static void save(Record record){
		new LogMonitorDao().saveRecord(record);
	}
	
	public static void main(String[] args) {
		String line = "1$$$$$error: Caused by: java.lang.NoClassDefFoundError: com/starit/gejie/dao/SysNameDao";
		Message message = parser(line);
//		System.out.println(message.toString());
//		boolean triggered = trigger(message);
//		System.out.println("triggered:"+triggered);
//		Map<String, List<User>> map = loadUserMap();
//		System.out.println("map"+map);
		notifly("1", message);
	}
	
	
	
}
