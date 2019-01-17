package com.pinyougou.core.controller.upload;

import cn.itcast.core.pojo.entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    /**
     * 商品图片上传
     * @param file
     * @return
     */
    @RequestMapping("/uploadFile.do")
    public Result uploadFile(MultipartFile file){
        try {
            //使用工具类将附件上传
            String conf = "classpath:fdfs_client.conf";
            //创建跟踪服务器客户端
            FastDFSClient fastDFSClient = new FastDFSClient(conf);
            //附件原始名称
            String filename = file.getOriginalFilename();
            //获取附件扩展名
            String extName = FilenameUtils.getExtension(filename);
            //获取文件在服务器端的路径
            String path = fastDFSClient.uploadFile(file.getBytes(), extName, null);
            //添加服务器IP地址
            path = FILE_SERVER_URL + path;
            return new Result(true,path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"上传失败");
        }
    }

}
