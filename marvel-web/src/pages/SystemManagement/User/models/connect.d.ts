import type { DepartmentsModelState } from './departments';
import type { Department, Pagination, User } from '../services/apiType';
import type { UsersModelState } from './users';
import type { UserEditorModelState } from './userEditor';
import type { UserPasswordModelState } from './userPassword';

export {
  Department,
  DepartmentModelState,
  Pagination,
  User,
  UsersModelState,
  UserEditorModelState
};

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    departmentTree?: boolean;
    users?: boolean;
    userEditor?: boolean;
    userPassword?: boolean;
  };
}

export interface ConnectState {
  loading: Loading;
  departmentTree: DepartmentsModelState;
  users: UsersModelState;
  userEditor: UserEditorModelState;
  userPassword: UserPasswordModelState;
}
