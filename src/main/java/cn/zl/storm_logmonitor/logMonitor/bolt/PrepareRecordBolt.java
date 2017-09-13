package cn.zl.storm_logmonitor.logMonitor.bolt;


import cn.zl.storm_logmonitor.logMonitor.domain.Message;
import cn.zl.storm_logmonitor.logMonitor.domain.Record;
import cn.zl.storm_logmonitor.logMonitor.utils.MonitorHandler;

import org.apache.commons.beanutils.BeanUtils;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月8日 下午3:36:42
 */
public class PrepareRecordBolt extends BaseBasicBolt{

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		Message message = (Message) input.getValueByField("message");
		String appId = input.getStringByField("appId");
		//将触发规则的信息进行通知
		MonitorHandler.notifly(appId, message);
		Record record = new Record();
		try {
			BeanUtils.copyProperties(record, message);
			collector.emit(new Values(record));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("record"));
	}

}
