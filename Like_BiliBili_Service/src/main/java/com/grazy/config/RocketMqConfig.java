package com.grazy.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.grazy.Common.UserMomentConstant;
import com.grazy.Service.UserFollowingService;
import com.grazy.domain.UserFollowing;
import com.grazy.domain.UserMoment;
import com.mysql.cj.util.StringUtils;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/4 15:49
 * @Description:  RocketMQ消息队列配置类
 */

@Configuration
public class RocketMqConfig {

    //rocketmq名称服务器地址
    @Value("${Rocketmq.name.server.address}")
    private String nameServerAddress;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;


    /**
     * 消息队列动态生产者实例
     */
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer mqProducer = new DefaultMQProducer(UserMomentConstant.GROUP_MOMENTS);   // 参数是分组名称
        mqProducer.setNamesrvAddr(nameServerAddress);
        mqProducer.start();
        return mqProducer;
    }


    /**
     *  消息队列动态消费者实例
     */
    @Bean("momentsCustomer")
    public DefaultMQPushConsumer momentsCustomer() throws MQClientException {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer(UserMomentConstant.GROUP_MOMENTS);
        mqPushConsumer.setNamesrvAddr(nameServerAddress);
        //订阅生产者
        mqPushConsumer.subscribe(UserMomentConstant.TOPIC_MOMENTS,"*");
        
        //设置监听生产者的动态消息
        mqPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            //监听方法
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    //因为当前测试一次只能发送一条动态，所以下面代码是按照一条消息处理

                MessageExt msg = list.get(0);
                if(msg == null){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

                //消息对象字节类型转换为字符串
                String magString = new String(msg.getBody());
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(magString), UserMoment.class);
                //获取关注当前用户的账号
                List<UserFollowing> fansFollowingList = userFollowingService.getFansInfo(userMoment.getUserId());
                //将动态信息按照粉丝的key存储到Redis中
                for(UserFollowing userFollowing: fansFollowingList){
                    String key = "subscribed-" + userFollowing.getUserId();
                    //因为一个账号会订阅很多信息未读，所以一个key中会有多个不同账号推送过来的动态，用list集合存储
                    String redisValue = redisTemplate.opsForValue().get(key);
                    //存储用户收到的用户动态推送消息
                    List<UserMoment> subscribedList;
                    if(StringUtils.isNullOrEmpty(redisValue)){
                        subscribedList = new ArrayList<>();
                    }else{
                        //将string类型集合的订阅列表转化为UserMoment对象类型集合
                        subscribedList = JSONArray.parseArray(redisValue, UserMoment.class);
                    }
                    //将新订阅消息添加到集合中
                    subscribedList.add(userMoment);
                    //集合转换为string类型并存储到Redis中
                    redisTemplate.opsForValue().set(key,JSONObject.toJSONString(subscribedList));
                }
                //返回处理成功结果
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        mqPushConsumer.start();
        return mqPushConsumer;
    }
}
