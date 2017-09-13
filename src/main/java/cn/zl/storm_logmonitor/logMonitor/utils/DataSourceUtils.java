package cn.zl.storm_logmonitor.logMonitor.utils;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;


import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月8日 下午7:21:37
 */
public class DataSourceUtils {

	//private static Logger logger = Logger.getLogger(DataSourceUtils.class);
	private static DataSource dataSource;
	static{
		dataSource = new ComboPooledDataSource("logMonitor");
	}
	public static synchronized DataSource getDataSource(){
		if(dataSource == null){
			dataSource = new ComboPooledDataSource();
		}
		return dataSource;
	}
	public static void main(String[] args) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "insert into log_monitor_user(id,name,mobile,email,isValid) values(201520602022,'hh','152','11@qq.com',1)";
		jdbcTemplate.execute(sql);
	}
}
