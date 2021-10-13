import type { Reducer, Effect } from 'umi';
import type { HttpResponse, PermissionType } from '../services/apiType';
import { getPermission, createPermission, updatePermission } from '../services/services';

export type Permission = PermissionType;

export type PermissionEditorModelState = {
  menuId: string;
  permission: Permission;
  timestamp: number;
  type: 'create' | 'update';
  visible: boolean;
};

export type PermissionEditorModelType = {
  namespace: 'permissionEditor';
  state: PermissionEditorModelState;
  effects: {
    change: Effect;
  };
  reducers: {
    save: Reducer<PermissionEditorModelState>;
  };
};

const PermissionEditor: PermissionEditorModelType = {
  namespace: 'permissionEditor',
  state: {
    menuId: '',
    permission: null,
    timestamp: 0,
    type: 'create',
    visible: false,
  },
  effects: {
    *initialize({ payload }, { call, put, select }) {
      const { isCreate } = payload;
      const state = yield select((states) => states.menuManagement);
      const { selectedKeys } = state;
      if (_.isArray(selectedKeys) && !_.isEmpty(selectedKeys)) {
        const menuId = selectedKeys[0];
        if (isCreate) {
          yield put({
            type: 'save',
            payload: {
              menuId,
              permission: {},
              timestamp: Date.now(),
              type: 'create',
              visible: true,
            },
          });
        } else {
          const { id } = payload;
          if (!_.isEmpty(id)) {
            const response: HttpResponse<Permission> = yield call(getPermission, id);
            if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
              yield put({
                type: 'save',
                payload: {
                  menuId,
                  permission: response.result,
                  timestamp: Date.now(),
                  type: 'update',
                  visible: true,
                },
              });
            }
          }
        }
      }
    },
    *submit({ payload }, { call, put, select }) {
      const state = yield select((states) => states.permissionEditor);
      const { type, menuId } = state;
      const response: HttpResponse<string> = yield call(
        type === 'create' ? createPermission : updatePermission,
        payload,
      );
      if (_.isObject(response) && response.code === 0) {
        yield put({ type: 'save', payload: { visible: false } });
        yield put({ type: 'permissions/fetch', payload: menuId });
      }
    },
  },
  reducers: {
    save(state, { payload }): PermissionEditorModelState {
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

export default PermissionEditor;
