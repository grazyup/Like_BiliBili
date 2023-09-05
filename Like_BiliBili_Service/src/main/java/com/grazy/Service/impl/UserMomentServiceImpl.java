package com.grazy.Service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.grazy.Common.UserMomentConstant;
import com.grazy.Service.UserMomentService;
import com.grazy.domain.UserMoment;
import com.grazy.mapper.UserMomentMapper;
import com.grazy.utils.RocketMqUtil;
import com.mysql.cj.util.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/5 15:20
 * @Description:
 */

@Service
public class UserMomentServiceImpl implements UserMomentService {

    @Autowired
    private UserMomentMapper userMomentMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public void addUserMoments(UserMoment userMoment) throws Exception{
        if(StringUtils.isNullOrEmpty(userMoment.getType())){
            userMoment.setType(UserMomentConstant.DEFAULT_MOMENTS_TYPE);
        }
        userMoment.setCreateTime(new Date());
        userMomentMapper.insertMoment(userMoment);
        //消息队列发布动态消息
        Message message = new Message(UserMomentConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        RocketMqUtil.syncSendMsg((DefaultMQProducer)applicationContext.getBean("momentsProducer"), message);
    }


    @Override
    public List<UserMoment> getUserSubscribeMoment(Long currentUserId) {
        //拼接Redis中的key
        String key = "subscribed-" + currentUserId;
        return JSONArray.parseArray(redisTemplate.opsForValue().get(key),UserMoment.class);
    }
}
