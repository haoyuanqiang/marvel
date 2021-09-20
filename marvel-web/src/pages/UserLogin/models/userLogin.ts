import { stringify } from 'querystring';
import type { Reducer, Effect } from 'umi';
import { history } from 'umi';
import { accountLogin, accountLogout } from '../services/services';
import { setAuthority } from '@/utils/utils';
import { getPageQuery } from '@/utils/utils';
// import { message } from 'antd';

export interface UserLoginState {
  status?: 'success' | 'error';
  currentAuthority?: 'user' | 'guest' | 'admin';
}

export interface UserLoginModelType {
  namespace: string;
  state: UserLoginState;
  effects: {
    login: Effect;
    logout: Effect;
  };
  reducers: {
    changeLoginStatus: Reducer<UserLoginState>;
    resetLoginStatus: Reducer<UserLoginState>;
  };
}

const Model: UserLoginModelType = {
  namespace: 'userLogin',

  state: {
    status: undefined,
  },

  effects: {
    *login({ payload }, { call, put }) {
      const response = yield call(accountLogin, payload);
      yield put({
        type: 'changeLoginStatus',
        payload: response,
      });
      // Login successfully
      if (response.code === 0 || response.code === 2009) {
        const urlParams = new URL(window.location.href);
        const params = getPageQuery();
        // message.success('🎉 🎉 🎉  登录成功！');
        let { redirect } = params as { redirect: string };
        if (redirect) {
          const redirectUrlParams = new URL(redirect);
          if (redirectUrlParams.origin === urlParams.origin) {
            redirect = redirect.substr(urlParams.origin.length);
            if (redirect.match(/^\/.*#/)) {
              redirect = redirect.substr(redirect.indexOf('#') + 1);
            }
          } else {
            window.location.href = '/';
            return;
          }
        }
        history.replace(redirect || '/');
      }
    },
    *logout(_, { call, put }) {
      yield call(accountLogout, {});
      yield put({
        type: 'resetLoginStatus'
      });
      const { redirect } = getPageQuery();
      // Note: There may be security issues, please note
      if (window.location.pathname !== '/user/login' && !redirect) {
        history.replace({
          pathname: '/user/login',
          search: stringify({
            redirect: window.location.href,
          }),
        });
      }
    },
  },

  reducers: {
    changeLoginStatus(state, { payload }) {
      const { code, result = {} } = payload || {};
      if (code === 0) {
        setAuthority(result.tokenValue);
      }
      return {
        ...state,
        status: code === 0 || code === 2009 ? 'success' : 'error',
      };
    },
    resetLoginStatus(state) {
      setAuthority('');
      return {
        ...state,
        status: undefined,
      }
    }
  },
};

export default Model;
