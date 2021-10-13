import type { Reducer, Effect } from 'umi';
import type { HttpResponse, User } from '../services/apiType';
import { forceModifyPassword } from '../services/services';

export type UserPasswordModelState = {
  user: User;
  visible: boolean;
};

export type UserPasswordModelType = {
  namespace: 'userPassword';
  state: UserPasswordModelState;
  effects: {
    submit: Effect;
  };
  reducers: {
    save: Reducer<UserPasswordModelState>;
  };
};

const UserPasswordModel: UserPasswordModelType = {
  namespace: 'userPassword',
  state: {
    user: null,
    visible: false,
  },
  effects: {
    *submit({ callback, payload }, { call }) {
      const response: HttpResponse<string> = yield call(forceModifyPassword, payload);
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    },
  },
  reducers: {
    save(state, { payload }): UserPasswordModelState {
      if (_.isObject(payload)) {
        return {
          ...state,
          ...payload,
        };
      }
      return state;
    },
  },
};

export default UserPasswordModel;
