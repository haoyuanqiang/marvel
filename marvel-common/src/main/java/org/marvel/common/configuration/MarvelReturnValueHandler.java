/*
 * Licensed to the organization of MARVEL under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The MARVEL licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.marvel.common.configuration;

import cn.hutool.core.util.ObjectUtil;
import org.marvel.common.annotation.ResponseFormat;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

public class MarvelReturnValueHandler implements HandlerMethodReturnValueHandler {

    private static final String CODE = "code";

    private static final String MESSAGE = "message";

    private static final String RESULT = "result";

    private static final String SUCCESS = "success";


    private final RequestResponseBodyMethodProcessor internalValueHandler;


    public MarvelReturnValueHandler(RequestResponseBodyMethodProcessor processor) {
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
        if (methodParameter.hasMethodAnnotation(ResponseFormat.class)) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put(CODE, 0);
            result.put(MESSAGE, SUCCESS);
            if (ObjectUtil.isNotNull(object)) {
                result.put(RESULT, object);
            }
            internalValueHandler.handleReturnValue(result, methodParameter, modelAndViewContainer, nativeWebRequest);
        } else {
            internalValueHandler.handleReturnValue(object, methodParameter, modelAndViewContainer, nativeWebRequest);
        }
    }
}
