import type { Reducer, Effect } from 'umi';
import type { HttpResponse, User } from '../services/apiType';
import { createUser, updateUser, getUser } from '../services/services';

export type UserEditorModelState = {
  entity: User;
  type: 'create' | 'update';
  timestamp: number;
  visible: boolean;
}

export type UserEditorModelType = {
  namespace: 'userEditor';
  state: UserEditorModelState;
  effects: {
    initialize: Effect;
    create: Effect;
    update: Effect;
  };
  reducers: {
    save: Reducer<UserEditorModelState>;
  };
}

const UserEditorModel: UserEditorModelType = {
  namespace: 'userEditor',
  state: {
    entity: {},
    type: 'create',
    timestamp: 0,
    visible: false
  },
  effects: {
    *initialize({ payload }, { call, put, select }) {
      const { isCreate } = payload;
      if (isCreate) {
        const state = yield select(states => states.users);
        const { departmentId } = state;
        yield put({
          type: 'save',
          payload: {
            entity: {
              departmentId
            },
            timestamp: Date.now(),
            type: 'create',
            visible: true
          }
        })
      } else {
        // const state = yield select(states => states.users);
        const { id } = payload;
        const response: HttpResponse<User> = yield call(getUser, id);
        if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
          yield put({
            type: 'save',
            payload: {
              entity: response.result,
              timestamp: Date.now(),
              type: 'update',
              visible: true
            }
          })
        }
      }
    },
    *create({ callback, payload }, { call, put }) {
      const response: HttpResponse<string> = yield call(createUser, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put ({
          type: 'users/fetch',
        });
      }
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    },
    *update({ payload, callback }, { call, put }) {
      const response: HttpResponse<string> = yield call(updateUser, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put ({
          type: 'users/fetch',
        });
      }
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    }
  },
  reducers: {
    save(state, { payload }): UserEditorModelState {
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

export default UserEditorModel;
