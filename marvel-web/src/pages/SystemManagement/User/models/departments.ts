import type { Reducer, Effect } from 'umi';
import type { HttpResponse, Department } from '../services/apiType';
import { getDepartments } from '../services/services';

export type DepartmentsModelState = {
  list: Department[];
  selectedKeys: string[];
};

export type DeaprtmentsModelType = {
  namespace: 'departmentTree';
  state: DepartmentsModelState;
  effects: {
    fetch: Effect;
  };
  reducers: {
    save: Reducer<DepartmentsModelState>;
  };
};

function formatData(departments: Department[]): Department[] {
  if (!_.isArray(departments)) {
    return [];
  }
  return _.map<Department, Department>(departments, (item) => ({
    ...item,
    key: item.id,
    title: item.name,
  }));
}

function convertToTree(departments: Department[]): Department[] {
  if (!_.isArray(departments)) {
    return [];
  }
  const tmpMap = new Map<string, Department>();
  const result: Department[] = [];
  _.forEach<Department>(departments, (department) => {
    tmpMap.set(department.id, department);
  });
  _.forEach<Department>(departments, (department) => {
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
  namespace: 'departmentTree',
  state: {
    list: [],
    selectedKeys: [],
  },
  effects: {
    *fetch(__, { call, put }) {
      const response: HttpResponse<Department[]> = yield call(getDepartments);
      let departments = [];
      if (_.isObject(response) && response.code === 0 && _.isArray(response.result)) {
        departments = response.result;
      }
      yield put({
        type: 'save',
        payload: { list: convertToTree(formatData(departments)) },
      });
    },
  },
  reducers: {
    save(state, { payload }): DepartmentsModelState {
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

export default DepartmentsModel;
