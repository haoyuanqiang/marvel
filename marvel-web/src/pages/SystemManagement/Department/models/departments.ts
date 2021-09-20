import type { Reducer, Effect } from 'umi';
import type { HttpResponse } from '../services/services';
import { getDepartments, getDepartment, deleteDepartments } from '../services/services';

export type Department = {
  key?: string;
  id: string;
  code: string;
  title?: string;
  name: string;
  status: number;
  sortNumber: number;
  parentId: string;
  modifyTime: number;
  children?: Department[];
}

export type DepartmentsModelState = {
  list: Department[];
  selectedRowKeys: string[];
}


export type DeaprtmentsModelType = {
  namespace: 'departments';
  state: DepartmentsModelState;
  effects: {
    fetch: Effect;
    edit: Effect;
    delete: Effect;
    deleteSingle: Effect;
  };
  reducers: {
    save: Reducer<DepartmentsModelState>;
  };
}

function formatData(departments: Department[]): Department[] {
  if (!_.isArray(departments)) {
    return []
  }
  return _.map<Department, Department>(departments, item => ({
    ...item,
    key: item.id,
    title: item.name
  }))
}

function convertToTree(departments: Department[]): Department[] {
  if (!_.isArray(departments)) {
    return []
  }
  const tmpMap = new Map<string, Department>();
  const result: Department[] = [];
  _.forEach<Department>(departments, department => {
    tmpMap.set(department.id, department);
  });
  _.forEach<Department>(departments, department => {
    const node = tmpMap.get(department.parentId);
    if (node && department.id !== department.parentId) {
      if (!_.isArray(node.children)) {
        node.children = [];
      }
      node.children.push(tmpMap.get(department.id));
    } else {
      result.push(tmpMap.get(department.id));
    }
  });
  return result;
}

const DepartmentsModel: DeaprtmentsModelType = {
  namespace: 'departments',
  state: {
    list: [],
    selectedRowKeys: []
  },
  effects: {
    *fetch(__, { call, put }) {
      const response: HttpResponse<Department[]> = yield call(getDepartments);
      let departments = [];
      if (_.isObject(response) && response.code === 0 && _.isArray(response.result)) {
        departments = response.result;
      }
      yield put ({
        type: 'save',
        payload: { list: convertToTree(formatData(departments)) }
      })
    },
    *edit({ callback, payload }, { call, put }) {
      const { id, isCreate, ...rest } = payload;
      if (isCreate) {
        yield put({
          type: 'departmentEditor/save',
          payload: {
            entity: { ...rest },
            type: 'create',
            visible: true
          }
        })
      } else {
        const response: HttpResponse<Department> = yield call(getDepartment, id);
        if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
          const department = response.result;
          yield put({
            type: 'departmentEditor/save',
            payload: {
              entity: department,
              type: 'update',
              visible: true
            }
          });
        } else if (_.isFunction(callback)) {
          callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
        }
      }
    },
    *delete(__, { call, select, put }) {
      const state: DepartmentsModelState = yield select(states => states.departments);
      const { selectedRowKeys } = state;
      if (_.isArray(selectedRowKeys) && !_.isEmpty(selectedRowKeys)) {
        const response = yield call(deleteDepartments, selectedRowKeys);
        if (_.isObject(response) && response.code === 0) {
          yield put({
            type: 'departments/fetch'
          });
        }
      }
    },
    *deleteSingle({ payload }, { call, put }) {
      const { keys } = payload;
      if (_.isArray(keys)) {
        const response = yield call(deleteDepartments, keys);
        if (_.isObject(response) && response.code === 0) {
          yield put({
            type: 'departments/fetch'
          });
        }
      }
    }
  },
  reducers: {
    save(state, { payload }): DepartmentsModelState {
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

export default DepartmentsModel;
