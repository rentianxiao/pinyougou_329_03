package cn.itcast.core.pojo.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页结果的对象
 * @author Administrator
 *
实现序列号接口的场景?
    1.orm(对象映射关系)框架的缓存
    2.网络传输


为什么需要实现序列号接口?
    好处:
        1.容灾(灾备):可以进行反序列化,把磁盘中的数据重新加载到内存中
        2.数据共享:序列化后的数据是二进制,而服务器与服务器之间进行的是二进制传输
 */
public class PageResult implements Serializable {

    private List rows;//当前页结果集
    private Long total;//总记录数

    public PageResult() {
    }

    public PageResult(List rows, Long total) {
        this.rows = rows;
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
