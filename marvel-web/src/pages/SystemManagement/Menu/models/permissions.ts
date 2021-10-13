import type { Reducer, Effect } from 'umi';
import type { HttpResponse, PermissionType } from '../services/apiType';
import { getPermissions, getPermission, deletePermissions } from '../services/services';

export type Permission = PermissionType;

export type PermissionsModelState = {
  list: Permission[];
  selectedKeys: string[];
  menuId?: string;
};

export type PermissionsModelType = {
  namespace: 'permissions';
  state: PermissionsModelState;
  effects: {
    fetch: Effect;
    edit: Effect;
    delete: Effect;
  };
  reducers: {
    save: Reducer<PermissionsModelState>;
  };
};

function formatData(permissions: Permission[]): Permission[] {
  if (!_.isArray(permissions)) {
    return [];
  }
  return _.map<Permission, Permission>(permissions, (item) => ({
    ...item,
    key: item.id,
  }));
}

const PermissionsModel: PermissionsModelType = {
  namespace: 'permissions',
  state: {
    list: [],
    selectedKeys: [],
    menuId: undefined,
  },
  effects: {
    *change({ payload }, { put }) {
      yield put({
        type: 'save',
        payload,
      });
      yield put({ type: 'fetch' });
    },
    *fetch(__, { call, put, select }) {
      const state: PermissionsModelState = yield select((states) => states.permissions);
      const response: HttpResponse<Permission[]> = yield call(getPermissions, state.menuId);
      let permissions = [];
      if (_.isObject(response) && response.code === 0 && _.isArray(response.result)) {
        permissions = response.result;
      }
      yield put({
        type: 'save',
        payload: { list: formatData(permissions) },
      });
    },
    *edit({ callback, payload }, { call, put }) {
      const { id, isCreate, ...rest } = payload;
      if (isCreate) {
        yield put({
          type: 'permissionEditor/save',
          payload: {
            entity: { ...rest },
            type: 'create',
            visible: true,
          },
        });
      } else {
        const response: HttpResponse<Permission> = yield call(getPermission, id);
        if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
          const permission = response.result;
          yield put({
            type: 'permissionEditor/save',
            payload: {
              entity: permission,
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
      const state: PermissionsModelState = yield select((states) => states.permissions);
      const { selectedKeys } = state;
      if (_.isArray(selectedKeys) && !_.isEmpty(selectedKeys)) {
        const response = yield call(deletePermissions, selectedKeys);
        if (_.isObject(response) && response.code === 0) {
          yield put({
            type: 'save',
            payload: {
              selectedKeys: [],
            },
          });
          yield put({
            type: 'fetch',
          });
        }
      }
    },
  },
  reducers: {
    save(state, { payload }): PermissionsModelState {
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

export default PermissionsModel;
