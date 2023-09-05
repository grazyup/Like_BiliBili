package com.grazy.config;

import com.grazy.Common.UserMomentConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/4 15:49
 * @Description:  RocketMQ消息队列配置类
 */

@Configuration
public class RocketMqConfig {

    //rocketmq名称服务器地址
    @Value("${Rocketmq.name.server.address")
    private String nameServerAddress;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 消息队列生产者实例
     */
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer mqProducer = new DefaultMQProducer(UserMomentConstant.GROUP_MOMENTS);   // 参数是分组名称
        mqProducer.setNamesrvAddr(nameServerAddress);
        mqProducer.start();
        return mqProducer;
    }


    /**
     *  消息队列消费者实例
     */
    @Bean("momentsCustomer")
    public DefaultMQPushConsumer momentsCustomer() throws MQClientException {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer(UserMomentConstant.GROUP_MOMENTS);
        mqPushConsumer.setNamesrvAddr(nameServerAddress);
        //订阅生产者
        mqPushConsumer.subscribe(UserMomentConstant.TOPIC_MOMENTS,"*");
        //设置监听生产者的动态消息
        mqPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                //简要处理
                for (MessageExt messageExt : list) {
                    System.out.println(messageExt);
                }
                //返回处理成功结果
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        mqPushConsumer.start();
        return mqPushConsumer;
    }
}
