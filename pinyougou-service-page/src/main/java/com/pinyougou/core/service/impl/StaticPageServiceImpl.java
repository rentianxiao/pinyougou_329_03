package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.pinyougou.core.service.StaticPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticPageServiceImpl implements StaticPageService,ServletContextAware{

    private Configuration configuration;

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    //注入freemarkerConfigurer好处：可以指定模板的相对路径，可以指定模板的编码格式
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //注入freemarkConfigure
    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    /**
     * 生成商品详情静态页面
     * @param id
     */
    @Override
    public void getStaticPage(Long id) {
        try {
            //1、创建configration并且指定模板的位置
            /*
                不可取：模板位置存在硬编码  希望：资源的位置在配置文件中去指定
            Configuration configuration = new Configuration(Configuration.getVersion());
            String pathName = "F:\\projects\\demo\\spring-demo\\spring-demo-freemark\\ftl\\";
            File file = new File(pathName);
            configuration.setDirectoryForTemplateLoading(file);
            */

            //2、获取该位置下的模板
            Template template = configuration.getTemplate("item.ftl");
            //3、准备数据，将数据封装到Map集合中
            Map<String,Object> dataModel = getDataModel(id);
            //4、模板 + 业务数据 = 输出（静态页面）
                //指定静态页生成的位置：项目发布的真实路径
                //request.getServletContext.getRealPath(pathName) 行不通，因为request实在web获取的
            String pathName = "/" + id + ".html";
                //项目发布的真实路径
            String realPath = servletContext.getRealPath(pathName);
                //指定文件生成的位置
            File file = new File(realPath);
            Writer out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            template.process(dataModel,out);
            //5、释放资源
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //静态页需要的数据
    private Map<String,Object> getDataModel(Long id) {
        Map<String,Object> dataModel = new HashMap<>();
        //1、商品的副标题： tb_goods
        Goods goods = goodsDao.selectByPrimaryKey(id);
        dataModel.put("goods",goods);
        //2、商品的分类：tb_item_cat
        ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
        ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
        ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
        dataModel.put("itemCat1",itemCat1);
        dataModel.put("itemCat2",itemCat2);
        dataModel.put("itemCat3",itemCat3);
        //3、商品的图片、包装清单、售后服务：tb_goods_desc
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        dataModel.put("goodsDesc",goodsDesc);

        //4、商品的标题、价格、规格：tb_item
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andStatusEqualTo("1").andGoodsIdEqualTo(id).andNumGreaterThan(0);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        dataModel.put("itemList",itemList);
        return dataModel;
    }
}
