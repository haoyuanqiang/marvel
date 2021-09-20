/** Http请求响应包装体 */
export type HttpResponse<T> = {
  code: number;
  message: string;
  result: T;
};

/**
 * 用户信息
 */
export type UserInfo = {
  /** ID */
  id: string;
  /** 用户名称 */
  name: string;
  /** 用户昵称 */
  nickname: string;
  /** 用户所属部门ID */
  departmentId: string;
  /** 用户所属部门名称 */
  departmentName: string;
  /** 性别 */
  sex: number;
  /** 邮件地址 */
  email: string;
  /** 手机号码 */
  telephone: string;
  /** 数据更新时间 */
  modifyTime: number;
}

/**
 * 修改用户密码
 */
export type ModifyPassword = {
  /** 旧密码 */
  oldPassword: string;
  /** 新密码 */
  newPassword: string;
}