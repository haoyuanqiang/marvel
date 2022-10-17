package org.marvel.deevy.common.configuration;

import lombok.extern.slf4j.Slf4j;
import org.marvel.deevy.common.exception.InterfaceException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author haoyuanqiang
 * @date 2022/4/8 14:02
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    @ExceptionHandler(value = {InterfaceException.class})
    public GlobalHttpResponseFormat handleInterfaceException(InterfaceException e) {
        GlobalHttpResponseFormat response = new GlobalHttpResponseFormat();
        response.setCode(e.getCode());
        response.setMessage(e.getMessage());
        response.setResult(e.getResult());
        return response;
    }


    @ExceptionHandler(value = {Exception.class})
    public GlobalHttpResponseFormat handleException(Exception e) {
        log.error("Catch unknown exception", e);
        GlobalHttpResponseFormat response = new GlobalHttpResponseFormat();
        response.setCode(1000);
        response.setMessage(e.getMessage());
        return response;
    }


    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public GlobalHttpResponseFormat handleServletException(InterfaceException e) {
        GlobalHttpResponseFormat response = new GlobalHttpResponseFormat();
        response.setCode(1001);
        response.setMessage(e.getMessage());
        return response;
    }
}
