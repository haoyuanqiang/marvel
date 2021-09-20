import { parse } from 'querystring';

/* eslint no-useless-escape:0 import/prefer-default-export:0 */
const reg = /(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+(?::\d+)?|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/;

export const isUrl = (path: string): boolean => reg.test(path);

export const isAntDesignPro = (): boolean => {
  if (ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION === 'site') {
    return true;
  }
  return window.location.hostname === 'preview.pro.ant.design';
};

// 给官方演示站点用，用于关闭真实开发环境不需要使用的特性
export const isAntDesignProOrDev = (): boolean => {
  const { NODE_ENV } = process.env;
  if (NODE_ENV === 'development') {
    return true;
  }
  return isAntDesignPro();
};

export const getPageQuery = () => parse(window.location.href.split('?')[1]);

// 获取后台服务器地址
export function getServerUrl(): string[] {
  if (_.isObject(window.constant)) {
    return window.constant.serverUrl;
  }
  return '';
}

/**
 * 从 sessionStorage 中获取 token
 */
export function getAuthority(): string {
  const token = sessionStorage ? sessionStorage.getItem('marvel-token') : '';
  return _.isString(token) ? token : '';
}

/**
 * 保存 token 到 sessionStorage
 * @param token token名称，可不设置
 */
export function setAuthority(token: string | undefined): void {
  if (sessionStorage) {
    sessionStorage.setItem('marvel-token', token || '');
  }
}
