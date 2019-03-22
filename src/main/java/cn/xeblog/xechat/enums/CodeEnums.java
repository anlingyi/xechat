package cn.xeblog.xechat.enums;

/**
 * 响应码枚举
 *
 * @author yanpanyi
 * @date 2019/3/20
 */
public enum CodeEnums {

    SUCCESS(200, "Success");

    private int code;
    private String desc;

    CodeEnums(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
