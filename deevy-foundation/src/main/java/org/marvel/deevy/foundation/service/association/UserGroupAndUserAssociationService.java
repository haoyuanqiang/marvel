package org.marvel.deevy.foundation.service.association;

import org.marvel.deevy.common.service.BaseService;
import org.marvel.deevy.foundation.entity.association.UserGroupAndUserAssociation;
import org.marvel.deevy.foundation.entity.organization.Staff;
import org.marvel.deevy.foundation.entity.security.UserGroup;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public interface UserGroupAndUserAssociationService extends BaseService<UserGroupAndUserAssociation> {

    void save(String userGroupId, String userId);


    int delete(String userGroupId, String userId);


    List<UserGroup> getUserGroupsByUserId(String userId);


    int deleteAssociationByUserId(String userId);


    int deleteAssociationByUserId(List<String> userIds);


    List<Staff> getUsersByUserGroupId(String userGroupId);


    int deleteAssociationByUserGroupId(String userGroupId);


    int deleteAssociationByUserGroupId(List<String> userGroupIds);
}
