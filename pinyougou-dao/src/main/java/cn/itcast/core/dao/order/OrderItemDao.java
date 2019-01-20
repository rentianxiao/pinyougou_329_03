package cn.itcast.core.dao.order;

import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderItemDao {
    int countByExample(OrderItemQuery example);

    int deleteByExample(OrderItemQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    List<OrderItem> selectByExample(OrderItemQuery example);

    OrderItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrderItem record, @Param("example") OrderItemQuery example);

    int updateByExample(@Param("record") OrderItem record, @Param("example") OrderItemQuery example);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<Map<String,String>>FindBiao();

    List<String> FindName();

    List<Map<String,String>>FindShop (@Param("Name") String name);
}