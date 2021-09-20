import type { Menu } from '../services/apiType';
import type { FrameworkMenusModelState } from './menus';
import type { LoginUserModelState } from './loginUser';
import type { OpenedTabsModelState, IframeTabPane } from './openedTabs';

export { FrameworkMenusModelState, Menu, IframeTabPane };

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    frameworkMenus?: boolean;
    loginUser?: boolean;
    openedTabs?: boolean; 
  };
}

export interface ConnectState {
  loading: Loading;
  frameworkMenus: FrameworkMenusModelState;
  loginUser: LoginUserModelState;
  openedTabs: OpenedTabsModelState;
}
