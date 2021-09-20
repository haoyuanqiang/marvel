import request from '@/utils/request';
import type { User } from './apiType';

export async function getDepartments(): Promise<any> {
  return request.get('/api/marvel-admin/departments');
}

export async function getUsers(payload: any): Promise<any> {
  return request.get('/api/marvel-admin/users', {
    params: payload
  });
}

export async function getUser(payload: string): Promise<any> {
  return request.get('/api/marvel-admin/user', {
    params: {
      id: payload
    }
  })
}

export async function createUser(payload: User): Promise<any> {
  return request.post('/api/marvel-admin/user', {
    data: payload
  });
}

export async function updateUser(payload: User): Promise<any> {
  return request.put('/api/marvel-admin/user', {
    data: payload
  });
}

export async function deleteUsers(payload: string[]): Promise<any> {
  return request.put('/api/marvel-admin/users/invalidation', {
    data: payload
  });
}

export async function forceModifyPassword(payload: any) {
  return request.post('/api/marvel-admin/user/password', {
    data: payload
  });
}
