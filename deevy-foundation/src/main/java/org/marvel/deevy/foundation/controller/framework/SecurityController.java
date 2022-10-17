package org.marvel.deevy.foundation.controller.framework;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.RegisteredPayload;
import lombok.extern.slf4j.Slf4j;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.foundation.entity.security.Session;
import org.marvel.deevy.foundation.entity.security.User;
import org.marvel.deevy.foundation.enums.SessionStatus;
import org.marvel.deevy.foundation.enums.UserLock;
import org.marvel.deevy.foundation.param.framework.UserLoginParam;
import org.marvel.deevy.foundation.service.organization.StaffService;
import org.marvel.deevy.foundation.service.security.SessionService;
import org.marvel.deevy.foundation.service.security.PasswordErrorCounterService;
import org.marvel.deevy.foundation.service.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author haoyuanqiang
 * @date 2022/9/11 19:43
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@RestController
@RequestMapping("/public/security")
@Slf4j
public class SecurityController {


    @Autowired
    private SessionService sessionService;


    @Autowired
    private StaffService staffService;


    @Autowired
    private UserService userService;


    @Autowired
    private PasswordErrorCounterService passwordErrorCounterService;


    /**
     * 系统登录
     * @param param    登录参数
     * @param request  Http请求
     * @param response Http相应
     * @return         Token字符串
     */
    @PostMapping("/login")
    public String login(@RequestBody UserLoginParam param, HttpServletRequest request, HttpServletResponse response) {
        if (StrUtil.isEmpty(param.getUsername()) || StrUtil.isEmpty(param.getPassword())) {
            throw ExceptionFactory.create("USERNAME_OR_PASSWORD_NOT_EMPTY");
        }

        User user = userService.getByUsername(param.getUsername());
        if (null == user) {
            throw ExceptionFactory.create("USER_NOT_FOUND");
        }

        /* 验证用户是否已锁定 */
        if (CompareUtil.compare(user.getHasLocked(), UserLock.LOCKED.ordinal()) == 0) {
            throw ExceptionFactory.create("USER_LOCKED");
        }

        /* 验证用户名密码是否匹配 */
        // 默认使用admin作为所有用户密码，待后续再次完善
        if (!StrUtil.equals(user.getPassword(), param.getPassword())) {
            // 密码验证不通过
            int restRetryTimes = handlePasswordError(user);
            throw ExceptionFactory.create("USER_PASSWORD_NOT_CORRECT", MapUtil.of("restRetryTimes", restRetryTimes));
        } else {
            passwordErrorCounterService.resetRetryTimes(user.getId());
        }

        /* 验证Session数量是否达到最大值 */
        int maxSessionCount = Math.max(1, ObjectUtil.defaultIfNull(user.getMaxSessionCount(), 1));
        int currentSessionCount = Long.valueOf(sessionService.getSessionCount(user.getId())).intValue();
        if (currentSessionCount >= maxSessionCount) {
            // 主动失效较早的Session
            sessionService.expireOriginalSession(user.getId(), currentSessionCount - maxSessionCount + 1);
        }

        /* 生成 Session */
        Session session = handleSession(sessionService.create(user), user, request);
        // 生成jwt
        String tokenStr = generateJWT(user, session);

        response.setHeader(HttpHeaders.AUTHORIZATION, StrUtil.format("Bearer {}", tokenStr));
        return tokenStr;
    }


    private int handlePasswordError(User user) {
        int restRetryTimes = passwordErrorCounterService.decrementRetryTimes(user.getId());
        if (restRetryTimes <= 0) {
            // 尝试次数达到最大限制，锁定用户
            userService.updateUserLock(user.getId(), UserLock.LOCKED.ordinal());
        }
        return restRetryTimes;
    }


    private Session handleSession(Session session, User user, HttpServletRequest request) {
        if (null == session) {
            session = sessionService.create(user);
        }
        // 记录客户端信息
        String clientIp = request.getHeader("Marvel-Real-IP");
        if (StrUtil.isBlank(clientIp)) {
            clientIp = request.getLocalAddr();
        }
        session.setIp(clientIp);
        UserAgent userAgent = UserAgentUtil.parse(request.getHeader(HttpHeaders.USER_AGENT));
        session.setBrowser(userAgent.getBrowser().getName());
        session.setOperationSystem(userAgent.getOs().getName());
        // 设置必要属性
        session.setStatus(SessionStatus.SUCCESS.ordinal());
        session.setLogoutTime(null);

        sessionService.save(session);
        return session;
    }

    /**
     * 生成JWT
     * @param user        用户信息
     * @param session  在线用户信息
     * @return            JWT
     */
    private String generateJWT(User user, Session session) {
        JWT jwt = JWT.create().setKey(StrUtil.bytes("marvel-deevy"));
        jwt.setJWTId(session.getId());
        jwt.setSubject(user.getId());
        long now = System.currentTimeMillis();
        jwt.setIssuedAt(DateUtil.date(ObjectUtil.defaultIfNull(session.getIssuedTime(), now)));
        jwt.setExpiresAt(DateUtil.date(ObjectUtil.defaultIfNull(session.getExpiredTime(), now)));
        jwt.setNotBefore(DateUtil.date(ObjectUtil.defaultIfNull(session.getNotBeforeTime(), now)));
        jwt.setCharset(CharsetUtil.CHARSET_UTF_8);
        return jwt.sign();
    }

    /**
     * 系统注销
     * @param request Http请求
     */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        String token = StrUtil.replace(request.getHeader(HttpHeaders.AUTHORIZATION), "Bearer ", "");
        if (StrUtil.isBlank(token)) {
            throw ExceptionFactory.create("EMPTY_TOKEN");
        }
        JWT jwt = JWT.of(token).setKey(StrUtil.bytes("marvel-deevy"));
        Object jwtId = jwt.getPayload(RegisteredPayload.JWT_ID);
        if (jwtId instanceof String sessionId) {
            sessionService.expireSession(sessionId);
        }
    }
}
