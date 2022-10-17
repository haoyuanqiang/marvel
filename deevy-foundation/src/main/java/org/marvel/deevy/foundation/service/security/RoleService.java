package org.marvel.deevy.foundation.service.security;

import org.marvel.deevy.common.model.PageResult;
import org.marvel.deevy.common.service.BaseService;
import org.marvel.deevy.foundation.entity.security.Role;
import org.springframework.stereotype.Service;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public interface RoleService extends BaseService<Role> {

    boolean isDuplicate(String code, String name, String ignorantId);

    PageResult<Role> getList(int pageSize, int current, String code, String name);
}
