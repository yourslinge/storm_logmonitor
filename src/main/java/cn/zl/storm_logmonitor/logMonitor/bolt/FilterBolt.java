package cn.zl.storm_logmonitor.logMonitor.bolt;

import java.util.Map;

import cn.zl.storm_logmonitor.logMonitor.domain.Message;
import cn.zl.storm_logmonitor.logMonitor.utils.MonitorHandler;

import org.apache.log4j.Logger;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月8日 下午3:35:34
 */
public class FilterBolt extends BaseBasicBolt{

	private static Logger logger = Logger.getLogger(FilterBolt.class);
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		super.prepare(stormConf, context);
	}
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String line = input.getString(0);
		//解析数据
		Message message = MonitorHandler.parser(line);
		if(message==null){
			return;
		}
		//判断该信息的appId所携带的内容是否触发规则,如果触发规则，就将信息发送到下个bolt处理
		if(MonitorHandler.trigger(message)){
			collector.emit(new Values(message.getAppId(),message));
		}
		//定时更新规则信息
		MonitorHandler.scheduleLoad();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("appId","message"));
	}

}
