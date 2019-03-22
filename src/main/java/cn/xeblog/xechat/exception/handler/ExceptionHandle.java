package cn.xeblog.xechat.exception.handler;

import cn.xeblog.xechat.domain.vo.ResponseVO;
import cn.xeblog.xechat.enums.CodeEnum;
import cn.xeblog.xechat.exception.ErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理
 *
 * @author yanpanyi
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO handle(Exception e) {
        log.error("error:", e);
        return new ResponseVO(CodeEnum.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ErrorCodeException.class)
    @ResponseBody
    public ResponseVO invalidParameterErrorHandler(ErrorCodeException e) {
        return new ResponseVO(e.getCode());
    }

}
