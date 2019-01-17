package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.core.service.PayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import util.HttpClient;
import util.IdWorker;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Value("${appid}")
    private String  appid;      //微信公众账号或开放平台APP的唯一标识

    @Value("${partner}")
    private String  partner;    //财付通平台的商户账号

    @Value("${partnerkey}")
    private String  partnerkey; //财付通平台的商户密钥

    @Value("${notifyurl}")
    private String notifyurl;   //回调地址（判断本次支付成功是否成功）

    @Resource
    private IdWorker idWorker;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private PayLogDao payLogDao;

    @Resource
    private OrderDao orderDao;

    /**
     * 生成支付页面需要的二维码
     * @return
     */
    @Override
    public Map<String, String> createNative(String userName) throws Exception {
        // 从缓存中取出交易日志
        PayLog paylog = (PayLog) redisTemplate.boundHashOps("paylog").get(userName);
        // 生成支付页面需要的数据
        HashMap<String, String> data = new HashMap<>();
        // 支付订单号（交易流水号)
        //long out_trade_no = idWorker.nextId();

        // 1、调用微信统一下单的接口地址
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        // 2、统一下单接口需要提交的参数
        // 公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        data.put("appid",appid);
        // 商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        data.put("mch_id",partner);
        // 随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        // 商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值
        data.put("body","品优购订单支付");
        //商品简单描述，该字段请按照规范传递，具体请见参数规定
        // 商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
        data.put("out_trade_no",String.valueOf(paylog.getOutTradeNo()));
        // 标价金额	total_fee	是	Int	88	订单总金额，单位为分，详见支付金额
        data.put("total_fee","1");      // 支付金额，测试阶段使用
        //data.put("total_fee",String.valueOf(paylog.getTotalFee())); //实际工作中是从redis中取
        // 终端IP	spbill_create_ip	是	String(16)	123.12.12.123	支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
        data.put("spbill_create_ip","192.168.200.128");
        // 通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        data.put("notify_url",notifyurl);
        // 交易类型	trade_type	是	String(16)	JSAPI
        data.put("trade_type","NATIVE");
        // 微信系统规定：提交的数据是xml格式，将map集合转成xml(并且会生成签名)
        WXPayUtil.generateSignedXml(data,partnerkey);

        //3、通过HttpClient模拟浏览器发送请求
        HttpClient httpClient = new HttpClient(url);    //请求的url
        httpClient.setHttps(true);          // 支持https
        String xmlParam = WXPayUtil.generateSignedXml(data, partnerkey);
        httpClient.setXmlParam(xmlParam);   // 微信下单接口需要的数据（请求的数据）
        httpClient.post();                  // 发送请求

        //4、请求完成后，获取响应的结果
        String strXml = httpClient.getContent();   //数据格式：xml

        //5、将响应的数据转成map
        Map<String, String> map = WXPayUtil.xmlToMap(strXml);
        map.put("total_fee",String.valueOf(paylog.getTotalFee()));    //展示的金额
        map.put("out_trade_no",String.valueOf(paylog.getOutTradeNo()));//交易的订单号
        return map;
    }

    /**
     * 查询订单
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no,String userName) throws Exception {
        //1、查询订单的接口地址
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        //2、封装接口需要的数据
        Map<String, String> data = new HashMap<>();
        //公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        data.put("appid",appid);
        //商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        data.put("mch_id",partner);
        //商户订单号	out_trade_no	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 详见商户订单号
        data.put("out_trade_no",out_trade_no);
        //随机字符串	nonce_str	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	随机字符串，不长于32位。推荐随机数生成算法
        data.put("nonce_str",WXPayUtil.generateNonceStr());
        //签名	sign	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	通过签名算法计算得出的签名值，详见签名生成算法
        String xmlParam = WXPayUtil.generateSignedXml(data, partnerkey);
        //3、通过httpClient发送请求
        HttpClient client = new HttpClient(url);
        client.setHttps(true);
        client.setXmlParam(xmlParam);
        client.post();

        //4、响应结果
        String strXml = client.getContent();
        //将响应的数据转成map
        Map<String, String> map = WXPayUtil.xmlToMap(strXml);
        map.put("total_fee","100000000");    //展示的金额
        map.put("out_trade_no",String.valueOf(out_trade_no));//交易的订单号

        // 5、支付成功后，需要更新支付日志表
        if ("SUCCESS".equals(map.get("trade_state"))) {
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(out_trade_no);     // 根据主键更新
            payLog.setPayTime(new Date());          // 支付完成时间
            payLog.setTransactionId(map.get("transaction_id"));    // 第三方提供的交易流水
            payLog.setTradeState("1");              // 支付状态：支付成功
            payLogDao.updateByPrimaryKeySelective(payLog);

            // TODO 订单表更新（根据order_list）
            //String orderList = payLog.getOrderList();
            PayLog payLog1 = (PayLog) redisTemplate.boundHashOps("paylog").get(userName);
            String orderList = payLog1.getOrderList();
            String[] order_IdList = orderList.split(", ");
            for (String order_Id : order_IdList) {
                Order order = new Order();
                order.setOrderId(Long.parseLong(order_Id));
                order.setStatus("2");
                order.setUpdateTime(new Date());
                order.setPaymentTime(new Date());
                order.setEndTime(new Date());
                orderDao.updateByPrimaryKeySelective(order);
            }


            // 删除缓存中的日志
            redisTemplate.boundHashOps("paylog").delete(userName);
        }
        System.out.println("调用微信查询订单api");
        System.out.println(strXml);
        return map;
    }

    /**
     * 关闭订单支付页面
     * @return
     */
    @Override
    public Map<String, String> orderClose(String out_trade_no) throws Exception {
        Map<String, String> data=new HashMap<>();
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        //应用ID	appid	是	String(32)	wx8888888888888888	微信开放平台审核通过的应用APPID
        data.put("appid",appid);
        //商户号	mch_id	是	String(32)	1900000109	微信支付分配的商户号
        data.put("mch_id",partner);
        //商户订单号	out_trade_no	是	String(32)	1217752501201407033233368018	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
        data.put("out_trade_no",out_trade_no);
        //随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	商户系统内部的订单号,32个字符内、可包含字母, 其他说明见安全规范
        data.put("nonce_str",WXPayUtil.generateNonceStr());
        // 签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
        String xmlParam=WXPayUtil.generateSignedXml(data,partnerkey);
        HttpClient httpClient = new HttpClient(url);
        httpClient.setXmlParam(xmlParam);
        httpClient.setHttps(true);
        httpClient.post();
        String strXML = httpClient.getContent();
        Map<String, String> map = WXPayUtil.xmlToMap(strXML);
        return map;
    }
}
