package org.marvel.deevy.foundation.service.security.impl;

import org.marvel.deevy.common.service.impl.BaseServiceImpl;
import org.marvel.deevy.foundation.entity.security.Permission;
import org.marvel.deevy.foundation.repository.security.PermissionRepository;
import org.marvel.deevy.foundation.service.security.PermissionService;
import org.springframework.stereotype.Service;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public class PermissionServiceImpl extends BaseServiceImpl<Permission, PermissionRepository> implements PermissionService {
}
