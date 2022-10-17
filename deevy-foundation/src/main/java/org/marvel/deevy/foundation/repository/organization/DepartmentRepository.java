package org.marvel.deevy.foundation.repository.organization;

import org.marvel.deevy.common.repository.BaseRepository;
import org.marvel.deevy.foundation.entity.organization.Department;
import org.springframework.stereotype.Repository;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Repository
public interface DepartmentRepository extends BaseRepository<Department, String> {
}
