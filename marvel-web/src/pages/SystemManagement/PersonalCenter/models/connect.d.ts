import type { UserModelState } from './user';

export { UserModelState };

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    userInfo?: boolean;
  };
}

export interface ConnectState {
  loading: Loading;
  userInfo: UserModelState;
}
