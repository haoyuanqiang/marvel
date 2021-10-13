import request from '@/utils/request';
import type { Role, RoleRequestParams } from './apiType';

export async function getRoles(params: RoleRequestParams): Promise<any> {
  return request.get('/api/marvel-admin/roles', {
    params,
  });
}

export async function getRole(payload: string): Promise<any> {
  return request.get('/api/marvel-admin/role', {
    params: {
      id: payload,
    },
  });
}

export async function createRole(payload: Role): Promise<any> {
  return request.post('/api/marvel-admin/role', {
    data: payload,
  });
}

export async function updateRole(payload: Role): Promise<any> {
  return request.put('/api/marvel-admin/role', {
    data: payload,
  });
}

export async function deleteRoles(payload: string[]): Promise<any> {
  return request.put('/api/marvel-admin/roles/invalidation', {
    data: payload,
  });
}

// 权限操作

/**
 * 加载全部菜单
 */
export async function getMenus(): Promise<any> {
  return request.get(`/api/marvel-admin/menus`);
}

/**
 * 获取所有权限
 * @param menuId 菜单ID
 */
export async function getPermissions(menuId: string): Promise<any> {
  return request.get(`/api/marvel-admin/permissions`, {
    params: {
      menuId,
    },
  });
}

export async function getRolePermissions(payload: string): Promise<any> {
  return request.get('/api/marvel-admin/role/permissions', {
    params: { roleId: payload },
  });
}

export async function saveRolePermissions(payload: any): Promise<any> {
  return request.post('/api/marvel-admin/role/permissions', {
    data: payload,
  });
}
