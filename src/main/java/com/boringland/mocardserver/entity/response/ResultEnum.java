package com.boringland.mocardserver.entity.response;

public enum ResultEnum implements IResult{

    SUCCESS(1000, "接口调用成功"),
    VALIDATE_FAILED(3002, "参数校验失败"),
    COMMON_FAILED(3000, "接口调用失败"),
    FORBIDDEN(3001, "没有权限访问资源"),
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_LIMIT_EXCEEDED(2002, "用户每日资源限制");


    private Integer code;
    private String message;

    // 构造函数
    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
