package cn.itcast.core.pojo.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SellerOrder implements Serializable {
//商品名称
//商品价格
//商品数量
//订单实付金额
//订单来源
//创建时间
//状态
    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 商品购买数量
     */
    private Integer num;

    /**
     * 实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分
     */
    private BigDecimal payment;

    /**
     * 订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
     */
    private String sourceType;

    /**
     * 订单创建时间
     */
    private Date createTime;


    /**
     * 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
     */
    private String status;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public SellerOrder() {
    }

    public SellerOrder(String title, Long itemId, BigDecimal price, Integer num, BigDecimal payment, String sourceType, Date createTime, String status) {
        this.title = title;
        this.itemId = itemId;
        this.price = price;
        this.num = num;
        this.payment = payment;
        this.sourceType = sourceType;
        this.createTime = createTime;
        this.status = status;
    }

    @Override
    public String toString() {
        return "SellerOrder{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", num=" + num +
                ", payment=" + payment +
                ", sourceType='" + sourceType + '\'' +
                ", createTime=" + createTime +
                ", status='" + status + '\'' +
                '}';
    }




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }



    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
