export type HttpResponse<T> = {
  code: number;
  message: string;
  result: T;
};

/**
 * 菜单项类型定义
 */
export interface Menu {
  /** Key */
  key?: string;

  /** 菜单ID */
  id: string;

  /** 菜单编码 */
  code: string;

  /** 菜单名称 */
  name: string;

  /** 菜单名称 */
  title?: string;

  /** 菜单路径 */
  path: string;

  /** 菜单类型 */
  type: number;

  /** 排序码 */
  sortNumber: number;

  /** 图标名称 */
  iconName?: string;

  /** 父级菜单ID */
  parentId: string;

  children?: Menu[];
}

/**
 * 登录用户信息
 * 用于系统右上角展示
 */
export type UserInfo = {
  /** ID */
  id: string;

  /** 用户名称 */
  name: string;
};
