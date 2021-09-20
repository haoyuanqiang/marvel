import type { Reducer, Effect } from 'umi';
import type { HttpResponse } from '../services/apiType';
import { getLoginUser } from '../services/services';

export type LoginUserModelState = {
  id?: string;
  code?: string;
  name?: string;
  loginName?: string;
  nickname?: string;
  sex?: string;
}

export type LoginUserModelType = {
  namespace: 'loginUser';
  state: LoginUserModelState;
  effects: {
    fetch: Effect;
  };
  reducers: {
    save: Reducer<LoginUserModelState>;
  };
}

const loginUserModel: LoginUserModelType = {
  namespace: 'loginUser',
  state: {},
  effects: {
    *fetch(__, { call, put }) {
      const response: HttpResponse<Record<string, string>> = yield call(getLoginUser);
      let user = {};
      if (_.isObject(response) && response.code === 0 && _.isArray(response.result)) {
        user = response.result;
      }
      yield put ({
        type: 'save',
        payload: { ...user }
      })
    }
  },
  reducers: {
    save(state, { payload }): LoginUserModelState {
      if (_.isObject(payload)) {
        return {
          ...state,
          ...payload
        }
      }
      return state;
    }
  }
}

export default loginUserModel;
