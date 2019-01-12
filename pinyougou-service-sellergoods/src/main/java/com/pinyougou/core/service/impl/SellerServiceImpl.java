package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.SellerService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {


    @Resource
    private SellerDao sellerDao;

    /**
     * 商家入驻申请
     * @param seller
     */
    @Override
    public void add(Seller seller) {
        //待审核状态
        seller.setStatus("0");
        seller.setCreateTime(new Date());
        //密码加密:MD5、BCtypt、spring盐值
        String password = seller.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        password = bCryptPasswordEncoder.encode(password);
        seller.setPassword(password);

        sellerDao.insertSelective(seller);
    }

    /**
     * 待审核商家的列表查询
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //2.设置查询条件
        SellerQuery sellerQuery = new SellerQuery();
        if (seller.getStatus() != null && !"".equals(seller.getStatus().trim())){
            sellerQuery.createCriteria().andStatusEqualTo(seller.getStatus().trim());
        }
        //3.根据条件查询
        Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        List<Seller> list = new ArrayList<>();
        List<Seller> sellerList = p.getResult();
        for (Seller seller1 : sellerList) {
            if (seller1.getStatus().equals("0")){
                list.add(seller1);
            }
        }

        //4.将结果封装到PageResult中并返回
        return new PageResult(list,(long) list.size());
    }

    /**
     * 回显商家详情
     * @param sellerId
     * @return
     */
    @Override
    public Seller findOne(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId);
    }

    /**
     * 商家审核(修改商家状态)
     * @param sellerId
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }
}
