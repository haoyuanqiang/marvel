package org.marvel.deevy.foundation.service.association;

import org.marvel.deevy.foundation.entity.organization.Staff;
import org.marvel.deevy.foundation.entity.security.Role;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public interface UserAndRoleAssociationService {

    void save(String userId, String roleId);


    int delete(String userId, String roleId);


    List<Role> getRolesByUserId(String userId);


    int deleteAssociationByUserId(String userId);

    int deleteAssociationByUserId(List<String> userIds);


    List<Staff> getUsersByRoleId(String roleId);


    int deleteAssociationByRoleId(String roleId);

    int deleteAssociationByRoleId(List<String> roleId);
}
