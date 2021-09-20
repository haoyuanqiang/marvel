import type { MenuDataItem, Settings as ProSettings } from '@ant-design/pro-layout';
import { GlobalModelState } from './system/global';
import { UserModelState } from './system/user';
import type { StateType } from './system/login';
import type { MenuModelState } from './system/menu';
import type { OpenedTabsModelState } from './system/openedTabs';

export { GlobalModelState, UserModelState };

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    global?: boolean;
    menu?: boolean;
    setting?: boolean;
    user?: boolean;
    login?: boolean;
    openedTabs?: boolean;
  };
}

export interface ConnectState {
  global: GlobalModelState;
  loading: Loading;
  settings: ProSettings;
  user: UserModelState;
  login: StateType;
  menu: MenuModelState;
  openedTabs: OpenedTabsModelState;
}

export interface Route extends MenuDataItem {
  routes?: Route[];
}

export interface MenuData {
  name?: string;
  path?: string;
  icon?: string;
  children?: MenuDataItem[];
}

export interface IframeTabPane {
  key: string;
  name: string;
  locale?: string;
  path: string;
}