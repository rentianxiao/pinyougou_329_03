package com.pinyougou.core.listener;


import com.pinyougou.core.service.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义消息监听器，将商品信息添加到索引库
 */
public class ItemSearchListener implements MessageListener{

    @Resource
    private ItemSearchService itemSearchService;

    /**
     * 监听容器，获取MQ中的消息并处理业务
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        // 监听容器获取消息
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
        try {
            String id = activeMQTextMessage.getText();
            System.out.println("service-search-id:"+id);
            //处理消息（处理业务）
            itemSearchService.updateSolr(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
