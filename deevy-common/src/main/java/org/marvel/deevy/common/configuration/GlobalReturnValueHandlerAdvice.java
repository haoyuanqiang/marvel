package org.marvel.deevy.common.configuration;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/4/8 14:02
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Configuration
public class GlobalReturnValueHandlerAdvice implements InitializingBean {

    private RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    public void setHandlerAdapter(RequestMappingHandlerAdapter handlerAdapter) {
        this.handlerAdapter = handlerAdapter;
    }


    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = handlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> handlers;
        if (ObjectUtil.isNotNull(returnValueHandlers)) {
            handlers = new ArrayList<>(returnValueHandlers);
        } else {
            handlers = new ArrayList<>(1);
        }
        GlobalReturnValueHandler decorator;
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor) {
                decorator = new GlobalReturnValueHandler((RequestResponseBodyMethodProcessor) handler);
                int index = handlers.indexOf(handler);
                handlers.set(index, decorator);
                break;
            }
        }
        handlerAdapter.setReturnValueHandlers(handlers);
    }

}
