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
import java.io.File;

/**
 * @author yanpanyi
 * @date 2019/3/27
 */
@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

    @Resource
    private FileConfig fileConfig;

    @Override
    public String uploadImage(MultipartFile multipartFile) throws Exception {
        if (multipartFile.isEmpty()) {
            throw new ErrorCodeException(CodeEnum.FAILED);
        }

        return execute(multipartFile);
    }

    private String execute(MultipartFile multipartFile) throws Exception {
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            throw new ErrorCodeException(CodeEnum.INVALID_PARAMETERS);
        }

        String type = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!CheckUtils.isImage(type)) {
            throw new ErrorCodeException(CodeEnum.UPLOADED_FILE_IS_NOT_AN_IMAGE);
        }

        String fileName = UUIDUtils.create() + "." + type;
        String respPath = fileConfig.getAccessAddress() + fileName;

        File file = new File(fileConfig.getDirectoryMapping().replace("file:", "") +
                fileConfig.getUploadPath() + fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        multipartFile.transferTo(file);

        return respPath;
    }
}
