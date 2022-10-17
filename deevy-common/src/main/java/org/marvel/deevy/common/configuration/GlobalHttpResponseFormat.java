package org.marvel.deevy.common.configuration;

/**
 * 请求响应包装类，仅针对请求结果是JSON格式的起效
 *
 * @author haoyuanqiang
 * @date 2022/4/26 16:37
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
public class GlobalHttpResponseFormat {

    /**
     * 请求状态码，不同于Http请求状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 请求结果
     */
    private Object result;


    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * 插入请求结果
     *
     * @param result 请求结果
     */
    public void insertResult(Object result) {
        this.result = result;
    }
}
