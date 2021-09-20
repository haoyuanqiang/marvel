import type { Reducer, Effect } from 'umi';
import type { HttpResponse, User, Pagination } from '../services/apiType';
import { getUsers, deleteUsers } from '../services/services';

export type UsersModelState = {
  departmentId?: string;
  list: User[];
  pagination: Pagination;
  selectedKeys: string[];
}

export type UsersModelType = {
  namespace: 'users';
  state: UsersModelState;
  effects: {
    fetch: Effect;
    delete: Effect;
  };
  reducers: {
    save: Reducer<UsersModelState>;
  };
}

function formatData(users: User[]): User[] {
  if (!_.isArray(users)) {
    return []
  }
  return _.map<User, User>(users, item => ({
    ...item,
    key: item.id,
  }))
}

const UsersModel: UsersModelType = {
  namespace: 'users',
  state: {
    list: [],
    pagination: {
      total: 0,
      pageSize: 20,
      current: 1
    },
    departmentId: undefined,
    selectedKeys: []
  },
  effects: {
    *changeDepartment({ payload }, { put }) {
      yield put ({
        type: 'save',
        payload
      });
      yield put ({ type: 'fetch', payload: { current: 1, pageSize: 20 } });
    },
    *fetch({ payload }, { call, put, select }) {
      const state: UsersModelState = yield select(states => states.users);
      const response: HttpResponse<User[]> = yield call(getUsers, {
        ...payload,
        departmentId: state.departmentId || undefined
      });
      if (_.isObject(response) && response.code === 0 && _.isObject(response.result)) {
        const list = formatData(_.get(response.result, 'list'));
        yield put ({
          type: 'save',
          payload: {
            list,
            pagination: _.get(response.result, 'pagination', { 
              total: 0, 
              pageSize: 20, 
              current: 1 
            })
          }
        })
        return;
      }
      yield put ({
        type: 'save',
        payload: { list: [], pagination: { total: 0, pageSize: 20, current: 1 } }
      })
    },
    *delete(__, { call, select, put }) {
      const state: RolesModelState = yield select(states => states.users);
      const { selectedKeys } = state;
      if (_.isArray(selectedKeys) && !_.isEmpty(selectedKeys)) {
        const response = yield call(deleteUsers, selectedKeys);
        if (_.isObject(response) && response.code === 0) {
          yield put({
            type: 'users/fetch'
          })
        }
      }
    }
  },
  reducers: {
    save(state, { payload }): UsersModelState {
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

export default UsersModel;
