package com.ruoyi.framework.config;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Configuration	//标识我是一个配置类
@PropertySource("classpath:/redis.properties")
public class RedisConfig {
	
	@Value("${redis.nodes}") 
	private String nodes;   //node,node,....
	
	/**
	 * 搭建redis集群
	 */
	@Bean
	public JedisCluster jedisCluster() {
		Set<HostAndPort> nodes = getNodes();
		return new JedisCluster(nodes);
	}

	//表示不要有重复数据
	private Set<HostAndPort> getNodes() {
		Set<HostAndPort> nodesSets = new HashSet<>();
		String[] strNode = nodes.split(",");
		for (String redisNode : strNode) {
			
			String host = redisNode.split(":")[0];
			int port = Integer.parseInt
			(redisNode.split(":")[1]);
			HostAndPort hostAndPort = 
					new HostAndPort(host, port);
			nodesSets.add(hostAndPort);
		}
		return nodesSets;
	}
}
