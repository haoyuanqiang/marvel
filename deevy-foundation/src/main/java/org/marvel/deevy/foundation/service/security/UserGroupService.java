package org.marvel.deevy.foundation.service.security;

import org.marvel.deevy.common.service.BaseService;
import org.marvel.deevy.foundation.entity.security.UserGroup;
import org.springframework.stereotype.Service;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public interface UserGroupService extends BaseService<UserGroup> {

    boolean isDuplicate(String code, String name, String ignorantId);
}
