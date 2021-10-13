import type { Reducer, Effect } from 'umi';
import type { HttpResponse } from '../services/services';
import { getPositions, getPosition, deletePositions } from '../services/services';

export type Position = {
  key?: string;
  id?: string;
  code: string;
  name: string;
  status: number;
  sortNumber?: number;
  parentId: string;
  modifyTime?: number;
  children?: Position[];
};

export type PositionsModelState = {
  list: Position[];
  selectedRowKeys: string[];
};

export type DeaprtmentsModelType = {
  namespace: 'positions';
  state: PositionsModelState;
  effects: {
    fetch: Effect;
    edit: Effect;
    delete: Effect;
    deleteSingle: Effect;
  };
  reducers: {
    save: Reducer<PositionsModelState>;
  };
};

function formatData(positions: Position[]): Position[] {
  if (!_.isArray(positions)) {
    return [];
  }
  return _.map<Position, Position>(positions, (item) => ({
    ...item,
    key: item.id,
    title: item.name,
  }));
}

function convertToTree(positions: Position[]): Position[] {
  if (!_.isArray(positions)) {
    return [];
  }
  const tmpMap = new Map<string, Position>();
  const result: Position[] = [];
  _.forEach<Position>(positions, (position) => {
    tmpMap.set(position.id, position);
  });
  _.forEach<Position>(positions, (position) => {
    const node = tmpMap.get(position.parentId);
    if (node && position.id !== position.parentId) {
      if (!_.isArray(node.children)) {
        node.children = [];
      }
      node.children.push(tmpMap.get(position.id));
    } else {
      result.push(tmpMap.get(position.id));
    }
  });
  return result;
}

const PositionsModel: DeaprtmentsModelType = {
  namespace: 'positions',
  state: {
    list: [],
    selectedRowKeys: [],
  },
  effects: {
    *fetch(__, { call, put }) {
      const response: HttpResponse<Position[]> = yield call(getPositions);
      let positions = [];
      if (_.isObject(response) && response.code === 0 && _.isArray(response.result)) {
        positions = response.result;
      }
      yield put({
        type: 'save',
        payload: { list: convertToTree(formatData(positions)) },
      });
    },
    *edit({ callback, payload }, { call, put }) {
      const { id, isCreate, ...rest } = payload;
      if (isCreate) {
        yield put({
          type: 'positionEditor/save',
          payload: {
            entity: { ...rest },
            type: 'create',
            visible: true,
          },
        });
      } else {
        const response: HttpResponse<Position> = yield call(getPosition, id);
        if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
          const position = response.result;
          yield put({
            type: 'positionEditor/save',
            payload: {
              entity: position,
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
      const state: PositionsModelState = yield select((states) => states.positions);
      const { selectedRowKeys } = state;
      if (_.isArray(selectedRowKeys) && !_.isEmpty(selectedRowKeys)) {
        const response = yield call(deletePositions, selectedRowKeys);
        if (_.isObject(response) && response.code === 0) {
          yield put({
            type: 'positions/fetch',
          });
        }
      }
    },
    *deleteSingle({ payload }, { call, put }) {
      const { keys } = payload;
      if (_.isArray(keys)) {
        const response = yield call(deletePositions, keys);
        if (_.isObject(response) && response.code === 0) {
          yield put({
            type: 'positions/fetch',
          });
        }
      }
    },
  },
  reducers: {
    save(state, { payload }): PositionsModelState {
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

export default PositionsModel;
