package org.marvel.deevy.foundation.repository.association;

import org.marvel.deevy.common.repository.BaseRepository;
import org.marvel.deevy.foundation.entity.association.UserGroupAndRoleAssociation;
import org.springframework.stereotype.Repository;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Repository
public interface UserGroupAndRoleAssociationRepository extends BaseRepository<UserGroupAndRoleAssociation, String> {
}
