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

import lombok.extern.slf4j.Slf4j;
import org.marvel.common.exception.InterfaceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class MarvelExceptionHandlerAdvice {
    private static final String CODE = "code";

    private static final String MESSAGE = "message";

    @ExceptionHandler(value = {InterfaceException.class})
    public Map<String, Object> handleInterfaceException(InterfaceException e) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(CODE, e.getCode());
        map.put(MESSAGE, e.getMessage());
        return map;
    }

    @ExceptionHandler(value = {Exception.class})
    public Map<String, Object> handleException(Exception e) {
        log.error("Catch unknown exception", e);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(CODE, 1000);
        map.put(MESSAGE, e.getMessage());
        return map;
    }
}
