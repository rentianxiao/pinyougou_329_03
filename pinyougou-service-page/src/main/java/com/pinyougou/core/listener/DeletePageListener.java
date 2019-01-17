package com.pinyougou.core.listener;

import com.pinyougou.core.service.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class DeletePageListener implements MessageListener {
    @Resource
    private StaticPageService staticPageService;
    @Override
    public void onMessage(Message message) {
        //获取消息
        ActiveMQTextMessage activeMQTextMessage= (ActiveMQTextMessage) message;
        try {
            String id = activeMQTextMessage.getText();
            System.out.println("service-page-id"+id);
            //消费消息
            boolean b = staticPageService.deleteFile(id);
            System.out.println(b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
