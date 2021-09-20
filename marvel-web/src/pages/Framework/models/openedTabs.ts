import { isObject, get } from 'lodash';
import type { Effect, Reducer } from 'umi';

export interface IframeTabPane {
  key: string;
  name: string;
  locale?: string;
  path: string;
}

export interface OpenedTabsModelState {
  activeTabPane: string;
  tabPanes: IframeTabPane[];
}

export interface OpenedTabsModelType {
  namespace: 'openedTabs';
  state: OpenedTabsModelState;
  effects: {
    active: Effect;
    add: Effect;
    remove: Effect;
  };
  reducers: {
    activeTabPane: Reducer<OpenedTabsModelState>;
    addTabPane: Reducer<OpenedTabsModelState>;
    removeTanPane: Reducer<OpenedTabsModelState>;
  };
}

const OpenedTabsModel: OpenedTabsModelType = {
  namespace: 'openedTabs',

  state: {
    tabPanes: [
      {
        key: 'HOME',
        name: '首页',
        path: '/welcome'
      }
    ],
    activeTabPane: 'HOME',
  },

  effects: {
    *active({ payload }, { put }) {
      if (isObject(payload)) {
        yield put({
          type: 'activeTabPane',
          payload,
        })
      }
    },
    *add({ payload }, { put }) {
      if (isObject(payload)) {
         yield put({
          type: 'addTabPane',
          payload,
        });
      }
    },
    *remove({ payload }, { put }) {
      if (isObject(payload)) {
         yield put({
          type: 'removeTanPane',
          payload,
        });
      }
    }
  },

  reducers: {
    activeTabPane(state, { payload }) {
      const { key } = payload;
      let nextActiveTabPane: string = '';
      let nextTabPanes: IframeTabPane[] = [];
      if (state) {
        ({ activeTabPane: nextActiveTabPane, tabPanes: nextTabPanes } = state);
        const tanPane = nextTabPanes.find(value => value.key === key);
        nextActiveTabPane = get(tanPane, 'key', nextActiveTabPane);
      }
      return {
        ...state,
        activeTabPane: nextActiveTabPane,
        tabPanes: nextTabPanes
      }
    },
    addTabPane(state, { payload }) {
      const nextTabPanes: IframeTabPane[] = [];
      if (state) {
        const { tabPanes } = state;
        const index = tabPanes.findIndex(value => value.key === payload.id);
        if (index >= 0) {
          return {
            ...state,
            activeTabPane: payload.id
          }
        }
        nextTabPanes.push(...tabPanes);
      }
      const { id: key, name, locale, path } = payload;
      if (key && name && path) {
        nextTabPanes.push({ key, name, locale, path });
      }
      return {
        ...state,
        activeTabPane: key || '',
        tabPanes: nextTabPanes,
      };
    },
    removeTanPane(state, { payload }) {
      const nextTabPanes: IframeTabPane[] = [];
      let nextActiveTabPane = '';
      if (state) {
        const { tabPanes } = state;
        const { key } = payload;
        tabPanes.forEach((value, index, array) => {
          if (value.key !== key) {
            nextTabPanes.push(value);
          } else if (index > 0) {
            nextActiveTabPane = array[index - 1].key;
          } else {
            nextActiveTabPane = index === 0 ? array[0].key : '';
          }
        });
      }
      return {
        ...state,
        tabPanes: nextTabPanes,
        activeTabPane: nextActiveTabPane
      }
    }
  },
};

export default OpenedTabsModel;
