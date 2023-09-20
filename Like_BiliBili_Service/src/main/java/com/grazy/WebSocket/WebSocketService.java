package com.grazy.WebSocket;

import com.alibaba.fastjson.JSONObject;
import com.grazy.Service.DanMuService;
import com.grazy.domain.DanMu;
import com.grazy.utils.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: grazy
 * @Date: 2023/9/19 19:21
 * @Description: WebSocket服务
 */

@Component
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    private static final ConcurrentHashMap<String,WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    private Session session;

    private String sessionId;

    private Long userId;

    /**
     * 多例模式的bean注入
     */
    private static ApplicationContext APPLICATION_CONTEXT;

    public static void setApplicationContext(ApplicationContext applicationContext){
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }


    /**
     * 打开连接
     */
    @OnOpen
    public void openConnection(Session session, @PathParam("token")String token){
        try{
            //未登录也可以获取连接，查看弹幕和在线人数
            this.userId = TokenUtil.verifyToken(token);
        }catch (Exception e){

        }
        this.sessionId = session.getId();
        this.session = session;
        if(WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId,this);
        }else{
            WEBSOCKET_MAP.put(sessionId,this);
            //更新在线人数
            ONLINE_COUNT.incrementAndGet();
        }
        //打印日志
        logger.info("用户连接成功！" + session + " 当前在线人数： " + ONLINE_COUNT.get());
        try {
            //发送消息通知客户端
            this.sentMessage("0");
        }catch (Exception e){
            logger.error("连接异常");
        }
    }


    /**
     * 关闭连接
     */
    @OnClose
    public void closeConnection(){
        if(WEBSOCKET_MAP.containsKey(this.sessionId)){
            WEBSOCKET_MAP.remove(this.sessionId);
            ONLINE_COUNT.getAndIncrement();
        }
        logger.info("用户退出：" + sessionId + "当前在线人数为：" + ONLINE_COUNT.get());
    }


    /**
     * 接收弹幕
     * @param message 弹幕信息
     */
    @OnMessage
    public void onMessage(String message){
        logger.info("用户信息：" + sessionId + "，报文：" + message);
        if(!StringUtils.isNullOrEmpty(message)){
            try {
                //群发弹幕消息给当前在线观看用户
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                    WebSocketService webSocketService = entry.getValue();
                    if (webSocketService.session.isOpen()) {
                        //对开启连接的用户推送消息
                        webSocketService.sentMessage(message);
                    }
                }
                if(userId != null){
                    //保存弹幕到数据库
                    DanMu danMu = JSONObject.parseObject(message, DanMu.class);
                    danMu.setCreateTime(new Date());
                    danMu.setUserId(userId);
                    //获取弹幕业务层
                    DanMuService danMuService = (DanMuService) APPLICATION_CONTEXT.getBean("DanMuService");
                    danMuService.addDanMu(danMu);
                    //保存到Redis中
                    danMuService.addDanMuToRedis(danMu);
                }
            }catch (Exception e){
                logger.error("弹幕接收抛出异常！");
                e.printStackTrace();
            }
        }
    }


    private void sentMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


}
