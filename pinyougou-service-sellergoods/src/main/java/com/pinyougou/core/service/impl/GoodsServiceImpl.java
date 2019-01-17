package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.GoodsService;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private BrandDao brandDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination topicPageAndSolrDestination;

    @Resource
    private Destination queueSolrDeleteDestination;

    @Resource
    private Destination SolrDeleteDestination;
    /**
     * 录入商品
     *
     * @param goodsVo
     */
    @Transactional
    @Override
    public void add(GoodsVo goodsVo) {
        //1.保存商品的基本信息
        Goods goods = goodsVo.getGoods();
        // 设置审核状态：audit_status=0,返回自增主键id
        goods.setAuditStatus("0");
        //设置返回自增主键Id
        goodsDao.insertSelective(goods);
        //2.保存商品的描述信息
        // 设置外键
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);
        //3.保存商品的库存信息
        //判断是否启用规格
        if ("1".equals(goods.getIsEnableSpec())) {
            //启用：一对多
            List<Item> itemList = goodsVo.getItemList();
            if (itemList != null && itemList.size() > 0) {
                for (Item item : itemList) {
                    //设置商品标题：SPU名称+副标题+规格名称
                    String title = goods.getGoodsName() + " " + goods.getCaption();
                    //获取规格名称
                    //{"机身内存":"16G","网络":"联通3G"}
                    String specName = item.getSpec();
                    //将json字符串转换为Map
                    Map<String, String> map = JSON.parseObject(specName, Map.class);
                    Set<Map.Entry<String, String>> entries = map.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        title += " " + entry.getValue();
                    }
                    item.setTitle(title);
                    setAttributeForItem(goods, goodsDesc, item);
                    //插入
                    itemDao.insertSelective(item);
                }
            }

        } else {
            //未启用：一对一
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption());
            item.setIsDefault("1");
            item.setPrice(goods.getPrice());
            item.setSpec("{}");
            setAttributeForItem(goods, goodsDesc, item);
            itemDao.insertSelective(item);
        }
    }

    /**
     * 查询当前商家下的商品列表
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page, rows);
        //设置查询条件
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.setOrderByClause("id desc");
        if (goods.getSellerId() != null && !"".equals(goods.getSellerId().trim())) {
            goodsQuery.createCriteria().andSellerIdEqualTo(goods.getSellerId().trim());
        }
        //根据条件查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getResult(), p.getTotal());
    }

    /**
     * 商品回显
     * @param id
     * @return
     */
    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo goodsVo = new GoodsVo();
        //商品基本信息
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsVo.setGoods(goods);
        //商品描述信息
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsVo.setGoodsDesc(goodsDesc);
        //库存信息
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        goodsVo.setItemList(itemList);
        return goodsVo;
    }

    /**
     * 商品更新
     * @param goodsVo
     */
    @Transactional
    @Override
    public void update(GoodsVo goodsVo) {
        //更新商品基本信息
        Goods goods = goodsVo.getGoods();
        //修改后的商品需要重新审核
        goods.setAuditStatus("0");
        goodsDao.updateByPrimaryKeySelective(goods);
        //更新商品描述信息
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKey(goodsDesc);
        //更新商品对应的库存信息
            //先删除
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(itemQuery);
            //再插入
        // 判断是否启用规格
        if ("1".equals(goods.getIsEnableSpec())) {
            //一个商品对应多个库存
            List<Item> itemList = goodsVo.getItemList();
            for (Item item : itemList) {
                String title = goods.getGoodsName() + " " + goods.getCaption();
                Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
                Set<Map.Entry<String, String>> entrySet = map.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    title += " " + entry.getValue();
                }
                item.setTitle(title);   //商品标题
                setAttributeForItem(goods,goodsDesc,item);
                itemDao.insertSelective(item);
            }
        }else {
            //一个商品对应一个库存
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption());// 商品标题
            item.setIsDefault("1"); // 是否默认
            item.setPrice(goods.getPrice());// 商品价格
            item.setSpec("{}");
            setAttributeForItem(goods, goodsDesc, item);
            itemDao.insertSelective(item);
        }
    }

    /**
     * 运营商查询查询待审核且未删除的商品列表信息
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult searchByManager(Integer page, Integer rows, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        goodsQuery.setOrderByClause("id desc");     // 根据id降序
        if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus().trim())){
            //待审核
            criteria.andAuditStatusEqualTo(goods.getAuditStatus().trim());
        }
        criteria.andIsDeleteIsNull();   // 查询未删除的商品
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getResult(),p.getTotal());
    }

    /**
     * 更新商品的审核状态
     * @param ids
     * @param Status
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String Status) {
        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            goods.setAuditStatus(Status);
            for (final Long id : ids) {
                goods.setId(id);
                //1、更新商品的审核状态
                goodsDao.updateByPrimaryKeySelective(goods);
                /*if("1".equals(Status)){    //审核成功
                    //发送消息到MQ中
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            //将商品id封装成消息体
                            TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                            return textMessage;
                        }
                    });
                }*/
            }
        }
    }





    //将数据库中库存的所有数据导入到索引库
    private void dataImportToSolr() {
        //查询所有库存的数据
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andStatusEqualTo("1");
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        //数据库中拿到的是json字符串：{"机身内存":"16G","网络":"联通3G"}
        //我们希望最终拼接的动态字段： {"item_spec_机身内存":"16G","item_spec_网络":"联通3G"}
        //所以需要我们处理规格
        if (itemList != null && itemList.size() > 0){
            for (Item item : itemList) {
                String spec = item.getSpec();
                //将json字符串转换成Map集合
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }

    }

    /**
     * 删除商品（逻辑删除）
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            goods.setIsDelete("1");
            for (final Long id : ids) {
                goods.setId(id);
                //逻辑删除：更新操作
                goodsDao.updateByPrimaryKeySelective(goods);

               //将商品id发送到MQ中
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        //将商品id封装成消息体
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
                // TODO:删除静态页（可选）


            }
        }
    }

    @Override
    public List<Goods> findAll() {
        return goodsDao.selectByExample(null);
    }

    /**
     * 设置库存公共属性
     *
     * @param goods
     * @param goodsDesc
     * @param item
     */
    public void setAttributeForItem(Goods goods, GoodsDesc goodsDesc, Item item) {
        //设置商品对应的图片
        //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},
        // {"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
        String itemImages = goodsDesc.getItemImages();
        //将字符串转换为List集合
        List<Map> imagesList = JSON.parseArray(itemImages, Map.class);
        if (imagesList != null && imagesList.size() > 0) {
            //取出第一张图片
            String image = imagesList.get(0).get("url").toString();
            item.setImage(image);
        }

        item.setCategoryid(goods.getCategory3Id()); //设置三级分类id
        item.setStatus("1");    //设置商品的状态
        item.setCreateTime(new Date());     //设置商品的创建时间
        item.setUpdateTime(new Date());     //设置商品的更新时间
        item.setGoodsId(goods.getId());     //设置商品id
        item.setSellerId(goods.getSellerId());//设置商家店铺id
        //设置分类名称
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
        //设置品牌名称
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());
        //设置商家店铺名称
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName());
    }

    /**
     * 上架
     * @param id
     * @param isMarketable
     */
    @Override
    public void shangjia(final Long id, String isMarketable) {
        Goods goods1 = goodsDao.selectByPrimaryKey(id);
        String auditStatus = goods1.getAuditStatus();
        if ("1".equals(auditStatus)) {
            Goods goods = new Goods();
            goods.setIsMarketable(isMarketable);
            goods.setId(id);
            goodsDao.updateByPrimaryKeySelective(goods);
            if ("1".equals(isMarketable)) {
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }
        }
    }

    /**
     * 下架
     * @param id
     * @param isMarketable
     */
    @Override
    public void xiajia(final Long id, String isMarketable) {
        Goods goods1 = goodsDao.selectByPrimaryKey(id);
        String auditStatus = goods1.getAuditStatus();
        if ("1".equals(auditStatus)) {
            Goods goods = new Goods();
            goods.setIsMarketable(isMarketable);
            goods.setId(id);
            goodsDao.updateByPrimaryKeySelective(goods);
            if ("0".equals(isMarketable)) {
                jmsTemplate.send(SolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }
        }
    }
}
