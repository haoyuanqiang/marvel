package org.marvel.deevy.foundation.repository.security;

import org.marvel.deevy.common.repository.BaseRepository;
import org.marvel.deevy.foundation.entity.security.Session;
import org.springframework.stereotype.Repository;

/**
 * @author haoyuanqiang
 * @date 2022/9/14 17:02
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Repository
public interface SessionRepository extends BaseRepository<Session, String> {

}
