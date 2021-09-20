import request from '@/utils/request';
import type { Department } from './apiType';

export type HttpResponse<T> = {
  code: number;
  message: string;
  result: T;
}

export async function getDepartments(): Promise<any> {
  return request.get('/api/marvel-admin/departments');
}

export async function getDepartment(payload: string): Promise<any> {
  return request.get('/api/marvel-admin/department', {
    params: {
      id: payload
    }
  })
}

export async function createDepartment(payload: Department): Promise<any> {
  return request.post('/api/marvel-admin/department', {
    data: payload
  });
}

export async function updateDepartment(payload: Department): Promise<any> {
  return request.put('/api/marvel-admin/department', {
    data: payload
  });
}

export async function deleteDepartments(payload: string[]): Promise<any> {
  return request.put('/api/marvel-admin/departments/invalidation', {
    data: payload
  });
}