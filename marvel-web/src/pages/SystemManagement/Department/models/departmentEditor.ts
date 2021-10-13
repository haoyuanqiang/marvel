import type { Reducer, Effect } from 'umi';
import type { Department } from './connect';
import type { HttpResponse } from '../services/services';
import { createDepartment, updateDepartment } from '../services/services';

export type DepartmentEditorModelState = {
  entity: Department;
  type: 'create' | 'update';
  timestamp: number;
  visible: boolean;
};

export type DeaprtmentEditorModelType = {
  namespace: 'departmentEditor';
  state: DepartmentEditorModelState;
  effects: {
    fetch: Effect;
  };
  reducers: {
    save: Reducer<DepartmentEditorModelState>;
  };
};

const DepartmentEditorModel: DeaprtmentEditorModelType = {
  namespace: 'departmentEditor',
  state: {
    entity: {},
    type: 'create',
    timestamp: 0,
    visible: false,
  },
  effects: {
    *create({ callback, payload }, { call, put }) {
      const response: HttpResponse<string> = yield call(createDepartment, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({
          type: 'departments/fetch',
        });
      }
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    },
    *update({ payload, callback }, { call, put }) {
      const response: HttpResponse<string> = yield call(updateDepartment, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({
          type: 'departments/fetch',
        });
      }
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    },
  },
  reducers: {
    save(state, { payload }): DepartmentEditorModelState {
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

export default DepartmentEditorModel;
