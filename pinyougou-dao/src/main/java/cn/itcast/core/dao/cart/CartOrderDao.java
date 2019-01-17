package cn.itcast.core.dao.cart;

import cn.itcast.core.pojo.cart.CartOrder;
import cn.itcast.core.pojo.cart.CartOrderQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartOrderDao {
    int countByExample(CartOrderQuery example);

    int deleteByExample(CartOrderQuery example);

    int deleteByPrimaryKey(Integer id);

    int insert(CartOrder record);

    int insertSelective(CartOrder record);

    List<CartOrder> selectByExample(CartOrderQuery example);

    CartOrder selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CartOrder record, @Param("example") CartOrderQuery example);

    int updateByExample(@Param("record") CartOrder record, @Param("example") CartOrderQuery example);

    int updateByPrimaryKeySelective(CartOrder record);

    int updateByPrimaryKey(CartOrder record);
}