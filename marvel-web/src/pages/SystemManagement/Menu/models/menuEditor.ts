import type { Reducer, Effect } from 'umi';
import type { Menu } from './menu-tree';
import type { HttpResponse } from '../services/apiType';
import { getMenu, createMenu, updateMenu } from '../services/services';

export type MenuEditorModelState = {
  menu: Menu;
  timestamp: number;
  type: 'create' | 'update';
  visible: boolean;
}


export type MenuEditorModelType = {
  namespace: 'menuEditor';
  state: MenuEditorModelState;
  effects: {
    change: Effect;
  };
  reducers: {
    save: Reducer<MenuEditorModelState>;
  };
}

const MenuEditor: MenuEditorModelType = {
  namespace: 'menuEditor',
  state: {
    menu: null,
    timestamp: 0,
    type: 'create',
    visible: false
  },
  effects: {
    *initialize({ payload }, { call, put, select }) {
      const { isCreate } = payload;
      if (isCreate) {
        const state = yield select(states => states.menuManagement);
        const { selectedKeys } = state;
        yield put({
          type: 'save',
          payload: {
            menu: {
              parentId: _.isArray(selectedKeys) ? selectedKeys[0] : undefined
            },
            timestamp: Date.now(),
            type: 'create',
            visible: true
          }
        })
      } else {
        const state = yield select(states => states.menuManagement);
        const { selectedKeys } = state;
        if (_.isArray(selectedKeys) && !_.isEmpty(selectedKeys)) {
          const response: HttpResponse<Menu> = yield call(getMenu, selectedKeys[0]);
          if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
            yield put({
              type: 'save',
              payload: {
                menu: response.result,
                timestamp: Date.now(),
                type: 'update',
                visible: true
              }
            })
          }
        }
      }
    },
    *submit({ payload }, { call, put, select }) {
      const state = yield select(states => states.menuEditor);
      const { type } = state;
      const response: HttpResponse<string> = yield call(type === 'create' ? createMenu : updateMenu, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({ type: 'save', payload: { visible: false }});
        yield put({ type: 'menuManagement/fetch' });
        if (window.parent) {
          window.parent.postMessage({ key: 'refreshMenus' });
        }
      }
    }
  },
  reducers: {
    save(state, { payload }): MenusModelState {
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

export default MenuEditor;