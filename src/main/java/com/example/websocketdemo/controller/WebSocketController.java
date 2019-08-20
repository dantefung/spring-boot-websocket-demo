package com.example.websocketdemo.controller;

import com.example.websocketdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

/**
 *
 */
@Controller
public class WebSocketController {

    /**
     * SimpMessagingTemplate：SpringBoot提供操作WebSocket的对象
     */
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 后台推送流程：template.convertAndSend（"xxx"）将消息转换成stomp格式、然后发送到broker中，broker利用websocket链接发送消息给所有的订阅者。
     *
     * @Scheduled(fixedRate = 10000)：为了测试，定时10S执行这个方法，向客户端推送
     */
    //广播推送消息
    @Scheduled(fixedRate = 10000)
    public void sendTopicMessage() {
        System.out.println("后台广播推送！");
        User user = new User();
        user.setUserName("oyzc");
        user.setAge(10);
        this.template.convertAndSend("/topic/getResponse", user);
        /**
         * template.convertAndSend("/topic/getResponse",new AricResponse("后台实时推送：,Oyzc!")); :直接向前端推送消息。
         * 参数一:客户端监听指定通道时，设定的访问服务器的URL
         * 参数二:发送的消息（可以是对象、字符串等等）
         */
    }

    //一对一推送消息
    @Scheduled(fixedRate = 10000)
    public void sendQueueMessage() {
        System.out.println("后台一对一推送！");
        User user = new User();
        user.setUserId(1);
        user.setUserName("oyzc");
        user.setAge(10);
        this.template.convertAndSendToUser(user.getUserId() + "", "/queue/getResponse", user);
        /**
         * template.convertAndSendToUser(user.getUserId()+"","/queue/getResponse",user); :直接向前端推送消息。
         * 参数一：指定客户端接收的用户标识（一般用用户ID）
         * 参数二：客户端监听指定通道时，设定的访问服务器的URL（客户端访问URL跟广播有些许不同)
         * 参数三：向目标发送消息体（实体、字符串等等）
         */
    }

    //这个注解其实就是用来定义接受客户端发送消息的url(不能是topic开头，如果是topic直接发送给broker了，要用/app/hello)
    //如果有返回只则会将返回的内容转换成stomp协议格式发送给broker(主题名：/topic/hello)。如果要换主题名可使用@sentTo
    //@SubscribeMapping注解和@messageMapping差不多，但不会再把内容发给broker，而是直接将内容响应给客户端，
    @MessageMapping("/hello")
    public void greeting(String content) throws Exception {
        System.out.println("收到内容: " + content);
    }

    @MessageMapping("/hi")
    @SendTo("/topic/hi")
    public String sendMessage(String message) {
        System.out.println("收到来自客户端的消息:" + message);
        return String.format("你好浏览器，我收到了你的消息:[%s]", message);
    }


}
