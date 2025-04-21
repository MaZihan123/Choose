package com.xuecheng.base.exception;


/**
 * @description TODO 项目的自定义异常类型
 * @author Mr.M
 * @version 1.0
 */
public class XueChengPlusException extends RuntimeException {
    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String message) {
        super(message);
        this.errMessage = message;
    }

    public static void cast(String errMessage)
    {
        throw new XueChengPlusException(errMessage);
    }
    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
    public String getErrMessage() {
        return errMessage;
    }


}