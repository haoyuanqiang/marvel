import type { Reducer, Effect } from 'umi';
import type { Position } from './connect';
import type { HttpResponse } from '../services/services';
import { createPosition, updatePosition } from '../services/services';

export type PositionEditorModelState = {
  entity: Position;
  type: 'create' | 'update';
  timestamp: number;
  visible: boolean;
};

export type DeaprtmentEditorModelType = {
  namespace: 'positionEditor';
  state: PositionEditorModelState;
  effects: {
    fetch: Effect;
  };
  reducers: {
    save: Reducer<PositionEditorModelState>;
  };
};

const PositionEditorModel: DeaprtmentEditorModelType = {
  namespace: 'positionEditor',
  state: {
    entity: {},
    type: 'create',
    timestamp: 0,
    visible: false,
  },
  effects: {
    *create({ callback, payload }, { call, put }) {
      const response: HttpResponse<string> = yield call(createPosition, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({
          type: 'positions/fetch',
        });
      }
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    },
    *update({ payload, callback }, { call, put }) {
      const response: HttpResponse<string> = yield call(updatePosition, payload);
      if (_.isObject(response) && response.code === 0) {
        yield put({
          type: 'positions/fetch',
        });
      }
      if (_.isFunction(callback)) {
        callback(_.get(response, 'code', -1), _.get(response, 'message', ''));
      }
    },
  },
  reducers: {
    save(state, { payload }): PositionEditorModelState {
      if (_.isObject(payload)) {
        return {
          ...state,
          ...payload,
          timestamp: Date.now(),
        };
      }
      return state;
    },
  },
};

export default PositionEditorModel;
