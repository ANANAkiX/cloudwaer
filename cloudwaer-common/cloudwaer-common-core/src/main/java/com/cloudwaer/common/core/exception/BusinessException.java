package com.cloudwaer.common.core.exception;

import com.cloudwaer.common.core.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author cloudwaer
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}


