package cn.zl.storm_logmonitor.logMonitor.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年5月31日 下午8:20:15
 * 短信模块的开发是利用"中正云通信",采用http协议
 * 网站：http://www.winic.org/api/SendMessage.asp网站的内容
 */
public class SMSBase {

	private static final String USER_ID = "";
	private static final String PASSWORD = "";
	public static boolean sendSms(String mobile,String content){
		HttpURLConnection httpconn = null;
		String result = "";
        try {
        	StringBuilder sb = new StringBuilder();
        	sb.append("http://service.winic.org:8009/sys_port/gateway/index.asp?");
        	//以下是参数
        	//id:中正云通信账号，中文帐号要求转码为GB2312编码
        	sb.append("id=").append(URLEncoder.encode(USER_ID, "gb2312"));
        	//pwd:中正云通信密码，无须加密
        	sb.append("&pwd=").append(PASSWORD);
        	//to:接收短信号码,多个号码使用英文逗号（,）分隔,比如：13800000000,13800000001
        	sb.append("&to=").append(mobile);
        	//content:短信发送内容,中文内容要求转码为GB2312编码
			sb.append("&content=").append(URLEncoder.encode(content, "gb2312"));
			//time:短信发送时间,可不填，为空即时发送，格式：yyyy/mm/dd hh:mm:ss，比如：2016/06/20 11:50:41
			sb.append("&time=").append("");
			URL url = new URL(sb.toString());
			httpconn = (HttpURLConnection)url.openConnection();
			//读取请求url返回的消息,即http接口返回消息
			//示例：000/Send:1/Consumption:.1/Tmoney:751.05/sid:0620135629504294
			//解释：状态码/扣费条数/扣费金额/余额/编号       000表示发送成功
			BufferedReader rd = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
			result = rd.readLine();
			System.out.println("==============="+result);
			rd.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(httpconn!=null){
				httpconn.disconnect();
			}
			httpconn = null;
		}
        if(StringUtils.isNotBlank(result)){
        	if(result.substring(0, 3).equals("000")){
        		return true;
        	}
        }
        return false;
        
	}
}
