package org.marvel.deevy.common.exception;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.marvel.deevy.common.model.Pair;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author haoyuanqiang
 * @date 2022/4/12 18:56
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Slf4j
public class ExceptionFactory {
    private static ExceptionFactory instance;

    private Map<String, ExceptionInfo> exceptions;

    private Map<Integer, String> codeIndexes;

    private ExceptionFactory() {
        try {
            Setting exceptionSetting = new Setting("exception.setting", CharsetUtil.CHARSET_UTF_8, true);
            Map<String, String> exceptionCodeMap = exceptionSetting.getMap("exception_code");

            List<Pair<String, Map<String, String>>> exceptionMessageSet = new ArrayList<>();
            for (String groupKey : exceptionSetting.getGroups()) {
                if (StrUtil.containsIgnoreCase(groupKey, "exception_message")) {
                    String locale = StrUtil.replace(groupKey, "exception_message:", "");
                    exceptionMessageSet.add(Pair.of(locale, exceptionSetting.getMap(groupKey)));
                }
            }
            Set<String> keys = new HashSet<>(exceptionCodeMap.size() * 2);
            keys.addAll(exceptionCodeMap.keySet());
            for (Pair<String, Map<String, String>> pair : exceptionMessageSet) {
                keys.addAll(pair.getValue().keySet());
            }
            exceptions = new HashMap<>(keys.size());
            codeIndexes = new HashMap<>(keys.size());
            for (String key : keys) {
                String codeString = exceptionCodeMap.get(key);
                int code = NumberUtil.isInteger(codeString) ? NumberUtil.parseInt(codeString) : 0;
                int i = 0;
                while (codeIndexes.containsKey(code) && i++ < 10000) {
                    code += 1;
                }
                MultilingualMessage message = new MultilingualMessage();
                for (Pair<String, Map<String, String>> pair : exceptionMessageSet) {
                    message.set(pair.getKey(), StrUtil.blankToDefault(pair.getValue().get(key), StrUtil.EMPTY));
                }
                ExceptionInfo info = new ExceptionInfo(code, message);
                exceptions.put(key, info);
                codeIndexes.put(code, key);
            }
            exceptionSetting.clear();
        } catch (Exception e) {
            log.warn("Failed to load 'exception.setting'. This may result in exception info displayed abnormally", e);
        }
    }

    /**
     * 获取异常工厂类实例
     *
     * @return 实例
     */
    public static ExceptionFactory getInstance() {
        if (ObjectUtil.isNull(instance)) {
            instance = new ExceptionFactory();
        }
        return instance;
    }

    /**
     * 根据异常码构造异常
     *
     * @param code 异常码
     * @return 异常
     */
    public static InterfaceException create(Integer code) {
        // 若平台支持获取请求的 locale 信息，则可以启用异常信息国际化功能
        return getInstance().create(null, code, "zh_cn", null);
    }

    /**
     * 根据异常码构造异常
     *
     * @param code 异常码
     * @param data 自定义数据
     * @return 异常
     */
    public static InterfaceException create(Integer code, Object data) {
        // 若平台支持获取请求的 locale 信息，则可以启用异常信息国际化功能
        return getInstance().create(null, code, "zh_cn", data);
    }

    /**
     * 根据异常Key值构造异常
     *
     * @param key 异常Key值
     * @return 异常
     */
    public static InterfaceException create(String key) {
        // 若平台支持获取请求的 locale 信息，则可以启用异常信息国际化功能
        return getInstance().create(key, null, "zh_cn", null);
    }

    /**
     * 根据异常Key值构造异常
     *
     * @param key  异常Key值
     * @param data 自定义数据
     * @return 异常
     */
    public static InterfaceException create(String key, Object data) {
        // 若平台支持获取请求的 locale 信息，则可以启用异常信息国际化功能
        return getInstance().create(key, null, "zh_cn", data);
    }


    public InterfaceException create(String exceptionKey, Integer exceptionCode, String locale, Object data) {
        if (!exceptions.containsKey(exceptionKey) && codeIndexes.containsKey(exceptionCode)) {
            exceptionKey = codeIndexes.get(exceptionCode);
        }
        ExceptionInfo info = exceptions.get(exceptionKey);
        if (null != info) {
            return new InterfaceException(info.getCode(), info.getMessage().get(locale), data);
        }
        // 未定义的异常，统一使用 1099 标识
        return new InterfaceException(1099,
                StrUtil.format("Undefined exception[key = {}, code = {}]", exceptionKey, exceptionCode), data);
    }

    @Data
    public static class ExceptionInfo {
        /**
         * 异常码
         */
        public int code;

        /**
         * 异常描述（默认）
         */
        public MultilingualMessage message;

        public ExceptionInfo(int code, MultilingualMessage message) {
            this.code = code;
            this.message = message;
        }
    }


    @Data
    public static class MultilingualMessage {

        private String zh_cn;

        private String en_us;


        private String handleLocale(String locale) {
            return StrUtil.replace(StrUtil.blankToDefault(locale, "zh_cn"), Pattern.compile("\\W"),
                    (matcher) -> "_");
        }

        public String get(String locale) {
            String formattedLocale = handleLocale(locale);
            if (StrUtil.equals("zh_cn", formattedLocale)) {
                return zh_cn;
            } else if (StrUtil.equals("en_us", formattedLocale)) {
                return en_us;
            } else {
                return zh_cn;
            }
        }

        public void set(String locale, String message) {
            if (StrUtil.equals("zh_cn", locale)) {
                zh_cn = message;
            } else if (StrUtil.equals("en_us", locale)) {
                en_us = message;
            }
        }
    }
}
