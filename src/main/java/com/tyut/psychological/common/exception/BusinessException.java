package com.tyut.psychological.common.exception;

public class BusinessException extends RuntimeException {
    private final Integer code;
    private final Object data;

    public BusinessException(Integer code, String message) {
        this(code, message, null);
    }

    public BusinessException(Integer code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
