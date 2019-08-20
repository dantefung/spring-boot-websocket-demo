package com.example.websocketdemo.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 官方文档-spring session 参见:https://docs.spring.io/spring-session/docs/1.3.0.RELEASE/reference/html5/#websocket
 * 集群环境下，如何实现session共享?
 * 简单点说就是请求http请求经过Filter职责链，根据配置信息过滤器将创建session的权利由tomcat交给了Spring-session中的SessionRepository，
 * spring session也是可以用来实现单点登录。
 *
 * 通过Spring-session创建会话，并保存到对应的地方(例如redis)。
 *
 * 如果是前后分离，使用cas作为认证授权服务器的话，这里的权限认证就不是用spring session共享session来实现。
 */
public class SessionAuthHandshakeInterceptor implements HandshakeInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 单机和集群(需要引入spring session)的认证方法
        HttpSession session = getSession(request);
        if(session==null || session.getAttribute("user")==null){
            logger.error("websocket权限拒绝");
            return false;
        }
        attributes.put("user",session.getAttribute("user"));
        // TODO: cas系统的票据认证
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    // 参考 HttpSessionHandshakeInterceptor
    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}