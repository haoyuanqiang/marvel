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

package org.marvel.common.exception;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;

public class ExceptionFactory {
    private static ExceptionFactory instance;

    private final Setting exceptionSetting;

    private ExceptionFactory() {
        exceptionSetting = new Setting("exception.setting", CharsetUtil.CHARSET_UTF_8, true);
    }

    public static ExceptionFactory getInstance() {
        if (ObjectUtil.isNull(instance)) {
            instance = new ExceptionFactory();
        }
        return instance;
    }

    public static InterfaceException create(Integer code) {
        return create(code, "default");
    }

    public static InterfaceException create(Integer code, String locale) {
        String message = getInstance().getExceptionMessage(code, locale);
        return new InterfaceException(code, message);
    }

    public String getExceptionMessage(Integer code, String locale) {
        String message = exceptionSetting.get(locale, code.toString());
        if (StrUtil.isEmpty(message)) {
            exceptionSetting.load();
        }
        return exceptionSetting.get(locale, code.toString());
    }
}
