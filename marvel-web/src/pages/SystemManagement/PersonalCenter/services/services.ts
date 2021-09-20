import request from '@/utils/request';
import type { ModifyPassword, UserInfo } from './apiType';

/**
 * 获取用户信息
 */
export async function getUserInfo(): Promise<any> {
  return request.get(`/api/marvel-admin/personal/info`);
}

/**
 * 更新用户信息
 */
export async function updateUserInfo(payload: UserInfo): Promise<any> {
  return request.put(`/api/marvel-admin/personal/info`, { data: payload });
}

/**
 * 更新用户密码
 */
export async function updateUserPassword(payload: ModifyPassword): Promise<any> {
  return request.put(`/api/marvel-admin/personal/password`, { data: payload });
}