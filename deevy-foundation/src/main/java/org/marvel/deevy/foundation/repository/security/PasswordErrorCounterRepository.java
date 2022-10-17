package org.marvel.deevy.foundation.repository.security;

import org.marvel.deevy.common.repository.BaseRepository;
import org.marvel.deevy.foundation.entity.security.PasswordErrorCounter;
import org.springframework.stereotype.Repository;

/**
 * @author haoyuanqiang
 * @date 2022/10/12 15:55
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Repository
public interface PasswordErrorCounterRepository extends BaseRepository<PasswordErrorCounter, String> {
}
