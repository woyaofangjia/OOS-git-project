package com.wsk.controller.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * WebSocket配置类
 * Created by wsk1103 on 2017/5/22.
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 优化WebSocket配置，使用标准路径格式
        registry.addHandler(chatWebSocketHandler(), "/webSocketIMServer")
                .setAllowedOrigins("*") // Spring 5.3兼容的CORS配置
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS(); // 启用SockJS支持
    }
    
    @Bean
    public ChatWebSocketHandler chatWebSocketHandler() {
        return new ChatWebSocketHandler();
    }
    
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 优化配置参数
        container.setMaxTextMessageBufferSize(65536); // 增加缓冲区大小
        container.setMaxBinaryMessageBufferSize(65536);
        container.setMaxSessionIdleTimeout(120000L); // 增加超时时间
        container.setAsyncSendTimeout(30000L); // 添加异步发送超时
        return container;
    }
}
