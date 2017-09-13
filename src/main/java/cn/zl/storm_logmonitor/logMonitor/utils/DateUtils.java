package cn.zl.storm_logmonitor.logMonitor.utils;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;




import org.apache.commons.beanutils.BeanUtils;

import cn.zl.storm_logmonitor.logMonitor.domain.Message;
import cn.zl.storm_logmonitor.logMonitor.domain.Record;


/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月9日 上午9:31:05
 */
public class DateUtils {

	public static String getDateTime(String formatter){
		SimpleDateFormat sdf = new SimpleDateFormat(formatter);
		return sdf.format(new Date());
	}
	
	public static String getDateTime(){
		return DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(getDateTime());
		
		Message message = new Message();
		message.setAppId("1");
		message.setLine("hello world");
		message.setRuleId("2");
		
		Record record = new Record();
		//copy属性成功，两个javabean的全部或者部分字段名必须相同
		BeanUtils.copyProperties(record, message);
		System.out.println(record.toString());
	}
	
}
