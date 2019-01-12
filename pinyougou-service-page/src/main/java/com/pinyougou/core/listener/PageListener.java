package com.pinyougou.core.listener;

import com.pinyougou.core.service.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义消息监听器，生成商品详情静态页面
 */
public class PageListener implements MessageListener{

    @Resource
    private StaticPageService staticPageService;
    /**
     * 监听容器，获取MQ中的消息并处理业务
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        //获取消息
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
        try {
            String id = activeMQTextMessage.getText();
            System.out.println("service-page-id:"+id);
            //消费消息（处理业务）
            staticPageService.getStaticPage(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
