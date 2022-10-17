package org.marvel.deevy.foundation.service.organization;

import org.marvel.deevy.common.model.PageResult;
import org.marvel.deevy.common.service.BaseService;
import org.marvel.deevy.foundation.entity.organization.Staff;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public interface StaffService extends BaseService<Staff> {

    void relateUserAndRole(String userId, String roleId);


    void relateUserAndRole(String userId, List<String> roleIds);


    Staff getByLoginName(String loginName);

    PageResult<Staff> getList(int pageSize, int current, String departmentId, String codeOrName);
}
