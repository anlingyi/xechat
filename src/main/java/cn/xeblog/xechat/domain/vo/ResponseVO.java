package cn.xeblog.xechat.domain.vo;

import cn.xeblog.xechat.enums.CodeEnums;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 响应数据结构
 *
 * @author yanpanyi
 * @date 2019/3/20
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseVO implements Serializable {

    private static final long serialVersionUID = -5327212050370584991L;
    private static final CodeEnums success = CodeEnums.SUCCESS;

    /**
     * 响应码
     */
    private int code;
    /**
     * 响应数据
     */
    private Object data;
    /**
     * 响应描述
     */
    private String desc;

    /**
     * 成功响应且带响应数据
     *
     * @param data
     */
    public ResponseVO(Object data) {
        this.code = success.getCode();
        this.desc = success.getDesc();
        this.data = data;
    }

    /**
     * 只带响应code和desc
     *
     * @param codeEnums
     */
    public ResponseVO(CodeEnums codeEnums) {
        this.code = codeEnums.getCode();
        this.desc = codeEnums.getDesc();
    }


}
