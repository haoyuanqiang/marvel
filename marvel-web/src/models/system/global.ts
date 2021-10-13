import type { Reducer, Effect } from 'umi';
import type { NoticeIconData } from '@/components/NoticeIcon';

export interface NoticeItem extends NoticeIconData {
  id: string;
  type: string;
  status: string;
}

export interface GlobalModelState {
  collapsed: boolean;
  notices: NoticeItem[];
}

export interface GlobalModelType {
  namespace: 'global';
  state: GlobalModelState;
  effects: {
    changeLayoutCollapsed: Effect;
  };
  reducers: {
    saveLayoutCollapsed: Reducer<GlobalModelState>;
  };
}

const GlobalModel: GlobalModelType = {
  namespace: 'global',

  state: {
    collapsed: false,
    notices: [],
  },

  effects: {
    *changeLayoutCollapsed({ payload }, { put }) {
      yield put({
        type: 'saveLayoutCollapsed',
        payload,
      });
    },
  },

  reducers: {
    saveLayoutCollapsed(state = { notices: [], collapsed: true }, { payload }): GlobalModelState {
      return {
        ...state,
        collapsed: payload,
      };
    },
  },
};

export default GlobalModel;
