/**
 * request 网络请求工具
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
import { notification } from 'antd';
import { isEmpty, isUndefined } from 'lodash';
import { stringify } from 'qs';
import { history } from 'umi';
import { extend } from 'umi-request';
import { getPageQuery } from '@/utils/utils';
import { getAuthority } from '@/utils/utils';

const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

/**
 * 异常处理程序
 */
const errorHandler = (error: { response: Response }): Response => {
  const { response } = error;
  if (response && response.status) {
    const errorText = codeMessage[response.status] || response.statusText;
    const { status, url } = response;
    if (status === 401) {
      // 授权无效，转到登录页面
      const { redirect } = getPageQuery();
      if (window.location.pathname !== '/user/login' && !redirect) {
        history.replace({
          pathname: '/user/login',
          search: stringify({
            redirect: window.location.href,
          }),
        });
      }
    } else {
      // 其他错误，弹窗提示
      notification.error({
        message: `请求错误 ${status}: ${url}`,
        description: errorText,
      });
    }
  } else if (!response) {
    // 接口无响应
    notification.error({
      description: '您的网络发生异常，无法连接服务器',
      message: '网络异常',
    });
  }
  return response;
};

/**
 * 配置request请求时的默认参数
 */
const request = extend({
  errorHandler, // 默认错误处理
  credentials: 'include', // 默认请求是否带上cookie
  headers: {
    'Access-Control-Allow-Origin': '*'
  }
});

request.interceptors.request.use((url, options) => {
  const authority = getAuthority();
  const headers: any = {};
  if (!isEmpty(authority)) {
    headers.Token = authority;
  }
  if (options.method === 'GET' || options.method === 'get') {
    _.set(options, 'params', { ...(options.params), ts: Date.now() });
  }
  return {
    url,
    options: { ...options, headers },
  };
});

request.interceptors.response.use(async response => {
  const data = await response.clone().json();
  if (response.status === 200 && _.isObject(data)) {
    if (!isUndefined(data.code) && data.code !== 0) {
      notification.error({
        message: `错误码 ${data.code}`,
        description: data.message
      });
    }
  }
  return response;
});

export default request;
