
export type HttpResponse<T> = {
  code: number;
  message: string;
  result: T;
}

export type pagination = {
  pageSize: number;
  current: number;
}

export type RoleRequestParams = pagination & {
  code?: string;
  name?: string;
}

export type Role = {
  /**
   * ID
   */
  id?: string;
  /**
   * 角色编码
   */
  code: string;
  /**
   * 角色名称
   */
  name: string;
  /**
   * 角色状态
   */
  status: number;
  /**
   * 排序
   */
  sortNumber?: number;
  /**
   * 更新时间
   */
  modifyTime?: number;
}

/**
 * 菜单项类型定义
 */
export interface Menu {
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
export type Permission = {
  /**
   * ID
   */
  id: string;

  /**
   * 更新时间
   */
  modifyTime: number;

  /**
   * 权限编码
   */
  code: string;

  /**
   * 权限名称
   */
  name: string;

  /**
   * 菜单ID
   */
  menuId: string;

  /**
   * 请求方法
   */
  method: string;

  /**
   * 权限路径
   */
  route: string;

  /**
   * 排序
   */
  sortNumber: number;
}

export type RolePermissions = {
  /**
   * 角色ID
   */
  roleId: string;

  /**
   * 菜单ID列表
   */
  menuIds: string[];

  /**
   * 权限ID列表
   */
  permissionIds: string[];
}
