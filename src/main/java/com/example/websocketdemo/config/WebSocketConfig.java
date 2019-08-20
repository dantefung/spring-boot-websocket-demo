package com.example.websocketdemo.config;

import com.example.websocketdemo.entity.User;
import com.example.websocketdemo.interceptor.SessionAuthHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;

import java.util.Map;

/**
 *  官方文档-spring websocket/stomp 参见:https://docs.spring.io/spring/docs/current/spring-framework-reference/#websocket-stomp
 *
 *
 * websocket定义了两种传输信息类型:文本信息和二进制信息。类型虽然被确定，但是他们的传输体是没有规定的。
 * 所以，需要用一种简单的文本传输类型来规定传输内容，它可以作为通讯中的文本传输协议。
 *
 * STOMP是基于帧的协议，客户端和服务器使用STOMP帧流通讯.
 *
 * Created by rajeevkumarsingh on 24/07/17.
 * Modified by DanteFung on 2019年08月20日 23:26:51
 */
@Configuration
//注解开启使用STOMP协议来传输基于代理(message broker)的消息,这时控制器支持使用@MessageMapping,就像使用@RequestMapping一样
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {//AbstractWebSocketMessageBrokerConfigurer：继承WebSocket消息代理的类，配置相关信息。

    //注册STOMP协议的节点(endpoint),并映射指定的url
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //注册一个STOMP的endpoint,并指定使用SockJS协议
        //registry.addEndpoint("/ws").withSockJS();
        /**
         * 测试 one2one.html one2many.html simplechat.html index.html 请打开
         * registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();的注释
         */
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
        //registry.addEndpoint("/endpointOyzc").setAllowedOrigins("*").withSockJS();
        //添加一个访问端点“/endpointGym”,客户端打开双通道时需要的url,允许所有的域名跨域访问，指定使用SockJS协议。

        // 带认证授权方式注册一个STOMP的endpoint,并指定使用SockJS协议
        /*registry.addEndpoint("/ws")
                .addInterceptors(new SessionAuthHandshakeInterceptor())
                //握手时获得用户
                .setHandshakeHandler(new  DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        User user = (User)(((ServletServerHttpRequest) request).getServletRequest().getSession().getAttribute("user"));
                        return new MyPrincipal(user.getUserName());
                    }

                })
                .setAllowedOrigins("*")
                .withSockJS();*/


    }


    //配置消息代理(Message Broker)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        /**
         *  这是配置到 @MessageMapping Controller
         *  当客户端发送消息或订阅消息时，url路径开头如果是/app/xxx 时，会先解析stomp协议，然后路由到@controller的@MessageMapping("/xxx")的方法上执行。
         *  如果不设置，客户端所有发送消息或订阅消息时、都将去匹配@messageMapping。所以最好还是配置上。
         */
        registry.setApplicationDestinationPrefixes("/app");
        //点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        //点对点发送前缀
        registry.setUserDestinationPrefix("/user");
        /**
         * 配置一个/topic广播消息代理和“/user”一对一消息代理
         * 点对点应配置一个/user消息代理，广播式应配置一个/topic消息代理
         * 广播消息:声明消息中间件Broker的主题名称，当向这个主题下发送消息时（js: stompclient.send("/topic/target1",{},"hello" )  ），订阅当前主题的客户端都可以收到消息。
         * 注意：js 客户端如果发送时、直接是/topic/xxx,spring收到消息会直接发送给broker中。
         * 点对点发送时：enableSimpleBroker 中要配置 /user才可能用： template.convertAndSendToUser("zhangsan","/aaa/hello","111")，否则收不到消息。
         */
        registry.enableSimpleBroker("/topic","/user");   // Enables a simple in-memory broker


        //   Use this for enabling a Full featured broker like RabbitMQ

        /*
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        */
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                System.out.println("recv : "+message);
                /*StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                User user = (User)accessor.getSessionAttributes().get("user");*/
                return super.preSend(message, channel);
            }

        });
    }


    class MyPrincipal implements Principal{

        private String key;

        public MyPrincipal(String key) {
            this.key = key;
        }

        @Override
        public String getName() {
            return key;
        }
    }

}
