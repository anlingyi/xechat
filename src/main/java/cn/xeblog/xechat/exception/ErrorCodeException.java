package cn.xeblog.xechat.exception;

import cn.xeblog.xechat.enums.CodeEnums;

/**
 * 自定义错误异常
 *
 * @author yanpanyi
 * @date 2019/3/20
 */
public class ErrorCodeException extends Exception {

    private CodeEnums codeEnums;

    public ErrorCodeException(CodeEnums codeEnums) {
        this.codeEnums = codeEnums;
    }

    public CodeEnums getCodeEnums() {
        return codeEnums;
    }

    public void setCodeEnums(CodeEnums codeEnums) {
        this.codeEnums = codeEnums;
    }
}
