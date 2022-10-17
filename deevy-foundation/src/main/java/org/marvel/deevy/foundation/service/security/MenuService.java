package org.marvel.deevy.foundation.service.security;

import org.marvel.deevy.common.model.PageResult;
import org.marvel.deevy.common.service.BaseService;
import org.marvel.deevy.foundation.entity.security.Menu;
import org.springframework.stereotype.Service;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public interface MenuService extends BaseService<Menu> {

    boolean isDuplicate(String code, String name, String ignorantId);

    PageResult<Menu> getList(int pageSize, int current, String code, String name);
}
