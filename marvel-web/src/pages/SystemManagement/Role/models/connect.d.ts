import type { RolesModelState } from './roles';
import type { RoleEditorModelState } from './roleEditor';
import type { RolePermissionModelState } from './permissionEditor';

export * from '../services/apiType';
export {
  RoleModelState
};

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    roles?: boolean;
    roleEditor?: boolean;
    rolePermission?: boolean;
  };
}

export interface ConnectState {
  loading: Loading;
  roles: RolesModelState;
  roleEditor: RoleEditorModelState;
  rolePermission: RolePermissionModelState;
}
