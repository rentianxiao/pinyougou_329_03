package cn.itcast.core.pojo.vo;

import cn.itcast.core.pojo.order.SellerOrder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class SellerOrderVo implements Serializable {

    /**
     * 实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分
     */
    private BigDecimal totalPrice;

  //,,订单id,实付金额,商品id,SPU名
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 商品id
     */
    private Long itemId;


    /**
     * SPU名
     */
    private String goodsName;



    @Override
    public String toString() {
        return "SellerOrderVo{" +
                "totalPrice=" + totalPrice +
                ", orderId=" + orderId +
                ", itemId=" + itemId +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public SellerOrderVo(BigDecimal totalPrice, Long orderId) {
        this.totalPrice = totalPrice;
        this.orderId = orderId;

    }

    public SellerOrderVo() {
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }


}
