import type { Reducer, Effect } from 'umi';
import type { HttpResponse, UserInfo } from '../services/apiType';
import { getUserInfo, updateUserInfo, updateUserPassword } from '../services/services';

export type UserModelState = UserInfo & { timestamp: number; };

export type UserModelType = {
  namespace: 'userInfo';
  state: UserModelState;
  effects: {
    fetch: Effect;
    updateUserInfo: Effect;
    updatePassword: Effect;
  };
  reducers: {
    save: Reducer<UserModelState>;
  };
}

const userModel: UserModelType = {
  namespace: 'userInfo',
  state: {
    timestamp: 0
  },
  effects: {
    *fetch(__, { call, put }) {
      const response: HttpResponse<UserInfo> = yield call(getUserInfo);
      let user: UserInfo = {};
      if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
        user = response.result;
      }
      yield put ({
        type: 'save',
        payload: { ...user }
      })
    },
    *updateUserInfo({ payload }, { call, put }) {
      const response: HttpResponse<void> = yield call(updateUserInfo, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({
          type: 'fetch'
        })
      }
    },
    *updatePassword({ payload }, { call, put }) {
      const response: HttpResponse<void> = yield call(updateUserPassword, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({
          type: 'fetch'
        })
      }
    }
  },
  reducers: {
    save(state, { payload }): UserModelState {
      if (_.isObject(payload)) {
        return {
          ...state,
          ...payload,
          timestamp: Date.now()
        }
      }
      return state;
    }
  }
}

export default userModel;
