package cn.zl.storm_logmonitor.logMonitor.spout;

import java.util.List;

import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * @author linge E-mail:
 * @version 
 * Created on 2017年4月8日 下午3:41:38
 */
//Scheme就实现了从byte[]到其他格式的转换（默认提供的是从字节流到字符串的转换）
//这里用来模拟接收kafka的输入流  因为kafka以流（byte）的形式传输数据
//KafkaSpout实现了Scheme
public class StringScheme implements Scheme{

	//反序列化方法，从流中读取数据
	@Override
	public List<Object> deserialize(byte[] bytes) {
		return new Values(new String(bytes));
	}

	@Override
	public Fields getOutputFields() {
		return new Fields("line");
	}

}
