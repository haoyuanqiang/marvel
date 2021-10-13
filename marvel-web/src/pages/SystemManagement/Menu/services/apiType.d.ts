export type HttpResponse<T> = {
  code: number;
  message: string;
  result: T;
};

/**
 * 菜单项类型定义
 */
export interface MenuType {
  /**
   * 菜单ID
   */
  id: string;
  /**
   * 菜单编码
   */
  code: string;
  name: string;
  path: string;
  type: number;
  method: string;
  sortNumber: number;
  icon: string;
  parentId: string;
}

/**
 * 权限类型
 */
export interface PermissionType {
  key?: string;
  id: string;
  code: string;
  name: string;
  permissionId: string;
  method: string;
  route: string;
  sortNumber: number;
  modifyTime: number;
}
