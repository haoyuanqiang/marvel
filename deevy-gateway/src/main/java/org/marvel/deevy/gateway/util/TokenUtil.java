package org.marvel.deevy.gateway.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;

/**
 * @author haoyuanqiang
 * @date 2022/9/9 14:45
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
public class TokenUtil {

    private static final String key = "marvel-deevy";


    public static byte[] getKey() {
        return StrUtil.bytes(key);
    }


    public static boolean verify(String token) {
        return JWT.of(token).setKey(getKey()).verify();
    }


//    public static AuthorizationInfo decode(String token) {
//        JWT jwt = JWT.of(token);
//        return JSONUtil.toBean(jwt.getPayloads(), AuthorizationInfo.class);
//    }
//
//
//    public static String encode(AuthorizationInfo info) {
//        JWT jwt = JWT.create().setKey(getKey());
//        jwt.setPayload("userId", info.getUserId());
//        jwt.setIssuedAt(DateUtil.date(info.getCreateTime()));
//        jwt.setExpiresAt(DateUtil.date(info.getExpireTime()));
//        return jwt.sign();
//    }
}
