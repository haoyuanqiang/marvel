import type { Menu, MenusModelState } from './menuManagement';
import type { MenuEditorModelState } from './menuEditor';
import type { Permission, PermissionsModelState } from './permissions';
import type { PermissionEditorModelState } from './permissionEditor';

export { Menu, MenusModelState, Permission };

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    menuManagement?: boolean;
    menuEditor?: boolean;
    permissions?: boolean;
    permissionEditor?: boolean; 
  };
}

export interface ConnectState {
  loading: Loading;
  menuManagement: MenusModelState;
  menuEditor: MenuEditorModelState;
  permissions: PermissionsModelState;
  permissionEditor: PermissionEditorModelState;
}
