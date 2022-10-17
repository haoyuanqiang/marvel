package org.marvel.deevy.foundation.repository.security;

import org.marvel.deevy.common.repository.BaseRepository;
import org.marvel.deevy.foundation.entity.security.Role;
import org.springframework.stereotype.Repository;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Repository
public interface RoleRepository extends BaseRepository<Role, String> {
}
