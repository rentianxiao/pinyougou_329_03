package cn.itcast.core.pojo.order;

import java.io.Serializable;

public class OrderType implements Serializable {

    /**
     * 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
     */
    private String status;

    /**
     * 状态：1.日订单  2.周订单   3.月订单
     */
    private String dateLimit;

    @Override
    public String toString() {
        return "OrderType{" +
                "status='" + status + '\'' +
                ", dateLimit='" + dateLimit + '\'' +
                '}';
    }

    public OrderType() {
    }

    public OrderType(String status, String dateLimit) {
        this.status = status;
        this.dateLimit = dateLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(String dateLimit) {
        this.dateLimit = dateLimit;
    }
}
