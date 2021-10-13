import request from '@/utils/request';
import type { MenuType, PermissionType } from './apiType';

const serverUrl = '/api/marvel-admin';

/**
 * 加载全部菜单
 */
export async function getMenus(): Promise<any> {
  return request.get(`${serverUrl}/menus`);
}

/**
 * 加载菜单
 */
export async function getMenu(menuId: string): Promise<any> {
  return request.get(`${serverUrl}/menu`, {
    params: {
      id: menuId,
    },
  });
}

/**
 * 新建菜单项
 * @param payload 菜单项
 */
export async function createMenu(payload: MenuType): Promise<any> {
  return request.post(`${serverUrl}/menu`, {
    data: payload,
  });
}

/**
 * 修改菜单项
 * @param payload 菜单数据
 */
export async function updateMenu(payload: MenuType): Promise<any> {
  return request.put(`${serverUrl}/menu`, {
    data: payload,
  });
}

/**
 * 删除菜单
 * @param payload 菜单数据
 */
export async function deleteMenus(payload: string[]): Promise<any> {
  return request.put(`${serverUrl}/menus/invalidation`, {
    data: payload,
  });
}

/**
 * 获取所有权限
 * @param menuId 菜单ID
 */
export async function getPermissions(menuId: string): Promise<any> {
  return request.get(`${serverUrl}/permissions`, {
    params: {
      menuId,
    },
  });
}

/**
 * 加载权限
 */
export async function getPermission(permissionId: string): Promise<any> {
  return request.get(`${serverUrl}/permission`, {
    params: {
      id: permissionId,
    },
  });
}

/**
 * 新建权限项
 * @param payload 权限
 */
export async function createPermission(payload: PermissionType): Promise<any> {
  return request.post(`${serverUrl}/permission`, {
    data: payload,
  });
}

/**
 * 修改权限
 * @param payload 权限
 */
export async function updatePermission(payload: PermissionType): Promise<any> {
  return request.put(`${serverUrl}/permission`, {
    data: payload,
  });
}

/**
 * 删除权限
 * @param payload 权限
 */
export async function deletePermissions(payload: string[]): Promise<any> {
  return request.put(`${serverUrl}/permissions/invalidation`, {
    data: payload,
  });
}
