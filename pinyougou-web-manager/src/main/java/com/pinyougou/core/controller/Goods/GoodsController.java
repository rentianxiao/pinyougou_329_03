package com.pinyougou.core.controller.Goods;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.GoodsService;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 运营商查询查询待审核且未删除的商品列表信息
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        return goodsService.searchByManager(page, rows, goods);
    }

    /**
     * 更新商品的审核状态
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            return new Result(true, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "操作失败");
        }
    }

    /**
     * 删除商品（逻辑删除）
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "删除失败");
        }
    }

    @RequestMapping("/downloadExcel.do")
    public ResponseEntity<byte[]> down(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException {
        System.out.println("111111111");
        // 从数据库读取数据
        List<Goods> allStu = goodsService.findAll();
        // 获取服务路径
        String path = request.getRealPath("/");
        String filename = "goods.xlsx";
        // 存储File
        File tfile = new File(path + "\\" + filename);
        // 目录
        File mfile = new File(path);
        if (!tfile.exists()) {
            mfile.mkdir();
        }
        // 生成excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("商品审核表");
        int rownum = 0;
        for (Goods stu : allStu) {
            XSSFRow row = sheet.createRow(rownum);

            row.createCell(0).setCellValue(String.valueOf(stu.getId()));
            row.createCell(1).setCellValue(stu.getGoodsName());
            row.createCell(2).setCellValue(String.valueOf(stu.getPrice()));
            row.createCell(3).setCellValue(stu.getCategory1Id());
            row.createCell(4).setCellValue(stu.getCategory2Id());
            row.createCell(5).setCellValue(stu.getCategory3Id());
            row.createCell(6).setCellValue(stu.getAuditStatus());
            rownum++;
        }
        // 保存workbook
        try {
            workbook.write(new FileOutputStream(tfile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建请求头对象
        HttpHeaders headers = new HttpHeaders();
        // 下载显示的文件名，解决中文名称乱码问题
        String downloadFileName = new String(filename.getBytes("UTF-8"), "iso-8859-1");
        // 通知浏览器以attachment(下载方式)打开
        headers.setContentDispositionFormData("attachment", downloadFileName);
        // application/octet-stream:二进制流数据（最常见的文件下载）
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(tfile),
                headers, HttpStatus.CREATED);
        return responseEntity;
    }
}
