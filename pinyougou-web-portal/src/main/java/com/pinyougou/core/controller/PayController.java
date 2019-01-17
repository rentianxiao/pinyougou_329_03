package com.pinyougou.core.controller;

import cn.itcast.core.pojo.entity.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.PayService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    /**
     * 生成支付页面需要的二维码
     *
     * @return
     */
    @RequestMapping("/createNative.do")
    private Map<String, String> createNative() throws Exception {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return payService.createNative(userName);
    }

    /**
     * 查询订单
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus.do")
    public Result queryPayStatus(String out_trade_no) {
        //生成二维码后，需要查询订单是否支付成功
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        int time = 0;
        while (true) {
            try {
                //查询订单：获取响应的结果
                Map<String, String> map = payService.queryPayStatus(out_trade_no,userName);
                //判断是否交易成功
                if ("SUCCESS".equals(map.get("trade_state"))) {
                    return new Result(true, "支付成功");
                } else {
                    //其他状态
                    Thread.sleep(5000);
                    time++;
                }
                //默认code_url是两个小时。本项目：设置半个小时
                if (time > 360) {
                    return new Result(false, "二维码超时");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("orderClose.do")
    public Result orderClose(String out_trade_no) throws Exception {
        int time=0;
        while (true){
            Thread.sleep(5000);
            time++;
            if (time>12){
                Map<String, String> map = payService.orderClose(out_trade_no);
                if ("SUCCESS".equals(map.get("result_code"))){
                    return new Result(true,"订单支付关闭");
                }else {
                    return new Result(false,"订单支付关闭失败");
                }
            }
        }
    }
}
