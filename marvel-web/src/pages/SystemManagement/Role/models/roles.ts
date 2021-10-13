import type { Reducer, Effect } from 'umi';
import type { HttpResponse, Role } from '../services/apiType';
import { getRoles, getRole, deleteRoles } from '../services/services';

export type RolesModelState = {
  list: Role[];
  selectedRowKeys: string[];
  searchKey?: string;
  searchValue?: string;
};

export type RolesModelType = {
  namespace: 'roles';
  state: RolesModelState;
  effects: {
    fetch: Effect;
    edit: Effect;
    delete: Effect;
  };
  reducers: {
    save: Reducer<RolesModelState>;
  };
};

function formatData(roles: Role[]): Role[] {
  if (!_.isArray(roles)) {
    return [];
  }
  return _.map<Role, Role>(roles, (item) => ({
    ...item,
    key: item.id,
    title: item.name,
  }));
}

function convertToTree(roles: Role[]): Role[] {
  if (!_.isArray(roles)) {
    return [];
  }
  const tmpMap = new Map<string, Role>();
  const result: Role[] = [];
  _.forEach<Role>(roles, (role) => {
    tmpMap.set(role.id, role);
  });
  _.forEach<Role>(roles, (role) => {
    const node = tmpMap.get(role.parentId);
    if (node && role.id !== role.parentId) {
      if (!_.isArray(node.children)) {
        node.children = [];
      }
      node.children.push(tmpMap.get(role.id));
    } else {
      result.push(tmpMap.get(role.id));
    }
  });
  return result;
}

const RolesModel: RolesModelType = {
  namespace: 'roles',
  state: {
    list: [],
    selectedRowKeys: [],
    searchKey: 'name',
    searchValue: '',
  },
  effects: {
    *fetch(__, { call, put, select }) {
      const state: RolesModelState = yield select((states) => states.roles);
      const { searchKey, searchValue } = state;
      const payload = {};
      if (_.isString(searchKey) && !_.isEmpty(searchKey)) {
        payload[searchKey] = searchValue;
      }
      const response: HttpResponse<Role[]> = yield call(getRoles, payload);
      let roles = [];
      if (_.isObject(response) && response.code === 0 && _.isArray(response.result)) {
        roles = response.result;
      }
      yield put({
        type: 'save',
        payload: { list: convertToTree(formatData(roles)) },
      });
    },
    *edit({ callback, payload }, { call, put }) {
      const { id, isCreate, ...rest } = payload;
      if (isCreate) {
        yield put({
          type: 'roleEditor/save',
          payload: {
            entity: { ...rest },
            type: 'create',
            visible: true,
          },
        });
      } else {
        const response: HttpResponse<Role> = yield call(getRole, id);
        if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
          const role = response.result;
          yield put({
            type: 'roleEditor/save',
            payload: {
              entity: role,
              type: 'update',
              visible: true,
            },
          });
        } else if (_.isFunction(callback)) {
          callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
        }
      }
    },
    *delete(__, { call, select, put }) {
      const state: RolesModelState = yield select((states) => states.roles);
      const { selectedRowKeys } = state;
      if (_.isArray(selectedRowKeys) && !_.isEmpty(selectedRowKeys)) {
        const response = yield call(deleteRoles, selectedRowKeys);
        if (_.isObject(response) && response.code === 0) {
          yield put({
            type: 'roles/fetch',
          });
        }
      }
    },
  },
  reducers: {
    save(state, { payload }): RolesModelState {
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

export default RolesModel;
