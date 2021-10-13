import type { Reducer, Effect } from 'umi';
import type { HttpResponse, Menu } from '../services/apiType';
import { getMenus } from '../services/services';

export type FrameworkMenusModelState = {
  list: Menu[];
};

export type FrameworkMenusModelType = {
  namespace: 'frameworkMenus';
  state: FrameworkMenusModelState;
  effects: {
    fetch: Effect;
  };
  reducers: {
    save: Reducer<FrameworkMenusModelState>;
  };
};

function formatData(menus: Menu[]): Menu[] {
  if (!_.isArray(menus)) {
    return [];
  }
  return _.map<Menu, Menu>(menus, (item) => ({
    ...item,
    key: item.id,
  }));
}

const frameworkMenusModel: FrameworkMenusModelType = {
  namespace: 'frameworkMenus',
  state: {
    list: [],
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
        payload: { list: formatData(menus) },
      });
    },
  },
  reducers: {
    save(state, { payload }): FrameworkMenusModelState {
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

export default frameworkMenusModel;
