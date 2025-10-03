package com.qmruan.aicodemother.exception;

public class ThrowUtils {


    public static void throwIf(boolean condition, RuntimeException runtimeException){
        if(condition){
            throw runtimeException;
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode){
        throwIf(condition,errorCode, errorCode.getMessage());
    }

    public static void throwIf(boolean condition, ErrorCode errorCode, String message){
        throwIf(condition, new BusinessException(errorCode, message));
    }


}
