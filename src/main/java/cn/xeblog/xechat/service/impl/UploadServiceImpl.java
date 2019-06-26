package cn.xeblog.xechat.service.impl;

import cn.xeblog.xechat.config.FileConfig;
import cn.xeblog.xechat.enums.CodeEnum;
import cn.xeblog.xechat.exception.ErrorCodeException;
import cn.xeblog.xechat.service.UploadService;
import cn.xeblog.xechat.utils.CheckUtils;
import cn.xeblog.xechat.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yanpanyi
 * @date 2019/3/27
 */
@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

    @Resource
    private FileConfig fileConfig;

    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 返回路径
     */
    private String respPath;
    private byte[] bytes;

    @Override
    public String uploadImage(MultipartFile multipartFile) throws Exception {
        if (multipartFile.isEmpty()) {
            throw new ErrorCodeException(CodeEnum.FAILED);
        }

        execute(getFile(multipartFile));

        return respPath;
    }

    private File getFile(MultipartFile multipartFile) throws Exception {
        String originalFilename = multipartFile.getOriginalFilename();
        if (!StringUtils.isEmpty(originalFilename)) {
            type = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        if (!CheckUtils.isImage(type)) {
            throw new ErrorCodeException(CodeEnum.UPLOADED_FILE_IS_NOT_AN_IMAGE);
        }

        fileName = UUIDUtils.create() + type;
        respPath = fileConfig.getAccessAddress() + fileName;
        bytes = multipartFile.getBytes();

        File file = new File(fileConfig.getDirectoryMapping().replace("file:", "") +
                fileConfig.getUploadPath() + fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return file;
    }

    private void execute(File file) throws Exception {
        BufferedOutputStream stream = null;

        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
            stream.write(bytes);
        } catch (Exception e) {
            log.error("上传文件出现异常！error -> ", e);
            throw new ErrorCodeException(CodeEnum.FAILED);
        } finally {
            if (null != stream) {
                stream.close();
            }
        }
    }
}
