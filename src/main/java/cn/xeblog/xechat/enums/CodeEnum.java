package cn.xeblog.xechat.enums;

import cn.xeblog.xechat.enums.inter.Code;

/**
 * 响应码枚举
 *
 * @author yanpanyi
 * @date 2019/3/20
 */
public enum CodeEnum implements Code {

    /**
     * 上传的文件不是图片
     */
    UPLOADED_FILE_IS_NOT_AN_IMAGE(1002, "上传的文件不是图片!"),
    /**
     * 消息已过期
     */
    MESSAGE_HAS_EXPIRED(1001, "消息已过期，不能撤回！"),
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "网络异常！"),
    /**
     * 参数验证失败
     */
    INVALID_PARAMETERS(501, "非法参数！"),
    /**
     * Token验证不通过
     */
    INVALID_TOKEN(502, "没有权限！"),
    /**
     * 处理失败
     */
    FAILED(503, "处理失败！"),
    /**
     * 响应成功
     */
    SUCCESS(200, "Success");

    private int code;
    private String desc;

    CodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
