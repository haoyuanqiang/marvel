package org.marvel.deevy.foundation.repository.security;

import org.marvel.deevy.common.repository.BaseRepository;
import org.marvel.deevy.foundation.entity.security.User;
import org.springframework.stereotype.Repository;

/**
 * @author haoyuanqiang
 * @date 2022/10/11 17:05
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Repository
public interface UserRepository extends BaseRepository<User, String> {
}
