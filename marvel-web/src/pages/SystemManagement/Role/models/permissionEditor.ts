import type { Reducer, Effect } from 'umi';
import type { HttpResponse, Menu, Permission, RolePermissions } from '../services/apiType';
import { getMenus, getPermissions, getRolePermissions, saveRolePermissions } from '../services/services';

export type RolePermissionModelState = {
  menus: Menu[];
  permissions: Permission[];
  data?: RolePermissions;
  timestamp: number;
  visible?: boolean;
}

export type RolePermissionModelType = {
  namespace: 'rolePermission';
  state: RolePermissionModelState;
  effects: {
    fetchMenus: Effect;
    fetchPermissions: Effect;
    fetchData: Effect;
    initialize: Effect;
    submit: Effect;
  };
  reducers: {
    save: Reducer<RolePermissionModelState>;
  };
}

function formatMenus(menus: Menu[], permissions: Permission[]): Menu[] {
  if (!_.isArray(menus)) {
    return []
  }
  return _.map<Menu, Menu>(menus, item => {
    const result = {
      ..._.omit(item, ['icon']),
      key: item.id,
      title: item.name,
      isLeaf: item.type !== 1,
      iconName: item.type !== 1 ? 'FileOutlined' : 'FolderOpenOutlined'
    }
    if (item.type !== 1 && _.isArray(permissions)) {
      result.children = permissions.filter(v => v.menuId === item.id);
      if (!_.isEmpty(result.children)) {
        result.isLeaf = false;
      }
    }
    return result;
  })
}

function formatPermissions(permissions: Permission[]): Permission[] {
  if (!_.isArray(permissions)) {
    return []
  }
  return _.map<Permission, Permission>(permissions, item => ({
    ...item,
    key: item.id,
    title: item.name,
    isLeaf: true,
    isPermission: true,
    iconName: 'KeyOutlined'
  }))
}

function convertToTree(menus: Menu[]): Menu[] {
  if (!_.isArray(menus)) {
    return undefined;
  }
  const tmpMap = new Map<string, Menu>();
  const result: Menu[] = [];
  _.forEach<Menu>(menus, menu => {
    tmpMap.set(menu.id, menu);
  });
  _.forEach<Menu>(menus, menu => {
    const node = tmpMap.get(menu.parentId);
    if (node && menu.id !== menu.parentId) {
      if (!_.isArray(node.children)) {
        node.children = [];
      }
      node.children.push(tmpMap.get(menu.id));
    } else {
      result.push(tmpMap.get(menu.id));
    }
  });
  return result;
}

const RolePermissionModel: RolePermissionModelType = {
  namespace: 'rolePermission',
  state: {
    menus: [],
    // permissions: [],
    data: null,
    timestamp: 0,
    visible: false
  },
  effects: {
    *fetchMenusAndPermissions(__, { call, put }) {
      const response: HttpResponse<Menu[]> = yield call(getMenus);
      let menus: Menu[] = [];
      if (_.isObject(response) && response.code === 0 && _.isArray(response.result)) {
        menus = response.result;
      }
      const response2: HttpResponse<Permission[]> = yield call(getPermissions);
      let permissions: Permission[] = [];
      if (_.isObject(response2) && response2.code === 0 && _.isArray(response2.result)) {
        permissions = formatPermissions(response2.result);
      }
      yield put ({
        type: 'save',
        payload: { menus: convertToTree(formatMenus(menus, permissions)) }
      })
    },
    *fetchData({ payload }, { call, put }) {
      yield put ({ type: 'fetchMenusAndPermissions' });
      const response: HttpResponse<RolePermissions> = yield call(getRolePermissions, payload);
      let data = null;
      if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
        data = response.result;
      }
      yield put ({
        type: 'save',
        payload: { data, visible: true }
      });
    },
    *submit({ payload }, { call, put }) {
      const response: HttpResponse<string> = yield call(saveRolePermissions, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put ({
          type: 'save',
          payload: { visible: false }
        });
      }
    }
  },
  reducers: {
    save(state, { payload }): RolePermissionModelState {
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

export default RolePermissionModel;
