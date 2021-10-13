import type { Department, DepartmentsModelState } from './departments';
import type { DepartmentEditorModelState } from './departmentEditor';

export { Department, DepartmentModelState };

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    departments?: boolean;
    departmentEditor: boolean;
  };
}

export interface ConnectState {
  loading: Loading;
  departments: DepartmentsModelState;
  departmentEditor: DepartmentEditorModelState;
}
