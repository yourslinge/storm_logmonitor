package cn.zl.storm_logmonitor.logMonitor.dao;

import java.util.Date;
import java.util.List;

import cn.zl.storm_logmonitor.logMonitor.domain.App;
import cn.zl.storm_logmonitor.logMonitor.domain.Record;
import cn.zl.storm_logmonitor.logMonitor.domain.Rule;
import cn.zl.storm_logmonitor.logMonitor.domain.User;
import cn.zl.storm_logmonitor.logMonitor.utils.DataSourceUtils;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月8日 下午7:04:02
 */
public class LogMonitorDao {

	private static Logger logger = Logger.getLogger(LogMonitorDao.class);
	private JdbcTemplate jdbcTemplate;
	public LogMonitorDao(){
		jdbcTemplate = new JdbcTemplate(DataSourceUtils.getDataSource());
	}
	
	public List<App> getAppList(){
		//查询所有上线的应用信息
		String sql = "select id,name,isOnline,typeId,userId from log_monitor_app where isOnline=1";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(App.class));
	}
	public List<Rule> getRuleList(){
		String sql = "select id,name,keyword,isValid,appId from log_monitor_rule where isValid=1";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Rule.class));
	}
	public List<User> getUserList(){
		String sql = "select id,name,mobile,email,isValid from log_monitor_user where isValid=1";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class));
	}
	public void saveRecord(Record record){
		String sql = "insert into log_monitor.log_monitor_rule_record(appId,ruleId,isEmail,isPhone,isClose,noticeInfo,updateDate) values(?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, record.getAppId(),record.getRuleId(),record.getIsEmail(),record.getIsPhone(),0,record.getLine(),new Date());
		
	}
	
	public static void main(String[] args) {
		LogMonitorDao logMonitor = new LogMonitorDao();
		List<User> userList = logMonitor.getUserList();
		for (User user : userList) {
			System.out.println(user.toString());
		}
//		List<Rule> ruleList = logMonitor.getRuleList();
//		for (Rule rule : ruleList) {
//			System.out.println(rule.toString());
//		}
//		List<App> appList = logMonitor.getAppList();
//		for (App app : appList) {
//			System.out.println(app.toString());
//		}
		
	}
	
	
}
