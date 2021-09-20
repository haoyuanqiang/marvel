import request from '@/utils/request';
// import type { MenuType, PermissionType } from './apiType';

/**
 * 加载可显示的菜单
 */
export async function getMenus(): Promise<any> {
  return request.get(`/api/marvel-admin/framework/menus`);
}

export async function getLoginUser(): Promise<any> {
  return request.get(`/api/marvel-admin/framework/login/user`);
}
