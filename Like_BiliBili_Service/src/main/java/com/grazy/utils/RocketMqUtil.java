package com.grazy.utils;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * @Author: grazy
 * @Date: 2023/9/5 14:53
 * @Description:
 */

public class RocketMqUtil {

    /**
     *  同步发送消息
     */
    public static void syncSendMsg(DefaultMQProducer producer, Message message) throws Exception{
        SendResult sendResult = producer.send(message);
        System.out.println(sendResult);
    }


    /**
     *  异步发送消息
     */
    public static void asyncSendMsg(DefaultMQProducer producer,Message message) throws Exception{
        int messageCount = 2;
        //计时器
        CountDownLatch2 downLatch2 = new CountDownLatch2(messageCount);
        for(int i = 0; i < messageCount; i++){
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    //计时器减一
                    downLatch2.countDown();
                    System.out.println("异步发送消息成功,消息Id为： " + sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable throwable) {
                    downLatch2.countDown();
                    System.out.println("发送消息发生异常 : " + throwable);
                    throwable.printStackTrace();
                }
            });
        }
        //计时器提留五秒钟
        downLatch2.await(5, TimeUnit.SECONDS);
    }
}
