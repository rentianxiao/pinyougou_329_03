package com.pinyougou.core.service;

import java.util.Map;

public interface PayService {

    /**
     * 生成支付页面需要的二维码
     * @return
     */
    Map<String,String> createNative(String userName) throws Exception;

    /**
     * 查询订单
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryPayStatus(String out_trade_no,String userName) throws Exception;
    /**
     * 关闭订单支付
     * @return
     */
    public Map<String,String> orderClose(String out_trade_no) throws Exception;
}
