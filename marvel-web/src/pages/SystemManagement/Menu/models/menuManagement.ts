import type { Reducer, Effect } from 'umi';
import type { HttpResponse } from '../services/apiType';
import { getMenus, getMenu, deleteMenus } from '../services/services';

export type Menu = {
  key?: string;
  id: string;
  code: string;
  title?: string;
  name: string;
  type: number;
  icon?: string;
  path?: string;
  status: number;
  visible: number;
  sortNumber: number;
  parentId: string;
  modifyTime: number;
  children?: Menu[];
};

export type MenusModelState = {
  list: Menu[];
  selectedKeys: string[];
};

export type MenusModelType = {
  namespace: 'menuTree';
  state: MenusModelState;
  effects: {
    fetch: Effect;
    edit: Effect;
    delete: Effect;
  };
  reducers: {
    save: Reducer<MenusModelState>;
  };
};

function formatData(menus: Menu[]): Menu[] {
  if (!_.isArray(menus)) {
    return [];
  }
  return _.map<Menu, Menu>(menus, (item) => ({
    ..._.omit(item, ['icon']),
    key: item.id,
    title: item.name,
    isLeaf: item.type !== 1,
  }));
}

function convertToTree(menus: Menu[]): Menu[] {
  if (!_.isArray(menus)) {
    return undefined;
  }
  const tmpMap = new Map<string, Menu>();
  const result: Menu[] = [];
  _.forEach<Menu>(menus, (menu) => {
    tmpMap.set(menu.id, menu);
  });
  _.forEach<Menu>(menus, (menu) => {
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

const MenusModel: MenusModelType = {
  namespace: 'menuManagement',
  state: {
    list: [],
    selectedKeys: [],
  },
  effects: {
    *fetch(__, { call, put }) {
      const response: HttpResponse<Menu[]> = yield call(getMenus);
      let menus = [];
      if (_.isObject(response) && response.code === 0 && _.isArray(response.result)) {
        menus = response.result;
      }
      yield put({
        type: 'save',
        payload: { list: convertToTree(formatData(menus)) },
      });
    },
    *edit({ callback, payload }, { call, put }) {
      const { id, isCreate, ...rest } = payload;
      if (isCreate) {
        yield put({
          type: 'menuEditor/save',
          payload: {
            entity: { ...rest },
            type: 'create',
            visible: true,
          },
        });
      } else {
        const response: HttpResponse<Menu> = yield call(getMenu, id);
        if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
          const menu = response.result;
          yield put({
            type: 'menuEditor/save',
            payload: {
              entity: menu,
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
      const state: MenusModelState = yield select((states) => states.menuManagement);
      const { selectedKeys } = state;
      if (_.isArray(selectedKeys) && !_.isEmpty(selectedKeys)) {
        const response = yield call(deleteMenus, selectedKeys);
        if (_.isObject(response) && response.code === 0) {
          yield put({
            type: 'menuManagement/fetch',
          });
          if (window.parent) {
            window.parent.postMessage({ key: 'refreshMenus' });
          }
        }
      }
    },
  },
  reducers: {
    save(state, { payload }): MenusModelState {
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

export default MenusModel;
