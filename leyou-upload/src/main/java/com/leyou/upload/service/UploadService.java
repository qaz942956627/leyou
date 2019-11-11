package com.leyou.upload.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author 小卢
 */
@Service
public class UploadService {

    private static final List<String> CONTENT_TYPE = Arrays.asList("image/gif","image/jpeg","image/png");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public String uploadImage(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();

        //校验文件类型
        if (!CONTENT_TYPE.contains(multipartFile.getContentType())) {
            LOGGER.info("文件类型不合法,文件名:{}",originalFilename);
            return null;
        }
        try {
            //校验文件内容
            BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());

            if (bufferedImage == null) {
                LOGGER.info("文件内容不合法,文件名:{}",originalFilename);
                return null;
            }
            //保存到服务器
            multipartFile.transferTo(new File("D:/images/"+originalFilename));
            //返回URL进行回显
            return "http://image.lutest.cn/"+originalFilename;
        } catch (IOException e) {
            LOGGER.info("服务器内部错误,文件名:{}",originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
