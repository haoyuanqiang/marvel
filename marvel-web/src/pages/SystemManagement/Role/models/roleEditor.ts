import type { Reducer, Effect } from 'umi';
import type { Role } from './connect';
import type { HttpResponse } from '../services/apiType';
import { createRole, updateRole } from '../services/services';

export type RoleEditorModelState = {
  entity: Role;
  type: 'create' | 'update';
  timestamp: number;
  visible: boolean;
};

export type RoleEditorModelType = {
  namespace: 'roleEditor';
  state: RoleEditorModelState;
  effects: {
    fetch: Effect;
  };
  reducers: {
    save: Reducer<RoleEditorModelState>;
  };
};

const RoleEditorModel: RoleEditorModelType = {
  namespace: 'roleEditor',
  state: {
    entity: {},
    type: 'create',
    timestamp: 0,
    visible: false,
  },
  effects: {
    *create({ callback, payload }, { call, put }) {
      const response: HttpResponse<string> = yield call(createRole, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({
          type: 'roles/fetch',
        });
      }
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    },
    *update({ payload, callback }, { call, put }) {
      const response: HttpResponse<string> = yield call(updateRole, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({
          type: 'roles/fetch',
        });
      }
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    },
  },
  reducers: {
    save(state, { payload }): RoleEditorModelState {
      if (_.isObject(payload)) {
        return {
          ...state,
          ...payload,
          timestamp: Date.now(),
        };
      }
      return state;
    },
  },
};

export default RoleEditorModel;
