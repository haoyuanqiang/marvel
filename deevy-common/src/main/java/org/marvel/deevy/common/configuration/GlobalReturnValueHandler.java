package org.marvel.deevy.common.configuration;

import cn.hutool.core.util.ObjectUtil;
import org.marvel.deevy.common.annotation.NoResponseFormat;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * @author haoyuanqiang
 * @date 2022/4/8 14:02
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
public class GlobalReturnValueHandler implements HandlerMethodReturnValueHandler {

    private static final String SUCCESS = "success";


    private final RequestResponseBodyMethodProcessor internalValueHandler;


    public GlobalReturnValueHandler(RequestResponseBodyMethodProcessor processor) {
        this.internalValueHandler = processor;
    }


    @Override
    public boolean supportsReturnType(@NonNull MethodParameter methodParameter) {
        return this.internalValueHandler.supportsReturnType(methodParameter);
    }


    @Override
    public void handleReturnValue(
            Object object,
            MethodParameter methodParameter,
            @NonNull ModelAndViewContainer modelAndViewContainer,
            @NonNull NativeWebRequest nativeWebRequest
    ) throws Exception {
        if (!methodParameter.hasMethodAnnotation(NoResponseFormat.class)) {
            GlobalHttpResponseFormat response = new GlobalHttpResponseFormat();
            response.setCode(0);
            response.setMessage(SUCCESS);
            if (ObjectUtil.isNotNull(object)) {
                response.insertResult(object);
            }
            internalValueHandler.handleReturnValue(response, methodParameter, modelAndViewContainer, nativeWebRequest);
        } else {
            internalValueHandler.handleReturnValue(object, methodParameter, modelAndViewContainer, nativeWebRequest);
        }
    }
}
