import request from '@/utils/request';
// import { getServerUrl } from '@/utils/utils';
import type { LoginParamsType } from './apiType';

const serverUrl = '/api';

export async function accountLogin(params: LoginParamsType): Promise<any> {
  return request.post(`${serverUrl}/marvel-auth/login`, {
    data: params,
  });
}

export async function accountLogout(): Promise<any> {
  return request.post(`${serverUrl}/marvel-auth/logout`);
}

export async function getLoginUser(): Promise<any> {
  return request.get(`${serverUrl}/marvel-basis/user/current`);
}

export async function getMenus(): Promise<any> {
  return request.get(`${serverUrl}/marvel-basis/menus`);
}

export async function checkPageAuthorization(path: string) {
  return request.post(`${serverUrl}/marvel-admin/authorization/menu`, {
    data: {
      menu: path
    }
  });
} 
