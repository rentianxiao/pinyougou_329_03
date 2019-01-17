package com.pinyougou.core.controller.orderManage;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.orderManage.OrderService;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/findAll.do")
    public List<Order> findAll(){
        return orderService.findAll();
    }

    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Order order) {

        return orderService.search(page, rows, order);
    }

    @RequestMapping("/downloadExcel")
    public ResponseEntity<byte[]> down(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException {
        System.out.println("111111111");
        // 从数据库读取数据
        List<Order> allStu = orderService.findAll();
        // 获取服务路径
        String path = request.getRealPath("/");
        String filename = "order.xlsx";
        // 存储File
        File tfile = new File(path + "\\" + filename);
        // 目录
        File mfile = new File(path);
        if (!tfile.exists()) {
            mfile.mkdir();
        }
        // 生成excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("订单信息表");

        XSSFRow row = sheet.createRow(0);
        row.setHeightInPoints(20);
        XSSFCell cell = row.createCell(0);

        cell = row.createCell(0);
        cell.setCellValue("订单ID");

        cell = row.createCell(1);
        cell.setCellValue("支付金额");

        cell = row.createCell(2);
        cell.setCellValue("用户ID");


        int rownum = 1;
        for (Order stu : allStu) {
            row = sheet.createRow(rownum);
            row.createCell(0).setCellValue(String.valueOf(stu.getOrderId()));
            row.createCell(1).setCellValue(String.valueOf(stu.getPayment()));
            row.createCell(2).setCellValue(stu.getUserId());

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

    //Excel上传数据库
    @RequestMapping(value = "/importExcel.do", method= RequestMethod.POST)
    public String uploadExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, Model model){
        //获取服务器端路径
        String path=request.getRealPath("upload");
        //获取到上传文件名称
        String fileName=file.getOriginalFilename();
        //创建目标File
        File targetFile=new File(path+"\\"+fileName);
        //创建存储目录
        File targetPath=new File(path);

        //判断服务器端目录是否存在,如果不存在创建目录
        if(!targetPath.exists()){
            targetPath.mkdir();
        }
        //把上传的文件存储到服务器端
        try {
            file.transferTo(targetFile);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //读取上传到服务器端的文件,遍历excel
        try {
            Workbook workbook= WorkbookFactory.create(targetFile);
            Sheet sheet = workbook.getSheet("Sheet1");
            //判断行数
            int rownum = sheet.getPhysicalNumberOfRows();
            for(int i=0;i<rownum;i++){
                Row row = sheet.getRow(i);
                //判断单元格数量
                int cellnum = row.getPhysicalNumberOfCells();
                StringBuffer buf=new StringBuffer();
                for(int j=0;j<cellnum;j++){
                    Cell cell = row.getCell(j);
                    if(cell.getCellType()== HSSFCell.CELL_TYPE_STRING){
                        buf.append(cell.getStringCellValue()+"~");
                    }else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
                        //创建数字格式化工具类
                        DecimalFormat df=new DecimalFormat("####");
                        //把从cell单元格读取到的数字,进行格式化防止科学计数法形式显示
                        buf.append(df.format(cell.getNumericCellValue())+"~");
                    }
                }
                //单元格循环完成后读取到的是一行内容  1~xingming~88
                String hang=buf.toString();
                String[] rows=hang.split("~");
                Order order =new Order();
                order.setOrderId(Long.valueOf(rows[0]));
                order.setPayment(new BigDecimal(rows[1]));
                order.setUserId(rows[2]);
                System.out.println("上传商品信息:"+order);
                orderService.saveOrder(order);
            }
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }

        return "成功";
    }
}
