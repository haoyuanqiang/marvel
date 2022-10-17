package org.marvel.deevy.foundation.service.security.impl;

import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.service.impl.BaseServiceImpl;
import org.marvel.deevy.foundation.entity.organization.Position;
import org.marvel.deevy.foundation.entity.security.UserGroup;
import org.marvel.deevy.foundation.repository.security.UserGroupRepository;
import org.marvel.deevy.foundation.service.security.UserGroupService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public class UserGroupServiceImpl extends BaseServiceImpl<UserGroup, UserGroupRepository> implements UserGroupService {
    @Override
    public boolean isDuplicate(String code, String name, String ignorantId) {
        Map<String, Object> matches = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(code)) {
            matches.put(UserGroup.Fields.code, code);
        }
        if (StrUtil.isNotEmpty(name)) {
            matches.put(Position.Fields.name, name);
        }
        Map<String, Object> ignorance = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(ignorantId)) {
            ignorance.put("id", ignorantId);
        }
        return super.isDuplicate(matches, ignorance);
    }
}
