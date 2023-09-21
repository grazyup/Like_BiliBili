package com.grazy;

import com.grazy.WebSocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class LikeBiliBiliApiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(LikeBiliBiliApiApplication.class, args);
        //用于多例模式
        WebSocketService.setApplicationContext(application);
    }

}
