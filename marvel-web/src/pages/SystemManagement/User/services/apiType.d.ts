export type HttpResponse<T> = {
  code: number;
  message: string;
  result: T;
};

export type Pagination = {
  total: number;
  pageSize: number;
  current: number;
};

export type Department = {
  key?: string;
  id: string;
  code: string;
  title?: string;
  name: string;
  status: number;
  sortNumber: number;
  parentId: string;
  modifyTime: number;
  children?: Department[];
};

export type User = {
  key?: string;

  /**
   * ID
   */
  id: string;

  /**
   * 数据更改时间
   */
  modifyTime: number;

  /**
   * 用户编码
   */
  code: string;

  /**
   * 用户名称
   */
  name: string;

  /**
   * 用户昵称
   */
  nickname: string;

  /**
   * 登录名称
   */
  loginName: string;

  /**
   * 电话号码
   */
  telephone: string;

  /**
   * 邮箱地址
   */
  email: string;

  /**
   * 性别： 1 = 男，2 = 女，3 = 保密
   */
  sex: number;

  /**
   * 用户状态
   */
  status: number;

  /**
   * 部门ID
   */
  departmentId: string;

  /**
   * 备注
   */
  comments: string;

  /**
   * 排序码
   */
  sortNumber: number;

  /**
   * 密码，可选字段，修改用户时不更新密码
   */
  password: string;

  /**
   * 是否只读，用于标识不可删除的数据
   */
  readOnly: boolean;
};
