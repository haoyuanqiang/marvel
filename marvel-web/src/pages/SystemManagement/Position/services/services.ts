import request from '@/utils/request';
import type { Position } from './apiType';

export type HttpResponse<T> = {
  code: number;
  message: string;
  result: T;
}

export async function getPositions(): Promise<any> {
  return request.get('/api/marvel-admin/positions');
}

export async function getPosition(payload: string): Promise<any> {
  return request.get('/api/marvel-admin/position', {
    params: {
      id: payload
    }
  })
}

export async function createPosition(payload: Position): Promise<any> {
  return request.post('/api/marvel-admin/position', {
    data: payload
  });
}

export async function updatePosition(payload: Position): Promise<any> {
  return request.put('/api/marvel-admin/position', {
    data: payload
  });
}

export async function deletePositions(payload: string[]): Promise<any> {
  return request.put('/api/marvel-admin/positions/invalidation', {
    data: payload
  });
}
