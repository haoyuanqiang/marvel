package org.marvel.deevy.gateway.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.gateway.model.User;
import org.springframework.stereotype.Service;

/**
 * @author haoyuanqiang
 * @date 2022/9/9 16:54
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Service
public class UserService {


    public boolean verifyUser(String username, String password) {
        return true;
    }


    public User fetchUser(String userId) {
        if (!StrUtil.equals(userId, "1000")) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        user.setName("admin");
        user.setDepartmentId("1002");
        user.setPositionId("1004");
        user.setLogoutDuration(DateUnit.MINUTE.getMillis() * 5);
        return user;
    }


}
