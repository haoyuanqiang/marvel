import request from '@/utils/request';
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
